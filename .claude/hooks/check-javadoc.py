#!/usr/bin/env python3
"""
Checks Java files for missing Javadoc on public/protected methods and classes.

Modes:
  Claude Code PostToolUse hook (no args) — reads JSON from stdin:
    python3 check-javadoc.py

  Standalone / pre-commit hook (file path as arg):
    python3 check-javadoc.py path/to/File.java
"""
import sys
import json
import re


def check_file(file_path: str) -> list[str]:
    if not file_path.endswith(".java"):
        return []
    try:
        with open(file_path, encoding="utf-8") as f:
            lines = f.readlines()
    except OSError:
        return []

    missing = []
    for i, line in enumerate(lines):
        stripped = line.strip()

        # Match public/protected method declarations (not class/interface/enum)
        if not re.match(r"(public|protected)\s+(?!class\b|interface\b|enum\b|@)", stripped):
            continue
        if "(" not in stripped:
            continue
        # Skip annotations that happen to have parentheses
        if stripped.startswith("@"):
            continue

        # Look back up to 15 lines for a Javadoc opening /**
        window = lines[max(0, i - 15) : i]
        has_javadoc = any("/**" in l for l in window)

        # Ensure no other method boundary resets the window
        intervening = any(
            re.match(r"\s*(public|protected|private)\s+", l) and "(" in l
            for l in window[-6:]
        )

        if not has_javadoc and not intervening:
            missing.append(f"  Line {i + 1}: {stripped[:100]}")

    return missing


def main() -> None:
    if len(sys.argv) > 1:
        file_path = sys.argv[1]
    else:
        # PostToolUse hook: Claude Code sends JSON on stdin
        try:
            data = json.load(sys.stdin)
            file_path = (
                data.get("tool_input", {}).get("file_path", "")
                or data.get("tool_input", {}).get("path", "")
            )
        except (json.JSONDecodeError, AttributeError):
            sys.exit(0)

    if not file_path:
        sys.exit(0)

    missing = check_file(file_path)
    if missing:
        print(f"JAVADOC WARNING: Missing Javadoc in {file_path}:")
        for m in missing[:5]:
            print(m)
        if len(missing) > 5:
            print(f"  ... and {len(missing) - 5} more undocumented members")
        print("Add /** ... */ blocks to all public/protected methods before proceeding.")
        sys.exit(1)


if __name__ == "__main__":
    main()
