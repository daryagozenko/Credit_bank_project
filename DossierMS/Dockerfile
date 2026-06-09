FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -pl DossierMS

FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY --from=builder /build/DossierMS/target/dossier-0.0.1-SNAPSHOT.jar dossier.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "dossier.jar", "--spring.profiles.active=docker"]
