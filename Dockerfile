FROM openjdk:18-jdk-slim
ARG JAR_FILE=rest-with-spring-boot-and-java-erudio/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]