"""按 top-level 声明块把一个超大 Kotlin 文件切成若干个。

用法（改 GROUPING 后执行）：
    python tools/split_kt.py

规则：
- 块起点 = 第 0 列以注解/修饰符/声明关键字开头、且不是 package/import 的行
- 块终点 = 下一个块起点之前
- 每个新文件复制原文件的 import 区（多余 import 只是警告，不影响编译）
- 新文件里的 private 顶层声明改成 internal：private 是文件级可见，
  跨文件后原文件就看不见了
"""

import io
import os
import re
import sys

REPO = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
    'app', 'src', 'main', 'java', 'me', 'kafuuneko', 'rpclient',
)

START = re.compile(
    r'^(?:@|private |internal |public |fun |suspend fun |class |data class |'
    r'sealed class |sealed interface |object |enum class |value class |'
    r'typealias |const val |val |var |expect |actual )'
)

NAME_RE = re.compile(
    r'\b(?:fun|class|object|interface|val|var|typealias)\s+(?:<[^>]*>\s*)?'
    r'(?:[A-Za-z0-9_.]+\.)?([A-Za-z0-9_]+)'
)


def decl_name(block_text):
    m = NAME_RE.search(block_text)
    if not m:
        return ''
    return m.group(1)


def split_blocks(lines):
    starts = []
    pending = None
    for i, line in enumerate(lines):
        if not line or line[0] in ' \t\r\n':
            continue
        if line.startswith('package ') or line.startswith('import '):
            continue
        if line.startswith('//') or line.startswith('/*') or line.startswith(' */') or line.startswith(' *'):
            continue
        if not START.match(line):
            continue
        if line.startswith('@'):
            # 注解属于它下面的那个声明，不能单独成块
            if pending is None:
                pending = i
            continue
        starts.append(i if pending is None else pending)
        pending = None
    blocks = []
    for idx, s in enumerate(starts):
        e = starts[idx + 1] if idx + 1 < len(starts) else len(lines)
        name = decl_name(''.join(lines[s:e]))
        blocks.append((s, e, name))
    return blocks


def relax_visibility(text):
    """private 顶层声明改成 internal。"""
    out = []
    for line in text.splitlines(True):
        if line.startswith('private '):
            line = 'internal ' + line[len('private '):]
        out.append(line)
    return ''.join(out)


def process(rel_path, groups, base_name):
    """groups: {输出文件名: set(声明名)}；未列出的留在原文件。"""
    path = os.path.join(REPO, rel_path)
    lines = io.open(path, encoding='utf-8').read().splitlines(True)
    blocks = split_blocks(lines)

    names = [b[2] for b in blocks]
    known = set()
    for nameset in groups.values():
        known |= nameset
    missing = known - set(names)
    if missing:
        print('MISSING in', rel_path, sorted(missing))
        return False

    pkg = next(l for l in lines if l.startswith('package '))
    imports = [l for l in lines if l.startswith('import ')]
    head_end = next(i for i, l in enumerate(lines) if l.startswith('package '))
    file_header = ''.join(lines[:head_end])

    bucket = {k: [] for k in groups}
    stay = []
    for (s, e, name) in blocks:
        placed = False
        for out_name, nameset in groups.items():
            if name in nameset:
                bucket[out_name].append((s, e))
                placed = True
                break
        if not placed:
            stay.append((s, e))

    outdir = os.path.dirname(path)
    for out_name, ranges in bucket.items():
        if not ranges:
            continue
        body = []
        for (s, e) in ranges:
            body.append(''.join(lines[s:e]).rstrip() + '\n\n')
        content = (
            file_header
            + pkg
            + ''.join(imports)
            + '\n'
            + relax_visibility(''.join(body)).rstrip()
            + '\n'
        )
        target = os.path.join(outdir, out_name)
        io.open(target, 'w', encoding='utf-8', newline='').write(content)
        print('WROTE', target, sum(1 for _ in content.splitlines()), 'lines')

    remaining = ''.join(''.join(lines[s:e]).rstrip() + '\n\n' for (s, e) in stay)
    content = (
        file_header
        + pkg
        + ''.join(imports)
        + '\n'
        + remaining.rstrip()
        + '\n'
    )
    io.open(path, 'w', encoding='utf-8', newline='').write(content)
    print('KEPT', rel_path, sum(1 for _ in content.splitlines()), 'lines')
    return True
