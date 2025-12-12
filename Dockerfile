FROM eclipse-temurin:21-jre
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "/app.jar"]