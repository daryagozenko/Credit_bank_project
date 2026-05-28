FROM eclipse-temurin:21-jre
WORKDIR /credit-bank
COPY target/dossier-0.0.1-SNAPSHOT.jar dossier.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar ", "dossier.jar", "--spring.profiles.active=docker"]
