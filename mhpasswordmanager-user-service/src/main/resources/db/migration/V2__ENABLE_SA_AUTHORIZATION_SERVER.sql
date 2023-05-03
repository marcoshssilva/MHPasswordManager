GRANT CONNECT ON DATABASE db_users TO "sa-authorization-server";
GRANT USAGE ON SCHEMA public TO "sa-authorization-server";
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO "sa-authorization-server";