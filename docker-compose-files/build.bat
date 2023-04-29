echo 'mhpasswordmanager-redis...'
docker build -t mhpasswordmanager/redis:latest ../redis
if "%~1"=="" (
  docker tag mhpasswordmanager/redis:latest mhpasswordmanager/redis:%1
)

echo 'mhpasswordmanager-postgres-db...'
docker build -t mhpasswordmanager/postgres:latest ../postgres
if "%~1"=="" (
    docker tag mhpasswordmanager/postgres:latest mhpasswordmanager/postgres:%1
)

echo 'mhpasswordmanager-service-registry...'
docker build -t mhpasswordmanager/service-registry:latest ../mhpasswordmanager-service-registry
if "%~1"=="" (
    docker tag mhpasswordmanager/service-registry:latest mhpasswordmanager/service-registry:%1
)

echo 'mhpasswordmanager-oauth2-authorizationserver...'
docker build -t mhpasswordmanager/oauth2-authorization-server:latest ../mhpasswordmanager-oauth2-authorizationserver
if "%~1"=="" (
    docker tag mhpasswordmanager/oauth2-authorization-server:latest mhpasswordmanager/oauth2-authorization-server:%1
)

echo 'mhpasswordmanager-api-gateway...'
docker build -t mhpasswordmanager/api-gateway:latest ../mhpasswordmanager-api-gateway
if "%~1"=="" (
    docker tag mhpasswordmanager/api-gateway:latest mhpasswordmanager/api-gateway:%1
)

echo 'mhpasswordmanager-user-service...'
docker build -t mhpasswordmanager/user-service:latest ../mhpasswordmanager-user-service
if "%~1"=="" (
    docker tag mhpasswordmanager/user-service:latest mhpasswordmanager/user-service:%1
)

echo 'mhpasswordmanager-password-service...'
docker build -t mhpasswordmanager/password-service:latest ../mhpasswordmanager-password-service
if "%~1"=="" (
    docker tag my-image:latest my-image:%1
)

echo 'mhpasswordmanager-config-services...'
docker build -t mhpasswordmanager/config-services:latest ../mhpasswordmanager-config-services
if "%~1"=="" (
    docker tag mhpasswordmanager/config-services:latest mhpasswordmanager/config-services:%1
)

echo 'mhpasswordmanager-email-service...'
docker build -t mhpasswordmanager/email-service:latest ../mhpasswordmanager-email-service
if "%~1"=="" (
    docker tag mhpasswordmanager/email-service:latest mhpasswordmanager/email-service:%1
)

echo 'mhpasswordmanager-file-service...'
docker build -t mhpasswordmanager/file-service:latest ../mhpasswordmanager-file-service
if "%~1"=="" (
    docker tag mhpasswordmanager/file-service:latest mhpasswordmanager/file-service:%1
)

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
