# MHPasswordManager-MinIO

Local S3-compatible object storage used by the file upload flow.

- S3 API: `http://localhost:9000`
- Console: `http://localhost:9001`

Credentials are configured in `etc/env/MINIO.env`. The data is persisted in
the `data-minio` Docker volume.
