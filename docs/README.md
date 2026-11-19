# Application flows

This guide describes the main flows implemented in MHPasswordManager. Storage
and integration details can vary with Spring profiles and configuration,
especially `config.users.mode`.

| Document | Flow |
|---|---|
| [Architecture and bootstrap](architecture-and-bootstrap.md) | Service topology, Config Server, Eureka and Gateway routing. |
| [Authentication and accounts](authentication-and-accounts.md) | OAuth2 login, JWT access, registration, verification and password recovery. |
| [Password vaults](password-vaults.md) | Vault key-pair creation and storage of encrypted password records. |
| [Asynchronous file encryption](file-encryption.md) | Upload lifecycle, RabbitMQ events, encryption and file retrieval. |
| [Messaging and email](messaging-and-email.md) | Email event production, consumption and delivery modes. |

These are implementation-oriented flow descriptions, not deployment
instructions. For runtime properties and deployment options, see the README
for the relevant service.
