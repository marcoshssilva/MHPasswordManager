def projectName = 'mhpassword-manager'
def projectFolders = ['mhpasswordmanager-service-registry', 'mhpasswordmanager-config-services', 'mhpasswordmanager-api-gateway', 'mhpasswordmanager-oauth2-authorizationserver', 'mhpasswordmanager-user-service', 'mhpasswordmanager-password-service', 'mhpasswordmanager-email-service', 'mhpasswordmanager-file-service']

def deployArtifactWithMaven(String dir) {
    sh "cd ${dir} && mvn deploy -Dmaven.test.skip=true && cd .."
}

pipeline {
    agent any
    stages {
        stage('Eureka Server - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
                    stash includes: "target/*.jar", name: "MHPasswordManager-Service-Discovery"
                }
            }
        }
        stage('Config-Services - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-config-services") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-ConfigServices'
                    stash includes: "target/*.jar", name: "MHPasswordManager-ConfigServices"
                }
            }
        }
        stage('API-Gateway - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-API-Gateway'
                    stash includes: "target/*.jar", name: "MHPasswordManager-API-Gateway"
                }
            }
        }
        stage('OAuth2-Server - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
                    stash includes: "target/*.jar", name: "MHPasswordManager-OAuth2-Authorization-Server"
                }
            }
        }
        stage('User-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-UserService'
                    stash includes: "target/*.jar", name: "MHPasswordManager-UserService"
                }
            }
        }
        stage('Password-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
                    stash includes: "target/*.jar", name: "MHPasswordManager-PasswordService"
                }
            }
        }
        stage('File-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-file-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-FileService'
                    stash includes: "target/*.jar", name: "MHPasswordManager-FileService"
                }
            }
        }
        stage('Email-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-email-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-EmailService'
                    stash includes: "target/*.jar", name: "MHPasswordManager-EmailService"
                }
            }
        }
        stage('Deploy artifacts in Nexus Registry') {
            steps {
                script {
                    dir("${env.WORKSPACE}") {
                        projectFolders.each { project -> deployArtifactWithMaven(project) }
                    }
                }
            }
        }
    }
}
