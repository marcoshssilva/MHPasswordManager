# MHPasswordManager-RabbitMQ
this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

---

## RabbitMQ Default Definitions

### Users

| User                    | Password                     | Virtual Hosts             |
|-------------------------|------------------------------|---------------------------|
| guest                   | guest                        | vhost-password-manager, / |
| email-service-pm        | email-service-pm-P@ss        | vhost-password-manager    |
| authorization-server-pm | authorization-server-pm-P@ss | vhost-password-manager    |

### Queues

| Virtual Host           | Queue                                   | Type    | Autodelete | Durable |
|------------------------|-----------------------------------------|---------|------------|---------|
| vhost-password-manager | email.send-confirmation-registered-user | Classic | False      | True    |
| vhost-password-manager | email.send-simple-email                 | Classic | False      | True    |
| vhost-password-manager | email.send-recovery-code                | Classic | False      | True    |