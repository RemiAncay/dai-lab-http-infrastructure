FROM alpine:latest
RUN apk add --no-cache openjdk21
WORKDIR /app
COPY target/foosball-api-1.0-jar-with-dependencies.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]