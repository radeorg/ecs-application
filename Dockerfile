FROM eclipse-temurin:17-jre-alpine as builder

WORKDIR application
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract && rm app.jar


FROM eclipse-temurin:17-jre-alpine

ARG RTE
ARG CT
ARG APM
ARG JAVA_OPTS

ENV RTE=$RTE
ENV CT=$CT
ENV APM=$APM
#CMD $TIMENOW=$(date +"%Y%m%d%H%M%S") && echo $TIMENOW
#CMD TIMENOW=$(date +"%Y%m%d%H%M%S") && echo $TIMENOW

ENV LANG en_US.UTF-8
ENV JAVA_OPTS=$JAVA_OPTS

ENV TZ=Asia/Shanghai

#RUN apt-get update
#RUN apt-get install -y ssh-client
#RUN apt-get install -y scp
#ENV JAVA_OPTS="-Xms128m -Xmx256m"
#ENV SPRING_PROFILE="-Dspring.profiles.active=${RTE}"
#配置开启debug
ENV JAVA_DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
#增加dump
#ENV JAVA_DUMP="-XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=/dows/wes/wes-tenant/logs/dump-${CT}.hprof"

#ENV SPRING_RUN="org.springframework.boot.loader.JarLauncher" 3.2之后改包了
ENV SPRING_RUN="org.springframework.boot.loader.launch.JarLauncher"
#RUN apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

#RUN set -xe \
#&& apk --no-cache add ttf-dejavu fontconfig
#RUN echo -e 'https://mirrors.aliyun.com/alpine/v3.6/main/\nhttps://mirrors.aliyun.com/alpine/v3.6/community/' > /etc/apk/repositories \
# && apk update \
# && apk upgrade \
# && apk --no-cache add ttf-dejavu fontconfig

WORKDIR application

#COPY font/simsun.ttf /usr/share/fonts
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS $JAVA_DEBUG $SPRING_RUN"]
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS $APM $JAVA_DEBUG $JAVA_OPTS $SPRING_RUN"]