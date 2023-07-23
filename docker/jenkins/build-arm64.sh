#!/usr/bin/env bash
docker build -t arm64-mhpasswordmanager/api-gateway:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-api-gateway

docker build -t arm64-mhpasswordmanager/config-services:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-config-services

docker build -t arm64-mhpasswordmanager/oauth2-server:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-oauth2-authorizationserver

docker build -t arm64-mhpasswordmanager/eureka-server:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-service-registry

docker build -t arm64-mhpasswordmanager/user-service:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-user-service

docker build -t arm64-mhpasswordmanager/password-service:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-password-service

docker build -t arm64-mhpasswordmanager/email-service:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-email-service

docker build -t arm64-mhpasswordmanager/file-service:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-file-service