FROM gradle:9.2-jdk25 AS builder
ARG SUB_PROJECT
ARG JAR_NAME
ARG BUILD_IN
ENV BUILD_IN=${BUILD_IN}
USER root
WORKDIR /opt/builder
COPY --chown=gradle:gradle . /opt/builder
RUN gradle clean :${SUB_PROJECT}:build -x test --no-daemon

ARG JAVA_VERSION=25
FROM azul/zulu-openjdk:${JAVA_VERSION}-jre-crac
ARG SUB_PROJECT
ARG JAR_NAME
ENV JAR_NAME=${JAR_NAME}
WORKDIR /opt/app
COPY --from=builder /opt/builder/${SUB_PROJECT}/build/libs/${JAR_NAME} /opt/app/${JAR_NAME}
CMD ["sh", "-c", "java $JAVA_OPTS -jar ${JAR_NAME}"]
