# JENKINS CI/CD

## Install Jenkins 

- Create droplet in digitalocean & install docker

```bash
docker run -d -p 8080:8080 -p 50000:50000 --name jenkins -v jenkins_home:/var/jenkins_home jenkins/jenkins
```

to get volume details & mount point

```bash
docker volume inspect <volume-name>
```

## Install Build Tools in Jenkins

- Can either be installed as plugin from jenkins UI, or directly on the server inside the the container
- Install maven from UI, under tools.
- Install "stage view" plugin under "available plugins" section the restart
- Install nodejs directly on the server

```bash
# debug container with root user (-u 0)
docker exec -u 0 -it <container_id> /bin/bash

# once inside the container, check the os distribution
cat /etc/issue

# For debian linux, use below script to download node 
curl -sL https://deb.nodesource.com/setup_20.x -o nodesource_setup.sh
bash nodesource_setup.sh
apt install -y nodejs
node -v 
npm -v
```

## Docker in Jenkins

mount docker run time from host system as a volume inside the the container

```bash
docker run -d -p 8080:8080 -p 50000:50000 --name jenkins -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock jenkins/jenkins
```

update the permission on the file to give jenkins user read & write access to the socket file

```bash
docker exec -u 0 -it jenkins bash
chmod 666 /var/run/docker.sock
ls -l /var/run/docker.sock
```

Install docker inside the container with the following

```bash
curl https://get.docker.com/ > dockerinstall && chmod 777 dockerinstall && ./dockerinstall
```

update docker daemon settings to allow connection to insecure registries (Nexus). In host OS add the following

```bash
vim /etc/docker/daemon.json

{
    "insecure-registries": ["x.x.x.x:8083"]
}
```

then restart docker daemon 

```bash
systemctl restart docker
```

start the jenkins container again & modify the permissions

```bash
docker exec -u 0 -it jenkins bash
chmod 666 /var/run/docker.sock
ls -l /var/run/docker.sock
```

create a private registry on docker hub "demo"

create new jenkins username & password credentials to authenticate to docker hub registry "docker-hub-creds"

update the freestyle job on jenkins to build the docker image and push it to the registry. create new build stage "Execute shell" to build the docker image with the following

```bash
docker build -t username/demo:jma-1.0 .
docker login -u $USERNAME -p $PASSWORD
docker push username/demo:jma-1.0

# if you get a warning that password is passed directly instead of stdin, update the login command 
echo $PASSWORD | docker login -u $USERNAME --password-stdin
```

Under "Environment" select "Use secret text(s) or file(s)" then choose "Username and password (separated)" to allow to the credentials created above

To push image to nexus repository, create "docker-hosted" repo in nexus & add a new credentials in jenkins to connect to nexus. add new "Execute shell" build step with the following to tag the image & push it to nexus

```bash
docker build -t x.x.x.x:8083/java-maven-app:1.0 .
echo $PASSWORD | docker login -u $USERNAME --password-stdin x.x.x.x:8083
docker push x.x.x.x:8083/java-maven-app:1.0
```

## Freestyle to Pipeline jobs

Freestyle is intended to run single job, there might be a freestyle job for testing the app, another for building the project & one for deploying it. It can be chained to run after each other. In "post build" actions you can choose the job to execute once the build is successful for example. Freestyle jobs are limited which led to the creation of pipeline jobs that allow scripting (pipeline as code) your configuration. the scripts are written in Groovy language.

## Intro to Pipeline Jobs

In Jenkins, create new pipeline job & link it to your git repository. in git repository, create "Jenkinsfile" that contains the script to build the pipeline. the pipeline syntax can either be "scripted" which heavily rely on Groovy or "declarative" that is more simpler to get started with. following is sample declarative pipeline

```groovy
pipeline {              // must be top-level
    agent any           // where to execute, agent is more relevant for jenkins cluster that contains multiple nodes (linux, windows, ...etc)

    stages {            // build, test, deploy, ...etc
        stage("build) {
            steps {
                echo 'building...'
            }
        }
    }
}
```

## Jenkins Syntax

