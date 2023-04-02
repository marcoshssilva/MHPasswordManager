pipeline {
    agent {
        label 'mestre'
    }
    stages {
        stage('Eureka Server - Compile, Tests and Deploy') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    sh "mvn clean install -DskipTests"
                    sh "mvn test"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
                }
            }
        }

        stage('API-Gateway - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
                    sh "mvn clean install -DskipTests"
                    sh "mvn test"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-API-Gateway'
                }
            }
        }

        stage('OAuth2-Authorization-Server - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
                    sh "mvn clean install -DskipTests"
                    sh "mvn test"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
                }
            }
        }

        stage('User-Service - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
                    sh "mvn clean install -DskipTests"
                    sh "mvn test"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-UserService'
                }
            }
        }

        stage('Password-Service - Compile, Tests and Deploy') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
                    sh "mvn clean install -DskipTests"
                    sh "mvn test"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
                }
            }
        }
    }
}