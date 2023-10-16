FROM openjdk:20
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} app.jar
CMD ["java", "-jar", "/app.jar"]