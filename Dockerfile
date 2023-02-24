FROM openjdk:17
ARG JAR_FILE=build/libs/calorie-counter.jar
COPY ${JAR_FILE} ./calorie-counter.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java","-jar","./calorie-counter.jar"]