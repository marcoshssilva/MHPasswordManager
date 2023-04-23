pipeline {
    agent {
        label 'master'
    }
    stages {
        stage('Eureka Server - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
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
                }
            }
        }

        stage('File-Service - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-FileService'
                }
            }
        }

        stage('Email-Service - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
                    sh "mvn clean"
                    sh "mvn test"
                    sh "mvn install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-EmailService'
                }
            }
        }
    }
}