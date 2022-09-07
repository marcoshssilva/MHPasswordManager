TRUNCATE TABLE db_users.authorities CASCADE;
TRUNCATE TABLE db_users.users CASCADE;

alter table db_users.users
    alter column password type varchar(120);

INSERT INTO db_users.users (username, "password", enabled)
VALUES ('johndoe@email.com',
        '$2a$10$6EG8N4N7jbOsY/RNPTK98eL2vft7ig7SPO/IOFd.PdnL66HgI6oDe',
        true),
       ('emmywatson@email.com',
        '$2a$10$BGG3r6z9udsLHBKLXZV0iuDvPdChNaEVoFINr.Myh0l2iEEwU.tFm',
        true),
       ('marcoshssilva.dev@gmail.com',
        '$2a$10$KkW5uV9JjrRJ13/56.5h.e.qovf4YMlxCR7FhZngm7/2m/HLDG7dO',
        true);

INSERT INTO db_users.authorities (username,authority)
VALUES
      ('johndoe@email.com',
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