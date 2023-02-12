CREATE SCHEMA IF NOT EXISTS db_passwords;
DROP TABLE IF EXISTS db_passwords.users_registration;
CREATE TABLE db_passwords.users_registration
(
    id                                  VARCHAR(36) PRIMARY KEY,
    email                               VARCHAR(255) NOT NULL,
    encoded_public_key                  TEXT,
    encrypted_private_key_with_password TEXT,
    encrypted_private_key0              TEXT,
    encrypted_private_key1              TEXT,
    encrypted_private_key2              TEXT,
    encrypted_private_key3              TEXT,
    encrypted_private_key4              TEXT,
    encrypted_private_key5              TEXT,
    encrypted_private_key6              TEXT,
    encrypted_private_key7              TEXT,
    encrypted_private_key8              TEXT,
    encrypted_private_key9              TEXT
);
