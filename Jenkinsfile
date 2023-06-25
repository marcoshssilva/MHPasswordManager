def projectName = 'mhpassword-manager'
def projectFolders = ['mhpasswordmanager-service-registry', 'mhpasswordmanager-config-services', 'mhpasswordmanager-api-gateway', 'mhpasswordmanager-oauth2-authorizationserver', 'mhpasswordmanager-user-service', 'mhpasswordmanager-password-service', 'mhpasswordmanager-email-service', 'mhpasswordmanager-file-service']

def deployArtifactWithMaven(String dir) {
    dir("${env.WORKSPACE}/${dir}") {
        sh "mvn deploy -Dmaven.test.skip=true"
    }
}

pipeline {
    agent any
    stages {
        stage('Eureka Server - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
                }
            }
        }

        stage('Eureka Server - Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Config-Services - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-config-services") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-ConfigServices'
                }
            }
        }

        stage('Config-Services - Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('API-Gateway - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-API-Gateway'
                }
            }
        }

        stage('API-Gateway - Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('OAuth2-Server - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
                }
            }
        }

        stage('OAuth2-Server - Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('User-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-UserService'
                }
            }
        }

        stage('User-Service - Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Password-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
                }
            }
        }

        stage('Password-Service - Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('File-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-file-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-FileService'
                }
            }
        }

        stage('File-Service - Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Email-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-email-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-EmailService'
                }
            }
        }

        stage('Email-Service - Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy artifacts in Nexus Registry') {
            steps {
                script {
                    projectFolders.each { project ->
                        deployArtifactWithMaven(project)
                    }
                }
            }
        }

    }
}