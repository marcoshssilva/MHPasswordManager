set COMPOSE_CONVERT_WINDOWS_PATHS="1"
set COMPOSE_PROJECT_NAME="password-manager"
docker compose -f "docker-compose.yml" --profile all down --volumes
pause