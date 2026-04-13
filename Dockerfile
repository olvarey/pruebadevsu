FROM eclipse-temurin:17-jdk AS build

ARG SERVICE_NAME

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY account-service/pom.xml account-service/pom.xml
COPY customer-service/pom.xml customer-service/pom.xml

RUN chmod +x mvnw

COPY account-service/src account-service/src
COPY customer-service/src customer-service/src

RUN ./mvnw -B -pl "${SERVICE_NAME}" -am -DskipTests package

FROM eclipse-temurin:17-jre

ARG SERVICE_NAME

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /workspace/${SERVICE_NAME}/target/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081 8082

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
