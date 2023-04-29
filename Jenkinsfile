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
                    sh "docker build -t ${project}/service-registry:${version} -f ./DockerfileJenkins ."
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
                    sh "docker build -t ${project}/api-gateway:${version} -f ./DockerfileJenkins ."
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
                    sh "docker build -t ${project}/oauth2-authorization-server:${version} -f ./DockerfileJenkins ."
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
                    sh "docker build -t ${project}/user-service:${version} -f ./DockerfileJenkins ."
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
                    sh "docker build -t ${project}/password-service:${version} -f ./DockerfileJenkins ."
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
                    sh "docker build -t ${project}/config-services:${version} -f ./DockerfileJenkins ."
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
                    sh "docker build -t ${project}/file-service:${version} -f ./DockerfileJenkins ."
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
                    sh "docker build -t ${project}/email-service:${version} -f ./DockerfileJenkins ."
                }
            }
        }

        stage('Docker - Build and Cleaning images') {
            steps{
                dir("${env.WORKSPACE}/postgres"){
                    sh "docker build -t ${project}/postgres:${version} ."
                }
                dir("${env.WORKSPACE}/redis"){
                    sh "docker build -t ${project}/redis:${version} ."
                }
                dir("${env.WORKSPACE}/mongo"){
                    echo "NOTHING TO COMPILE YET."
                }
                sh "docker rmi --force \$(docker images -f dangling=true)"
            }
        }
    }
}