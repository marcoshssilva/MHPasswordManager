set COMPOSE_CONVERT_WINDOWS_PATHS=1
set COMPOSE_PROJECT_NAME=MHPASSWORDMANAGER
docker-compose up -f "docker-compose.yml" -d eureka-service-discovery
pause