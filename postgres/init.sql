-- create users
CREATE ROLE "sa-authorization-server" WITH LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT NOREPLICATION CONNECTION LIMIT -1 PASSWORD 'P@sswordAuthS3rv3r';
CREATE ROLE "sa-password-service" WITH LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION PASSWORD 'P@ssw0rdP@ssw0rds';
CREATE ROLE "sa-user-service" WITH LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION CONNECTION LIMIT -1 PASSWORD 'P@ssw0rdUs3rS3rvic3';

-- create databases
CREATE DATABASE db_users WITH OWNER = 'sa-user-service' ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;
CREATE DATABASE db_passwords WITH OWNER = 'sa-password-service' ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;
CREATE DATABASE db_auth WITH OWNER = 'sa-authorization-server' ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;

-- enable sa-authorization-server to use db_users, use inside db_users context
GRANT CONNECT ON DATABASE db_users TO "sa-authorization-server";
GRANT USAGE ON SCHEMA public TO "sa-authorization-server";
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO "sa-authorization-server";