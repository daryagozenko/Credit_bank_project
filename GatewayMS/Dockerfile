FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY target/gateway-0.0.1-SNAPSHOT.jar gateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "gateway.jar", "--spring.profiles.active=docker"]
