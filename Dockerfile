FROM eclipse-temurin:24-jre-alpine
LABEL authors="robin"
LABEL service="teggr/deploy4j-demo:0.0.2-SNAPSHOT"

EXPOSE 8080

# Create a mountpoint for data
VOLUME /data

# Copy the Spring Boot jar into the image
COPY target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
