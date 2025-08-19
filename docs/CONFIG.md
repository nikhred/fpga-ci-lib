# Config Schema

This library expects a `ci/config.yaml` in the caller repository. Example keys and types:

project: string
vendor: enum[xilinx,intel] (xilinx default)
sim:
  test_pattern: string (default: "*")
build:
  top: string (required for vendor builds)
  part: string (required for vendor builds)
  constraints: list[string] (optional)
  timing:
    wns_min_ns: number (default: 0.00)
deploy:
  tftp:
    enabled: bool (default: false)
    host: string
    root: string
    user: string (default: tftp)
    subdir: string (default: ${GIT_SHA})
    update_latest: bool (default: true)
notify:
  google_chat:
    enabled: bool (default: false)
    credential_id: string

Values not provided fall back to sensible defaults where possible.

