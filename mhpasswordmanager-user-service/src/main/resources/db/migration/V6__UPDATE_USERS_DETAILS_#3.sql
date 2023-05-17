ALTER TABLE authorities
    DROP CONSTRAINT fk_authorities_users;

UPDATE users
SET username = SPLIT_PART(username, '@', 1)
WHERE username LIKE '%mhpasswordmanager.com';

UPDATE authorities
SET username = SPLIT_PART(username, '@', 1)
WHERE username LIKE '%mhpasswordmanager.com';

UPDATE group_members
SET username = SPLIT_PART(username, '@', 1)
WHERE username LIKE '%mhpasswordmanager.com';

UPDATE users_details
SET username = SPLIT_PART(username, '@', 1)
WHERE username LIKE '%mhpasswordmanager.com';

ALTER TABLE authorities
    ADD CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE group_members
    ADD CONSTRAINT fk_group_members_users FOREIGN KEY (username) REFERENCES users (username) ON DELETE RESTRICT ON UPDATE CASCADE;