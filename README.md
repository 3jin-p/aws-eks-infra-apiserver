AWS 인프라 구축 테스트용 컨테이너 생성 소스 
---
서버 : EKS (machine : fargate)  
배포 : CodePipeLine, CodeBuild  
특이사항 : 1POD 2Container (fluentd 를 sidecar로 배포)  

### buildspec.yaml  
배포정보 deployment dir 이하.  

``` baash
version: 0.2
phases:
  install:
    runtime-versions:
      docker: 18
    commands:
      - curl -o kubectl https://amazon-eks.s3.us-west-2.amazonaws.com/1.16.12/2020-07-08/bin/linux/amd64/kubectl
      - chmod +x ./kubectl
      - mv ./kubectl /usr/local/bin/kubectl
      - mkdir ~/.kube
      - aws eks --region ap-northeast-2 update-kubeconfig --name ekstest --role-arn <codeBuildArn>
  pre_build:
    commands:
      - echo Logging in to Amazon ECR....
      - aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com
  build:
    commands:
      - echo Build Starting on `date`
      - echo Building with gradle...
      - chmod +x ./gradlew
      - ./gradlew build
      - echo Building the Docker image...
      - docker build -t $IMAGE_REPO_NAME:$IMAGE_TAG .
      - docker tag $IMAGE_REPO_NAME:$IMAGE_TAG $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG
      - docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG
  post_build:
    commands:
      - AWS_ECR_URI=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG
      - DATE=`date`
      - echo Build completed on $DATE
      - sed -i.bak 's#AWS_ECR_URI#'"$AWS_ECR_URI"'#' ./deployment/deployment.yaml
      - kubectl apply -f ./deployment/deployment.yaml
```
---
### fluentd DockerFile  
Fluentd Dir 이하.  
fluentd 설정용 td-agent 파일을 Configmap 으로 등록, Deployment File 수정
``` bash

FROM ubuntu:16.04
RUN apt-get update
RUN ulimit -n 65536
RUN apt-get install -y curl
RUN curl https://packages.treasuredata.com/GPG-KEY-td-agent | apt-key add -
RUN echo "deb http://packages.treasuredata.com/3/ubuntu/xenial/ xenial contrib" > /etc/apt/sources.list.d/treasure-data.list
RUN apt-get update && apt-get install -y -q curl make g++ && apt-get clean && apt-get install -y td-agent && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
RUN sed -i -e "s/USER=td-agent/USER=root/" -e "s/GROUP=td-agent/GROUP=root/" /etc/init.d/td-agent
RUN /usr/sbin/td-agent-gem install fluent-plugin-aws-elasticsearch-service -v 2.2.0
CMD /usr/sbin/td-agent $FLUENTD_ARGS
```
