# build the application
FROM eclipse-temurin:17-jdk-focal as build-stage
WORKDIR /workspace/spring-sse
COPY .gradle .gradle
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY src src
RUN ./gradlew build -x test
# run the application
FROM eclipse-temurin:17-jdk-focal
VOLUME /tmp
COPY --from=build-stage /workspace/spring-sse/build/libs/*.jar spring-sse.jar
ENTRYPOINT ["java", "-jar","/spring-sse.jar"]
EXPOSE 8080
