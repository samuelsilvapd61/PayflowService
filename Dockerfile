# ---------- Build ----------
FROM maven:3.9.11-eclipse-temurin-25 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ---------- Runtime ----------
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copia o JAR da sua aplicação
COPY --from=builder /app/target/*.jar app.jar

# Copia a pasta do New Relic (contendo o newrelic.jar e newrelic.yml) para o container
COPY newrelic/ /app/newrelic/

EXPOSE 8888

# Executa a aplicação incluindo a flag do New Relic
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-jar", "app.jar"]
