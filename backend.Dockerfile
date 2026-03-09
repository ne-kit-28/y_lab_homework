# Stage 1: Сборка
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# 1. Устанавливаем стартер
COPY audit-logging-spring-boot-starter ./audit-logging-spring-boot-starter
RUN mvn -f audit-logging-spring-boot-starter/pom.xml clean install -DskipTests

# 2. Копируем и собираем бэкенд
COPY homework2 ./homework2
RUN mvn -f homework2/pom.xml clean package -DskipTests

# Stage 2: Запуск
# Stage 2: Запуск
FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /app

# Очищаем папку перед копированием, чтобы там был только один JAR
RUN mkdir -p /app/target

# Копируем файлы, исключая .original (если они есть)
COPY --from=build /build/homework2/target/*.jar /app/target/
RUN rm -f /app/target/*.original  # Удаляем лишнее, если оно попало

# Копируем скрипт запуска
COPY homework2/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8080
RUN ls -lh /app/target
ENTRYPOINT ["/docker-entrypoint.sh"]
