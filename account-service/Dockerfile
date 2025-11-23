# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# First, build and install customer-service modules (customer-dto, customer-client)
COPY customer-service/pom.xml customer-service/
COPY customer-service/customer-domain/pom.xml customer-service/customer-domain/
COPY customer-service/customer-domain/src customer-service/customer-domain/src
COPY customer-service/customer-dto/pom.xml customer-service/customer-dto/
COPY customer-service/customer-dto/src customer-service/customer-dto/src
COPY customer-service/customer-client/pom.xml customer-service/customer-client/
COPY customer-service/customer-client/src customer-service/customer-client/src
COPY customer-service/customer-application/pom.xml customer-service/customer-application/
COPY customer-service/customer-infrastructure/pom.xml customer-service/customer-infrastructure/
COPY customer-service/customer-api/pom.xml customer-service/customer-api/

# Install customer-service modules to local Maven repository
RUN cd customer-service && mvn clean install -pl customer-domain,customer-dto,customer-client -am -DskipTests -B

# Copy account-service poms
COPY account-service/pom.xml .
COPY account-service/account-domain/pom.xml account-domain/
COPY account-service/account-dto/pom.xml account-dto/
COPY account-service/account-application/pom.xml account-application/
COPY account-service/account-infrastructure/pom.xml account-infrastructure/
COPY account-service/account-api/pom.xml account-api/
COPY account-service/account-client/pom.xml account-client/

# Copy account-service source code
COPY account-service/account-domain/src account-domain/src
COPY account-service/account-dto/src account-dto/src
COPY account-service/account-application/src account-application/src
COPY account-service/account-infrastructure/src account-infrastructure/src
COPY account-service/account-api/src account-api/src
COPY account-service/account-client/src account-client/src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR from build stage
COPY --from=build /app/account-api/target/*.jar app.jar

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
