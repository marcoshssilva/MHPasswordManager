# MHPasswordManager-MinIO

Local S3-compatible object storage used by the file upload flow.

The Dockerfiles compile the server and client from pinned upstream Go module
versions because the historical `minio/minio` and `minio/mc` container images
are no longer publicly accessible from the registries tested. This follows the
[upstream source-only distribution](https://github.com/minio/minio#source-only-distribution).
The first build downloads Go dependencies and can take several minutes.
Use the `MINIO_VERSION` and `MC_VERSION` build arguments to change source versions.

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
