#!/bin/sh
echo 'mhpasswordmanager-postgres-db...'
docker build -t mhpasswordmanager/postgres:latest ../postgres

echo 'mhpasswordmanager-service-registry...'
docker build -t mhpasswordmanager/service-registry:latest ../mhpasswordmanager-service-registry

echo 'mhpasswordmanager-oauth2-authorizationserver...'
docker rmi mhpasswordmanager/oauth2-authorization-server:latest
docker build -t mhpasswordmanager/oauth2-authorization-server:latest ../mhpasswordmanager-oauth2-authorizationserver

echo 'mhpasswordmanager-api-gateway...'
docker build -t mhpasswordmanager/api-gateway:latest ../mhpasswordmanager-api-gateway

echo 'mhpasswordmanager-user-service...'
docker build -t mhpasswordmanager/user-service:latest ../mhpasswordmanager-user-service

echo 'mhpasswordmanager-password-service...'
docker build -t mhpasswordmanager/password-service:latest ../mhpasswordmanager-password-service

echo 'Cleaning images...'
docker rmi --force $(docker images -f dangling=true)

echo 'BUILD CONTAINERS SUCCESSFULLY.'
