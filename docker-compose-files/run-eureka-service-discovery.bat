call ../mhpasswordmanager-service-registry/build-image.bat
set COMPOSE_CONVERT_WINDOWS_PATHS=1
set COMPOSE_PROJECT_NAME=MHPASSWORDMANAGER
docker-compose -f "docker-compose.yml" up -d eureka-service-discovery
pause