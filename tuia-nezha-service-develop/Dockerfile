FROM registry.cn-hangzhou.aliyuncs.com/duiba/duiba-jdk8
MAINTAINER op@duiba.com.cn
ARG appname
ARG environment
ENV LANG en_US.utf8
RUN mkdir /root/duiba-deploy/
ADD ./duiba-deploy /root/duiba-deploy/
ADD ./scripts /root/scripts
#define entry point which will be run first when the container starts up
ENTRYPOINT java $JAVA_OPS -jar /root/duiba-deploy/$appname.jar --spring.profiles.active=$SPRING_ENV --spring.cloud.config.uri=$CONFIG_SERVER