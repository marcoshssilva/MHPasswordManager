ALTER TABLE users_details
    RENAME TO users_details_old_2;

DROP INDEX idx_email_users_registration;

CREATE TABLE users_details
(
    username    varchar(255) NOT NULL,
    email       varchar(255) NOT NULL,
    firstName   varchar(36)  NOT NULL,
    lastName    varchar(36)  NOT NULL,
    verified    bool         NOT NULL,
    verified_at timestamp,
    imageUrl    varchar(255),
    PRIMARY KEY (username, email),
    CONSTRAINT fk_user_details_users FOREIGN KEY (username) REFERENCES users (username)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE UNIQUE INDEX idx_email_users_registration ON users_details(email ASC);

INSERT INTO users_details (username, firstname, lastname, imageurl, email, verified, verified_at)
SELECT username, firstname, lastName, imageUrl, email, true, current_timestamp
FROM users_details_old_2;

DROP TABLE IF EXISTS users_details_old;
DROP TABLE IF EXISTS users_details_old_2;

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.users_details TO "sa-authorization-server";