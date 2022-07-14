# need-help-task-api-service

### common description
This project aims to help people quickly find helpers for different jobs and for me to better understand `webflux`.

### service description
Service for storing basic information. This service built with using reactive library `webflux`. Main database for storing information selected `elasticsearch`. For file storage using `minio`. 

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

# base minio config
file:
  storage:
    endpoint: http://localhost:9000
    port: 9000
    accessKey: TMOmHBBwZzXpjRVq
    secretKey: awi6dzSfPbARMUli23uQN66z8EUyvan6
    secure: false
    bucket-name: flux-test-market-2022
    image-size: 10485760
    file-size: 1073741824
```
*`user-groups` and `credentials` using for checking authorities* 