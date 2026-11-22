FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/*.jar app.jar
USER 10001:10001
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
