DROP TABLE IF EXISTS db_users.users_details CASCADE;

CREATE TABLE db_users.users_details
(
    username  varchar(50) NOT NULL,
    firstName varchar(36) NOT NULL,
    lastName  varchar(36) NOT NULL,
    imageUrl  varchar(255),
    PRIMARY KEY (username),
    CONSTRAINT fk_user_details_users FOREIGN KEY (username) REFERENCES db_users.users (username)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

INSERT INTO db_users.users_details (username, firstName, lastName, imageUrl)
VALUES ('marcoshssilva.dev@gmail.com',
        'Marcos',
        'Silva',
        null),
       ('johndoe@email.com',
        'John',
        'Doe',
        null),
       ('emmywatson@email.com',
        'Emmy',
        'Watson',
        null);
