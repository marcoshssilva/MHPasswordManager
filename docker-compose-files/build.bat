@echo off
setlocal EnableDelayedExpansion

echo 'mhpasswordmanager-postgres-db...'
docker build -t mhpasswordmanager/postgres:latest ../postgres

echo 'mhpasswordmanager-service-registry...'
docker build -t mhpasswordmanager/service-registry:latest ../mhpasswordmanager-service-registry

echo 'mhpasswordmanager-oauth2-authorizationserver...'
docker build -t mhpasswordmanager/oauth2-authorization-server:latest ../mhpasswordmanager-oauth2-authorizationserver

echo 'mhpasswordmanager-api-gateway...'
docker build -t mhpasswordmanager/api-gateway:latest ../mhpasswordmanager-api-gateway

echo 'mhpasswordmanager-user-service...'
docker build -t mhpasswordmanager/user-service:latest ../mhpasswordmanager-user-service

echo 'mhpasswordmanager-password-service...'
docker build -t mhpasswordmanager/password-service:latest ../mhpasswordmanager-password-service

echo 'Cleaning images...'
for /f "tokens=*" %%a in ('docker images -f dangling=true -q ^| awk "{print $3}" ^| sort -u') do (set "image_id=%%a")
if not !image_id! == x (docker rmi --force $(docker images -f dangling=true))

echo 'BUILD CONTAINERS SUCCESSFULLY.'
