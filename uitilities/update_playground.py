#!/usr/bin/env python3
"""
Deploy FlexiCore model/service JARs to playground.wizzdi.com.

Behavior:
  - Scans local Maven module target folders recursively.
  - Collects only real JARs whose filename contains:
      * "model"   -> /home/flexicore/entities
      * "service" -> /home/flexicore/plugins
  - Excludes sources/original/shade/gradle/migration/javadoc/test jars.
  - Connects over SSH/SFTP using Paramiko.
  - Backs up existing remote plugins/entities into /home/avishay/backups.
  - Uploads JARs to /tmp first.
  - Installs JARs into the correct remote folder using sudo.
  - Removes older remote JARs with the same artifactId but a different version.
"""

import argparse
import getpass
import hashlib
import os
import posixpath
import re
import shlex
import sys
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Tuple

import paramiko


SCRIPT_VERSION = "remote-dry-run-v2"


EXCLUDED_JAR_KEYWORDS = (
    "sources",
    "source",
    "javadoc",
    "original",
    "shade",
    "gradle",
    "migration",
    "test",
)

VERSIONED_JAR_RE = re.compile(r"^(.+)-([0-9][A-Za-z0-9_.-]*)\.jar$")


@dataclass(frozen=True)
class JarCandidate:
    local_path: Path
    filename: str
    artifact_id: str
    version: str
    target_kind: str       # "model" or "service"
    remote_dir: str        # /home/flexicore/entities or /home/flexicore/plugins
    local_sha256: str
    mtime: float


def q(value: str) -> str:
    """Shell-quote a string."""
    return shlex.quote(value)


def calculate_checksum(file_path: Path) -> str:
    sha256 = hashlib.sha256()
    with file_path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            sha256.update(chunk)
    return sha256.hexdigest()


def parse_artifact_version(filename: str) -> Tuple[str, str]:
    """
    Parse Maven-like JAR names:
      alert-service-9.0.2.jar -> ("alert-service", "9.0.2")
      basic-iot-model-9.0.2-SNAPSHOT.jar -> ("basic-iot-model", "9.0.2-SNAPSHOT")

    If the filename is not versioned, use the filename without .jar as artifact_id.
    """
    match = VERSIONED_JAR_RE.match(filename)
    if match:
        return match.group(1), match.group(2)
    if filename.lower().endswith(".jar"):
        return filename[:-4], ""
    return filename, ""


def classify_jar(filename: str) -> Optional[str]:
    """
    Return:
      "model"   when the JAR filename contains "model"
      "service" when the JAR filename contains "service"
      None      otherwise
    """
    lower = filename.lower()
    if "model" in lower:
        return "model"
    if "service" in lower:
        return "service"
    return None


def is_real_deployable_jar(filename: str) -> bool:
    lower = filename.lower()
    return lower.endswith(".jar") and not any(keyword in lower for keyword in EXCLUDED_JAR_KEYWORDS)


def path_contains_target(path: Path) -> bool:
    return any(part.lower() == "target" for part in path.parts)


def collect_local_jars(
    local_base_path: Path,
    remote_plugins_dir: str,
    remote_entities_dir: str,
) -> List[JarCandidate]:
    """
    Walk local_base_path recursively, find deployable model/service JARs under target folders.

    If more than one local JAR has the same artifactId and target folder, keep the newest by mtime.
    This prevents deploying two versions of the same artifact from old build leftovers.
    """
    found: Dict[Tuple[str, str], JarCandidate] = {}

    for root, _dirs, files in os.walk(local_base_path):
        root_path = Path(root)

        if not path_contains_target(root_path):
            continue

        for filename in files:
            if not is_real_deployable_jar(filename):
                continue

            target_kind = classify_jar(filename)
            if target_kind is None:
                continue

            local_path = root_path / filename
            if not local_path.is_file():
                continue

            artifact_id, version = parse_artifact_version(filename)
            remote_dir = remote_entities_dir if target_kind == "model" else remote_plugins_dir
            mtime = local_path.stat().st_mtime

            candidate = JarCandidate(
                local_path=local_path,
                filename=filename,
                artifact_id=artifact_id,
                version=version,
                target_kind=target_kind,
                remote_dir=remote_dir,
                local_sha256=calculate_checksum(local_path),
                mtime=mtime,
            )

            key = (remote_dir, artifact_id)
            previous = found.get(key)
            if previous is None or candidate.mtime > previous.mtime:
                found[key] = candidate

    return sorted(found.values(), key=lambda item: (item.target_kind, item.artifact_id, item.filename))


