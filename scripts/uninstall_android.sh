#!/usr/bin/env bash
set -euo pipefail

PACKAGES=(
  "com.shifthackz.aisdv1.app"
  "com.shifthackz.aisdv1.app.foss"
  "com.shifthackz.aisdv1.app.full"
)

for PACKAGE in "${PACKAGES[@]}"; do
  echo "Uninstalling ${PACKAGE} from connected Android devices..."
  adb devices | tail -n +2 | cut -sf 1 | xargs -I {} adb -s {} uninstall "${PACKAGE}" || true
done
