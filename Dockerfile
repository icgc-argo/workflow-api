FROM openjdk:11-jdk as builder
WORKDIR /usr/src/app
ADD . .
RUN ./mvnw clean package

FROM openjdk:11-jre-slim
COPY --from=builder /usr/src/app/target/workflow-search-*.jar /usr/bin/workflow-search.jar
RUN adduser --disabled-password --disabled-login --quiet --gecos '' search
USER search
CMD ["java", "-ea", "-jar", "/usr/bin/workflow-search.jar"]
EXPOSE 8082/tcp
