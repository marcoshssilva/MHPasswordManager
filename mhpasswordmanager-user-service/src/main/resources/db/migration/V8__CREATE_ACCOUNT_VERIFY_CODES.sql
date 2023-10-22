DROP TABLE IF EXISTS users_verify_codes;

CREATE TABLE users_verify_codes
(
    uuid_code         VARCHAR(36) NOT NULL,
    username          VARCHAR(255) NOT NULL,
    ip_client         VARCHAR(15),
    user_agent_client VARCHAR(255),
    created_at        TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_users_verify_codes PRIMARY KEY (uuid_code),
    CONSTRAINT fk_users_verify_codes_username FOREIGN KEY (username) REFERENCES users (username)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.users_verify_codes TO "sa-authorization-server";