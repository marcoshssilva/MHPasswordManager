DROP INDEX IF EXISTS idx_oauth2_jwk_keys_active_true;
DROP INDEX IF EXISTS idx_oauth2_jwk_keys_not_deleted;
DROP TABLE IF EXISTS oauth2_jwk_keys CASCADE;

CREATE TABLE oauth2_jwk_keys
(
    uuid               varchar(36) NOT NULL,
    algorithm          varchar(10) NOT NULL DEFAULT 'RSA',
    base64_private_key text        NOT NULL,
    base64_public_key  text        NOT NULL,
    created_at         timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at         timestamp,
    active             boolean     NOT NULL DEFAULT false,
    PRIMARY KEY (uuid)
);

INSERT INTO oauth2_jwk_keys(base64_private_key, base64_public_key, uuid, active, algorithm)
VALUES ('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',
        '0107f47a-2263-421c-81bb-10210c9c2e6d',
        true,
        'RSA');

CREATE INDEX idx_oauth2_jwk_keys_active_true ON oauth2_jwk_keys (created_at DESC) WHERE active = true AND deleted_at IS NULL;
CREATE INDEX idx_oauth2_jwk_keys_not_deleted ON oauth2_jwk_keys (deleted_at);