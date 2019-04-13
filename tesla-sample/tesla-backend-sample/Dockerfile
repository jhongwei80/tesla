FROM openjdk:11.0.3-jre-stretch

ADD target/tesla-backend-sample-1.0.0.jar /root/app.jar
ADD bin/start.sh /root/
RUN chmod +x /root/*.sh;mkdir /root/logs
ENV APP_NAME tesla-backend-sample
ENV JAVA_OPTS ""
ENV LANG C.UTF-8
WORKDIR /root
EXPOSE 8902 2181
CMD ["./start.sh"]