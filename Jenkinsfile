def NodeRunAMD64 = 'node1-ubuntu-amd64'
def NodeRunARM64 = 'node-master-arm64'
def NodeRunMaven = 'node-master-arm64'

def version = "1.0.0-SNAPSHOT"
def project = "mhpasswordmanager"

pipeline {
    agent none
    tools {
        maven 'maven-default'
    }
    stages {
//        stage('Eureka Server - Compile, Tests and Deploy') {
//            agent{
//                label NodeRunMaven
//            }
//            steps{
//                // maven cycle
//                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry"){
//                    sh "mvn clean test install"
//                    runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
//                    sh "mvn deploy -Dmaven.test.skip=true"
//                }
//            }
//        }
//
//        stage('Config-Services - Compile, Tests and Deploy') {
//            agent{
//                label NodeRunMaven
//            }
//            steps{
//                // maven cycle
//                dir("${env.WORKSPACE}/mhpasswordmanager-config-services"){
//                    sh "mvn clean test install"
//                    runSonarQubeWithMavenPlugin 'MHPasswordManager-ConfigServices'
//                    sh "mvn deploy -Dmaven.test.skip=true"
//                }
//            }
//        }
//
//        stage('API-Gateway - Compile, Tests and Deploy') {
//            agent{
//                label NodeRunMaven
//            }
//            steps{
//                // maven cycle
//                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
//                    sh "mvn clean test install"
//                    runSonarQubeWithMavenPlugin 'MHPasswordManager-API-Gateway'
//                    sh "mvn deploy -Dmaven.test.skip=true"
//                }
//            }
//        }
//
//        stage('OAuth2-Authorization-Server - Compile, Tests and Deploy') {
//            agent{
//                label NodeRunMaven
//            }
//            steps{
//                // maven cycle
//                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
//                    sh "mvn clean test install"
//                    runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
//                    sh "mvn deploy -Dmaven.test.skip=true"
//                }
//            }
//        }
//
//        stage('User-Service - Compile, Tests and Deploy') {
//            agent{
//                label NodeRunMaven
//            }
//            steps{
//                // maven cycle
//                dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
//                    sh "mvn clean test install"
//                    runSonarQubeWithMavenPlugin 'MHPasswordManager-UserService'
//                    sh "mvn deploy -Dmaven.test.skip=true"
//                }
//            }
//        }
//
//        stage('Password-Service - Compile, Tests and Deploy') {
//            agent{
//                label NodeRunMaven
//            }
//            steps{
//                // maven cycle
//                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
//                    sh "mvn clean test install"
//                    runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
//                    sh "mvn deploy -Dmaven.test.skip=true"
//                }
//            }
//        }
//
//        stage('File-Service - Compile, Tests and Deploy') {
//            agent{
//                label NodeRunMaven
//            }
//            steps{
//                // maven cycle
//                dir("${env.WORKSPACE}/mhpasswordmanager-file-service"){
//                    sh "mvn clean test install"
//                    runSonarQubeWithMavenPlugin 'MHPasswordManager-FileService'
//                    sh "mvn deploy -Dmaven.test.skip=true"
//                }
//            }
//        }
//
//        stage('Email-Service - Compile, Tests and Deploy') {
//            agent{
//                label NodeRunMaven
//            }
//            steps{
//                // maven cycle
//                dir("${env.WORKSPACE}/mhpasswordmanager-email-service"){
//                    sh "mvn clean"
//                    sh "mvn test"
//                    sh "mvn install"
//                    runSonarQubeWithMavenPlugin 'MHPasswordManager-EmailService'
//                    sh "mvn deploy -Dmaven.test.skip=true"
//                }
//            }
//        }
//        stage('Stash compiled files') {
//            agent{
//                label NodeRunMaven
//            }
//            steps {
//                stash(name: 'stashedData')
//            }
//        }
//
//        stage('Generating Docker images and push at Nexus Docker Registry') {
//            steps{
//                parallel(
//                    'ARCH-LINUX-ARM64': {
//                        node(NodeRunARM64) {
//                            // get stashed data
//                            unstash(name: 'stashedData')
//
//                            // build image for mhpasswordmanager/service-discovery
//                            dir("${env.WORKSPACE}/mhpasswordmanager-service-registry"){
//                                sh "docker build -t ${project}-arm64/service-registry:${version} -f ./DockerfileJenkinsArm64 ."
//                                deployImageInPrivateRegistry "${project}-arm64/service-registry", "${version}", true
//                                sh "docker rmi ${project}-arm64/service-registry:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/config-services
//                            dir("${env.WORKSPACE}/mhpasswordmanager-config-services"){
//                                sh "docker build -t ${project}-arm64/config-services:${version} -f ./DockerfileJenkinsArm64 ."
//                                deployImageInPrivateRegistry "${project}-arm64/config-services", "${version}", true
//                                sh "docker rmi ${project}-arm64/config-services:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/api-gateway
//                            dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
//                                sh "docker build -t ${project}-arm64/api-gateway:${version} -f ./DockerfileJenkinsArm64 ."
//                                deployImageInPrivateRegistry "${project}-arm64/api-gateway", "${version}", true
//                                sh "docker rmi ${project}-arm64/api-gateway:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/user-service
//                            dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
//                                sh "docker build -t ${project}-arm64/user-service:${version} -f ./DockerfileJenkinsArm64 ."
//                                deployImageInPrivateRegistry "${project}-arm64/user-service", "${version}", true
//                                sh "docker rmi ${project}-arm64/user-service:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/oauth2-authorization-server
//                            dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
//                                sh "docker build -t ${project}-arm64/oauth2-authorization-server:${version} -f ./DockerfileJenkinsArm64 ."
//                                deployImageInPrivateRegistry "${project}-arm64/oauth2-authorization-server", "${version}", true
//                                sh "docker rmi ${project}-arm64/oauth2-authorization-server:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/password-service
//                            dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
//                                sh "docker build -t ${project}-arm64/password-service:${version} -f ./DockerfileJenkinsArm64 ."
//                                deployImageInPrivateRegistry "${project}-arm64/password-service", "${version}", true
//                                sh "docker rmi ${project}-arm64/password-service:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/file-service
//                            dir("${env.WORKSPACE}/mhpasswordmanager-file-service"){
//                                sh "docker build -t ${project}-arm64/file-service:${version} -f ./DockerfileJenkinsArm64 ."
//                                deployImageInPrivateRegistry "${project}-arm64/file-service", "${version}", true
//                                sh "docker rmi ${project}-arm64/file-service:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/email-service
//                            dir("${env.WORKSPACE}/mhpasswordmanager-email-service"){
//                                sh "docker build -t ${project}-arm64/email-service:${version} -f ./DockerfileJenkinsArm64 ."
//                                deployImageInPrivateRegistry "${project}-arm64/email-service", "${version}", true
//                                sh "docker rmi ${project}-arm64/email-service:${version}"
//                            }
//
//                            // build image for postgres
//                            dir("${env.WORKSPACE}/postgres"){
//                                sh "docker build -t ${project}-arm64/postgres:${version} ."
//                                deployImageInPrivateRegistry "${project}-arm64/postgres","${version}", true
//                                sh "docker rmi ${project}-arm64/postgres:${version}"
//                            }
//
//                            // build image for redis
//                            dir("${env.WORKSPACE}/redis"){
//                                sh "docker build -t ${project}-arm64/redis:${version} ."
//                                deployImageInPrivateRegistry "${project}-arm64/redis","${version}", true
//                                sh "docker rmi ${project}-arm64/redis:${version}"
//                            }
//
//                            // build image for mongo-db
//                            dir("${env.WORKSPACE}/mongo"){
//                                sh "docker build -t ${project}-arm64/mongo:${version} ."
//                                deployImageInPrivateRegistry "${project}-arm64/mongo","${version}", true
//                                sh "docker rmi ${project}-arm64/mongo:${version}"
//                            }
//
//                            // build image for rabbitmq
//                            dir("${env.WORKSPACE}/rabbitmq"){
//                                sh "docker build -t ${project}-arm64/rabbitmq:${version} ."
//                                deployImageInPrivateRegistry "${project}-arm64/rabbitmq","${version}", true
//                                sh "docker rmi ${project}-arm64/rabbitmq:${version}"
//                            }
//
//                            script {
//                                // cleaning docker dangling images
//                                try {
//                                    sh "docker rmi --force \$(docker images -f dangling=true)"
//                                } catch(err) {
//                                    echo "OK. Always return error."
//                                }
//                            }
//
//                            echo 'OK!'
//                        }
//                    },
//                    'ARCH-LINUX-AMD64': {
//                        node(NodeRunAMD64) {
//                            // get stashed data
//                            unstash(name: 'stashedData')
//
//                            // build image for mhpasswordmanager/service-discovery
//                            dir("${env.WORKSPACE}/mhpasswordmanager-service-registry"){
//                                sh "docker build -t ${project}/service-registry:${version} -f ./DockerfileJenkinsAmd64 ."
//                                deployImageInPrivateRegistry "${project}/service-registry", "${version}", true
//                                sh "docker rmi ${project}/service-registry:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/config-services
//                            dir("${env.WORKSPACE}/mhpasswordmanager-config-services"){
//                                sh "docker build -t ${project}/config-services:${version} -f ./DockerfileJenkinsAmd64 ."
//                                deployImageInPrivateRegistry "${project}/config-services", "${version}", true
//                                sh "docker rmi ${project}/config-services:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/api-gateway
//                            dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
//                                sh "docker build -t ${project}/api-gateway:${version} -f ./DockerfileJenkinsAmd64 ."
//                                deployImageInPrivateRegistry "${project}/api-gateway", "${version}", true
//                                sh "docker rmi ${project}/api-gateway:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/user-service
//                            dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
//                                sh "docker build -t ${project}/user-service:${version} -f ./DockerfileJenkinsAmd64 ."
//                                deployImageInPrivateRegistry "${project}/user-service", "${version}", true
//                                sh "docker rmi ${project}/user-service:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/oauth2-authorization-server
//                            dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
//                                sh "docker build -t ${project}/oauth2-authorization-server:${version} -f ./DockerfileJenkinsAmd64 ."
//                                deployImageInPrivateRegistry "${project}/oauth2-authorization-server", "${version}", true
//                                sh "docker rmi ${project}/oauth2-authorization-server:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/password-service
//                            dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
//                                sh "docker build -t ${project}/password-service:${version} -f ./DockerfileJenkinsAmd64 ."
//                                deployImageInPrivateRegistry "${project}/password-service", "${version}", true
//                                sh "docker rmi ${project}/password-service:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/file-service
//                            dir("${env.WORKSPACE}/mhpasswordmanager-file-service"){
//                                sh "docker build -t ${project}/file-service:${version} -f ./DockerfileJenkinsAmd64 ."
//                                deployImageInPrivateRegistry "${project}/file-service", "${version}", true
//                                sh "docker rmi ${project}/file-service:${version}"
//                            }
//
//                            // build image for mhpasswordmanager/email-service
//                            dir("${env.WORKSPACE}/mhpasswordmanager-email-service"){
//                                sh "docker build -t ${project}/email-service:${version} -f ./DockerfileJenkinsAmd64 ."
//                                deployImageInPrivateRegistry "${project}/email-service", "${version}", true
//                                sh "docker rmi ${project}/email-service:${version}"
//                            }
//
//                            // build image for postgres
//                            dir("${env.WORKSPACE}/postgres"){
//                                sh "docker build -t ${project}/postgres:${version} ."
//                                deployImageInPrivateRegistry "${project}/postgres","${version}", true
//                                sh "docker rmi ${project}/postgres:${version}"
//                            }
//
//                            // build image for redis
//                            dir("${env.WORKSPACE}/redis"){
//                                sh "docker build -t ${project}/redis:${version} ."
//                                deployImageInPrivateRegistry "${project}/redis","${version}", true
//                                sh "docker rmi ${project}/redis:${version}"
//                            }
//
//                            // build image for mongo-db
//                            dir("${env.WORKSPACE}/mongo"){
//                                sh "docker build -t ${project}/mongo:${version} ."
//                                deployImageInPrivateRegistry "${project}/mongo","${version}", true
//                                sh "docker rmi ${project}/mongo:${version}"
//                            }
//
//                            // build image for rabbitmq
//                            dir("${env.WORKSPACE}/rabbitmq"){
//                                sh "docker build -t ${project}/rabbitmq:${version} ."
//                                deployImageInPrivateRegistry "${project}/rabbitmq","${version}", true
//                                sh "docker rmi ${project}/rabbitmq:${version}"
//                            }
//
//                            script {
//                                // cleaning docker dangling images
//                                try {
//                                    sh "docker rmi --force \$(docker images -f dangling=true)"
//                                } catch(err) {
//                                    echo "OK. Always return error."
//                                }
//                            }
//
//                            echo 'OK!'
//                        }
//                    }
//                )
//            }
//        }

        stage('Deploy dev-environment in SERVER') {
            agent any
            steps {
                dir("${env.WORKSPACE}/ansible-deploy-dev-environment") {
                    runAnsiblePlaybook('deploy-dev.yaml')
                }
            }
        }
    }
}