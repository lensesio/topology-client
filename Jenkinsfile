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

        // Build either 'dev' or 'prod' docker image of API docs. The first can
        // be triggered by a backend job build or manually by user whereas the latter
        // is reserved to be build only manually.
    }

    /* post { */
    /*     failure { */
    /*         script { */
    /*             slackHelper.jobStatus("#dev-ops") */
    /*         } */
    /*     } */
    /* } */
}
