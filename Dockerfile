FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=/build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} yeonieum_productservice.jar
ENTRYPOINT ["java","-jar","/yeonieum_productservice.jar"]