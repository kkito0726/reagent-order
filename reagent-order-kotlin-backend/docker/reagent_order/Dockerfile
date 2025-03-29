FROM gradle:8.13.0-jdk21 AS builder

COPY . /home/gradle/project

WORKDIR /home/gradle/project
RUN gradle :reagent_order:build --no-daemon -x test

FROM openjdk:21-jdk-slim

COPY --from=builder /home/gradle/project/reagent_order/build/libs/reagent_order-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
