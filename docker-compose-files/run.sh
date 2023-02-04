#!/bin/sh
set COMPOSE_CONVERT_WINDOWS_PATHS=1
set COMPOSE_PROJECT_NAME=MHPASSWORDMANAGER
docker-compose -f "docker-compose.yml" up -d eureka-service-discovery oauth2-authorization-server api-gateway user-service password-service
