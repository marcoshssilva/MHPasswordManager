ALTER TABLE users_details
    ADD COLUMN email varchar(255) NOT NULL default '';

UPDATE users_details
SET email = username WHERE username IS NOT NULL;

CREATE UNIQUE INDEX idx_email_users_registration ON users_details(email ASC);

ALTER TABLE users_details
    DROP CONSTRAINT users_details_pkey;

ALTER TABLE users_details
    ADD PRIMARY KEY (username, email);