FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . /app
RUN ./mvnw clean package
CMD ["java", "-cp", "target/ScalableInstallableJavaApp-1.0.0.jar", "com.example.App"]

