FROM eclipse-temurin:17.0.8.1_1-jre-alpine
VOLUME /tmp
COPY ./build/libs/risk_management-0.0.1-SNAPSHOT.jar /app/risk_management.jar
ENTRYPOINT ["java","-jar","/app/risk_management.jar"]