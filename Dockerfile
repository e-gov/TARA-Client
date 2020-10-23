FROM maven:3.6.3-jdk-8-slim as builder
WORKDIR application
COPY . .
RUN mvn clean package
ENTRYPOINT ["sh", "-c", "java -jar $JAVA_OPTS target/tara-client-jar-with-dependencies.jar"]
