CREATE SCHEMA IF NOT EXISTS db_passwords;

DROP TABLE IF EXISTS db_passwords.users_registration;

CREATE TABLE db_passwords.users_registration (
      id VARCHAR(255) NOT NULL,
      email VARCHAR(255) NOT NULL,
      public_key TEXT
);