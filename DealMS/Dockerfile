FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -pl DealMS

FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY --from=builder /build/DealMS/target/deal-0.0.1-SNAPSHOT.jar deal.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "deal.jar", "--spring.profiles.active=docker"]
