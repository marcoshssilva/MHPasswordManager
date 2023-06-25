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
        stage('Create Docker images for Mongo, Postgres, RabbitMQ and Redis') {
            agent {
                label 'amd64'
            }
            steps {
                // build image for postgres
                dir("${env.WORKSPACE}/postgres"){
                    sh "docker build -t ${project}/postgres:${version} --platform=linux/arm64,linux/amd64 ."
                    deployImageInPrivateRegistry "${project}/postgres","${version}", true
                    sh "docker rmi ${project}/postgres:${version}"
                }

                // build image for redis
                dir("${env.WORKSPACE}/redis"){
                    sh "docker build -t ${project}/redis:${version} --platform=linux/arm64,linux/amd64 ."
                    deployImageInPrivateRegistry "${project}/redis","${version}", true
                    sh "docker rmi ${project}/redis:${version}"
                }

                // build image for mongo-db
                dir("${env.WORKSPACE}/mongo"){
                    sh "docker build -t ${project}/mongo:${version} --platform=linux/arm64,linux/amd64 ."
                    deployImageInPrivateRegistry "${project}/mongo","${version}", true
                    sh "docker rmi ${project}/mongo:${version}"
                }

                // build image for rabbitmq
                dir("${env.WORKSPACE}/rabbitmq"){
                    sh "docker build -t ${project}/rabbitmq:${version} --platform=linux/arm64,linux/amd64 ."
                    deployImageInPrivateRegistry "${project}/rabbitmq","${version}", true
                    sh "docker rmi ${project}/rabbitmq:${version}"
                }

                script {
                    // cleaning docker dangling images
                    try {
                        sh "docker rmi --force \$(docker images -f dangling=true)"
                    } catch(err) {
                        echo "OK. Should have error."
                    }
                }
            }
        }
    }
}
