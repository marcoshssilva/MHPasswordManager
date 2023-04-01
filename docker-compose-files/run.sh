#!/bin/sh
export COMPOSE_CONVERT_WINDOWS_PATHS="1"
export COMPOSE_PROJECT_NAME="MHPASSWORDMANAGER"
docker-compose -f "docker-compose.yml" up -d postgres-db eureka-service-discovery oauth2-authorization-server api-gateway user-service password-service redis-store
