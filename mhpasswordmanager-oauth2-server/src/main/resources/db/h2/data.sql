INSERT INTO users (username, password, enabled)
VALUES ('admin@mhpasswordmanager.com',
        '$2a$10$DoB60YPE7LytjiVBrNR7ieKkMUVB5QaIWm0gtTeOPpgSOkkNi1L3i',
        true),
       ('anonymous@mhpasswordmanager.com',
        '$2a$10$DoB60YPE7LytjiVBrNR7ieKkMUVB5QaIWm0gtTeOPpgSOkkNi1L3i',
        true);

INSERT INTO authorities (username, authority)
VALUES ('anonymous@mhpasswordmanager.com',
        'ROLE_USER'),
       ('admin@mhpasswordmanager.com',
        'ROLE_ADMIN'),
       ('admin@mhpasswordmanager.com',
        'ROLE_MASTER'),
       ('admin@mhpasswordmanager.com',
        'ROLE_USER');

INSERT INTO users_details (username, firstName, lastName, imageUrl)
VALUES ('admin@mhpasswordmanager.com',
        'Default',
        'Administrator',
        null),
       ('anonymous@mhpasswordmanager.com',
        'Anonymous',
        'User',
        null);