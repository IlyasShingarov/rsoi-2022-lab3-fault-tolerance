FROM openjdk:21-jdk
EXPOSE 8070
ARG JAR_FILE=CarServiceImpl/build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar 
ENTRYPOINT ["java", "-jar", "/application.jar"]
