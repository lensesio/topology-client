#!/usr/bin/env groovy

// Files used for this Jenkins CICD job:
//.

pipeline {
    agent { label 'docker && ephemeral' }

    libraries { lib('lenses-jenkins-pipeline') }

    options {
        ansiColor('xterm')
        copyArtifactPermission('*')
        buildDiscarder(logRotator(numToKeepStr: '200', artifactNumToKeepStr: '200'))
        parallelsAlwaysFailFast()
        timeout(time: 5, unit: 'MINUTES')
    }

   /* environment { } */

    stages() {
        // Optional stage used during development that can print both env. vars.
        // and load any Jenkinsfile parameters. When used, job will be marked as
        // aborted.
        stage('Setup Environment') {
            steps {
                script {
                    echo 'Setup Environment stage'
                    if (env.DRY_RUN == "true") {
                        sh 'printenv | sort'
                        currentBuild.result = 'ABORTED'
                        error('Stopping initial manually-triggered build as we only want to get the parameters')
                        return
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    docker.image('gradle:5.1-jdk8').inside {
                        sh './gradlew clean build'
                    }
                }
            }
        }

        stage('Publish') {
            steps {
                script {
                    docker.image('gradle:5.1-jdk8').inside {
                        withCredentials([
                            file(credentialsId: 'a3a9a2bd-6c2e-40bf-be0f-1e24f3597d90', variable: 'GRADLE_PROPERTIES'),
                            file(credentialsId: 'e5c260e3-0bc6-4e7d-a4c0-4138d6305a8b', variable: 'SIGNING_GPG_KEY')
                        ]) {
                            sh 'cat $GRADLE_PROPERTIES > gradle.properties '
                            sh 'echo -e "\nsigning.secretKeyRingFile=$SIGNING_GPG_KEY" >> gradle.properties'
                            /* sh './gradlew uploadArchives' */
                            /* sh 'rm -f gradle.properties' */
                        }
                    }
                }
            }
        }

    }

    /* post { */
    /*     failure { */
    /*         script { */
    /*             slackHelper.jobStatus("#dev-ops") */
    /*         } */
    /*     } */
    /* } */
}