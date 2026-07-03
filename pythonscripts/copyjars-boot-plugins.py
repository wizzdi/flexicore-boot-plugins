import os
import re
import sys
import shlex
import getpass
import hashlib
import argparse
import posixpath
from collections import defaultdict

import paramiko


EXCLUDED_JAR_KEYWORDS = [
    "gradle",
    "migration",
    "sources",
    "shade",
    "original",
]

REMOTE_PLUGINS_DIR = "/home/flexicore/plugins"
REMOTE_ENTITIES_DIR = "/home/flexicore/entities"
REMOTE_TMP_DIR = "/tmp"


def calculate_checksum(file_path):
    sha256 = hashlib.sha256()
    with open(file_path, "rb") as f:
        while chunk := f.read(8192):
            sha256.update(chunk)
    return sha256.hexdigest()


def run_remote_command(ssh, command, sudo_password=None, use_sudo=False, print_command=True):
    if use_sudo:
        if sudo_password is None:
            raise ValueError("sudo_password is required when use_sudo=True")

        # Run the command through sudo safely.
        # The command itself is passed to sh -c as one quoted argument.
        formatted_command = (
            f"printf '%s\\n' {shlex.quote(sudo_password)} "
            f"| sudo -S -p '' sh -c {shlex.quote(command)}"
        )
    else:
        formatted_command = command

    if print_command:
        shown = command if use_sudo else formatted_command
        print(f"Executing remote command: {shown}")

    stdin, stdout, stderr = ssh.exec_command(formatted_command)

    stdout_data = stdout.read().decode(errors="replace")
    stderr_data = stderr.read().decode(errors="replace")
    exit_code = stdout.channel.recv_exit_status()

    return exit_code, stdout_data, stderr_data


def list_remote_jars(ssh, remote_dir, sudo_password):
    command = (
        f"find {shlex.quote(remote_dir)} "
        f"-maxdepth 1 -type f -name '*.jar' -printf '%f\\n'"
    )

    exit_code, stdout_data, stderr_data = run_remote_command(
        ssh,
        command,
        sudo_password=sudo_password,
        use_sudo=True,
        print_command=True,
    )

    if exit_code != 0:
        print(f"Failed to list remote jars in {remote_dir}")
        if stderr_data:
            print(stderr_data)
        return []

    return [line.strip() for line in stdout_data.splitlines() if line.strip()]


def get_remote_checksum(ssh, remote_path, sudo_password):
    command = (
        f"sha256sum {shlex.quote(remote_path)} 2>/dev/null "
        f"| awk '{{print $1}}'"
    )

    exit_code, stdout_data, stderr_data = run_remote_command(
        ssh,
        command,
        sudo_password=sudo_password,
        use_sudo=True,
        print_command=False,
    )

    checksum = stdout_data.strip()

    if exit_code != 0 or not checksum:
        return None

    return checksum


def is_valid_jar(filename):
    lower = filename.lower()

    if not lower.endswith(".jar"):
        return False

    return not any(keyword in lower for keyword in EXCLUDED_JAR_KEYWORDS)


def artifact_key(filename):
    """
    Returns the jar artifact name without the version.

    Examples:
      maps-service-6.0.5.jar              -> maps-service
      maps-service-6.0.5-PARKING.jar      -> maps-service
      basic-iot-model-1.0.0-SNAPSHOT.jar  -> basic-iot-model
      maps-service.jar                    -> maps-service
    """
    name = filename[:-4] if filename.lower().endswith(".jar") else filename

    match = re.match(
        r"^(?P<artifact>.+)-(?P<version>\d+(?:\.\d+)*(?:[-._A-Za-z0-9]+)*)$",
        name,
    )

    if match:
        return match.group("artifact")

    return name


def jar_target_kind(filename):
    """
    Only service and model jars are deployable.

    Returns:
      "plugins"  for service jars
      "entities" for model jars
      None       for anything else
    """
    key = artifact_key(filename).lower()
    parts = key.split("-")

    if "model" in parts:
        return "entities"

    if "service" in parts:
        return "plugins"

    return None


def build_remote_index(remote_jars):
    index = defaultdict(list)

    for jar in remote_jars:
        key = artifact_key(jar)
        index[key].append(jar)

    return index


