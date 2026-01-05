#!/bin/bash
set -e
SHELL_FOLDER=$(cd "$(dirname "$0")" && pwd)
cd "$SHELL_FOLDER"
source <(curl -sSL "https://dev.kubectl.net/github/tools/trigger.lib.sh")

branch="main"

# 可用关键字列表
key_word_list=(
  "build_tcp_server_test"
  "build_tcp_proxy"
  "build_socks_server"
  "delete_runs"
)

trigger_git_commit "key_word_list" "$branch" "$@"
