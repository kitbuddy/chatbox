# Stage 1: Build Backend (Maven)
FROM maven:3.9-eclipse-temurin-17 AS backend-builder
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# Stage 2: Build Frontend (Node.js)
FROM node:18-alpine AS frontend-builder
WORKDIR /app

# Copy frontend files
COPY frontend/package*.json ./
RUN npm ci

COPY frontend/public ./public
COPY frontend/src ./src
COPY frontend/angular.json .
COPY frontend/tsconfig*.json ./

# Build Angular app
RUN npm run build -- --configuration production

# Stage 3: Runtime
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy built JAR from backend stage
COPY --from=backend-builder /app/target/chatbox-1.0-SNAPSHOT.jar app.jar

# Copy built Angular frontend
COPY --from=frontend-builder /app/dist/chatbox-frontend ./src/main/resources/static

# Expose port
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/api/chat/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
