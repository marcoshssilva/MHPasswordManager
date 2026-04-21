set COMPOSE_CONVERT_WINDOWS_PATHS=1
set COMPOSE_PROJECT_NAME=password-manager
docker compose -f "docker-compose.yml" --profile tools up -d
Write-Output "Please visit link: http://127.0.0.1:8080/mypass-manager/auth/ to open application."
Start-Process "http://127.0.0.1:8080/mypass-manager/auth/"