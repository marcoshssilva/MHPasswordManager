#!/bin/bash
folders=("mhpasswordmanager-api-gateway" "mhpasswordmanager-config-services" "mhpasswordmanager-email-service" "mhpasswordmanager-file-service" "mhpasswordmanager-oauth2-server" "mhpasswordmanager-password-service" "mhpasswordmanager-service-registry" "mhpasswordmanager-user-service" "tool-mail" "tool-mongo" "tool-postgres" "tool-rabbitmq" "tool-redis")

for folder in "${folders[@]}"; do
    cd "$folder" || continue
    git init
    git branch -M develop
    git remote remove origin
    git remote add origin "https://$1:$2@app-gi.marcoshssilva.com.br/git/MHPasswordManager/$folder.git"
    git fetch origin
    git pull origin develop

    git add .
    git commit -m "update project - $3"
    git push -u origin develop

    cd ..
done