def connect_ssh(
    host: str,
    port: int,
    username: str,
    secret: str,
    private_key: Optional[str],
) -> paramiko.SSHClient:
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    if private_key:
        ssh.connect(
            host,
            port=port,
            username=username,
            key_filename=private_key,
            passphrase=secret,
            timeout=30,
        )
    else:
        ssh.connect(
            host,
            port=port,
            username=username,
            password=secret,
            timeout=30,
        )

    return ssh


def run_remote(
    ssh: paramiko.SSHClient,
    command: str,
    *,
    sudo: bool,
    sudo_password: Optional[str],
    check: bool = True,
    quiet: bool = False,
) -> Tuple[int, str, str]:
    """
    Execute a remote command.

    When sudo=True, command is executed as:
      printf password | sudo -S -p '' bash -lc '<command>'
    """
    if sudo:
        if sudo_password is None:
            raise ValueError("sudo_password is required when sudo=True")
        full_command = (
            f"printf '%s\\n' {q(sudo_password)} | "
            f"sudo -S -p '' bash -lc {q(command)}"
        )
    else:
        full_command = f"bash -lc {q(command)}"

    if not quiet:
        shown = command if not sudo else f"sudo bash -lc {q(command)}"
        print(f"[remote] {shown}")

    stdin, stdout, stderr = ssh.exec_command(full_command)
    exit_code = stdout.channel.recv_exit_status()
    stdout_data = stdout.read().decode(errors="replace")
    stderr_data = stderr.read().decode(errors="replace")

    if stdout_data and not quiet:
        print(stdout_data.rstrip())
    if stderr_data and not quiet:
        print(stderr_data.rstrip(), file=sys.stderr)

    if check and exit_code != 0:
        raise RuntimeError(
            f"Remote command failed with exit code {exit_code}\n"
            f"Command: {command}\n"
            f"STDOUT:\n{stdout_data}\n"
            f"STDERR:\n{stderr_data}"
        )

    return exit_code, stdout_data, stderr_data


def remote_sha256(
    ssh: paramiko.SSHClient,
    remote_path: str,
    sudo_password: str,
) -> str:
    command = (
        f"if [ -f {q(remote_path)} ]; then "
        f"sha256sum {q(remote_path)} | awk '{{print $1}}'; "
        f"fi"
    )
    _code, stdout_data, _stderr_data = run_remote(
        ssh,
        command,
        sudo=True,
        sudo_password=sudo_password,
        check=True,
        quiet=True,
    )
    return stdout_data.strip()


def list_remote_jars(
    ssh: paramiko.SSHClient,
    remote_dir: str,
    sudo_password: str,
) -> List[str]:
    command = (
        f"if [ -d {q(remote_dir)} ]; then "
        f"find {q(remote_dir)} -maxdepth 1 -type f -name '*.jar' -printf '%f\\n'; "
        f"fi"
    )
    _code, stdout_data, _stderr_data = run_remote(
        ssh,
        command,
        sudo=True,
        sudo_password=sudo_password,
        check=True,
        quiet=True,
    )
    return [line.strip() for line in stdout_data.splitlines() if line.strip()]


def find_remote_same_artifact_different_version(
    remote_filenames: Iterable[str],
    local_candidate: JarCandidate,
) -> List[str]:
    old_files: List[str] = []

    for remote_filename in remote_filenames:
        remote_artifact_id, _remote_version = parse_artifact_version(remote_filename)

        if remote_artifact_id != local_candidate.artifact_id:
            continue

        if remote_filename == local_candidate.filename:
            continue

        old_files.append(remote_filename)

    return sorted(old_files)


def mkdir_sftp_safe(sftp: paramiko.SFTPClient, remote_dir: str) -> None:
    """
    Create a remote directory over SFTP if missing.
    Only intended for /tmp/... staging directories owned by the SSH user.
    """
    parts = [part for part in remote_dir.split("/") if part]
    current = ""
    for part in parts:
        current = "/" + part if not current else current + "/" + part
        try:
            sftp.stat(current)
        except FileNotFoundError:
            sftp.mkdir(current)


def upload_to_stage(
    sftp: paramiko.SFTPClient,
    candidate: JarCandidate,
    stage_dir: str,
) -> str:
    remote_stage_path = posixpath.join(stage_dir, candidate.filename)
    print(f"[upload] {candidate.local_path} -> {remote_stage_path}")
    sftp.put(str(candidate.local_path), remote_stage_path)
    return remote_stage_path


