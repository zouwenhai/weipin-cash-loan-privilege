FROM registry.gs.youyuwo.com/base/docker-java-base:jdk_8

COPY yofishdk-cash-loan-privilege-1.0-SNAPSHOT.jar app.jar

ENV PATH="${JAVA_HOME}/bin:${PATH}"
VOLUME "/opt/export"

CMD ["java","-jar","app.jar"]