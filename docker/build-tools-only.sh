#!/bin/sh
echo 'mhpasswordmanager-redis...'
docker build -t mhpasswordmanager/redis:dev ../redis
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/redis:dev mhpasswordmanager/redis:"$1"
fi

echo 'mhpasswordmanager-postgres-db...'
docker build -t mhpasswordmanager/postgres:dev ../postgres
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/postgres:dev mhpasswordmanager/postgres:"$1"
fi

echo 'mhpasswordmanager-mongo-db...'
docker build -t mhpasswordmanager/mongo:dev ../mongo
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/mongo:dev mhpasswordmanager/mongo:"$1"
fi

echo 'mhpasswordmanager-rabbit-mq...'
docker build -t mhpasswordmanager/rabbit:dev ../rabbitmq
if [ $# -eq 1 ]; then
  docker tag mhpasswordmanager/rabbit:dev mhpasswordmanager/rabbit:"$1"
fi

echo 'mhpasswordmanager-mail...'
docker build -t mhpasswordmanager/mail-server:dev ../mail
if [ $# -eq 1 ]; then
    docker tag mhpasswordmanager/mail-server:dev mhpasswordmanager/mail-server:"$1"
fi

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
