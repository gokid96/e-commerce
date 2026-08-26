FROM eclipse-temurin:21-jre
COPY build/libs/order.jar order.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "order.jar"]
