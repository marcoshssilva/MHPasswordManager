echo 'mhpasswordmanager-redis...'
docker build -t mhpasswordmanager/redis:dev ./tools/redis
if "%~1"=="" (
  docker tag mhpasswordmanager/redis:dev mhpasswordmanager/redis:%1
)

echo 'mhpasswordmanager-postgres-db...'
docker build -t mhpasswordmanager/postgres:dev ./tools/postgres
if "%~1"=="" (
    docker tag mhpasswordmanager/postgres:dev mhpasswordmanager/postgres:%1
)

echo 'mhpasswordmanager-rabbit-mq...'
docker build -t mhpasswordmanager/rabbit:dev ./tools/rabbitmq
if "%~1"=="" (
    docker tag mhpasswordmanager/rabbit:dev mhpasswordmanager/rabbit:%1
)

echo 'mhpasswordmanager-mongo-db...'
docker build -t mhpasswordmanager/mongo:dev ./tools/mongo
if "%~1"=="" (
    docker tag mhpasswordmanager/mongo:dev mhpasswordmanager/mongo:%1
)

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
