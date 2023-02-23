FROM openjdk:18-jdk-slim
ARG JAR_FILE=/home/runner/work/rest-with-spring-boot-and-java-erudio/rest-with-spring-boot-and-java-erudio/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]