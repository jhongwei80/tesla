FROM openjdk:11.0.3-jre-stretch

RUN apt-get update && apt-get install -y wget

ENV DOCKERIZE_VERSION v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

ADD target/tesla-ops-1.0.0.jar /root/app.jar
ADD bin/start.sh /root/
RUN chmod +x /root/*.sh;mkdir /root/logs
ENV APP_NAME tesla-ops
ENV JAVA_OPTS ""
ENV LANG C.UTF-8
WORKDIR /root
EXPOSE 8080
CMD ["./start.sh"]