#!/bin/bash
# SHRINK VM RUNNING ON VMWARE WORKSTATION

main() {
  areYouSure
  zeroOutUnusedSpace
  shrinkVm
}

areYouSure() {
  echo "! Make a copy of Virtual Machine before shrinking just in case."
  echo "! Also read carefully:"
  echo "! https://wiki.vi-toolkit.com/index.php?title=Shrink_guest_on_hosted_platform"
  echo -e "\n! The technique to zero out the unused space on the guest OS"
  echo "! will in fact make your guest virtual disk grow to the maximum size first."
  echo "! For each byte that is changed to zero the virtual disk will need to claim a byte."
  echo "! This means that while you can use this technique"
  echo "! to reclaim disk space after the unused space is zero'd out,"
  echo -e "! it is important to have enough space before you start!\n"
  read -p "Do you want to shrink VM now? [y/n]: " -n 1 -r
  echo
  if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    [[ "$0" == "${BASH_SOURCE}" ]] && exit 1 || return 1
  fi
}

zeroOutUnusedSpace() {
  echo -e "\n> Fill the unused space with zeros, so VMware knows it's indeed unused..."
  echo "! NOTE: \"No space left on device\" write error is expected"
  cat /dev/zero > zero.fill;sync;sleep 1;sync;rm -f zero.fill
}

shrinkVm() {
  echo -e "\n> Shrink VM..."
  sudo vmware-toolbox-cmd disk shrink /
}

main
