#!/bin/sh
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

echo 'BUILD CONTAINERS SUCCESSFULLY.'
