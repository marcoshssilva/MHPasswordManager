DROP TABLE IF EXISTS users_buckets CASCADE;

CREATE TABLE users_buckets
(
    id                                  VARCHAR(36) PRIMARY KEY,
    name                                VARCHAR(40)              NOT NULL,
    description                         VARCHAR(255)             NOT NULL,
    encoded_public_key                  TEXT                     NOT NULL,
    encrypted_private_key_with_password TEXT                     NOT NULL,
    created_at                          timestamp with time zone NOT NULL,
    last_update                         timestamp with time zone NOT NULL
);

INSERT INTO users_buckets(id, name, description, encoded_public_key, encrypted_private_key_with_password, created_at,
                          last_update)
SELECT id,
       email,
       CONCAT('Migrate data from user ', email),
       encoded_public_key,
       encrypted_private_key_with_password,
       created_at,
       last_update
FROM users_registration;

ALTER TABLE users_keys
    ADD COLUMN user_bucket_id character varying(36) COLLATE pg_catalog."default" NOT NULL DEFAULT '';

UPDATE users_keys
SET user_bucket_id = user_registration_id
WHERE 1 = 1;

ALTER TABLE users_keys
    ADD CONSTRAINT saf46as4fas56f46as FOREIGN KEY (user_bucket_id) REFERENCES users_buckets (id)
        MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

DROP INDEX IF EXISTS idx_users_keys_user_registration_id CASCADE;

ALTER TABLE users_keys DROP CONSTRAINT fka1k0u478yb5ic1e2djsy8r1q7;
ALTER TABLE users_keys DROP COLUMN user_registration_id;

CREATE INDEX idx_users_keys_user_bucket_id ON users_keys (user_bucket_id);