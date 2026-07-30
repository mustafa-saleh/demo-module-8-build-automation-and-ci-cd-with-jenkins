@Library('my-shared-library')       // _ is not required since the "@Library" declaration is followed by "gv" definition before the pipeline
def gv

pipeline {   
    agent any

    tools {
        maven 'maven-3.9.16'
    }

    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }

        stage("build jar") {
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

        stage("build image") {
            when {
                expression { 
                    BRANCH_NAME == 'main'
                }
            }

            steps {
                script {
                    // gv.buildImage()
                    buildImage()
                }
            }
        }         

    //     stage("deploy") {
    //         when {
    //             expression { 
    //                 BRANCH_NAME == 'main'
    //             }
    //         }

    //         steps {
    //             script {
    //                 gv.deployApp()
    //             }
    //         }
    //     }         
    }
}