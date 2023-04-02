pipeline {
    agent any
    stages {
        stage('Compile - Eureka Server') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    sh "mvn clean install -DskipTests"
                }
            }
        }
        stage('Unit tests - Eureka Server') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    sh "mvn test"
                }
            }
        }
        stage('Deploy SonarQube - Eureka Server') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
                }
            }
        }

        stage('Compile - Api-Gateway') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
                    sh "mvn clean install -DskipTests"
                }
            }
        }
        stage('Unit tests - Api-Gateway') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
                    sh "mvn test"
                }
            }
        }
        stage('Deploy SonarQube - Api-Gateway') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-API-Gateway'
                }
            }
        }

        stage('Compile - OAuth2-Authorization Server') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
                    sh "mvn clean install -DskipTests"
                }
            }
        }
        stage('Unit tests - OAuth2-Authorization Server') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
                    sh "mvn test"
                }
            }
        }
        stage('Deploy SonarQube - OAuth2-Authorization Server') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
                }
            }
        }

        stage('Compile - User-Service-Api') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
                    sh "mvn clean install -DskipTests"
                }
            }
        }
        stage('Unit tests - User-Service-Api') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
                    sh "mvn test"
                }
            }
        }
        stage('Deploy SonarQube - User-Service-Api') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-UserService'
                }
            }
        }

        stage('Compile - Password-Service-Api') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
                    sh "mvn clean install -DskipTests"
                }
            }
        }
        stage('Unit tests - Password-Service-Api') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
                    sh "mvn test"
                }
            }
        }
        stage('Deploy SonarQube - Password-Service-Api') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
                }
            }
        }
    }
}