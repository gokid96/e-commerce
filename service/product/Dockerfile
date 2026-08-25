FROM eclipse-temurin:21-jre
COPY build/libs/payment.jar payment.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "payment.jar"]
