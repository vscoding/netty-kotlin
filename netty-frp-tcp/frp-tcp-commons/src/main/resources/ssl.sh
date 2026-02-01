#!/bin/bash
# shellcheck disable=SC1090 disable=SC2086 disable=SC2155 disable=SC2128 disable=SC2028 disable=SC2164

set -Eeuo pipefail

SHELL_FOLDER="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SHELL_FOLDER"

source <(curl -SL https://dev.kubectl.net/func/log.sh)

C=CN
ST=Shanghai
L=Shanghai
O=devops
OU=devops
# CN...
emailAddress="tech@intellij.io"

# different from the above
CA_CN="frp-ca"
SERVER_CN="frp-server"
SERVER_DNS1=$SERVER_CN
SERVER_DNS2="localhost"
SERVER_IP1="127.0.0.1"

CLIENT_CN="frp-client"

SSL_DIR="ssl"

system_type=""
SUBJECT_PREFIX=""

init() {
  log_info "INIT" "init ssl folder"
  if [ -f "$SSL_DIR" ]; then
    rm -rf "$SSL_DIR"
  fi
  if [ -d "$SSL_DIR" ]; then
    rm -rf "$SSL_DIR"
    mkdir -p "$SSL_DIR"/{client,server}
  else
    log_info "validate" "ssl folder is not exist, create it"
    mkdir -p "$SSL_DIR"/{client,server}
  fi
}

detect_system_type() {
  # https://stackoverflow.com/questions/31506158/running-openssl-from-a-bash-script-on-windows-subject-does-not-start-with
  local OS
  OS="$(uname -s)"
  case $OS in
    Linux)
      system_type="Linux"
      SUBJECT_PREFIX="/"
      ;;
    Darwin)
      system_type="Mac"
      SUBJECT_PREFIX="/"
      ;;
    CYGWIN* | MINGW32* | MSYS* | MINGW*)
      system_type="Windows"
      SUBJECT_PREFIX="//"
      ;;
    *)
      system_type="Unknown"
      ;;
  esac

  if [ "$system_type" = "Unknown" ]; then
    log_error "system_type" "Unknown system type"
    exit 1
  fi
}

create_key() {
  local target_file=$1
  openssl genrsa -out "$target_file" 2048
}

create_ca() {
  log_info "[CA]" "create ca"

  local CA_KEY_FILE="$SSL_DIR/ca.key"

  log_info "[CA]" "1. Create CA Key: $CA_KEY_FILE"
  create_key "$CA_KEY_FILE"

  log_info "[CA]" "2. Create CA CRT"
  local CA_SUBJ="${SUBJECT_PREFIX}C=${C}\ST=${ST}\L=${L}\O=${O}\OU=${OU}\emailAddress=${emailAddress}\CN=${CA_CN}"
  openssl req -x509 -sha256 -new -nodes -key "$CA_KEY_FILE" -days 3650 -out \
    "$SSL_DIR/ca.crt" \
    -subj "${CA_SUBJ}"

  openssl x509 -in "$SSL_DIR/ca.crt" -text
}

create_server() {
  log_info "[Server]" "create server"

  local SERVER_DIR="$SSL_DIR/server"
  local SERVER_KEY_FILE="$SERVER_DIR/server.key"

  log_info "[Server]" "1. Create Server Key: $SERVER_KEY_FILE"
  create_key "$SERVER_KEY_FILE"

  create_server_csr() {
    log_info "[Server]" "2. create server certificate signing request"
    local SERVER_SUBJ="${SUBJECT_PREFIX}C=${C}\ST=${ST}\L=${L}\O=${O}\OU=${OU}\emailAddress=${emailAddress}\CN=${SERVER_CN}"
    openssl req -new -key "$SERVER_KEY_FILE" -sha256 \
      -subj "$SERVER_SUBJ" \
      -out "$SERVER_DIR/server.csr"
  }

  create_server_crt() {
    log_info "[Server]" "3.1 create v3.ext.ini"
    cat >"$SERVER_DIR/v3.ext.ini" <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = $SERVER_DNS1
DNS.2 = $SERVER_DNS2
IP.1 = $SERVER_IP1
EOF

    log_info "[Server]" "3.2 create server certificate"
    openssl x509 -req -sha256 \
      -in "$SERVER_DIR/server.csr" \
      -CA "$SSL_DIR/ca.crt" -CAkey "$SSL_DIR/ca.key" \
      -CAcreateserial \
      -out "$SERVER_DIR/server.crt" \
      -days 3650 \
      -extfile "$SERVER_DIR/v3.ext.ini"
  }

  create_server_csr
  create_server_crt
  # openssl pkcs8 -topk8 -in $server_folder/server.key -out $server_folder/pkcs8_server.key -nocrypt
  openssl x509 -in "$SERVER_DIR/server.crt" -text
}

create_client() {
  log_info "[Client]" "create client"
  local CLIENT_DIR="$SSL_DIR/client"
  local CLIENT_KEY_FILE="$CLIENT_DIR/client.key"

  log_info "[Client]" "1. Create Client Key: $CLIENT_KEY_FILE"
  create_key "$CLIENT_DIR/client.key"

  create_client_csr() {
    log_info "[Client]" "2. create client certificate signing request"
    local CLIENT_SUBJ="${SUBJECT_PREFIX}C=${C}\ST=${ST}\L=${L}\O=${O}\OU=${OU}\emailAddress=${emailAddress}\CN=${CLIENT_CN}"
    openssl req -new -key "$CLIENT_KEY_FILE" -sha256 \
      -subj "$CLIENT_SUBJ" \
      -out "$CLIENT_DIR/client.csr"
  }

  create_client_crt() {
    log_info "[Client]" "3.1 create v3.ext.ini"
    cat >"$CLIENT_DIR/v3.ext.ini" <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = $CLIENT_CN
EOF

    log_info "[Client]" "3.2 create client certificate"
    openssl x509 -req -sha256 \
      -in "$CLIENT_DIR/client.csr" \
      -CA "$SSL_DIR/ca.crt" -CAkey "$SSL_DIR/ca.key" \
      -CAcreateserial \
      -out "$CLIENT_DIR/client.crt" \
      -days 3650 \
      -extfile "$CLIENT_DIR/v3.ext.ini"
  }

  create_client_csr
  create_client_crt
  openssl x509 -in "$CLIENT_DIR/client.crt" -text
}

openssl_validate() {
  openssl verify -CAfile "$SSL_DIR/ca.crt" "$SSL_DIR/server/server.crt"
  openssl verify -CAfile "$SSL_DIR/ca.crt" "$SSL_DIR/client/client.crt"
}

init
detect_system_type
create_ca
create_server
create_client
openssl_validate
