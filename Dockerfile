FROM eclipse-temurin:21-jre
COPY build/libs/e-commerce-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]