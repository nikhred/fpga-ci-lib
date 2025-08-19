#!/usr/bin/env python3
import json
import re
import sys

def parse_timing(path):
    data = {"wns": None, "tns": None, "fmax_mhz": None}
    try:
        txt = open(path, 'r', errors='ignore').read()
    except Exception:
        return data
    wns = re.search(r'WNS\s*\(ns\)\s*[:=]?\s*([-+]?\d+\.\d+|[-+]?\d+)', txt, re.I)
    tns = re.search(r'TNS\s*\(ns\)\s*[:=]?\s*([-+]?\d+\.\d+|[-+]?\d+)', txt, re.I)
    fmax = re.search(r'Fmax\s*[:=]?\s*([\d\.]+)\s*MHz', txt, re.I)
    if wns: data["wns"] = float(wns.group(1))
    if tns: data["tns"] = float(tns.group(1))
    if fmax: data["fmax_mhz"] = float(fmax.group(1))
    return data

def main():
    path = sys.argv[1] if len(sys.argv) > 1 else 'timing_summary.rpt'
    print(json.dumps(parse_timing(path)))

if __name__ == '__main__':
    main()

