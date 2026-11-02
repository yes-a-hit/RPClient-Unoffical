import glob, io, re

total = 0
failed = 0
for path in glob.glob(r'E:\dsh-sp\rpclient\app\build\test-results\testDebugUnitTest\*.xml'):
    text = io.open(path, encoding='utf-8').read()
    m = re.search(r'tests="(\d+)".*?failures="(\d+)"', text)
    if m:
        total += int(m.group(1))
        failed += int(m.group(2))
print('tests=%d failures=%d' % (total, failed))
