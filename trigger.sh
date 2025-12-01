#!/bin/bash
set -e # 出错立即退出
# shellcheck disable=SC2164

SHELL_FOLDER=$(cd "$(dirname "$0")" && pwd)
cd "$SHELL_FOLDER"

branch="main"

# 可用关键字列表
key_word_list=(
  "build_tcp_server_test"
  "build_tcp_proxy"
  "build_socks_server"
)

# 如果没有提供参数，直接退出
if [[ $# -eq 0 ]]; then
  echo "⚠️  No keyword provided. Nothing to trigger."
  echo "👉  Valid keywords are: ${key_word_list[*]}"
  exit 0
fi

# 参数即关键字
key_words=("$@")

# 验证关键字
invalid_keys=()
for key_word in "${key_words[@]}"; do
  match=false
  for valid in "${key_word_list[@]}"; do
    if [[ "$key_word" == "$valid" ]]; then
      match=true
      break
    fi
  done

  if [[ $match == false ]]; then
    invalid_keys+=("$key_word")
  fi
done

# 如果有非法关键字则退出
if [[ ${#invalid_keys[@]} -gt 0 ]]; then
  echo "❌ Invalid keyword(s): ${invalid_keys[*]}"
  echo "   Valid keywords are: ${key_word_list[*]}"
  exit 1
fi

# 拼接提交信息
joined_keywords=$(
  IFS=' '
  echo "${key_words[*]}"
)
msg="GitHub Actions Trigger: ${joined_keywords} ($(date +'%Y-%m-%d %H:%M:%S'))"

echo "✅ Using keywords: $joined_keywords"
echo "🔄 Switching to branch: $branch"

git checkout "$branch"

# 创建空提交并推送
git commit --allow-empty -m "$msg"
git push origin "$branch"

echo "🚀 Trigger pushed successfully!"
