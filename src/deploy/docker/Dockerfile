FROM adoptopenjdk/openjdk11

ENV APP_HOME /app
RUN mkdir $APP_HOME

RUN mkdir $APP_HOME/log
VOLUME $APP_HOME/log

WORKDIR $APP_HOME

ADD sj-1.jar app.jar
# 파일이나 디렉토리를 docker image로 복사
EXPOSE 8080
# 외부에 노출할 포트 지정
ENTRYPOINT ["java", "-jar", "app.jar"]
