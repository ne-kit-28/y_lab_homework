## Build stage: compile the starter using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml pom.xml
COPY src ./src
RUN mvn -B -DskipTests package

## Run stage: provide a runnable image (starter is a library by default)
FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /app
COPY --from=build /build/target/*.jar /app/starter.jar
RUN mkdir -p /opt/starter && cp /app/starter.jar /opt/starter/
CMD ["bash", "-lc", "echo Starter image ready; sleep infinity"]
