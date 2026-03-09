## Build stage: compile the backend using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml pom.xml
COPY src ./src
RUN mvn -B -DskipTests package

## Run stage: run the built jar
FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /app
COPY --from=build /build/target /app/target
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]
