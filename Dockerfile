# Stage 1 — compilation Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2 — image Tomcat finale
FROM tomcat:10-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=builder /app/target/clubs.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
