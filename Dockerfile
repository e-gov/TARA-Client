ARG DOCKERHUB_MIRROR=
FROM ${DOCKERHUB_MIRROR}library/maven:3.6.3-jdk-8-slim as builder
COPY . /application
WORKDIR /application
RUN mvn -s settings.xml clean package

FROM ${DOCKERHUB_MIRROR}library/openjdk:8-jre-slim
COPY --from=builder /application/target/tara-client-jar-with-dependencies.jar /
ENTRYPOINT ["sh", "-c", "java -jar $JAVA_OPTS /tara-client-jar-with-dependencies.jar"]
