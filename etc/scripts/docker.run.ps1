Set-Variable COMPOSE_CONVERT_WINDOWS_PATHS=1
Set-Variable COMPOSE_PROJECT_NAME=password-manager
docker compose -f "docker-compose.yml" up --profile all -d
Write-Output "Please visit link: http://127.0.0.1:8080/mypass-manager/auth/ to open application."
Start-Process "http://127.0.0.1:8080/mypass-manager/auth/"