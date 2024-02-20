@echo off

SET DB_USERNAME=sas
SET DB_PASSWORD=sas12345678
SET DB_URL=jdbc:postgresql://localhost:5432/sas
SET REST_SERVER_PORT=8990
SET CONTEXT_PATH=/api
SET JWT_SECRET_ID=sas-id-jwt
SET JWT_SECRET_KEY=sas-secretKey-We345ifu
SET JWT_EXPIRATION=3600000
SET JPA_SHOW_SQL=false
SET JPA_FORMAT_SQL=true
SET JPA_GENERATE_SCHEMA=validate
SET PATHS_ALLOWED=/actuator/**,/v2/api-docs,/configuration/ui,/swagger-resources/**,/configuration/**,/swagger-ui/**,/webjars/**,/h2-console/**,/sso/**,/users/security/**
SET LOGGER_LEVEL=debug
SET JPA_BATCH_SIZE=5
SET SECURITY_CIPHER_SECRET=mYSecretK31

java -jar C:\opt\sas-admin\sas-admin-api-0.0.1-SNAPSHOT.jar