FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/ma-api-1.0.0.jar ma-api.jar
ENTRYPOINT ["java","-jar","/ma-api.jar"]