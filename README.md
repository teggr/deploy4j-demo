# deploy4j demo

Example setup for running deploy4j with a standard Spring Boot application.

## Prerequisites

- Java 24
- Maven 3.9+
- Docker

## Build and DEPLOY

* Maven is responsible for building the jar and docker image.

```shell
# Build and test the application
./mvnw clean verify

# Build and deploy the docker image
./mvw clean deploy
```

## Deployment Requirements

* Need a data volume for the database (/data)
* Need to expose port 8080 for the application