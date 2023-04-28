set COMPOSE_CONVERT_WINDOWS_PATHS=1
set COMPOSE_PROJECT_NAME=MHPASSWORDMANAGER
docker-compose -f "docker-compose.yml" up -d config-services eureka-service-discovery
docker-compose -f "docker-compose.yml" up -d postgres-db redis-store
docker-compose -f "docker-compose.yml" up -d oauth2-authorization-server user-service password-service
pause