# Jenkins Setup

Global Pipeline Library
- Name: `fpga-ci-lib`
- Default version: `v0.1.0`
- Retrieval: Git (HTTPS/SSH)
- Load implicitly: unchecked

Credentials
- `gchat-webhook` (Secret text): Google Chat incoming webhook URL.
- `tftp-ssh` (SSH Username with private key): for `tftp@<host>` if needed.

Nodes / Labels
- Docker/sim node label: `docker`
- Vivado node label: `vivado`
- Board/TFTP node label: `boardfarm`

Usage in a caller repo Jenkinsfile
```
@Library('fpga-ci-lib@v0.1.0') _
def cfg = readYaml(file: 'ci/config.yaml')
fpgaPipeline(cfg)
```

