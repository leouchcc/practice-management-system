FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY backend/pom.xml .
COPY backend/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/practice-management-system-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]