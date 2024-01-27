#!/bin/bash
set -e
mongosh --norc --quiet -u "$MONGO_INITDB_ROOT_USERNAME" -p "$MONGO_INITDB_ROOT_PASSWORD" admin <<EOF
    use admin;
    db.createDatabase("db_files");

    use db_files;
    db.createCollection("remove_it");
    print("Database 'db_files' created successfully.");

    use admin;
    db.createUser({
        user: "sa-files-service",
        pwd: "${SA_FILES_SERVICE_PASS:-P@sswordFil3s}",
        roles: [
            {
                role: "readWrite",
                db: "db_files"
            }
        ]
    });
    print("User 'sa-files-service' created successfully.");
EOF
