INSERT INTO users (username, password, enabled)
VALUES ('johndoe@email.com',
        '$2a$10$6EG8N4N7jbOsY/RNPTK98eL2vft7ig7SPO/IOFd.PdnL66HgI6oDe',
        true),
       ('emmywatson@email.com',
        '$2a$10$BGG3r6z9udsLHBKLXZV0iuDvPdChNaEVoFINr.Myh0l2iEEwU.tFm',
        true),
       ('marcoshssilva.dev@gmail.com',
        '$2a$10$KkW5uV9JjrRJ13/56.5h.e.qovf4YMlxCR7FhZngm7/2m/HLDG7dO',
        true);

INSERT INTO authorities (username, authority)
VALUES ('johndoe@email.com',
        'ROLE_ADMIN'),
       ('johndoe@email.com',
        'ROLE_USER'),
       ('emmywatson@email.com',
        'ROLE_USER'),
       ('marcoshssilva.dev@gmail.com',
        'ROLE_ADMIN'),
       ('marcoshssilva.dev@gmail.com',
        'ROLE_MASTER'),
       ('marcoshssilva.dev@gmail.com',
        'ROLE_USER');

INSERT INTO users_details (username, firstName, lastName, imageUrl)
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