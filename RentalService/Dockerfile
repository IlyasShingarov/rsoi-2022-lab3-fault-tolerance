FROM openjdk:21-jdk
EXPOSE 8060
ARG JAR_FILE=RentalServiceImpl/build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar 
ENTRYPOINT ["java", "-jar", "/application.jar"]
