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

# Setup the server first time only
./mvnw deploy4j:setup

# or
deploy4j setup --version 0.0.2-SNAPSHOT

# Deploy the image to a server
./mvnw deploy4j:deploy
```

## Deployment Requirements

* Need a data volume for the database (/data)
* Need to expose port 8080 for the application

## Testing Locally

## Testing

Spin up a local ssh docker container for testing:

```bash
# Running instructions
docker run -d -p 2222:22 --name deploy4j-droplet -v "C:\Users\YOUR_USER\.ssh\id_rsa.pub":/tmp/authorized_keys:ro -v /var/run/docker.sock:/var/run/docker.sock teggr/deploy4j-docker-droplet:latest

# connect via ssh
ssh -o StrictHostKeyChecking=no -p 2222 root@localhost 

# connect to shell
docker exec -it deploy4j-droplet /bin/bash
```