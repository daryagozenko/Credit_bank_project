FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY target/statement-0.0.1-SNAPSHOT.jar statement.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "statement.jar", "--spring.profiles.active=docker"]
