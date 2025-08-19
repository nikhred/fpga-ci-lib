fpga-ci-lib — Jenkins Shared Library for FPGA CI/CD

This repository provides a reusable Jenkins Shared Library for FPGA projects. It focuses on a Vivado-first flow with VUnit simulation, timing-gated bitstreams, TFTP deploy, and Google Chat notifications. Quartus support can be added later by extending build steps.

- Library entry points live under `vars/`
- Helper TCL/Python scripts live under `resources/`
- Usage and configuration are documented under `docs/`

Quick start and configuration details are in `docs/JENKINS.md` and `docs/CONFIG.md`.

