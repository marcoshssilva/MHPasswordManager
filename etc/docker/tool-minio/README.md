# MHPasswordManager-MinIO

Local S3-compatible object storage used by the file upload flow.

- S3 API: `http://localhost:9000`
- Console: `http://localhost:9001`

Credentials are configured in `etc/env/MINIO.env`. The data is persisted in
the `data-minio` Docker volume.

`minio-bootstrap` creates the private `mhp-files` bucket and two scoped users:

- `file-service`: full access to that bucket, including generation of signed URLs;
- `password-service`: read access only to `staging/` and write access only to
  `encrypted/`.

The service-specific development settings are in `MINIO-FILE-SERVICE.env` and
`MINIO-PASSWORD-SERVICE.env`. They are not yet consumed until the services gain
the S3 storage implementation.
