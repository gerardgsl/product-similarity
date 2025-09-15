# ====== Build stage ======
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom primero para cachear dependencias
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Ahora el código
COPY src ./src
# Si tu OpenAPI genera código, asegúrate de incluir lo necesario
RUN mvn -B -q -DskipTests package

# ====== Runtime stage (slim JRE) ======
FROM eclipse-temurin:21-jre AS runtime
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
WORKDIR /app

# Copiamos el jar final (ajusta nombre si tienes otro finalName)
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 5000
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
