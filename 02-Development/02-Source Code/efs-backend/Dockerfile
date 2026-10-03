FROM maven:3.9.16-eclipse-temurin-21-noble@sha256:aabe2f8902f17a63be0d223846322678a2d3e62d18ca0aef5648bcb02f6bfe4b AS build

WORKDIR /workspace

COPY pom.xml .

RUN mvn -B -ntp -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -B -ntp -DskipTests clean package


FROM eclipse-temurin:21.0.12_8-jre-noble@sha256:86883d2dc1d0e57d4fb2c539f5fd3a2155749c0bdcdccb9cb452828bdc8b0caf AS runtime

WORKDIR /app

RUN mkdir -p /var/log/efs \
    && chown -R 10001:10001 /app /var/log/efs

COPY --from=build \
    --chown=10001:10001 \
    /workspace/target/efs-backend-0.0.1-SNAPSHOT.jar \
    /app/efs-backend.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/efs-backend.jar"]