FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/somuga-1.0.jar .

ENTRYPOINT ["java", "-jar", "somuga-1.0.jar", "--host", "0.0.0.0"]