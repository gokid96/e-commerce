FROM eclipse-temurin:21-jre
COPY build/libs/user.jar user.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "user.jar"]
