# Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build production jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create non-root system user for security
RUN groupadd -r bizsmart && useradd -r -g bizsmart bizsmart
USER bizsmart

# Copy compiled jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render & Railway supply dynamic PORT env var (default 8080)
ENV PORT=8080
EXPOSE ${PORT}

# Memory flags optimized for free-tier containers (512MB RAM limit)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Xss512k", "-jar", "app.jar"]
