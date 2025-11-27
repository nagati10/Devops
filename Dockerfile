# Use OpenJDK 17 slim (Debian Bullseye)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/student-management-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