def build_install_command(
    stage_path: str,
    final_path: str,
    remote_dir: str,
    owner: Optional[str],
) -> str:
    owner_args = ""

    if owner:
        if ":" in owner:
            user, group = owner.split(":", 1)
            owner_args = f" -o {q(user)} -g {q(group)}"
        else:
            owner_args = f" -o {q(owner)}"

    return (
        "set -e\n"
        f"mkdir -p {q(remote_dir)}\n"
        f"install -m 0644{owner_args} {q(stage_path)} {q(final_path)}\n"
        f"rm -f {q(stage_path)}"
    )


def create_remote_backup(
    ssh: paramiko.SSHClient,
    *,
    backup_root: str,
    backup_name: str,
    remote_plugins_dir: str,
    remote_entities_dir: str,
    sudo_password: str,
) -> str:
    backup_dir = posixpath.join(backup_root.rstrip("/"), backup_name)
    plugins_backup = posixpath.join(backup_dir, "plugins")
    entities_backup = posixpath.join(backup_dir, "entities")
    manifest_path = posixpath.join(backup_dir, "MANIFEST.txt")

    command = (
        "set -e\n"
        f"mkdir -p {q(plugins_backup)} {q(entities_backup)}\n"
        f"if [ -d {q(remote_plugins_dir)} ]; then cp -a {q(remote_plugins_dir)}/. {q(plugins_backup)}/; fi\n"
        f"if [ -d {q(remote_entities_dir)} ]; then cp -a {q(remote_entities_dir)}/. {q(entities_backup)}/; fi\n"
        f"date -Is > {q(manifest_path)}\n"
        f"echo 'Backed up from:' >> {q(manifest_path)}\n"
        f"echo '  {remote_plugins_dir} -> {plugins_backup}' >> {q(manifest_path)}\n"
        f"echo '  {remote_entities_dir} -> {entities_backup}' >> {q(manifest_path)}\n"
        f"echo '' >> {q(manifest_path)}\n"
        f"echo 'Files:' >> {q(manifest_path)}\n"
        f"find {q(backup_dir)} -type f -printf '%P\\n' | sort >> {q(manifest_path)}\n"
    )

    run_remote(
        ssh,
        command,
        sudo=True,
        sudo_password=sudo_password,
        check=True,
    )

    return backup_dir


def cleanup_stage_dir(
    ssh: paramiko.SSHClient,
    stage_dir: str,
    sudo_password: str,
) -> None:
    run_remote(
        ssh,
        f"rm -rf {q(stage_dir)}",
        sudo=True,
        sudo_password=sudo_password,
        check=False,
        quiet=True,
    )


