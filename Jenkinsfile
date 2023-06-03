def version = "1.0.0-SNAPSHOT"
def project = "mhpasswordmanager"

pipeline {
    agent {
        label 'master'
    }
    tools {
        maven 'maven-default'
    }
    stages {
        stage('Eureka Server - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
                    sh "mvn deploy -Dmaven.test.skip=true"
                    sh "docker build -t ${project}-arm64/service-registry:${version} -f ./DockerfileJenkins ."
                    deployImageInPrivateRegistry "${project}-arm64/service-registry", "${version}"
                }
            }
        }

        stage('API-Gateway - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-API-Gateway'
                    sh "mvn deploy -Dmaven.test.skip=true"
                    sh "docker build -t ${project}-arm64/api-gateway:${version} -f ./DockerfileJenkins ."
                    deployImageInPrivateRegistry "${project}-arm64/api-gateway", "${version}"
                }
            }
        }

        stage('OAuth2-Authorization-Server - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
                    sh "mvn deploy -Dmaven.test.skip=true"
                    sh "docker build -t ${project}-arm64/oauth2-authorization-server:${version} -f ./DockerfileJenkins ."
                    deployImageInPrivateRegistry "${project}-arm64/oauth2-authorization-server","${version}"
                }
            }
        }

        stage('User-Service - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-UserService'
                    sh "mvn deploy -Dmaven.test.skip=true"
                    sh "docker build -t ${project}-arm64/user-service:${version} -f ./DockerfileJenkins ."
                    deployImageInPrivateRegistry "${project}-arm64/user-service", "${version}"
                }
            }
        }

        stage('Password-Service - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
                    sh "mvn deploy -Dmaven.test.skip=true"
                    sh "docker build -t ${project}-arm64/password-service:${version} -f ./DockerfileJenkins ."
                    deployImageInPrivateRegistry image: "${project}-arm64/password-service", tag: "${version}"
                }
            }
        }

        stage('Config-Services - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-config-services"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-ConfigServices'
                    sh "mvn deploy -Dmaven.test.skip=true"
                    sh "docker build -t ${project}-arm64/config-services:${version} -f ./DockerfileJenkins ."
                    deployImageInPrivateRegistry "${project}-arm64/config-services", "${version}"
                }
            }
        }

        stage('File-Service - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-file-service"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-FileService'
                    sh "mvn deploy -Dmaven.test.skip=true"
                    sh "docker build -t ${project}-arm64/file-service:${version} -f ./DockerfileJenkins ."
                    deployImageInPrivateRegistry "${project}-arm64/file-service", "${version}"
                }
            }
        }

        stage('Email-Service - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-email-service"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-EmailService'
                    sh "mvn deploy -Dmaven.test.skip=true"
                    sh "docker build -t ${project}-arm64/email-service:${version} -f ./DockerfileJenkins ."
                    deployImageInPrivateRegistry "${project}-arm64/service-registry", "${version}"
                }
            }
        }

        stage('Docker - Build and Cleaning images') {
            steps{
                dir("${env.WORKSPACE}/postgres"){
                    sh "docker build -t ${project}-arm64/postgres:${version} ."
                    deployImageInPrivateRegistry "${project}-arm64/postgres","${version}"
                }
                dir("${env.WORKSPACE}/redis"){
                    sh "docker build -t ${project}-arm64/redis:${version} ."
                    deployImageInPrivateRegistry "${project}-arm64/redis","${version}"
                }
                dir("${env.WORKSPACE}/mongo"){
                    sh "docker build -t ${project}-arm64/mongo:${version} ."
                    deployImageInPrivateRegistry "${project}-arm64/mongo","${version}"
                }
                dir("${env.WORKSPACE}/rabbitmq"){
                    sh "docker build -t ${project}-arm64/rabbitmq:${version} ."
                    deployImageInPrivateRegistry "${project}-arm64/rabbitmq","${version}"
                }
                script {
                    try {
                        sh "docker rmi --force \$(docker images -f dangling=true)"
                    } catch(err) {
                        echo "OK. Always return error."
                    }
                }
            }
        }
    }
}