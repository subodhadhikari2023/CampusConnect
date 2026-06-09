#!/usr/bin/env python3
"""
PostToolUse hook: auto-inserts template Javadoc for undocumented
public/protected members when a Java file is edited via Edit or Write.

Behaviour:
  - Class / interface / enum  →  /** TODO: describe this type. */
  - Regular method            →  /** TODO: ... @param ... @return ... @throws ... */
  - @Override method          →  /** {@inheritDoc} */
  - Already-documented        →  left untouched
"""

import json
import re
import sys
from pathlib import Path

# Matches a public/protected class, interface, or enum declaration
_CLASS_RE = re.compile(
    r'^(\s*)'
    r'(?:public|protected)\s+'
    r'(?:(?:static|abstract|final|strictfp)\s+)*'
    r'(class|interface|enum)\s+(\w+)'
)

# Matches a public/protected method declaration.
# Groups: 1=indent  2=return-type  3=name  4=params  5=throws (optional)
_METHOD_RE = re.compile(
    r'^(\s*)'
    r'(?:public|protected)\s+'
    r'(?:(?:static|final|abstract|synchronized|native|default|strictfp)\s+)*'
    r'([\w<>\[\]?,\s]+?)\s+'   # return type (non-greedy)
    r'(\w+)\s*'                  # method name
    r'\(([^)]*)\)'               # params — simplified single-line
    r'(?:\s+throws\s+([\w\s,]+))?'
)

_OVERRIDE_RE = re.compile(r'^\s*@Override\b')


def _parse_param_names(params_str: str) -> list:
    """Extract parameter names from a raw parameter list string."""
    if not params_str.strip():
        return []
    parts, depth, current = [], 0, ''
    for ch in params_str:
        if ch in '<([':
            depth += 1
        elif ch in '>)]':
            depth -= 1
        if ch == ',' and depth == 0:
            parts.append(current.strip())
            current = ''
        else:
            current += ch
    if current.strip():
        parts.append(current.strip())

    names = []
    for part in parts:
        part = re.sub(r'@\w+(?:\s*\([^)]*\))?\s*', '', part).strip()
        part = re.sub(r'\bfinal\b\s*', '', part).strip()
        tokens = part.split()
        if tokens:
            name = re.sub(r'\[.*', '', tokens[-1])
            if name:
                names.append(name)
    return names


def _build_javadoc(indent, is_override, return_type, param_names, throws):
    if is_override:
        return f'{indent}/** {{@inheritDoc}} */'

    lines = [f'{indent}/**', f'{indent} * TODO: describe this member.']
    has_tags = param_names or (return_type and return_type not in ('void', 'Void')) or throws
    if has_tags:
        lines.append(f'{indent} *')
    for name in param_names:
        lines.append(f'{indent} * @param {name} TODO')
    if return_type and return_type not in ('void', 'Void'):
        lines.append(f'{indent} * @return TODO')
    for exc in [t.strip() for t in throws] if throws else []:
        if exc:
            lines.append(f'{indent} * @throws {exc} TODO')
    lines.append(f'{indent} */')
    return '\n'.join(lines)


def _process(lines):
    """Return updated line list with template Javadoc inserted, or None if unchanged."""
    insertions = []
    in_block_comment = False

    for i, raw_line in enumerate(lines):
        stripped = raw_line.strip()

        # Track block comments (includes Javadoc blocks) to skip their content
        if in_block_comment:
            if '*/' in stripped:
                in_block_comment = False
            continue
        if stripped.startswith('/*'):
            if '*/' not in stripped:
                in_block_comment = True
            continue
        if stripped.startswith('//') or stripped.startswith('@'):
            continue

        is_class = _CLASS_RE.match(raw_line)
        is_method = _METHOD_RE.match(raw_line) if '(' in raw_line else None

        if not is_class and not is_method:
            continue

        # Skip if Javadoc already present in the preceding 15 lines
        window = lines[max(0, i - 15): i]
        if any('/**' in ln for ln in window):
            continue

        is_override = any(
            _OVERRIDE_RE.match(lines[j]) for j in range(max(0, i - 3), i)
        )

        if is_class:
            m = is_class
            doc = _build_javadoc(m.group(1), False, None, [], [])
        else:
            m = is_method
            return_type = (m.group(2) or 'void').strip()
            param_names = _parse_param_names(m.group(4) or '')
            throws_raw = m.group(5) or ''
            throws = [t.strip() for t in throws_raw.split(',') if t.strip()]
            doc = _build_javadoc(m.group(1), is_override, return_type, param_names, throws)

        insertions.append((i, doc))

    if not insertions:
        return None

    result = lines[:]
    for idx, doc in reversed(insertions):
        result.insert(idx, doc)
    return result


def add_javadoc(file_path: str) -> bool:
    """Insert template Javadoc into file_path. Returns True if the file was modified."""
    path = Path(file_path)
    if not path.exists() or path.suffix != '.java':
        return False

    text = path.read_text(encoding='utf-8')
    updated = _process(text.splitlines())
    if updated is None:
        return False

    path.write_text('\n'.join(updated) + ('\n' if text.endswith('\n') else ''),
                    encoding='utf-8')
    return True


def main():
    if len(sys.argv) > 1:
        file_path = sys.argv[1]
    else:
        try:
            data = json.load(sys.stdin)
        except (json.JSONDecodeError, AttributeError):
            sys.exit(0)
        tool_name = data.get('tool_name', '')
        if tool_name not in ('Edit', 'Write'):
            sys.exit(0)
        file_path = (
            data.get('tool_input', {}).get('file_path', '')
            or data.get('tool_input', {}).get('path', '')
        )

    if not file_path:
        sys.exit(0)

    if add_javadoc(file_path):
        print(f'[javadoc-auto] Template Javadoc inserted in {Path(file_path).name} — fill in the TODO descriptions.')


if __name__ == '__main__':
    main()
