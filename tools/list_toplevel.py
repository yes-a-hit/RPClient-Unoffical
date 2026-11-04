import io
import os
import re
import sys

BASE = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
    'app', 'src', 'main', 'java', 'me', 'kafuuneko', 'rpclient',
)

START = re.compile(
    r'^(?:@|private |internal |public |fun |suspend fun |class |data class |'
    r'sealed class |sealed interface |object |enum class |value class |'
    r'typealias |const val |val |var )'
)

rel = sys.argv[1]
lines = io.open(os.path.join(BASE, rel), encoding='utf-8').read().splitlines()
pending = None
for i, line in enumerate(lines):
    if not line or line[0] in ' \t\r\n':
        continue
    if line.startswith(('package ', 'import ', '//', '/*', ' */', ' *')):
        continue
    if not START.match(line):
        continue
    if line.startswith('@'):
        if pending is None:
            pending = i
        continue
    start = pending if pending is not None else i
    body = '\n'.join(lines[start:start + 8])
    m = re.search(
        r'\b(?:fun|class|object|interface|val|var|typealias)\s+(?:<[^>]*>\s*)?'
        r'(?:([A-Za-z0-9_]+)\.)?([A-Za-z0-9_]+)',
        body,
    )
    if not m:
        name = '?'
    else:
        name = (m.group(1) + '.' + m.group(2)) if m.group(1) else m.group(2)
    print('%5d  %s' % (start + 1, name))
    pending = None
