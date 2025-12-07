##
## Build stage
##
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Копируем pom.xml и скачиваем зависимости (оптимизация кэша)
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline

# Копируем исходный код и собираем fat-jar
COPY src ./src
RUN mvn -q -e -B package -DskipTests

##
## Runtime stage
##
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=default

# Копируем собранный jar из стадии сборки
COPY --from=build /app/target/mobile-operator-tariff-management-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Запускаем Spring Boot приложение
ENTRYPOINT ["java", "-jar", "app.jar"]


