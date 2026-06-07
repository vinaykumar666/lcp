FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven && mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S lncp && adduser -S lncp -G lncp
COPY --from=builder /app/target/*.jar app.jar
RUN chown lncp:lncp app.jar

USER lncp
EXPOSE 8081

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
