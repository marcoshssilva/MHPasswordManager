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

echo 'mhpasswordmanager-service-registry...'
docker build -t mhpasswordmanager/service-registry:dev ./mhpasswordmanager-service-registry
if "%~1"=="" (
    docker tag mhpasswordmanager/service-registry:dev mhpasswordmanager/service-registry:%1
)

echo 'mhpasswordmanager-oauth2-authorizationserver...'
docker build -t mhpasswordmanager/oauth2-authorization-server:dev ./mhpasswordmanager-oauth2-server
if "%~1"=="" (
    docker tag mhpasswordmanager/oauth2-server:dev mhpasswordmanager/oauth2-server:%1
)

echo 'mhpasswordmanager-api-gateway...'
docker build -t mhpasswordmanager/api-gateway:dev ./mhpasswordmanager-api-gateway
if "%~1"=="" (
    docker tag mhpasswordmanager/api-gateway:dev mhpasswordmanager/api-gateway:%1
)

echo 'mhpasswordmanager-user-service...'
docker build -t mhpasswordmanager/user-service:dev ./mhpasswordmanager-user-service
if "%~1"=="" (
    docker tag mhpasswordmanager/user-service:dev mhpasswordmanager/user-service:%1
)

echo 'mhpasswordmanager-password-service...'
docker build -t mhpasswordmanager/password-service:dev ./mhpasswordmanager-password-service
if "%~1"=="" (
    docker tag my-image:dev my-image:%1
)

echo 'mhpasswordmanager-config-services...'
docker build -t mhpasswordmanager/config-services:dev ./mhpasswordmanager-config-services
if "%~1"=="" (
    docker tag mhpasswordmanager/config-services:dev mhpasswordmanager/config-services:%1
)

echo 'mhpasswordmanager-email-service...'
docker build -t mhpasswordmanager/email-service:dev ./mhpasswordmanager-email-service
if "%~1"=="" (
    docker tag mhpasswordmanager/email-service:dev mhpasswordmanager/email-service:%1
)

echo 'mhpasswordmanager-file-service...'
docker build -t mhpasswordmanager/file-service:dev ./mhpasswordmanager-file-service
if "%~1"=="" (
    docker tag mhpasswordmanager/file-service:dev mhpasswordmanager/file-service:%1
)

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
