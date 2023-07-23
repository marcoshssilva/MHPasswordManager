#!/usr/bin/env sh
docker run -d \
    --name spider \
    -p 25:25 \
    -p 8080:8080 \
    mhpasswordmanager/mail-server:latest