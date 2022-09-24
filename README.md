# MHPasswordManager

## Java Configuration
| Maven Project's                                   | Group ID                 | Artifact ID                                       | Version            | Java Version | Spring Boot | Spring Cloud |
|---------------------------------------------------|--------------------------|---------------------------------------------------|--------------------|--------------|-------------|--------------|
| mhpasswordmanager-api-gateway                     | **br.com.marcoshssilva** | mhpasswordmanager.api.gateway                     | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.3       | 2021.0.3     |
| mhpasswordmanager-flyway-database-migrations-tool | **br.com.marcoshssilva** | mhpasswordmanager.flyway.database.migrations.tool | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.3       | 2021.0.3     |
| mhpasswordmanager-oauth2-authorizationserver      | **br.com.marcoshssilva** | mhpasswordmanager.oauth2.authorization.server     | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.3       | 2021.0.3     |
| mhpasswordmanager-service-registry                | **br.com.marcoshssilva** | mhpasswordmanager.service.registry                | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.3       | 2021.0.3     |
| mhpasswordmanager-user-service                    | **br.com.marcoshssilva** | mhpasswordmanager.userservice                     | **0.0.1-SNAPSHOT** | temurin-11   | 2.7.3       | 2021.0.3     |

## Data Storage
| DataSources | Version | Host                          | Databases | Schemas  | Username | Password |
|-------------|---------|-------------------------------|-----------|----------|----------|----------|
| Postgres    | 14.5    | **host.docker.internal:5432** | postgres  | db_users | postgres | postgres |

## Default Users from System
| Username                    | Password        | Roles                              |
|-----------------------------|-----------------|------------------------------------|
| johndoe@email.com           | 4dm1nPass@w@rd  | ROLE_USER, ROLE_ADMIN              |
| emmywatson@email.com        | Cli&ntPass@w@rd | ROLE_USER                          |
| marcoshssilva.dev@gmail.com | M@st#rPass@w@rd | ROLE_USER, ROLE_ADMIN, ROLE_MASTER |