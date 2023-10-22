DROP TABLE IF EXISTS users_recovery_password_code;

CREATE TABLE users_recovery_password_code
(
    code              VARCHAR(11)  NOT NULL,
    username          VARCHAR(255) NOT NULL,
    ip_client         VARCHAR(15)  NOT NULL,
    user_agent_client VARCHAR(255),
    created_at        TIMESTAMP WITH TIME ZONE,
    expires_at        TIMESTAMP WITH TIME ZONE,
    completed         BOOL,
    CONSTRAINT pk_users_recovery_password_code PRIMARY KEY (code, username, ip_client),
    CONSTRAINT fk_users_recovery_password_code_username FOREIGN KEY (username) REFERENCES users (username)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.users_recovery_password_code TO "sa-authorization-server";