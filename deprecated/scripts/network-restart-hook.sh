#!/bin/bash
# FIX FOR MISSING NETWORK AFTER VMWARE RESUME
# COPY IT TO: /etc/vmware-tools/scripts/resume-vm-default.d

sudo systemctl restart NetworkManager docker >/dev/null
