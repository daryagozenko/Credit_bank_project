FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY target/calculator-0.0.1-SNAPSHOT.jar calculator.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar ", "calculator.jar", "--spring.profiles.active=docker"]
