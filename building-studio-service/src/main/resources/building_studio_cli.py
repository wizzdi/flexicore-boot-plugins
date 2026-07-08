#!/usr/bin/env python3
"""
Small backend runner contract used by BuildingStudioService.
Install the full Building Studio Python package beside this script, or replace this
wrapper with the existing app.py conversion code. The Java service expects this
script to write a JSON file with dxfPath/svgPath/glbPath/pngPath/log/status.
"""
import argparse
import json
import os
import shutil
import subprocess
from pathlib import Path


def run_oda(input_path: Path, work_dir: Path, oda: str | None, output_version: str) -> Path:
    if input_path.suffix.lower() == ".dxf":
        dxf = work_dir / input_path.name
        if input_path.resolve() != dxf.resolve():
            shutil.copy2(input_path, dxf)
        return dxf
    if not oda:
        raise RuntimeError("DWG input requires --oda / building.studio.odaExecutable")
    input_dir = work_dir / "converter_input"
    output_dir = work_dir / "converter_output"
    input_dir.mkdir(parents=True, exist_ok=True)
    output_dir.mkdir(parents=True, exist_ok=True)
    staged = input_dir / input_path.name
    shutil.copy2(input_path, staged)
    cmd = [oda, str(input_dir), str(output_dir), output_version, "DXF", "0", "1", "*.dwg"]
    proc = subprocess.run(cmd, text=True, capture_output=True, timeout=300)
    if proc.returncode != 0:
        raise RuntimeError(f"ODA failed: {proc.returncode}\n{proc.stdout}\n{proc.stderr}")
    candidates = sorted(output_dir.rglob("*.dxf"))
    if not candidates:
        raise RuntimeError(f"ODA completed but no DXF was produced.\n{proc.stdout}\n{proc.stderr}")
    return candidates[0]


def write_placeholder_svg(dxf: Path, out: Path) -> None:
    out.write_text(f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 800">
  <rect width="1200" height="800" fill="#111827"/>
  <g fill="none" stroke="#60a5fa" stroke-width="8">
    <rect x="120" y="120" width="960" height="560" rx="18"/>
    <path d="M120 320H1080M120 520H1080M420 120V680M780 120V680"/>
  </g>
  <text x="600" y="760" text-anchor="middle" fill="#e5e7eb" font-family="Arial" font-size="36">{dxf.name}</text>
</svg>''', encoding="utf-8")


def write_placeholder_glb(out: Path) -> None:
    # Minimal placeholder; replace with the full Building Studio GLB exporter.
    out.write_bytes(b"glTF\x02\x00\x00\x00\x14\x00\x00\x00\x00\x00\x00\x00")


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--input", required=True)
    ap.add_argument("--work-dir", required=True)
    ap.add_argument("--job-id", required=True)
    ap.add_argument("--result-json", required=True)
    ap.add_argument("--oda")
    ap.add_argument("--output-version", default="ACAD2018")
    ap.add_argument("--export-svg", action="store_true")
    ap.add_argument("--export-glb", action="store_true")
    ap.add_argument("--export-png", action="store_true")
    args = ap.parse_args()

    work_dir = Path(args.work_dir)
    work_dir.mkdir(parents=True, exist_ok=True)
    input_path = Path(args.input)
    log: list[str] = []
    try:
        dxf = run_oda(input_path, work_dir, args.oda, args.output_version)
        log.append(f"DXF ready: {dxf}")
        result = {"status": "READY", "dxfPath": str(dxf), "log": "\n".join(log)}
        if args.export_svg:
            svg = work_dir / "building.svg"
            write_placeholder_svg(dxf, svg)
            result.update({"svgPath": str(svg), "svgWidth": 1200, "svgHeight": 800})
            log.append(f"SVG ready: {svg}")
        if args.export_glb:
            glb = work_dir / "building.glb"
            write_placeholder_glb(glb)
            result.update({"glbPath": str(glb), "cameraPointsJson": "[]"})
            log.append(f"GLB ready: {glb}")
        result["log"] = "\n".join(log)
        Path(args.result_json).write_text(json.dumps(result, indent=2), encoding="utf-8")
        print(result["log"])
        return 0
    except Exception as e:
        result = {"status": "FAILED", "log": str(e)}
        Path(args.result_json).write_text(json.dumps(result, indent=2), encoding="utf-8")
        print(str(e))
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
