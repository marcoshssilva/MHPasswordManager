pipeline {
    agent any
    stages {
        stage('Compile and Test Eureka Server') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry"){
                    sh "mvn clean test package"
                }
            }    
        }
        stage('Compile and Test OAuth2-Authorization Server') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver"){
                    sh "mvn clean test package"
                }
            }
        }
        stage('Compile and Test User-Service-Api') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service"){
                    sh "mvn clean test package"
                }
            }
        }
        stage('Compile and Test Password-Service-Api') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service"){
                    sh "mvn clean test package"
                }
            }
        }
        stage('Compile and Test Api-Gateway') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway"){
                    sh "mvn clean test package"
                }
            }
        }
        stage('Compile and Test Flyway-Database-Migrations') {
            steps{
                dir("${env.WORKSPACE}/mhpasswordmanager-flyway-database-migrations"){
                    sh "mvn clean test package"
                }
            }
        }
    }
}