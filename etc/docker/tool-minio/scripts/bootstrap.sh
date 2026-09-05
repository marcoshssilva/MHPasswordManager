#!/bin/sh
set -eu

mc alias set local "${MINIO_ENDPOINT}" "${MINIO_ROOT_USER}" "${MINIO_ROOT_PASSWORD}"
mc mb --ignore-existing "local/${MINIO_BUCKET}"

cat > /tmp/file-service-policy.json <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:GetBucketLocation", "s3:ListBucket", "s3:ListBucketMultipartUploads"],
      "Resource": ["arn:aws:s3:::${MINIO_BUCKET}"]
    },
    {
      "Effect": "Allow",
      "Action": ["s3:GetObject", "s3:PutObject", "s3:DeleteObject", "s3:AbortMultipartUpload", "s3:ListMultipartUploadParts"],
      "Resource": ["arn:aws:s3:::${MINIO_BUCKET}/*"]
    }
  ]
}
EOF

cat > /tmp/password-service-policy.json <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:GetBucketLocation"],
      "Resource": ["arn:aws:s3:::${MINIO_BUCKET}"]
    },
    {
      "Effect": "Allow",
      "Action": ["s3:ListBucket"],
      "Resource": ["arn:aws:s3:::${MINIO_BUCKET}"],
      "Condition": {
        "StringLike": {
          "s3:prefix": ["staging/*", "encrypted/*"]
        }
      }
    },
    {
      "Effect": "Allow",
      "Action": ["s3:GetObject"],
      "Resource": ["arn:aws:s3:::${MINIO_BUCKET}/staging/*"]
    },
    {
      "Effect": "Allow",
      "Action": ["s3:PutObject", "s3:AbortMultipartUpload", "s3:ListMultipartUploadParts"],
      "Resource": ["arn:aws:s3:::${MINIO_BUCKET}/encrypted/*"]
    }
  ]
}
EOF

mc admin policy create local file-service-policy /tmp/file-service-policy.json
mc admin policy create local password-service-policy /tmp/password-service-policy.json

mc admin user info local "${MINIO_FILE_SERVICE_ACCESS_KEY}" >/dev/null 2>&1 || \
  mc admin user add local "${MINIO_FILE_SERVICE_ACCESS_KEY}" "${MINIO_FILE_SERVICE_SECRET_KEY}"
mc admin user info local "${MINIO_PASSWORD_SERVICE_ACCESS_KEY}" >/dev/null 2>&1 || \
  mc admin user add local "${MINIO_PASSWORD_SERVICE_ACCESS_KEY}" "${MINIO_PASSWORD_SERVICE_SECRET_KEY}"

mc admin policy attach local file-service-policy --user "${MINIO_FILE_SERVICE_ACCESS_KEY}"
mc admin policy attach local password-service-policy --user "${MINIO_PASSWORD_SERVICE_ACCESS_KEY}"