def print_candidates(candidates: List[JarCandidate]) -> None:
    if not candidates:
        print("No deployable model/service JARs found.")
        return

    print(f"Collected {len(candidates)} deployable JAR(s):")
    for candidate in candidates:
        print(
            f"  [{candidate.target_kind:7}] "
            f"{candidate.filename} -> {candidate.remote_dir} "
            f"(artifact={candidate.artifact_id}, version={candidate.version or 'unknown'})"
        )


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Deploy FlexiCore model/service JARs to playground.wizzdi.com"
    )

    parser.add_argument("--username", default="avishay", help="SSH username. Default: avishay")
    parser.add_argument("--host", default="playground.wizzdi.com", help="SSH host. Default: playground.wizzdi.com")
    parser.add_argument("--port", type=int, default=22, help="SSH port. Default: 22")
    parser.add_argument("--private-key", help="Path to SSH private key file")
    parser.add_argument("--ask-sudo-password", action="store_true", help="Prompt separately for sudo password")

    parser.add_argument("--local-base-path", default=os.getcwd(), help="Local repository root. Default: current directory")
    parser.add_argument("--remote-plugins-dir", default="/home/flexicore/plugins", help="Remote plugins directory")
    parser.add_argument("--remote-entities-dir", default="/home/flexicore/entities", help="Remote entities directory")
    parser.add_argument("--remote-backup-root", default="/home/avishay/backups", help="Remote backup root")
    parser.add_argument("--remote-stage-root", default="/tmp", help="Remote temp/stage root")
    parser.add_argument("--owner", default="flexicore:flexicore", help="Remote owner for installed JARs. Use empty string to skip chown.")
    parser.add_argument("--dry-run", action="store_true", help="Only scan and print what would be deployed")

    args = parser.parse_args()

    local_base_path = Path(args.local_base_path).resolve()
    if not local_base_path.exists():
        print(f"Local base path does not exist: {local_base_path}", file=sys.stderr)
        return 2

    owner = args.owner.strip() or None

    print(f"Script version: {SCRIPT_VERSION}")
    print(f"Local base path: {local_base_path}")
    candidates = collect_local_jars(
        local_base_path,
        args.remote_plugins_dir,
        args.remote_entities_dir,
    )
    print_candidates(candidates)

    if not candidates:
        return 1

    auth_secret = getpass.getpass(
        "Enter SSH password or private-key passphrase "
        "(also used for sudo unless --ask-sudo-password is set): "
    )

    if args.ask_sudo_password:
        sudo_password = getpass.getpass("Enter sudo password: ")
    else:
        sudo_password = auth_secret

    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
    backup_name = f"flexicore-jars-{timestamp}"
    stage_dir = posixpath.join(args.remote_stage_root.rstrip("/"), f"flexicore-jar-deploy-{timestamp}")

    ssh: Optional[paramiko.SSHClient] = None
    sftp: Optional[paramiko.SFTPClient] = None

    uploaded = 0
    installed = 0
    skipped_same_checksum = 0
    removed_old = 0

    try:
        print(f"Connecting to SSH: {args.username}@{args.host}:{args.port}")
        ssh = connect_ssh(
            args.host,
            args.port,
            args.username,
            auth_secret,
            args.private_key,
        )
        print("SSH connection established.")

        sftp = ssh.open_sftp()
        print("SFTP session established.")

        print(f"Creating remote stage directory: {stage_dir}")
        mkdir_sftp_safe(sftp, stage_dir)

        print("Creating remote backup before deployment...")
        backup_dir = create_remote_backup(
            ssh,
            backup_root=args.remote_backup_root,
            backup_name=backup_name,
            remote_plugins_dir=args.remote_plugins_dir,
            remote_entities_dir=args.remote_entities_dir,
            sudo_password=sudo_password,
        )
        print(f"Backup created: {backup_dir}")

        for candidate in candidates:
            print("")
            print(f"=== Deploying {candidate.filename} ===")
            final_path = posixpath.join(candidate.remote_dir, candidate.filename)

            remote_existing_sha = remote_sha256(ssh, final_path, sudo_password)
            exact_same_remote_file = remote_existing_sha and remote_existing_sha == candidate.local_sha256

            if exact_same_remote_file:
                print(f"[skip-upload] Exact file already exists with same checksum: {final_path}")
                skipped_same_checksum += 1
            else:
                stage_path = upload_to_stage(sftp, candidate, stage_dir)
                uploaded += 1

                install_command = build_install_command(
                    stage_path,
                    final_path,
                    candidate.remote_dir,
                    owner,
                )
                run_remote(
                    ssh,
                    install_command,
                    sudo=True,
                    sudo_password=sudo_password,
                    check=True,
                )
                installed += 1
                print(f"[installed] {final_path}")

            remote_jars = list_remote_jars(ssh, candidate.remote_dir, sudo_password)
            old_versions = find_remote_same_artifact_different_version(remote_jars, candidate)

            if old_versions:
                old_paths = [
                    posixpath.join(candidate.remote_dir, old_filename)
                    for old_filename in old_versions
                ]
                print("[cleanup] Removing older same-artifact JAR(s):")
                for old_path in old_paths:
                    print(f"  {old_path}")

                remove_command = "rm -f " + " ".join(q(path) for path in old_paths)
                run_remote(
                    ssh,
                    remove_command,
                    sudo=True,
                    sudo_password=sudo_password,
                    check=True,
                )
                removed_old += len(old_versions)
            else:
                print("[cleanup] No older same-artifact remote JARs found.")

        print("")
        print("Deployment completed.")
        print(f"Backup:                  {backup_dir}")
        print(f"Uploaded to stage:       {uploaded}")
        print(f"Installed/replaced:      {installed}")
        print(f"Skipped same checksum:   {skipped_same_checksum}")
        print(f"Removed old versions:    {removed_old}")

        return 0

    finally:
        if ssh is not None:
            try:
                cleanup_stage_dir(ssh, stage_dir, sudo_password if "sudo_password" in locals() else "")
            except Exception as exc:
                print(f"Warning: failed to clean stage dir {stage_dir}: {exc}", file=sys.stderr)

        if sftp is not None:
            print("Closing SFTP session.")
            sftp.close()

        if ssh is not None:
            print("Closing SSH connection.")
            ssh.close()


if __name__ == "__main__":
    raise SystemExit(main())
