# Multi-stage Docker build for BizSmart Spring Boot Backend
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy backend pom.xml and cache dependencies
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B || true

# Copy backend source code and build production jar
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# Runtime Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Non-root user for cloud security
RUN groupadd -r bizsmart && useradd -r -g bizsmart bizsmart
USER bizsmart

# Copy compiled jar
COPY --from=build /app/target/*.jar app.jar

# Dynamic PORT for Render / Railway (defaults to 8080)
ENV PORT=8080
EXPOSE ${PORT}

# Memory optimization for 512MB RAM free-tier containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Xss512k", "-jar", "app.jar"]
