ALTER TABLE users_details
    RENAME TO users_details_old;

DROP INDEX idx_email_users_registration;

CREATE TABLE users_details
(
    username  varchar(255) NOT NULL,
    email     varchar(255) NOT NULL,
    firstName varchar(36)  NOT NULL,
    lastName  varchar(36)  NOT NULL,
    imageUrl  varchar(255),
    PRIMARY KEY (username, email),
    CONSTRAINT fk_user_details_users FOREIGN KEY (username) REFERENCES users (username)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE UNIQUE INDEX idx_email_users_registration ON users_details(email ASC);

INSERT INTO users_details (username, firstname, lastname, imageurl, email)
    SELECT username, firstname, lastName, imageUrl, email
    FROM users_details_old;