FROM openjdk:18-jdk-slim
WORKDIR /home/runner/work/rest-with-spring-boot-and-java-erudio/rest-with-spring-boot-and-java-erudio
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]