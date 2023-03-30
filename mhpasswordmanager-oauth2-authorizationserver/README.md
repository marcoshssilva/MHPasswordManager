# MHPasswordManager-OAuth2-AuthorizationServer
this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

---

## Profiles

| Profile           | Description                                                    |
|-------------------|----------------------------------------------------------------|
| in-jdbc-users     | Use a JDBC connection to find and register new users           |
| in-memory-users   | Use a memorized list to find and register new users            |
| in-memory-clients | Use a memorized list fo find and register new oauth-clients    |
| embedded-database | Use a embedded H2 to register oauth-clients and users          |
| test              | Start application using test-mode **exclusive for test units** |

## Users

When in profiles: **in-memory-users** or **embedded-database**.
This application register following users unless changed:

| Username                             | Password          | Roles                               |
|--------------------------------------|-------------------|-------------------------------------|
| **admin@mhpasswordmanager.com**      | **P@ssword123**   | ROLE_USER, ROLE_ADMIN, ROLE_MASTER  |
| **anonymous@mhpasswordmanager.com**  | **P@ssword123**   | ROLE_USER                           |

## Clients

When in profiles: **in-memory-client** or **embedded-database**.
This application register following clients unless changed:

| Client Id                     | Client Secret                        | Scopes                                                                        | Authorization Grant_Types        | TokenSettings                                                                                     | ClientSettings                                             | Redirect URIs                                                                                                                                                                                                                                                                                                                                                        |
|-------------------------------|--------------------------------------|-------------------------------------------------------------------------------|----------------------------------|---------------------------------------------------------------------------------------------------|------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| MHPasswordManager             | fd04f93e-5e4d-4f16-98ae-9247f68d8619 | user:canSelfRead user:canSelfWrite user:canSelfDelete                         | AUTHORIZATION_CODE REFRESH_TOKEN | ACCESS_TOKEN_TIME_TO_LIVE=15Minutes REUSE_REFRESH_TOKENS=False REFRESH_TOKEN_TIME_TO_LIVE=24Hours | REQUIRE_AUTHORIZATION_CONSENT=True REQUIRE_PROOF_KEY=False | https://oidcdebugger.com/debug  https://oauth.pstmn.io/v1/callback  http://127.0.0.1:8100/authorize  http://127.0.0.1:12050/users/swagger-ui/redirect  http://127.0.0.1:12050/passwords/swagger-ui/redirect  http://127.0.0.1:12050/emails/swagger-ui/redirect  http://127.0.0.1:12050/documents/swagger-ui/redirect  http://127.0.0.1:12050/log/swagger-ui/redirect |
| MHPasswordManager-GlobalAdmin | 8e18ee56-ab7c-4ed9-b192-ff4472e5c697 | openid profile email user:canRead user:canWrite user:canDelete user:canCreate | CLIENT_CREDENTIALS               | ACCESS_TOKEN_TIME_TO_LIVE=3Minutes                                                                | none                                                       |