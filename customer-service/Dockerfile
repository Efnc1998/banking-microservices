# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy parent pom and all module poms first for better caching
COPY pom.xml .
COPY customer-domain/pom.xml customer-domain/
COPY customer-dto/pom.xml customer-dto/
COPY customer-application/pom.xml customer-application/
COPY customer-infrastructure/pom.xml customer-infrastructure/
COPY customer-api/pom.xml customer-api/
COPY customer-client/pom.xml customer-client/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY customer-domain/src customer-domain/src
COPY customer-dto/src customer-dto/src
COPY customer-application/src customer-application/src
COPY customer-infrastructure/src customer-infrastructure/src
COPY customer-api/src customer-api/src
COPY customer-client/src customer-client/src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR from build stage
COPY --from=build /app/customer-api/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
