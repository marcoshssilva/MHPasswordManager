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

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
