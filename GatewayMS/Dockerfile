FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -pl GatewayMS

FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY --from=builder /build/GatewayMS/target/gateway-0.0.1-SNAPSHOT.jar gateway.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "gateway.jar", "--spring.profiles.active=docker"]
