FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY target/deal-0.0.1-SNAPSHOT.jar deal.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "deal.jar", "--spring.profiles.active=docker"]
