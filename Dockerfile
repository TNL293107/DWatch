### Build WAR
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

### Run on Tomcat 9 (javax.servlet.*)
FROM tomcat:9.0-jdk17-temurin

# Railway provides $PORT; Tomcat defaults to 8080, so we rewrite server.xml at runtime.
COPY docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Deploy as / (ROOT)
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/DWatch.war /usr/local/tomcat/webapps/ROOT.war

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
