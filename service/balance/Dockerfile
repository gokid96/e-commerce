FROM eclipse-temurin:21-jre
COPY build/libs/balance.jar balance.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "balance.jar"]
