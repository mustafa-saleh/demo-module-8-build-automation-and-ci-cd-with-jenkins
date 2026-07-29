# Build Automation & CI/CD with Jenkins

This project is for the DevOps Bootcamp demo for:

Containers with Docker - [DevOps Bootcamp](https://techworld-with-nana.teachable.com/p/devops-bootcamp)

## Demo Project

- Install Jenkins on DigitalOcean
- Create a CI Pipeline with Jenkinsfile (Freestyle, Pipeline, Multibranch Pipeline)
- Create a Jenkins Shared Library
- Configure Webhook to trigger CI Pipeline automatically on every change
- Dynamically Increment Application version in Jenkins Pipeline

### Technologies used

Jenkins, Groovy, Docker, GitLab, Git, DigitalOcean, Linux, Java, Maven

### Project Description

- Create an Ubuntu server on DigitalOcean
- Set up and run Jenkins as Docker container
- Initialize Jenkins
- 

### Implementation

#### Install & Initialize Jenkins on DigitalOcean

In DigitalOcean, create a new droplet (Server). Install docker & pull the latest Jenkins image from docker hub

```bash
ssh root@x.x.x.x        # x.x.x.x is public server ip

apt update 
apt install -y docker.io

# pull jenkins image & run the container
docker run -d -p 8080:8080 -p 50000:50000 --name jenkins -v jenkins_home:/var/jenkins_home jenkins/jenkins
```

Navigate to the browser "http://x.x.x.x:8080" & login with the default credentials then update the password

```bash
docker volume ls

# to get volume details & mount point
docker volume inspect jenkins_home

# print the default password for login, username is admin
cat /var/lib/docker/volumes/jenkins_home/_data/secrets/initialAdminPassword
```

Once logged in successfully, Install & configure the build tools, credentials & plugins that will be used in Jenkins pipelines. Build tools can either be installed as plugins or directly on the server which can then be invoked as shell commands in the pipeline.

- Under "Settings" -> "Plugins" -> "available plugins", install "stage view" plugin (useful to see the progress of each pipeline stage)
- Under "Settings" -> "Tools" we can configure maven, gradle & nodejs as build tools to be available during run time in the pipeline
- We can also install nodejs directly on the server as an alternative with the following

```bash
docker exec -u 0 -it jenkins /bin/bash

# once inside the container, check the os distribution
cat /etc/issue

# For debian linux, use below script to download node 
curl -sL https://deb.nodesource.com/setup_20.x -o nodesource_setup.sh
bash nodesource_setup.sh
apt install -y nodejs
node -v 
npm -v
```

To use docker to build the images in the pipeline, mount docker run time from host system as a volume inside the the container

```bash
docker run -d -p 8080:8080 -p 50000:50000 --name jenkins -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock jenkins/jenkins
```

Install docker inside the container with the following

```bash
# -u 0 to execute bash as root user
docker exec -u 0 -it jenkins bash

# fetch & install docker 
curl https://get.docker.com/ > dockerinstall && chmod 777 dockerinstall && ./dockerinstall
```

update the permission on the socket file to give jenkins user read & write access to the socket file

```bash
docker exec -u 0 -it jenkins bash
chmod 666 /var/run/docker.sock
ls -l /var/run/docker.sock
```

Now docker command can be used as a shell command in the pipeline to build docker images & push to the remote registries.

