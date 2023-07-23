#!/usr/bin/env sh
openssl genrsa -out key.pem -des3 2048
openssl req -x509 -sha256 -new -nodes -days 3650 -key key.pem -out cert.pem
chmod 0400 secret.key