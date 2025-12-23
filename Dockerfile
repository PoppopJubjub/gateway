FROM eclipse-temurin:17-jre

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 18000

ENTRYPOINT ["java","-jar","/app.jar"]