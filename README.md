# MHPasswordManager

## Java Configuration
| Maven Project's                                   | Group ID                 | Artifact ID                                       | Version            | Java Version | Spring Boot | Spring Cloud |
|---------------------------------------------------|--------------------------|---------------------------------------------------|--------------------|--------------|-------------|--------------|
| mhpasswordmanager-api-gateway                     | **br.com.marcoshssilva** | mhpasswordmanager.api.gateway                     | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.7       | 2021.0.3     |
| mhpasswordmanager-flyway-database-migrations-tool | **br.com.marcoshssilva** | mhpasswordmanager.flyway.database.migrations.tool | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.7       | 2021.0.3     |
| mhpasswordmanager-oauth2-authorizationserver      | **br.com.marcoshssilva** | mhpasswordmanager.oauth2.authorization.server     | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.7       | 2021.0.3     |
| mhpasswordmanager-service-registry                | **br.com.marcoshssilva** | mhpasswordmanager.service.registry                | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.7       | 2021.0.3     |
| mhpasswordmanager-user-service                    | **br.com.marcoshssilva** | mhpasswordmanager.userservice                     | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.7       | 2021.0.3     |

## Data Storage
| DataSources | Version | Databases | Schemas  | Username | Password |
|-------------|---------|-----------|----------|----------|----------|
| Postgres    | 14.5    | postgres  | db_users | postgres | postgres |

## Default Users from System
| Username                    | Password        | Roles                              |
|-----------------------------|-----------------|------------------------------------|
| johndoe@email.com           | 4dm1nPass@w@rd  | ROLE_USER, ROLE_ADMIN              |
| emmywatson@email.com        | Cli&ntPass@w@rd | ROLE_USER                          |
| marcoshssilva.dev@gmail.com | M@st#rPass@w@rd | ROLE_USER, ROLE_ADMIN, ROLE_MASTER |

## Default Registered Clients
| Client Id                     | Client Secret                        | Scopes                                                   | Authorization Grant_Types        | TokenSettings                                                                                    | ClientSettings                                             | Redirect URIs                                                                                                                                                          |
|-------------------------------|--------------------------------------|----------------------------------------------------------|----------------------------------|--------------------------------------------------------------------------------------------------|------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| pwa-oauth2-authorization-code | fd04f93e-5e4d-4f16-98ae-9247f68d8619 | user:canSelfRead user:canSelfWrite user:canSelfDelete    | AUTHORIZATION_CODE REFRESH_TOKEN | ACCESS_TOKEN_TIME_TO_LIVE=15Minutes REUSE_REFRESH_TOKENS=False REFRESH_TOKEN_TIME_TO_LIVE=3Hours | REQUIRE_AUTHORIZATION_CONSENT=True REQUIRE_PROOF_KEY=False | https://oidcdebugger.com/debug  https://oauth.pstmn.io/v1/callback  http://127.0.0.1:4200/authorize  http://127.0.0.1:8100/authorize  capacitor-electron://-/authorize |
| pwa-oauth2-client-credentials | 8e18ee56-ab7c-4ed9-b192-ff4472e5c697 | user:canRead user:canWrite user:canDelete user:canCreate | CLIENT_CREDENTIALS               | ACCESS_TOKEN_TIME_TO_LIVE=3Minutes                                                               | none                                                       |                                                                                                                                                                        |
