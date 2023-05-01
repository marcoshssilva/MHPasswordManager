# MHPasswordManager-Config-Services
this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

## Profiles

If you want to use this project in `local` or `docker`, you can use profile `native` to run
this project without extra settings.

You can change in any moment editing in `mhpasswordmanager-config-services/src/main/resources`, there are
configurations for microservices.

Only you can need is run services included in docker:

- redis
- postgres
- mongo
- rabbitmq

### Profile *local* assumes that you will run all apps via Localhost

- Redis will use hostname **localhost** from port 6379
- Postgres will use hostname **localhost** from port 5432
- OAuth2-Authorization Server use hostname **localhost** from port 12010

### Profile *docker* assumes that you will run all apps via host.internal.docker, but remember:

- Redis will use hostname **redis-store** from port 6379
- Postgres will use hostname **postgres-db** from port 5432
- OAuth2-Authorization Server use hostname **host.internal.docker** from port 12010
