CREATE SCHEMA IF NOT EXISTS db_users;

DROP TABLE IF EXISTS db_users.oauth2_registered_client CASCADE;
DROP TABLE IF EXISTS db_users.oauth2_authorization_consent CASCADE;
DROP TABLE IF EXISTS db_users.oauth2_authorization CASCADE;
DROP TABLE IF EXISTS db_users.users CASCADE;
DROP TABLE IF EXISTS db_users.authorities CASCADE;
DROP TABLE IF EXISTS db_users.groups CASCADE;
DROP TABLE IF EXISTS db_users.group_authorities CASCADE;
DROP TABLE IF EXISTS db_users.group_members CASCADE;

CREATE TABLE db_users.oauth2_registered_client
(
    id                            varchar(100)                            NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamp     DEFAULT NULL,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE db_users.oauth2_authorization_consent
(
    registered_client_id varchar(100)  NOT NULL,
    principal_name       varchar(200)  NOT NULL,
    authorities          varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE db_users.oauth2_authorization
(
    id                            varchar(100) NOT NULL,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    attributes                    text          DEFAULT NULL,
    state                         varchar(500)  DEFAULT NULL,
    authorization_code_value      text          DEFAULT NULL,
    authorization_code_issued_at  timestamp     DEFAULT NULL,
    authorization_code_expires_at timestamp     DEFAULT NULL,
    authorization_code_metadata   text          DEFAULT NULL,
    access_token_value            text          DEFAULT NULL,
    access_token_issued_at        timestamp     DEFAULT NULL,
    access_token_expires_at       timestamp     DEFAULT NULL,
    access_token_metadata         text          DEFAULT NULL,
    access_token_type             varchar(100)  DEFAULT NULL,
    access_token_scopes           varchar(1000) DEFAULT NULL,
    oidc_id_token_value           text          DEFAULT NULL,
    oidc_id_token_issued_at       timestamp     DEFAULT NULL,
    oidc_id_token_expires_at      timestamp     DEFAULT NULL,
    oidc_id_token_metadata        text          DEFAULT NULL,
    refresh_token_value           text          DEFAULT NULL,
    refresh_token_issued_at       timestamp     DEFAULT NULL,
    refresh_token_expires_at      timestamp     DEFAULT NULL,
    refresh_token_metadata        text          DEFAULT NULL,
    PRIMARY KEY (id)
);

create table db_users.users
(
    username varchar(50) NOT NULL,
    password varchar(50) NOT NULL,
    enabled  boolean     NOT NULL,
    PRIMARY KEY (username)
);

create table db_users.authorities
(
    username  varchar(50) NOT NULL,
    authority varchar(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES db_users.users (username)
);
create unique index ix_auth_username on db_users.authorities (username, authority);

create table db_users.groups
(
    id         bigint GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
    group_name varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

create table db_users.group_authorities
(
    group_id  bigint      NOT NULL,
    authority varchar(50) NOT NULL,
    CONSTRAINT fk_group_authorities_group FOREIGN KEY (group_id) REFERENCES db_users.groups (id)
);

create table db_users.group_members
(
    id       bigint GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
    username varchar(50) NOT NULL,
    group_id bigint      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES db_users.groups (id)
);

INSERT INTO db_users.oauth2_registered_client (id,client_id,client_id_issued_at,client_secret,client_secret_expires_at,client_name,client_authentication_methods,authorization_grant_types,redirect_uris,scopes,client_settings,token_settings)
VALUES
    ('738c62bd-a915-4c18-a6cb-547c5f9037f4','738c62bd-a915-4c18-a6cb-547c5f9037f4','2022-08-21 04:51:40.075092','$2a$10$A7UiwftOhoZnBsJoBTIo/.tp4XU.2uDp/L.Wcyw.gHJNHG5KckQey',NULL,'738c62bd-a915-4c18-a6cb-547c5f9037f4','client_secret_basic','refresh_token,authorization_code','http://localhost:8888,http://localhost,https://oidcdebugger.com/debug,http://localhost:4200,http://localhost:8100','user:canWrite,user:canRead','{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}','{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.core.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",172800.000000000]}'),
    ('57d7425b-9851-41dd-9b56-16f62c3aa294','57d7425b-9851-41dd-9b56-16f62c3aa294','2022-08-21 04:51:40.099606','$2a$10$Gri6O2yl9ZhyVbNzzjzDGO49JhYdOA7pLpt86tm/Pa4OPTh6knmtm',NULL,'57d7425b-9851-41dd-9b56-16f62c3aa294','client_secret_basic','refresh_token,client_credentials','http://localhost:8888,http://localhost,https://oidcdebugger.com/debug,http://localhost:4200,http://localhost:8100','user:canWrite,user:canRead','{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}','{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.core.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",172800.000000000]}')
;
