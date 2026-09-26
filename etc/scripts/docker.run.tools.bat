@echo off
set COMPOSE_CONVERT_WINDOWS_PATHS=1
set COMPOSE_PROJECT_NAME=mhpasswordmanager
docker compose -f "docker-compose.yml" --profile tools up -d
if errorlevel 1 exit /b %errorlevel%

echo Tools started.
echo PostgreSQL: 127.0.0.1:5432
echo Redis: 127.0.0.1:6379
echo MongoDB: 127.0.0.1:27017
echo RabbitMQ: AMQP 127.0.0.1:5672 ^| Console http://127.0.0.1:15672 ^| Cluster 127.0.0.1:25672 ^| Metrics http://127.0.0.1:15692/metrics
echo MailDev: SMTP 127.0.0.1:1025 ^| Console http://127.0.0.1:1080
echo MinIO: S3 API http://127.0.0.1:9000 ^| Console http://127.0.0.1:9001
