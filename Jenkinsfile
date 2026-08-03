// @Library('my-shared-library')       // _ is not required since the "@Library" declaration is followed by "gv" definition before the pipeline
library identifier: 'my-shared-library@main', retriever: modernSCM([
    $class: 'GitSCMSource',
    remote: 'https://github.com/mustafa-saleh/demo-module-8-jenkins-shared-library.git',
    credentialsId: 'github-repo'
])

def gv

pipeline {
    agent any

    tools {
        maven 'maven-3.9.16'
    }

    stages {
        stage('init') {
            steps {
                script {
                    gv = load 'script.groovy'
                }
            }
        }
        
        stage('increment version') {
            steps {
                script {
                    echo "incrementing the version..."
                    sh 'mvn build-helper:parse-version versions:set \
                    -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                    versions:commit'
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    echo "new version is: ${version}"
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }

        stage('build jar') {
            steps {
                script {
                    // gv.buildJar()
                    buildJar()
                }
            }
        }

        // stage("test") {
        //     steps {
        //         script {
        //             gv.testApp()
        //         }
        //     }
        // }

        stage('build image') {
            when {
                expression {
                    BRANCH_NAME == 'main'
                }
            }

            steps {
                script {
                    // gv.buildImage()
                    buildImage "mustafa199b/demo:jma-${IMAGE_NAME}"
                    dockerLogin()
                    dockerPush "mustafa199b/demo:jma-${IMAGE_NAME}"
                }
            }
        }

        stage('deploy') {
            when {
                expression {
                    BRANCH_NAME == 'main'
                }
            }

            steps {
                script {
                    gv.deployApp()
                }
            }
        }

        stage('commit version update') {
            steps {
                script {
                    echo "incrementing the version..."
                    withCredentials([usernamePassword(credentialsId: 'github-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh 'git config --global user.email "jenkins@example.com"'
                        sh 'git config --global user.name "Jenkins"'

                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/mustafa-saleh/demo-module-8-build-automation-and-ci-cd-with-jenkins.git"
                        sh 'git add .'
                        sh "git commit -m \"ci: Increment version to ${IMAGE_NAME}\""
                        sh 'git push origin HEAD:main'
                    }
                }
            }
        }
    }
}
