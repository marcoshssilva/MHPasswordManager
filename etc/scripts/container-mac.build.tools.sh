#!/bin/bash

# Este script utiliza o comando 'container' nativo da Apple (macOS 15 Sequoia+)
# Certifique-se de que o comando 'container' está instalado e configurado.

# Network
NETWORK_NAME="net"
# O comando 'container' pode gerenciar redes de forma diferente, mas mantemos a tentativa de criação por compatibilidade.
container network inspect $NETWORK_NAME >/dev/null 2>&1 || container network create $NETWORK_NAME

# Volumes
VOLUMES=("data-postgres-db" "data-redis" "data-mongo-db" "data-rabbitmq-logs" "data-rabbitmq-data")
for vol in "${VOLUMES[@]}"; do
    container volume inspect "$vol" >/dev/null 2>&1 || container volume create "$vol"
done

# Function to build and run
build_and_run() {
    local service_name=$1
    local image_name=$2
    local context_path=$3
    local run_args=$4

    # Check if image exists
    if [[ "$(container image ls -q "$image_name" 2> /dev/null)" == "" ]]; then
        echo "Building image $image_name..."
        container build -t "$image_name" "$context_path"
    else
        echo "Image $image_name already exists."
    fi

    # Check if container is running
    if container inspect "$service_name" >/dev/null 2>&1; then
        if container ls --all | grep "$service_name" | grep -q "running"; then
            echo "Container $service_name is already running."
        else
            echo "Starting existing container $service_name..."
            container start "$service_name" || echo "Failed to start container $service_name"
        fi
    else
        echo "Running new container $service_name..."
        # Note: Added '|| true' or error check to see why it fails
        container run -d --name "$service_name" --network "$NETWORK_NAME" $run_args "$image_name" || {
            echo "Error: Failed to run container $service_name."
            echo "Check if the network '$NETWORK_NAME' exists and if the ports are not in use."
        }
    fi
}

# tool-postgres -> tool-postgres-db
build_and_run "tool-postgres-db" "tool-postgres-db" "./etc/docker/tool-postgres" \
    "--env-file etc/env/POSTGRES-DB.env -p 5432:5432 -v data-postgres-db:/var/lib/postgresql/data"

# tool-redis -> tool-redis
build_and_run "tool-redis" "tool-redis" "./etc/docker/tool-redis" \
    "--env-file etc/env/REDIS-STORE.env -p 6379:6379 -v data-redis:/data"

# tool-mongo -> tool-mongo
build_and_run "tool-mongo" "tool-mongo" "./etc/docker/tool-mongo" \
    "--env-file etc/env/MONGO-DB.env -p 27017:27017 -v data-mongo-db:/data/db"

# tool-rabbitmq -> tool-rabbitmq
build_and_run "tool-rabbitmq" "tool-rabbitmq" "./etc/docker/tool-rabbitmq" \
    "--env-file etc/env/RABBITMQ.env -p 5672:5672 -p 15672:15672 -p 25672:25672 -p 15692:15692 -v data-rabbitmq-data:/var/lib/rabbitmq"

# tool-mail -> tool-mailserver
build_and_run "tool-mailserver" "tool-mailserver" "./etc/docker/tool-mail" \
    "--env-file etc/env/MAILSERVER.env -p 1025:1025 -p 1080:1080"
