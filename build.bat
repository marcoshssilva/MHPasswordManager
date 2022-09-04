docker build -t mhpasswordmanager/service-registry:latest mhpasswordmanager-service-registry
docker build -t mhpasswordmanager/oauth2-authorization-server:latest mhpasswordmanager-oauth2-authorizationserver
docker build -t mhpasswordmanager/api-gateway:latest mhpasswordmanager-api-gateway
docker build -t mhpasswordmanager/pwa-client:latest mhpasswordmanager-pwa-client
docker build -t mhpasswordmanager/user-service:latest mhpasswordmanager-user-service

echo 'BUILD CONTAINERS COMPLETED.'
pause