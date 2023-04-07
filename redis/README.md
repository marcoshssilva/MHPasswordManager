# MHPasswordManager-Redis
this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

---
# ENVIRONMENTS AVAILABLE

| Environment        | Default Value | Available values                                                                     |
|--------------------|---------------|--------------------------------------------------------------------------------------|
| REDIS_LOG_LEVEL    | warning       | debug, verbose, notice, warning                                                      |
| REDIS_REQUIRE_PASS | P@ssword      | Any str you need                                                                     |
| REDIS_SAVE         | 20 1          | Number sequence for persistence, visit https://redis.io/docs/management/persistence/ |
| REDIS_USE_PASS     | 0             | 0 -> Disable required pass, 1 -> Enable required pass to connect                     |