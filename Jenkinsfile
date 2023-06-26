def projectName    = 'mhpassword-manager'
def projectVersion = '1.0.0-SNAPSHOT'
def projectFolders = ['mhpasswordmanager-service-registry', 'mhpasswordmanager-config-services', 'mhpasswordmanager-api-gateway', 'mhpasswordmanager-oauth2-authorizationserver', 'mhpasswordmanager-user-service', 'mhpasswordmanager-password-service', 'mhpasswordmanager-email-service', 'mhpasswordmanager-file-service']

def deployArtifactWithMaven(String dir) {
    sh "cd ${dir} && mvn deploy -Dmaven.test.skip=true && cd .."
}

def deployImagesArm64(String dir, String projName, String projVersion, String path) {
    unstash(name: "${dir}")
    sh "cd ${dir} && docker build -t arm64-${projName}/${dir}:${projVersion} ${path}/${dir}/DockerfileJenkinsAmd64 && cd .."
    deployImageInPrivateRegistry "arm64-${projName}/${dir}", "${projVersion}", true
    sh "docker rmi arm64-${projName}/${dir}:${projVersion}"
}

def deployImagesX64(String dir, String projName, String projVersion, String path) {
    unstash(name: "${dir}")
    sh "cd ${dir} && docker build -t ${projName}/${dir}:${projVersion} ${path}/${dir}/DockerfileJenkinsArm64 && cd .."
    deployImageInPrivateRegistry "${projName}/${dir}", "${projVersion}", true
    sh "docker rmi ${projName}/${dir}:${projVersion}"
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
        stage('Config-Services - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-config-services") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-ConfigServices'
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
        stage('OAuth2-Server - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
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
        stage('Password-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
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
        stage('Email-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-email-service") {
                    sh "mvn clean test install"
                    runSonarQubeWithMavenPlugin 'MHPasswordManager-EmailService'
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

        stage('Create Docker x86/64 images for Mongo, Postgres, RabbitMQ and Redis') {
            agent{
                label 'amd64'
            }
            steps {
                // build image for postgres
                dir("${env.WORKSPACE}/postgres"){
                    sh "docker build -t ${projectName}/postgres:${projectVersion} ."
                    deployImageInPrivateRegistry "${projectName}/postgres","${projectVersion}", true
                    sh "docker rmi ${projectName}/postgres:${projectVersion}"
                }

                // build image for redis
                dir("${env.WORKSPACE}/redis"){
                    sh "docker build -t ${projectName}/redis:${projectVersion} ."
                    deployImageInPrivateRegistry "${projectName}/redis","${projectVersion}", true
                    sh "docker rmi ${projectName}/redis:${projectVersion}"
                }

                // build image for mongo-db
                dir("${env.WORKSPACE}/mongo"){
                    sh "docker build -t ${projectName}/mongo:${projectVersion} ."
                    deployImageInPrivateRegistry "${projectName}/mongo","${projectVersion}", true
                    sh "docker rmi ${projectName}/mongo:${projectVersion}"
                }

                // build image for rabbitmq
                dir("${env.WORKSPACE}/rabbitmq"){
                    sh "docker build -t ${projectName}/rabbitmq:${projectVersion} ."
                    deployImageInPrivateRegistry "${projectName}/rabbitmq","${projectVersion}", true
                    sh "docker rmi ${projectName}/rabbitmq:${projectVersion}"
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

        stage('Create Docker arm64/v8 images for Mongo, Postgres, RabbitMQ and Redis') {
            agent{
                label 'arm64'
            }
            steps {
                // build image for postgres
                dir("${env.WORKSPACE}/postgres"){
                    sh "docker build -t arm64-${projectName}/postgres:${projectVersion} ."
                    deployImageInPrivateRegistry "arm64-${projectName}/postgres","${projectVersion}", true
                    sh "docker rmi arm64-${projectName}/postgres:${projectVersion}"
                }

                // build image for redis
                dir("${env.WORKSPACE}/redis"){
                    sh "docker build -t arm64-${projectName}/redis:${projectVersion} ."
                    deployImageInPrivateRegistry "arm64-${projectName}/redis","${projectVersion}", true
                    sh "docker rmi arm64-${projectName}/redis:${projectVersion}"
                }

                // build image for mongo-db
                dir("${env.WORKSPACE}/mongo"){
                    sh "docker build -t arm64-${projectName}/mongo:${projectVersion} ."
                    deployImageInPrivateRegistry "arm64-${projectName}/mongo","${projectVersion}", true
                    sh "docker rmi arm64-${projectName}/mongo:${projectVersion}"
                }

                // build image for rabbitmq
                dir("${env.WORKSPACE}/rabbitmq"){
                    sh "docker build -t arm64-${projectName}/rabbitmq:${projectVersion} ."
                    deployImageInPrivateRegistry "arm64-${projectName}/rabbitmq","${projectVersion}", true
                    sh "docker rmi arm64-${projectName}/rabbitmq:${projectVersion}"
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

        stage('Stash all data') {
            steps {
                stash name: 'MHPasswordManager'
            }
        }

        stage('Create Docker images x86/64 to artifacts') {
            agent{
                label 'amd64'
            }
            steps {
                script {
                    dir("${env.WORKSPACE}") {
                        unstash name: 'MHPasswordManager'
                        projectFolders.each { project -> deployImagesX64(project, projectName, projectVersion, env.WORKSPACE) }
                    }
                }
            }
        }

        stage('Create Docker images arm64/v8 to artifacts') {
            agent{
                label 'amd64'
            }
            steps {
                script {
                    dir("${env.WORKSPACE}") {
                        unstash name: 'MHPasswordManager'
                        projectFolders.each { project -> deployImagesArm64(project, projectName, projectVersion, env.WORKSPACE) }
                    }
                }
            }
        }
    }
}
