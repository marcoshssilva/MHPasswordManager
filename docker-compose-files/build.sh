#!/bin/sh
echo 'mhpasswordmanager-redis...'
docker build -t mhpasswordmanager/redis:latest ../redis
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/redis:latest mhpasswordmanager/redis:"$1"
fi

echo 'mhpasswordmanager-postgres-db...'
docker build -t mhpasswordmanager/postgres:latest ../postgres
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/postgres:latest mhpasswordmanager/postgres:"$1"
fi

echo 'mhpasswordmanager-service-registry...'
docker build -t mhpasswordmanager/service-registry:latest ../mhpasswordmanager-service-registry
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/service-registry:latest mhpasswordmanager/service-registry:"$1"
fi

echo 'mhpasswordmanager-oauth2-authorizationserver...'
docker build -t mhpasswordmanager/oauth2-authorization-server:latest ../mhpasswordmanager-oauth2-authorizationserver
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/oauth2-authorizationserver:latest mhpasswordmanager/oauth2-authorizationserver:"$1"
fi

echo 'mhpasswordmanager-api-gateway...'
docker build -t mhpasswordmanager/api-gateway:latest ../mhpasswordmanager-api-gateway
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/api-gateway:latest mhpasswordmanager/api-gateway:"$1"
fi

echo 'mhpasswordmanager-user-service...'
docker build -t mhpasswordmanager/user-service:latest ../mhpasswordmanager-user-service
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/user-service:latest mhpasswordmanager/user-service:"$1"
fi

echo 'mhpasswordmanager-password-service...'
docker build -t mhpasswordmanager/password-service:latest ../mhpasswordmanager-password-service
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/password-service:latest mhpasswordmanager/password-service:"$1"
fi

echo 'mhpasswordmanager-config-services...'
docker build -t mhpasswordmanager/config-services:latest ../mhpasswordmanager-config-services
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/config-services:latest mhpasswordmanager/config-services:"$1"
fi

echo 'mhpasswordmanager-email-service...'
docker build -t mhpasswordmanager/email-service:latest ../mhpasswordmanager-email-service
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/email-service:latest mhpasswordmanager/email-service:"$1"
fi

echo 'mhpasswordmanager-file-service...'
docker build -t mhpasswordmanager/file-service:latest ../mhpasswordmanager-file-service
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/file-service:latest mhpasswordmanager/file-service:"$1"
fi

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
