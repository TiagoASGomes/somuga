FROM maven:3.9.8-amazoncorretto-17-al2023 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean install

FROM amazoncorretto:17-al2023

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/somuga-1.0.jar .

CMD ["java", "-jar", "somuga-1.0.jar"]