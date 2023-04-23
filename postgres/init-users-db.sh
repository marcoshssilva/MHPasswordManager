#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
DO \$\$
BEGIN
IF NOT EXISTS(SELECT * FROM pg_user WHERE usename = 'sa-authorization-server')
THEN
    CREATE ROLE "sa-authorization-server" WITH LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT NOREPLICATION CONNECTION LIMIT -1 PASSWORD 'P@sswordAuthS3rv3r';
END IF;
IF NOT EXISTS(SELECT * FROM pg_user WHERE usename = 'sa-password-service')
THEN
    CREATE ROLE "sa-password-service" WITH LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION PASSWORD 'P@ssw0rdP@ssw0rds';
END IF;
IF NOT EXISTS(SELECT * FROM pg_user WHERE usename = 'sa-user-service')
THEN
    CREATE ROLE "sa-user-service" WITH LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION CONNECTION LIMIT -1 PASSWORD 'P@ssw0rdUs3rS3rvic3';
END IF;
END \$\$;
EOSQL

if psql -lqt | cut -d \| -f 1 | grep -wq db_users; then
    echo "Database db_users already exists."
else
    psql -c "CREATE DATABASE db_users WITH OWNER = 'sa-user-service' ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;"
    echo "db_users OK."
fi

if psql -lqt | cut -d \| -f 1 | grep -wq db_passwords; then
    echo "Database db_passwords already exists."
else
    psql -c "CREATE DATABASE db_passwords WITH OWNER = 'sa-password-service' ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;"
    echo "db_passwords OK."
fi


if psql -lqt | cut -d \| -f 1 | grep -wq db_auth; then
    echo "Database db_auth already exists."
else
    psql -c "CREATE DATABASE db_auth WITH OWNER = 'sa-authorization-server' ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;"
    echo "db_auth OK."
fi

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "db_users" <<-EOSQL

DO \$\$
BEGIN
  IF EXISTS(SELECT * FROM pg_user WHERE usename = 'sa-authorization-server')
      GRANT CONNECT ON DATABASE db_users TO "sa-authorization-server";
      GRANT USAGE ON SCHEMA public TO "sa-authorization-server";
      GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO "sa-authorization-server";
  END IF;
END \$\$;

EOSQL
