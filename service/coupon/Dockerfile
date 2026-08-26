FROM eclipse-temurin:21-jre
COPY build/libs/coupon.jar coupon.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "coupon.jar"]
