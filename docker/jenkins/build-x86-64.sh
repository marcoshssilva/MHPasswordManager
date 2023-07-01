#!/usr/bin/env bash
docker build -t mhpasswordmanager/api-gateway:"$1" -f Dockerfile-x86-64 ../../mhpasswordmanager-api-gateway

docker build -t mhpasswordmanager/config-services:"$1" -f Dockerfile-x86-64 ../../mhpasswordmanager-config-services

docker build -t mhpasswordmanager/oauth2-server:"$1" -f Dockerfile-x86-64 ../../mhpasswordmanager-oauth2-authorizationserver

docker build -t mhpasswordmanager/eureka-server:"$1" -f Dockerfile-x86-64 ../../mhpasswordmanager-service-registry

docker build -t mhpasswordmanager/user-service:"$1" -f Dockerfile-x86-64 ../../mhpasswordmanager-user-service

docker build -t mhpasswordmanager/password-service:"$1" -f Dockerfile-x86-64 ../../mhpasswordmanager-password-service

docker build -t mhpasswordmanager/email-service:"$1" -f Dockerfile-x86-64 ../../mhpasswordmanager-email-service

docker build -t mhpasswordmanager/file-service:"$1" -f Dockerfile-x86-64 ../../mhpasswordmanager-file-service