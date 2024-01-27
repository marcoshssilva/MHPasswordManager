echo 'mhpasswordmanager-redis...'
docker build -t mhpasswordmanager/redis:dev ./tool-redis
if "%~1"=="" (
  docker tag mhpasswordmanager/redis:dev mhpasswordmanager/redis:%1
)

echo 'mhpasswordmanager-postgres-db...'
docker build -t mhpasswordmanager/postgres:dev ./tool-postgres
if "%~1"=="" (
    docker tag mhpasswordmanager/postgres:dev mhpasswordmanager/postgres:%1
)

echo 'mhpasswordmanager-rabbit-mq...'
docker build -t mhpasswordmanager/rabbit:dev ./tool-rabbitmq
if "%~1"=="" (
    docker tag mhpasswordmanager/rabbit:dev mhpasswordmanager/rabbit:%1
)

echo 'mhpasswordmanager-mongo-db...'
docker build -t mhpasswordmanager/mongo:dev ./tool-mongo
if "%~1"=="" (
    docker tag mhpasswordmanager/mongo:dev mhpasswordmanager/mongo:%1
)

echo 'mhpasswordmanager-mail-server...'
docker build -t mhpasswordmanager/mail-server:dev ./tool-mail
if "%~1"=="" (
    docker tag mhpasswordmanager/mail-server:dev mhpasswordmanager/mail-server:%1
)

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
