FROM openjdk:21-jdk-slim
EXPOSE 8080
ARG JAR_FILE=GatewayImpl/build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar 
ENTRYPOINT ["java", "-jar", "/application.jar"]
