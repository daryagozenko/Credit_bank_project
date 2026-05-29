FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -pl StatementMS

FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY --from=builder /build/StatementMS/target/statement-0.0.1-SNAPSHOT.jar statement.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "statement.jar", "--spring.profiles.active=docker"]
