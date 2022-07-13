# need-help-task-api-service

### common description
This project aims to help people quickly find helpers for different jobs and for me to better understand `webflux`.

### service description
Service for storing basic information. This service built with using reactive library `webflux`. Main database for storing information selected `elasticsearch`.

example application.yaml:
```yaml
app:
  services:
    ext-ui: http://localhost:8081
  user-groups:
    default-group: "NEED_HELP_CLIENT"
    admin-group: "NEED_HELP_ADMIN"
  credentials:
    phone: 12345
    password: 12345
```
*`user-groups` and `credentials` using for checking authorities* 