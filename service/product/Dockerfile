FROM eclipse-temurin:21-jre
COPY build/libs/product.jar product.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "product.jar"]