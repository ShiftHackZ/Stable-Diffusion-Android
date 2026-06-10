#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT="${ROOT_DIR}/app/ios/iosApp.xcodeproj"
SCHEME="${SDAI_IOS_SCHEME:-Release}"
CONFIGURATION="${SDAI_IOS_CONFIGURATION:-Release}"
ARCHIVE_PATH="${SDAI_IOS_ARCHIVE_PATH:-${ROOT_DIR}/build/ios/iosApp.xcarchive}"
EXPORT_PATH="${SDAI_IOS_EXPORT_PATH:-${ROOT_DIR}/build/ios/export}"
ALLOW_PROVISIONING_UPDATES="${SDAI_IOS_ALLOW_PROVISIONING_UPDATES:-1}"
EXPORT_METHOD="${SDAI_IOS_EXPORT_METHOD:-app-store-connect}"
EXPORT_DESTINATION="${SDAI_IOS_EXPORT_DESTINATION:-export}"
EXPORT_OPTIONS_PLIST="${SDAI_IOS_EXPORT_OPTIONS_PLIST:-}"
GENERATED_EXPORT_OPTIONS=""
IOS_TEAM_ID="${SDAI_IOS_TEAM_ID:-383EQFRA4A}"
IOS_BUNDLE_ID="${SDAI_IOS_BUNDLE_ID:-com.shifthackz.aisdv1.app}"
IOS_EXPORT_SIGNING_STYLE="${SDAI_IOS_EXPORT_SIGNING_STYLE:-automatic}"
export SDAI_GRADLE_JVMARGS="${SDAI_GRADLE_JVMARGS:--Xmx8g -XX:MaxMetaspaceSize=1536m -Dfile.encoding=UTF-8}"
export SDAI_GRADLE_MAX_WORKERS="${SDAI_GRADLE_MAX_WORKERS:-2}"

cleanup() {
  if [[ -n "${GENERATED_EXPORT_OPTIONS}" && -f "${GENERATED_EXPORT_OPTIONS}" ]]; then
    rm -f "${GENERATED_EXPORT_OPTIONS}"
  fi
}
trap cleanup EXIT

create_export_options_plist() {
  GENERATED_EXPORT_OPTIONS="$(mktemp "${TMPDIR:-/tmp}/sdai-ios-export-options.XXXXXX.plist")"
  cat > "${GENERATED_EXPORT_OPTIONS}" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>destination</key>
  <string>${EXPORT_DESTINATION}</string>
  <key>manageAppVersionAndBuildNumber</key>
  <false/>
  <key>method</key>
  <string>${EXPORT_METHOD}</string>
  <key>signingStyle</key>
  <string>${IOS_EXPORT_SIGNING_STYLE}</string>
  <key>stripSwiftSymbols</key>
  <true/>
  <key>uploadSymbols</key>
  <true/>
EOF

  if [[ -n "${IOS_TEAM_ID}" ]]; then
    cat >> "${GENERATED_EXPORT_OPTIONS}" <<EOF
  <key>teamID</key>
  <string>${IOS_TEAM_ID}</string>
EOF
  fi

  if [[ "${SDAI_IOS_TESTFLIGHT_INTERNAL_ONLY:-0}" == "1" ]]; then
    cat >> "${GENERATED_EXPORT_OPTIONS}" <<EOF
  <key>testFlightInternalTestingOnly</key>
  <true/>
EOF
  fi

  cat >> "${GENERATED_EXPORT_OPTIONS}" <<EOF
</dict>
</plist>
EOF
  EXPORT_OPTIONS_PLIST="${GENERATED_EXPORT_OPTIONS}"
}

XCODE_ARGS=(
  -project "${PROJECT}"
  -scheme "${SCHEME}"
  -configuration "${CONFIGURATION}"
  -destination "generic/platform=iOS"
  DEVELOPMENT_TEAM="${IOS_TEAM_ID}"
  PRODUCT_BUNDLE_IDENTIFIER="${IOS_BUNDLE_ID}"
  CODE_SIGN_STYLE=Automatic
)

if [[ "${ALLOW_PROVISIONING_UPDATES}" == "1" ]]; then
  XCODE_ARGS+=(-allowProvisioningUpdates)
fi

xcodebuild "${XCODE_ARGS[@]}" clean archive -archivePath "${ARCHIVE_PATH}"

if [[ -z "${EXPORT_OPTIONS_PLIST}" ]]; then
  create_export_options_plist
fi

EXPORT_ARGS=(
  -exportArchive
  -archivePath "${ARCHIVE_PATH}"
  -exportOptionsPlist "${EXPORT_OPTIONS_PLIST}"
  -exportPath "${EXPORT_PATH}"
)

if [[ "${ALLOW_PROVISIONING_UPDATES}" == "1" ]]; then
  EXPORT_ARGS+=(-allowProvisioningUpdates)
fi

xcodebuild "${EXPORT_ARGS[@]}"

echo "iOS archive created at ${ARCHIVE_PATH}"
echo "iOS export artifacts:"
find "${EXPORT_PATH}" -maxdepth 2 -type f | sort
