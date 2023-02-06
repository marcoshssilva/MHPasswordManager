CREATE SCHEMA IF NOT EXISTS db_passwords;
DROP TABLE IF EXISTS db_passwords.users_registration;
CREATE TABLE db_passwords.users_registration
(
    id             VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    public_key     TEXT,
    encriptedKey10 TEXT,
    encriptedKey1  TEXT,
    encriptedKey2  TEXT,
    encriptedKey3  TEXT,
    encriptedKey4  TEXT,
    encriptedKey5  TEXT,
    encriptedKey6  TEXT,
    encriptedKey7  TEXT,
    encriptedKey8  TEXT,
    encriptedKey9  TEXT,
    PRIMARY KEY (id)
);