def buildJar() {
    echo 'building the application...'
    sh 'mvn clean package'
}

def testApp() {
    echo 'testing the application...'
    sh 'mvn test'
}

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "docker build -t mustafa199b/demo:${IMAGE_NAME} ."
        sh 'echo $PASS | docker login -u $USER --password-stdin'
        sh "docker push mustafa199b/demo:${IMAGE_NAME}"
    }
}

def deployApp() {
    echo "deploying the application..."
}

return this
