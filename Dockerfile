FROM gradle:9.2-jdk21 AS builder
ARG SUB_MODULE
ARG JAR_NAME
USER root
WORKDIR /opt/builder
COPY --chown=gradle:gradle . /opt/builder
RUN gradle clean :${SUB_MODULE}:build -x test --no-daemon

FROM azul/zulu-openjdk:21-jdk-crac
ARG SUB_MODULE
ARG JAR_NAME
ENV JAR_NAME=${JAR_NAME}
WORKDIR /opt/app
COPY --from=builder /opt/builder/${SUB_MODULE}/build/libs/${JAR_NAME} /opt/app/${JAR_NAME}
CMD java $JAVA_OPTS -jar ${JAR_NAME}
