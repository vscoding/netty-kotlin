#!/bin/bash
# shellcheck disable=SC2164 disable=SC1090 disable=SC2034
set -e
SHELL_FOLDER=$(cd "$(dirname "$0")" && pwd)
cd "$SHELL_FOLDER"
source <(curl -sSL "https://dev.kubectl.net/github/tools/trigger.lib.sh")

branch="main"

key_word_list=(
  ":ci:img:build:tcp_server_test"
  ":ci:img:build:tcp_proxy"
  ":ci:img:build:socks_server"
  ":ci:ops:cleanup:runs"
)

trigger_git_commit "key_word_list" "$branch" "$@"
