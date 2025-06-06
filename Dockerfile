FROM openjdk:17
WORKDIR /app
RUN mkdir -p /app/uploads/temp
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
