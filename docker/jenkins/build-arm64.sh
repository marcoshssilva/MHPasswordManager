#!/usr/bin/env bash
docker build --build-arg="SERVICE_PORT=12050" -t arm64-mhpasswordmanager/api-gateway:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-api-gateway

docker build --build-arg="SERVICE_PORT=8888" -t arm64-mhpasswordmanager/config-services:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-config-services

docker build --build-arg="SERVICE_PORT=12010" -t arm64-mhpasswordmanager/oauth2-server:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-oauth2-authorizationserver

docker build --build-arg="SERVICE_PORT=8761" -t arm64-mhpasswordmanager/eureka-server:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-service-registry

docker build --build-arg="SERVICE_PORT=12011" -t arm64-mhpasswordmanager/user-service:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-user-service

docker build --build-arg="SERVICE_PORT=12012" -t arm64-mhpasswordmanager/password-service:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-password-service

docker build --build-arg="SERVICE_PORT=12013" -t arm64-mhpasswordmanager/email-service:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-email-service

docker build --build-arg="SERVICE_PORT=12014" -t arm64-mhpasswordmanager/file-service:"$1" -f Dockerfile-arm64 ../../mhpasswordmanager-file-service