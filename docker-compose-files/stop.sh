#!/bin/sh
export COMPOSE_CONVERT_WINDOWS_PATHS="1"
export COMPOSE_PROJECT_NAME="MHPASSWORDMANAGER"
docker-compose down --volumes
