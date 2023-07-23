#!/usr/bin/env sh
openssl genrsa -out secret.pem -des3 2048
openssl req -x509 -sha256 -new -nodes -days 3650 -key secret.pem -out cert.pem
chmod 0400 secret.pem