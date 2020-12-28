FROM adoptopenjdk/openjdk11:jre-11.0.7_10-alpine

ENV APP_HOME /app
RUN mkdir $APP_HOME

WORKDIR $APP_HOME

RUN mkdir  $APP_HOME/log
VOLUME  $APP_HOME/log

ADD /build/libs/sj-1.jar $APP_HOME/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
