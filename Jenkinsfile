pipeline {
    agent any
    stages {
        stage('Eureka Server - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    // maven cycle
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
                    sh "mvn deploy -Dmaven.test.skip=true"
                }
            }
        }

        stage('Config-Services - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-config-services") {
                    // maven cycle
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-ConfigServices'
                    sh "mvn deploy -Dmaven.test.skip=true"
                }
            }
        }

        stage('API-Gateway - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway") {
                    // maven cycle
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-API-Gateway'
                    sh "mvn deploy -Dmaven.test.skip=true"
                }
            }
        }

        stage('OAuth2-Authorization-Server - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver") {
                    // maven cycle
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
                    sh "mvn deploy -Dmaven.test.skip=true"
                }
            }
        }

        stage('User-Service - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service") {
                    // maven cycle
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-UserService'
                    sh "mvn deploy -Dmaven.test.skip=true"
                }
            }
        }

        stage('Password-Service - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service") {
                    // maven cycle
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
                    sh "mvn deploy -Dmaven.test.skip=true"
                }
            }
        }

        stage('File-Service - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-file-service") {
                    // maven cycle
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-FileService'
                    sh "mvn deploy -Dmaven.test.skip=true"
                }
            }
        }

        stage('Email-Service - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-email-service") {
                    // maven cycle
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-EmailService'
                    sh "mvn deploy -Dmaven.test.skip=true"
                }
            }
        }
    }
}