FROM mongo:6.0.5

WORKDIR /scripts
COPY scripts/create-user-and-database.sh /docker-entrypoint-initdb.d/
EXPOSE 27017