def find_local_candidate_jars(local_base_path):
    candidates = []

    for root, dirs, files in os.walk(local_base_path):
        if "target" not in root.split(os.sep):
            continue

        for filename in files:
            if not is_valid_jar(filename):
                continue

            target_kind = jar_target_kind(filename)
            if target_kind is None:
                print(f"Skipping non service/model jar: {filename}")
                continue

            local_path = os.path.join(root, filename)
            key = artifact_key(filename)

            candidates.append(
                {
                    "filename": filename,
                    "local_path": local_path,
                    "key": key,
                    "target_kind": target_kind,
                }
            )

    return candidates


def sftp_transfer(sftp, local_path, remote_path):
    print(f"Copying {local_path}")
    print(f"  -> {remote_path}")
    sftp.put(local_path, remote_path)


def move_tmp_to_final(ssh, remote_tmp_path, remote_dir, sudo_password):
    command = f"mv {shlex.quote(remote_tmp_path)} {shlex.quote(remote_dir)}/"

    exit_code, stdout_data, stderr_data = run_remote_command(
        ssh,
        command,
        sudo_password=sudo_password,
        use_sudo=True,
        print_command=True,
    )

    if exit_code != 0:
        print("Failed moving jar into final destination.")
        if stdout_data:
            print(stdout_data)
        if stderr_data:
            print(stderr_data)
        return False

    return True


def delete_old_versions(ssh, remote_dir, artifact, keep_filename, sudo_password):
    """
    Delete jars with the same versionless artifact name, but keep the newly copied jar.
    """
    pattern_versioned = f"{artifact}-*.jar"
    pattern_unversioned = f"{artifact}.jar"

    command = (
        f"find {shlex.quote(remote_dir)} "
        f"-maxdepth 1 -type f "
        f"\\( -name {shlex.quote(pattern_versioned)} "
        f"-o -name {shlex.quote(pattern_unversioned)} \\) "
        f"! -name {shlex.quote(keep_filename)} "
        f"-exec rm -f {{}} +"
    )

    exit_code, stdout_data, stderr_data = run_remote_command(
        ssh,
        command,
        sudo_password=sudo_password,
        use_sudo=True,
        print_command=True,
    )

    if exit_code != 0:
        print(f"Failed deleting old versions for artifact {artifact}.")
        if stdout_data:
            print(stdout_data)
        if stderr_data:
            print(stderr_data)
        return False

    return True


