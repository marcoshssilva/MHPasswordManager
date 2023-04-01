DELETE FROM oauth2_registered_client
WHERE client_id in ('MHPasswordManager-GlobalAdmin', 'MHPasswordManager');

INSERT INTO oauth2_registered_client (id,client_id,client_id_issued_at,client_secret,client_secret_expires_at,client_name,client_authentication_methods,authorization_grant_types,redirect_uris,scopes,client_settings,token_settings)
VALUES ('399a39ed-bf4d-4721-98f9-58edf64984a0',
        'MHPasswordManager-GlobalAdmin',
        '2023-04-01 10:53:22.796498',
        '$2a$10$TfYG9ksju9ObO2Z1YK72qeQqJHrVK3VGepz1ehDByQUguykOxNi5e',
        NULL,
        'Registered client for Client Credentials',
        'client_secret_basic',
        'client_credentials',
        '',
        'user:canDelete,user:canCreate,user:canWrite,user:canRead',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",180.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}'),
       ('1e0f890b-ca60-4af2-a4c3-19a19d76f116',
        'MHPasswordManager',
        '2023-04-01 10:53:22.826858',
        '$2a$10$Ux5z/isLxTngym2YyuDec.VgGVOpdjlg5.K1RfGW5GaEVrAZ3EITW',
        NULL,
        'Registered client for PWA Client',
        'client_secret_basic',
        'refresh_token,authorization_code',
        'http://127.0.0.1:12050/passwords/swagger-ui/redirect,http://127.0.0.1:12050/log/swagger-ui/redirect,http://127.0.0.1:12050/documents/swagger-ui/redirect,http://127.0.0.1:8100/authorize,https://oidcdebugger.com/debug,http://127.0.0.1:12050/users/swagger-ui/redirect,http://127.0.0.1:12050/emails/swagger-ui/redirect,http://127.0.0.1:12010/swagger-ui/redirect,https://oauth.pstmn.io/v1/callback',
        'user:canSelfRead,openid,profile,user:canSelfDelete,email,user:canSelfWrite',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",900.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",10800.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}');