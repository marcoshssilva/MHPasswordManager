def version = "1.0.0-SNAPSHOT"
def project = "mhpasswordmanager"

pipeline {
    agent none
    tools {
        maven 'maven-default'
    }
    stages {
        stage('Eureka Server - Compile, Tests and Deploy') {
            agent{
                label 'node1-ubuntu-amd64'
            }
            steps{
                 // checkout branch
                checkout scm

                // maven cycle
                sh "mvn clean test install"
                runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
                sh "mvn deploy -Dmaven.test.skip=true"
            }
        }
        stage('Generating Docker images and push at Nexus Docker Registry') {
            steps{
                parallel(
                    'ARCH-LINUX-ARM64': {
                        node('node-master-arm64') {
                             // checkout branch
                            checkout scm

                            // build image for postgres
                            dir("${env.WORKSPACE}/postgres"){
                                sh "docker build -t ${project}-arm64/postgres:${version} ."
                                deployImageInPrivateRegistry "${project}-arm64/postgres","${version}"
                                sh "docker rmi ${project}-arm64/postgres:${version}"
                            }

                            // build image for redis
                            dir("${env.WORKSPACE}/redis"){
                                sh "docker build -t ${project}-arm64/redis:${version} ."
                                deployImageInPrivateRegistry "${project}-arm64/redis","${version}"
                                sh "docker rmi ${project}-arm64/redis:${version}"
                            }

                            // build image for mongo-db
                            dir("${env.WORKSPACE}/mongo"){
                                sh "docker build -t ${project}-arm64/mongo:${version} ."
                                deployImageInPrivateRegistry "${project}-arm64/mongo","${version}"
                                sh "docker rmi ${project}-arm64/mongo:${version}"
                            }

                            // build image for rabbitmq
                            dir("${env.WORKSPACE}/rabbitmq"){
                                sh "docker build -t ${project}-arm64/rabbitmq:${version} ."
                                deployImageInPrivateRegistry "${project}-arm64/rabbitmq","${version}"
                                sh "docker rmi ${project}-arm64/rabbitmq:${version}"
                            }

                            script {
                                // cleaning docker dangling images
                                try {
                                    sh "docker rmi --force \$(docker images -f dangling=true)"
                                } catch(err) {
                                    echo "OK. Always return error."
                                }
                            }

                            echo 'OK!'
                        }
                    },
                    'ARCH-LINUX-AMD64': {
                        node('node1-ubuntu-amd64') {
                            // checkout branch
                            checkout scm

                            // build image for postgres
                            dir("${env.WORKSPACE}/postgres"){
                                sh "docker build -t ${project}/postgres:${version} ."
                                deployImageInPrivateRegistry "${project}/postgres","${version}"
                                sh "docker rmi ${project}/postgres:${version}"
                            }

                            // build image for redis
                            dir("${env.WORKSPACE}/redis"){
                                sh "docker build -t ${project}/redis:${version} ."
                                deployImageInPrivateRegistry "${project}/redis","${version}"
                                sh "docker rmi ${project}/redis:${version}"
                            }

                            // build image for mongo-db
                            dir("${env.WORKSPACE}/mongo"){
                                sh "docker build -t ${project}/mongo:${version} ."
                                deployImageInPrivateRegistry "${project}/mongo","${version}"
                                sh "docker rmi ${project}/mongo:${version}"
                            }

                            // build image for rabbitmq
                            dir("${env.WORKSPACE}/rabbitmq"){
                                sh "docker build -t ${project}/rabbitmq:${version} ."
                                deployImageInPrivateRegistry "${project}/rabbitmq","${version}"
                                sh "docker rmi ${project}/rabbitmq:${version}"
                            }

                            script {
                                // cleaning docker dangling images
                                try {
                                    sh "docker rmi --force \$(docker images -f dangling=true)"
                                } catch(err) {
                                    echo "OK. Always return error."
                                }
                            }

                            echo 'OK!'
                        }
                    }
                )
            }
        }

    }
}