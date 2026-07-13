#!/bin/sh
export COMPOSE_CONVERT_WINDOWS_PATHS="1"
export COMPOSE_PROJECT_NAME="password-manager"
docker compose -f "docker-compose.yml" up --profile all -d
echo "Please visit link: http://127.0.0.1:8080/mypass-manager/auth/ to open application."