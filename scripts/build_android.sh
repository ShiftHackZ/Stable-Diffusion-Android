#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
IMAGE_NAME="${SDAI_ANDROID_DOCKER_IMAGE:-sdai-android-arch-jdk17}"
DOCKER_PLATFORM="${SDAI_ANDROID_DOCKER_PLATFORM:-linux/amd64}"
DOCKERFILE="${ROOT_DIR}/scripts/docker/android/Dockerfile"
DOCKER_LOCAL_PROPERTIES_DIR=""
DOCKER_LOCAL_PROPERTIES=""
GRADLE_CACHE="${SDAI_GRADLE_CACHE:-${ROOT_DIR}/.gradle-docker-cache}"
GRADLE_JVMARGS="${SDAI_GRADLE_JVMARGS:--Xmx4g -XX:MaxMetaspaceSize=1024m -Dfile.encoding=UTF-8}"
GRADLE_MAX_WORKERS="${SDAI_GRADLE_MAX_WORKERS:-1}"
SIGNING_DIR="${ROOT_DIR}/app/keystore"
SIGNING_PROPERTIES="${SIGNING_DIR}/signing.properties"
GENERATED_SIGNING_FILES=0

cleanup() {
  if [[ "${SDAI_KEEP_GENERATED_SIGNING_FILES:-0}" != "1" && "${GENERATED_SIGNING_FILES}" == "1" ]]; then
    rm -f "${SIGNING_DIR}/docker-release.jks" "${SIGNING_PROPERTIES}"
  fi

  if [[ -n "${DOCKER_LOCAL_PROPERTIES_DIR}" && -d "${DOCKER_LOCAL_PROPERTIES_DIR}" ]]; then
    rm -rf "${DOCKER_LOCAL_PROPERTIES_DIR}"
  fi
}
trap cleanup EXIT

prepare_signing() {
  if [[ -f "${SIGNING_PROPERTIES}" ]]; then
    return
  fi

  if [[ -z "${ANDROID_KEYSTORE_PASSWORD:-}" || -z "${ANDROID_KEYSTORE_ALIAS:-}" ]]; then
    echo "Missing signing config. Provide existing app/keystore/signing.properties or set ANDROID_KEYSTORE_PASSWORD and ANDROID_KEYSTORE_ALIAS." >&2
    exit 1
  fi

  mkdir -p "${SIGNING_DIR}"

  if [[ -n "${ANDROID_KEYSTORE_BASE64:-}" ]]; then
    printf "%s" "${ANDROID_KEYSTORE_BASE64}" | base64 --decode > "${SIGNING_DIR}/docker-release.jks"
  elif [[ -n "${ANDROID_KEYSTORE_FILE:-}" ]]; then
    cp "${ANDROID_KEYSTORE_FILE}" "${SIGNING_DIR}/docker-release.jks"
  else
    echo "Missing keystore. Set ANDROID_KEYSTORE_BASE64 or ANDROID_KEYSTORE_FILE." >&2
    exit 1
  fi

  cat > "${SIGNING_PROPERTIES}" <<EOF
keystore=keystore/docker-release.jks
keystore.alias=${ANDROID_KEYSTORE_ALIAS}
keystore.password=${ANDROID_KEYSTORE_PASSWORD}
EOF
  GENERATED_SIGNING_FILES=1
}

prepare_docker_local_properties() {
  DOCKER_LOCAL_PROPERTIES_DIR="$(mktemp -d)"
  DOCKER_LOCAL_PROPERTIES="${DOCKER_LOCAL_PROPERTIES_DIR}/local.properties"
  printf "sdk.dir=/opt/android-sdk\n" > "${DOCKER_LOCAL_PROPERTIES}"
}

prepare_signing
prepare_docker_local_properties
mkdir -p "${GRADLE_CACHE}"

docker build \
  --platform "${DOCKER_PLATFORM}" \
  -t "${IMAGE_NAME}" \
  -f "${DOCKERFILE}" \
  "${ROOT_DIR}/scripts/docker/android"

docker run --rm \
  --platform "${DOCKER_PLATFORM}" \
  -e SDAI_GRADLE_JVMARGS="${GRADLE_JVMARGS}" \
  -e SDAI_GRADLE_MAX_WORKERS="${GRADLE_MAX_WORKERS}" \
  -v "${ROOT_DIR}:/workspace" \
  -v "${DOCKER_LOCAL_PROPERTIES}:/workspace/local.properties:ro" \
  -v "${GRADLE_CACHE}:/root/.gradle" \
  -w /workspace \
  "${IMAGE_NAME}" \
  bash -lc '
    set -euo pipefail

    gradle_args=(
      --no-daemon
      --max-workers="${SDAI_GRADLE_MAX_WORKERS}"
      -Dorg.gradle.jvmargs="${SDAI_GRADLE_JVMARGS}"
      -Dorg.gradle.workers.max="${SDAI_GRADLE_MAX_WORKERS}"
      -Dorg.gradle.vfs.watch=false
      -Dkotlin.native.ignoreDisabledTargets=true
      -Pkotlin.native.ignoreDisabledTargets=true
    )

    ./gradlew clean "${gradle_args[@]}"

    for task_group in \
      "assembleFossRelease bundleFossRelease" \
      "assembleFullRelease bundleFullRelease" \
      "assemblePlaystoreRelease bundlePlaystoreRelease"
    do
      read -r -a tasks <<< "${task_group}"
      ./gradlew "${tasks[@]}" "${gradle_args[@]}"
    done
  '

echo "Android artifacts:"
find "${ROOT_DIR}/app/android/build/outputs" -type f \( -name "*.apk" -o -name "*.aab" \) | sort