def deploy_candidate(
        ssh,
        sftp,
        candidate,
        remote_plugins_index,
        remote_entities_index,
        sudo_password,
):
    filename = candidate["filename"]
    local_path = candidate["local_path"]
    artifact = candidate["key"]
    target_kind = candidate["target_kind"]

    if target_kind == "plugins":
        remote_dir = REMOTE_PLUGINS_DIR
        remote_index = remote_plugins_index
    elif target_kind == "entities":
        remote_dir = REMOTE_ENTITIES_DIR
        remote_index = remote_entities_index
    else:
        print(f"Skipping unsupported jar kind: {filename}")
        return False

    matching_remote_jars = remote_index.get(artifact, [])

    if not matching_remote_jars:
        print(
            f"Skipping {filename}: no matching artifact already exists on target "
            f"in {remote_dir}. Artifact key: {artifact}"
        )
        return False

    print()
    print(f"Candidate: {filename}")
    print(f"Artifact key: {artifact}")
    print(f"Target dir: {remote_dir}")
    print(f"Remote matches: {', '.join(matching_remote_jars)}")

    local_checksum = calculate_checksum(local_path)
    remote_final_path = posixpath.join(remote_dir, filename)

    # If the exact same filename exists remotely and checksum is identical, skip.
    if filename in matching_remote_jars:
        remote_checksum = get_remote_checksum(
            ssh,
            remote_final_path,
            sudo_password=sudo_password,
        )

        if remote_checksum == local_checksum:
            print(f"Skipping {filename}: same filename and same checksum already deployed.")
            return False

        print(f"{filename} exists remotely but checksum differs. It will be replaced.")
    else:
        print(f"New version/name detected for {artifact}. It will replace old remote version(s).")

    remote_tmp_path = posixpath.join(REMOTE_TMP_DIR, filename)

    try:
        sftp_transfer(sftp, local_path, remote_tmp_path)
    except PermissionError as e:
        print(f"Permission error while copying {filename} to {remote_tmp_path}: {e}")
        return False

    moved = move_tmp_to_final(
        ssh,
        remote_tmp_path,
        remote_dir,
        sudo_password=sudo_password,
    )

    if not moved:
        print(f"Keeping old remote jars because new jar was not moved successfully: {filename}")
        return False

    deleted = delete_old_versions(
        ssh,
        remote_dir,
        artifact,
        filename,
        sudo_password=sudo_password,
    )

    if deleted:
        print(f"Deployed {filename} and removed old versions for {artifact}.")
    else:
        print(f"Deployed {filename}, but failed to remove old versions for {artifact}.")

    return True


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--username", required=True, help="Username for the remote machine")
    parser.add_argument("--private-key", help="Path to the private key file")
    parser.add_argument("--target-ip", required=True, help="IP address of the target machine")
    parser.add_argument("--port", type=int, default=22, help="SSH port to connect to")
    parser.add_argument(
        "--local-base",
        default=os.getcwd(),
        help="Local flexicore-boot-plugins folder. Defaults to current directory.",
    )

    args = parser.parse_args()

    password_or_passphrase = getpass.getpass(
        "Enter password/passphrase. This is also used for sudo: "
    )

    local_base_path = os.path.abspath(args.local_base)

    print(f"Local base path: {local_base_path}")
    print(f"Remote plugins dir: {REMOTE_PLUGINS_DIR}")
    print(f"Remote entities dir: {REMOTE_ENTITIES_DIR}")
    print(f"Connecting to SSH: {args.target_ip}:{args.port} as {args.username}")

    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    if args.private_key:
        ssh.connect(
            args.target_ip,
            port=args.port,
            username=args.username,
            key_filename=args.private_key,
            passphrase=password_or_passphrase,
        )
    else:
        ssh.connect(
            args.target_ip,
            port=args.port,
            username=args.username,
            password=password_or_passphrase,
        )

    print("SSH connection established.")

    sftp = ssh.open_sftp()
    print("SFTP session started.")

    try:
        print()
        print("Reading remote plugin jars first...")
        remote_plugin_jars = list_remote_jars(
            ssh,
            REMOTE_PLUGINS_DIR,
            sudo_password=password_or_passphrase,
        )

        print("Reading remote entity/model jars first...")
        remote_entity_jars = list_remote_jars(
            ssh,
            REMOTE_ENTITIES_DIR,
            sudo_password=password_or_passphrase,
        )

        remote_plugins_index = build_remote_index(remote_plugin_jars)
        remote_entities_index = build_remote_index(remote_entity_jars)

        print()
        print(f"Remote plugin artifacts found: {len(remote_plugins_index)}")
        print(f"Remote entity artifacts found: {len(remote_entities_index)}")

        print()
        print("Scanning local target folders for service/model jars...")
        candidates = find_local_candidate_jars(local_base_path)

        if not candidates:
            print("No local service/model jars found under target folders.")
            return 0

        print(f"Local service/model candidates found: {len(candidates)}")

        deployed_count = 0
        skipped_count = 0

        for candidate in candidates:
            deployed = deploy_candidate(
                ssh,
                sftp,
                candidate,
                remote_plugins_index,
                remote_entities_index,
                sudo_password=password_or_passphrase,
            )

            if deployed:
                deployed_count += 1

                # Update index locally so repeated modules do not act on stale remote state.
                if candidate["target_kind"] == "plugins":
                    remote_plugins_index[candidate["key"]] = [candidate["filename"]]
                else:
                    remote_entities_index[candidate["key"]] = [candidate["filename"]]
            else:
                skipped_count += 1

        print()
        print("Deployment completed.")
        print(f"Deployed jars: {deployed_count}")
        print(f"Skipped jars: {skipped_count}")

        return 0

    finally:
        print("Closing SFTP session.")
        sftp.close()

        print("Closing SSH connection.")
        ssh.close()


if __name__ == "__main__":
    print("Starting deployment script...")
    sys.exit(main())
