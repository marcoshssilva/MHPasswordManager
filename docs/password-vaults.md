# Password vaults

## Vault creation and encrypted password records

```mermaid
sequenceDiagram
    participant C as Client
    participant G as API Gateway
    participant P as Password Service
    participant DB as SQL database

    C->>G: POST /mypass-manager/passwords/bucket/create + JWT
    G->>P: Forward authenticated request
    P->>P: Validate owner and generate 4096-bit RSA key pair
    P->>P: Protect private key with vault secret
    P->>DB: Store vault, public key and protected private key
    P-->>G: Vault ID and data
    G-->>C: Vault ID and data
    C->>G: Create or update password data + JWT
    G->>P: Forward authenticated request
    P->>P: Serialize data and encrypt with vault public key
    P->>DB: Store encrypted value as Base64
    P-->>G: Return stored value/identifier
    G-->>C: Return stored value/identifier
```

Password Service checks vault ownership before performing operations. On
creation, it generates an RSA key pair; the private key is protected using
the vault secret, and the public key encrypts password records. Encrypted
records and vault information are stored in the SQL database.

## Related implementation

| Area | Source |
|---|---|
| Vault creation and key-pair generation | [UserBucketServiceImpl](../mhpasswordmanager-password-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/passwordservice/domain/services/data/buckets/impl/UserBucketServiceImpl.java) |
| Stored password encryption and persistence | [UserStoredKeysServiceImpl](../mhpasswordmanager-password-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/passwordservice/domain/services/data/keys/impl/UserStoredKeysServiceImpl.java) |
| RSA implementation | [RSACryptServiceImpl](../mhpasswordmanager-password-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/passwordservice/domain/services/crypt/impl/RSACryptServiceImpl.java) |
| HTTP endpoints | [BucketController](../mhpasswordmanager-password-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/passwordservice/web/controllers/BucketController.java), [ManageKeysController](../mhpasswordmanager-password-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/passwordservice/web/controllers/ManageKeysController.java) |

