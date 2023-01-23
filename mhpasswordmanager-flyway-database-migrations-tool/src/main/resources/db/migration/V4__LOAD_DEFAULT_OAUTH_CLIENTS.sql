INSERT INTO db_users.oauth2_registered_client (id, client_id, client_id_issued_at, client_secret,
                                               client_secret_expires_at, client_name, client_authentication_methods,
                                               authorization_grant_types, redirect_uris, scopes, client_settings,
                                               token_settings)
VALUES ('0fbef37c-443c-4084-aa68-83914e28f182', 'MHPasswordManager', '2023-01-17 05:49:34.776',
        '$2a$10$6dTcMLYP4dYKuc5qfxvomu3ZzXYTqjx.hkEEgBadlJNTlqSxwf5H.', NULL, 'Registered client for PWA Client',
        'client_secret_basic', 'refresh_token,authorization_code',
        'http://127.0.0.1:4200/authorize,http://127.0.0.1:8100/authorize,https://oidcdebugger.com/debug,https://oauth.pstmn.io/v1/callback',
        'user:canSelfRead,user:canSelfDelete,user:canSelfWrite',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",900.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",10800.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}'),
       ('082343aa-86f1-4645-8701-50bc33b210fb', 'MHPasswordManager-GlobalAdmin', '2023-01-17 05:49:34.811',
        '$2a$10$WIZDmPi06nlGlDdPTcvW3e/XVZH7CGnCfWopGOtlS9buQpg./PL1G', NULL,
        'Registered client for Client Credentials', 'client_secret_basic', 'client_credentials', '',
        'user:canDelete,user:canCreate,user:canWrite,user:canRead',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",180.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}');
