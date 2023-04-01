DROP TABLE IF EXISTS users_keys_types CASCADE;
DROP TABLE IF EXISTS users_registration CASCADE;
DROP TABLE IF EXISTS users_key_tags CASCADE;
DROP TABLE IF EXISTS users_keys CASCADE;
DROP TABLE IF EXISTS users_stored_values CASCADE;

CREATE TABLE users_registration
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

CREATE TABLE users_keys_types
(
    id          bigint                                              NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    description character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_keys_types_pkey PRIMARY KEY (id)
);

CREATE TABLE users_keys
(
    id                   bigint                                              NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    created_at           timestamp without time zone,
    last_update          timestamp without time zone,
    user_key_type_id     integer                                             NOT NULL,
    description          character varying(255) COLLATE pg_catalog."default" NOT NULL,
    user_registration_id character varying(36) COLLATE pg_catalog."default"  NOT NULL,
    CONSTRAINT users_keys_pkey PRIMARY KEY (id),
    CONSTRAINT fka1k0u478yb5ic1e2djsy8r1q7 FOREIGN KEY (user_registration_id)
        REFERENCES users_registration (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fkasf1as16as1f65a1as65f1as FOREIGN KEY (user_key_type_id)
        REFERENCES users_keys_types (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS users_stored_values
(
    id          bigint                            NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    created_at  timestamp without time zone,
    data        text COLLATE pg_catalog."default" NOT NULL,
    last_update timestamp without time zone,
    user_key_id bigint                            NOT NULL,
    CONSTRAINT users_stored_values_pkey PRIMARY KEY (id),
    CONSTRAINT fkpmiofvr032gh7q3xjlx49anty FOREIGN KEY (user_key_id)
        REFERENCES users_keys (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS users_key_tags
(
    user_password_key_id bigint                                              NOT NULL,
    tags                 character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT fk8pjkk63uvcwo9qsgwxgaxm19r FOREIGN KEY (user_password_key_id)
        REFERENCES users_keys (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_users_keys_user_registration_id ON users_keys (user_registration_id);
CREATE INDEX idx_users_keys_user_key_type_id ON users_keys (user_key_type_id);
CREATE INDEX idx_users_stored_values_user_user_key_id ON users_stored_values (user_key_id);

INSERT INTO users_keys_types(id, description)
VALUES (1, 'EMAILS'),
       (2, 'SOCIAL_MEDIA'),
       (3, 'WEBSITE'),
       (4, 'APPLICATION'),
       (5, 'BANK_CARD');