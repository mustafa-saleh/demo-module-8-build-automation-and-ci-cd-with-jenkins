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
                    buildImage 'mustafa199b/demo:jma-3.0'
                    dockerLogin()
                    dockerPush 'mustafa199b/demo:jma-3.0'
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
    }
}
