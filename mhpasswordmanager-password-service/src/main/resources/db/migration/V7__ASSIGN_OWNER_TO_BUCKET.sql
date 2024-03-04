ALTER TABLE users_buckets
    ADD COLUMN owner_name VARCHAR(255) NOT NULL DEFAULT '';

UPDATE users_buckets SET owner_name = name WHERE name IS NOT NULL;