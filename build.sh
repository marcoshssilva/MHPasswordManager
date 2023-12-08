#!/bin/sh
echo 'mhpasswordmanager-redis...'
docker build -t mhpasswordmanager/redis:dev tool-redis
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/redis:dev mhpasswordmanager/redis:"$1"
fi

echo 'mhpasswordmanager-postgres-db...'
docker build -t mhpasswordmanager/postgres:dev tool-postgres
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/postgres:dev mhpasswordmanager/postgres:"$1"
fi

echo 'mhpasswordmanager-mongo-db...'
docker build -t mhpasswordmanager/mongo:dev tool-mongo
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/mongo:dev mhpasswordmanager/mongo:"$1"
fi

echo 'mhpasswordmanager-rabbit-mq...'
docker build -t mhpasswordmanager/rabbit:dev tool-rabbitmq
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/rabbit:dev mhpasswordmanager/rabbit:"$1"
fi

echo 'mhpasswordmanager-service-registry...'
docker build -t mhpasswordmanager/service-registry:dev mhpasswordmanager-service-registry
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/service-registry:dev mhpasswordmanager/service-registry:"$1"
fi

echo 'mhpasswordmanager-oauth2-server...'
docker build -t mhpasswordmanager/oauth2-server:dev mhpasswordmanager-oauth2-server
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/oauth2-server:dev mhpasswordmanager/oauth2-server:"$1"
fi

echo 'mhpasswordmanager-api-gateway...'
docker build -t mhpasswordmanager/api-gateway:dev mhpasswordmanager-api-gateway
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/api-gateway:dev mhpasswordmanager/api-gateway:"$1"
fi

echo 'mhpasswordmanager-user-service...'
docker build -t mhpasswordmanager/user-service:dev mhpasswordmanager-user-service
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/user-service:dev mhpasswordmanager/user-service:"$1"
fi

echo 'mhpasswordmanager-password-service...'
docker build -t mhpasswordmanager/password-service:dev mhpasswordmanager-password-service
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/password-service:dev mhpasswordmanager/password-service:"$1"
fi

echo 'mhpasswordmanager-config-services...'
docker build -t mhpasswordmanager/config-services:dev mhpasswordmanager-config-services
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/config-services:dev mhpasswordmanager/config-services:"$1"
fi

echo 'mhpasswordmanager-email-service...'
docker build -t mhpasswordmanager/email-service:dev mhpasswordmanager-email-service
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/email-service:dev mhpasswordmanager/email-service:"$1"
fi

echo 'mhpasswordmanager-file-service...'
docker build -t mhpasswordmanager/file-service:dev mhpasswordmanager-file-service
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/file-service:dev mhpasswordmanager/file-service:"$1"
fi

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
