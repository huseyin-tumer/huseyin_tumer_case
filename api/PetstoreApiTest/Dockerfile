FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .

# Download dependencies to be offline-ready (optional but good for caching)
RUN mvn dependency:go-offline -B

COPY src ./src

# Default command matches what we want to run, but can be overridden
CMD ["mvn", "clean" ,"test"]
