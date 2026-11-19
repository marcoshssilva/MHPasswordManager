# Asynchronous file encryption

## Upload, encryption and retrieval

```mermaid
sequenceDiagram
    participant C as Client
    participant G as API Gateway
    participant F as File Service
    participant Mongo as MongoDB / GridFS
    participant S3 as S3 / MinIO
    participant R as RabbitMQ
    participant P as Password Service

    C->>G: PUT /mypass-manager/files/bucket/{bucketUuid}/create-file (multipart)
    G->>F: Forward upload
    F->>Mongo: Store metadata/status UPLOAD_RECEIVED
    F-->>G: 202 Accepted + status Location
    G-->>C: 202 Accepted + status Location
    F->>S3: Store original at staging/{fileId}/source
    F->>Mongo: Set status ENCRYPTING
    F->>R: Publish file.encryption.requested
    R->>P: Deliver encryption request
    P->>S3: Read staged original
    P->>P: Encrypt bytes with vault public key
    P->>S3: Store temporary encrypted object
    P->>R: Publish completed or failed event
    R->>F: Deliver encryption result
    F->>Mongo: Set status FINALIZING
    F->>S3: Copy encrypted object to final location
    F->>Mongo: Store GridFS copy and set status READY
    F->>S3: Remove temporary objects when possible
    C->>G: GET /mypass-manager/files/bucket/{bucketUuid}/{key}/info
    G->>F: Query file status
    F-->>G: Status and metadata
    G-->>C: Status and metadata
```

The upload returns `202 Accepted`: this confirms receipt for processing, not
that encryption has completed. The client can poll
`GET /mypass-manager/files/bucket/{bucketUuid}/{key}/info`; the file can be
downloaded only after `ready=true`.

The processing states include `UPLOAD_RECEIVED`, `ENCRYPTING`, `FINALIZING`,
`READY` and `FAILED`. On success, File Service stores the final object in S3
and a copy in GridFS. Reads try S3 first and fall back to GridFS. Updating an
existing file uses the same asynchronous processing flow.

Events use the `files.events` exchange and the routing keys
`file.encryption.requested`, `file.encryption.completed` and
`file.encryption.failed`. Password Service reads the staged original and
encrypts it with the vault's public key. File Service marks the record ready
only after completing the final object and GridFS writes.

## Related implementation

| Area | Source |
|---|---|
| Upload, file metadata and reads | [MongoGridFSStorageFileServiceImpl](../mhpasswordmanager-file-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/fileservice/internal/impl/MongoGridFSStorageFileServiceImpl.java) |
| Staging, events and finalization | [FileProcessingWorkerQueue](../mhpasswordmanager-file-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/fileservice/amqp/queues/FileProcessingWorkerQueue.java) |
| Encryption worker | [FileEncryptionWorkerQueue](../mhpasswordmanager-password-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/passwordservice/amqp/queues/FileEncryptionWorkerQueue.java) |
| HTTP endpoints | [BucketFileOperationsController](../mhpasswordmanager-file-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/fileservice/http/rest/BucketFileOperationsController.java) |
| Exchange, queues and bindings | [File Service RabbitConfig](../mhpasswordmanager-file-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/fileservice/configs/RabbitConfig.java), [Password Service RabbitConfig](../mhpasswordmanager-password-service/src/main/java/br/com/marcoshssilva/mhpasswordmanager/passwordservice/configs/RabbitConfig.java) |

