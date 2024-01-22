# SAS Admin Microservices

## Prerequisites

- Java 8
- Docker (Lastest version)
  - Docker compose (Include in desktop version)
- Intellij Community Edition (Last version)
  - Lombock plugin

## Build

```bash
## On windows 
gradlew bootBuildImage

## On Linux
sudo ./gradlew bootBuildImage
```

## Run

### Unit Test

For execute unit test, open **_application.yml_** and **turn off flyway** 

```bash
## On windows 
gradlew test
gradlew test --tests ClassName
gradlew test --tests ClassName.methodName

## On Linux
sudo ./gradlew test
sudo ./gradlew test --tests ClassName
sudo ./gradlew test --tests ClassName.methodName
```

### Docker compose

- Create .env file with the next variables:
  - **LOGGER_LEVEL**="\<\<info | debug | warn | error | none\>\>"
  - **JPA_SHOW_SQL**="\<\<true | false\>\>"
  - **JPA_FORMAT_SQL**="\<\<true | false\>\>"
  - **JPA_GENERATE_SCHEMA**="validate"
  - **POSTGRES_USER**="\<\<db_user\>\>"
  - **POSTGRES_PASSWORD**="\<\<db_pswd\>\>"
  - **POSTGRES_DB**="\<\<db_name\>\>"
  - **POSTGRES_INITDB_ARGS**="--encoding=UTF-8 --lc-collate=C --lc-ctype=C"
  - **JPA_BATCH_SIZE**="\<\<any_number\>\>"
  - **IMAGE_SAS_API**="\<\<owner\>\>/sas-admin-api:\<\<version\>\>"
  - **TZ**="America/Mexico_City"
  - **REST_SERVER_PORT**="\<\<service_port\>\>"
  - **CONTEXT_PATH**="/api"
  - **DB_HOST**="postgres-database"
  - **DB_PORT**="5432"
  - **DB_URL**=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${POSTGRES_DB} 
  - **JWT_SECRET_ID**="\<\<jwt_secret_id\>\>"
  - **JWT_SECRET_KEY**="\<\<jwt_secret_key\>\>"
  - **JWT_EXPIRATION**="\<\<expiration_in_miliseconds\>\>"
  - **PATHS_ALLOWED**="/actuator/**,/v2/api-docs,/configuration/ui,/swagger-resources/\*\*,/configuration/\*\*,/swagger-ui/\*\*,/webjars/\*\*,/h2-console/\*\*,/sso/\*\*,/users/security/\*\*"
  - **SECURITY_CIPHER_SECRET**=\<\<secret_phrase\>\>


- Execute docker-compose file
```bash
# Being at root path
docker-compose up

# From another path
docker-compose -f <<proyect_path>>\docker-compose.yml up
```

## Urls

- PhpMyAdmin: http://localhost:8080
- Swagger: http://localhost:8990/api/swagger-ui/

## Clean Docker container

docker container prune -f

## Docker container Postgres

```bash
docker run --network host --name postgres -e POSTGRES_PASSWORD=postgres -d postgres
```

## Docker install fonts in local

```bash
docker exec -u 0 api-service bash -c "apt-get update && apt-get install fontconfig ttf-dejavu -y"