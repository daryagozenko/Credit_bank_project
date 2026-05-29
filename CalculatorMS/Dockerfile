FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -pl CalculatorMS

FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY --from=builder /build/CalculatorMS/target/calculator-0.0.1-SNAPSHOT.jar calculator.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "calculator.jar", "--spring.profiles.active=docker"]
