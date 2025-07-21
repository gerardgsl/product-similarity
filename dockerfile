FROM eclipse-temurin:17-jdk-alpine

# Crea el directorio de la app
WORKDIR /app

# Copia el jar construido por Maven
COPY target/product-similarity-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que corre Spring Boot
EXPOSE 5000

# Ejecuta la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
