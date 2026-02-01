#!/bin/bash
# shellcheck disable=SC2164,SC1090,SC2086
SHELL_FOLDER=$(cd "$(dirname "$0")" && pwd) && cd "$SHELL_FOLDER"
export ROOT_URI="https://dev.kubectl.org"
set -Eeuo pipefail
bash <(curl -sSL $ROOT_URI/openssl/local_test.sh)
