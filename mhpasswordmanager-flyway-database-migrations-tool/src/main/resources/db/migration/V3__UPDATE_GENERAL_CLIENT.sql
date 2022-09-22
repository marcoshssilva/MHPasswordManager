UPDATE db_users.oauth2_registered_client
SET redirect_uris = '',
    scopes        = 'user:canWrite,user:canRead,user:canDelete,password:canRead,password:canWrite,password:canDelete',
    client_id     = 'master-client-id'
WHERE id = '57d7425b-9851-41dd-9b56-16f62c3aa294';

UPDATE db_users.oauth2_registered_client
SET scopes = 'user:canWrite,user:canRead,password:canRead,password:canWrite,password:canDelete',
    client_id = 'internal-client-id',
    redirect_uris = 'http://localhost/auth/authorize,https://oidcdebugger.com/debug,http://localhost:4200/auth/authorize,http://localhost:8100/auth/authorize'
WHERE id = '738c62bd-a915-4c18-a6cb-547c5f9037f4';