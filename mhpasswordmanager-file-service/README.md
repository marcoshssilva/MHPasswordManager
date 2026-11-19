# MHPasswordManager-File-Service

The File Service manages files associated with vaults using MongoDB and
S3-compatible storage. It coordinates asynchronous file encryption with the
Password Service through RabbitMQ and stores encrypted files in S3 and GridFS.
Uploaded originals are staged in S3 before encryption.

this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))