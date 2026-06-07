# MHPasswordManager-OAuth2-AuthorizationServer
this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

---

## Builtin Profiles

| Profile           | Description                                                    |
|-------------------|----------------------------------------------------------------|
| embedded          | Use a embedded H2 to register oauth-clients and users          |
| test              | Start application using test-mode **exclusive for test units** |

## Users

This application registers the following users unless changed:

| Username      | Password        | Roles                              |
|---------------|-----------------|------------------------------------|
| **admin**     | **P@ssword123** | ROLE_USER, ROLE_ADMIN, ROLE_MASTER |
| **anonymous** | **P@ssword123** | ROLE_USER                          |

## Clients

This application registers the following clients unless changed:

| Client Id                         | Client Secret                          | Redirect URIs                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | Scopes            | Authorization Grant Types           | Token Settings                                                          | Client Settings                           |
|:----------------------------------|:---------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------|:------------------------------------|:------------------------------------------------------------------------|:------------------------------------------|
| **MHPasswordManager**             | `b07968bd-8989-4f10-82c7-3e427e22c924` | • http://127.0.0.1:8080/mypass-manager/auth/swagger-ui/redirect<br>• http://127.0.0.1:8080/mypass-manager/emails/swagger-ui/redirect<br>• http://127.0.0.1:8080/mypass-manager/files/swagger-ui/redirect<br>• http://127.0.0.1:8080/mypass-manager/passwords/swagger-ui/redirect<br>• http://127.0.0.1:8080/mypass-manager/users/swagger-ui/redirect<br>• http://127.0.0.1:8080/mypass-manager/client/authorize<br>• http://127.0.0.1:8080/authorize<br>• http://127.0.0.1:8100/authorize<br>• https://oauth.pstmn.io/v1/callback<br>• https://oidcdebugger.com/debug | profile<br>email  | AUTHORIZATION_CODE<br>REFRESH_TOKEN | ACCESS_TOKEN_TTL: 15m<br>REUSE_REFRESH: False<br>REFRESH_TOKEN_TTL: 24h | REQ_AUTH_CONSENT: True<br>REQ_PKCE: False |
| **MHPasswordManager-GlobalAdmin** | `9f45dc98-8e4f-11ee-b9d1-0242ac120002` | -                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | global:fullAccess | CLIENT_CREDENTIALS                  | ACCESS_TOKEN_TTL: 3m                                                    | none                                      |
