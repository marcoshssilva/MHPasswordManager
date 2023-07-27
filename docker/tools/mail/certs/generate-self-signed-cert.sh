#!/usr/bin/env sh
openssl genrsa -out secret.key -des3 2048
openssl req -x509 -sha256 -new -nodes -days 3650 -key secret.key -out cert.pem
chmod 0400 secret.key