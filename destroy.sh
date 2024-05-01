#!/bin/sh
export COMPOSE_CONVERT_WINDOWS_PATHS="1"
export COMPOSE_PROJECT_NAME="password-manager"
docker compose down --volumes
