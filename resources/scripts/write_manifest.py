#!/usr/bin/env python3
import argparse
import json
import os
import platform
from datetime import datetime

def main():
    p = argparse.ArgumentParser()
    p.add_argument('--out', required=True)
    p.add_argument('--project', default='unknown')
    p.add_argument('--sha', default='unknown')
    p.add_argument('--build', default='0')
    args = p.parse_args()

    manifest = {
        'project': args.project,
        'git_sha': args.sha,
        'build_number': args.build,
        'timestamp': datetime.utcnow().isoformat() + 'Z',
        'runner': {
            'host': platform.node(),
            'os': platform.platform(),
            'python': platform.python_version(),
        },
        'artifacts': sorted([
            os.path.join('build/xilinx', f)
            for f in os.listdir('build/xilinx')
            if os.path.isfile(os.path.join('build/xilinx', f))
        ]) if os.path.isdir('build/xilinx') else [],
    }

    os.makedirs(os.path.dirname(args.out), exist_ok=True)
    with open(args.out, 'w') as fh:
        json.dump(manifest, fh, indent=2)

if __name__ == '__main__':
    main()

