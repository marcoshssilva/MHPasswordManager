/***
 * <h2>Developed by Marcos Henrique de Santana</h2>
 *
 * This Jenkinsfile is only for backup. <br/>
 * The real Jenkins pipeline is loaded directly inside Jenkins. <br/>
 *
 * <br/>
 * <br/>
 *
 * Date: 29/Jun/2023
 */

def projectVersion= '1.0.0-SNAPSHOT'

def projectFolders = [
        "MHPasswordManager-Service-Discovery": 'mhpasswordmanager-service-registry',
        "MHPasswordManager-ConfigServices": 'mhpasswordmanager-config-services',
        "MHPasswordManager-API-Gateway": 'mhpasswordmanager-api-gateway',
        "MHPasswordManager-OAuth2-Authorization-Server": 'mhpasswordmanager-oauth2-authorizationserver',
        "MHPasswordManager-UserService": 'mhpasswordmanager-user-service',
        "MHPasswordManager-PasswordService": 'mhpasswordmanager-password-service',
        "MHPasswordManager-EmailService": 'mhpasswordmanager-email-service',
        "MHPasswordManager-FileService": 'mhpasswordmanager-file-service'
]

def deployArtifactWithMaven(String dir, String key) {
    sh "cd ${dir} && mvn deploy -Dmaven.test.skip=true"
    runSonarQubeWithMavenPlugin key
    sh "cd .."
}

def deployImagesToNexusPrivate(String prefix, String tag) {
    deployImageInPrivateRegistry "${prefix}/api-gateway","${tag}", true
    deployImageInPrivateRegistry "${prefix}/config-services","${tag}", true
    deployImageInPrivateRegistry "${prefix}/oauth2-server","${tag}", true
    deployImageInPrivateRegistry "${prefix}/eureka-server","${tag}", true
    deployImageInPrivateRegistry "${prefix}/user-service","${tag}", true
    deployImageInPrivateRegistry "${prefix}/password-service","${tag}", true
    deployImageInPrivateRegistry "${prefix}/email-service","${tag}", true
    deployImageInPrivateRegistry "${prefix}/file-service","${tag}", true
}

def cleanImages() {
    sh "echo 'Y' || docker system prune --all --volumes"
}

pipeline {
    agent any
    stages {
        stage('Eureka Server - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                    sh "mvn clean test install"
                }
            }
        }
        stage('Config-Services - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-config-services") {
                    sh "mvn clean test install"
                }
            }
        }
        stage('API-Gateway - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway") {
                    sh "mvn clean test install"
                }
            }
        }
        stage('OAuth2-Server - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver") {
                    sh "mvn clean test install"
                }
            }
        }
        stage('User-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-user-service") {
                    sh "mvn clean test install"
                }
            }
        }
        stage('Password-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-password-service") {
                    sh "mvn clean test install"
                }
            }
        }
        stage('File-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-file-service") {
                    sh "mvn clean test install"
                }
            }
        }
        stage('Email-Service - Compile and Tests') {
            steps {
                dir("${env.WORKSPACE}/mhpasswordmanager-email-service") {
                    sh "mvn clean test install"
                }
            }
        }
        stage('Deploy artifacts in Nexus Registry and Sonarqube') {
            when {
                branch 'main'
            }
            steps {
                script {
                    dir("${env.WORKSPACE}") {
                        projectFolders.each { project -> deployArtifactWithMaven("$project.value" as String, "$project.key" as String) }
                    }
                }
            }
        }

        stage('Stash all data') {
            when {
                branch 'main'
            }
            steps {
                stash name: 'MHPasswordManager'
            }
        }

        stage('Create Docker x86/64 images for Mongo, Postgres, RabbitMQ and Redis') {
            agent{
                label 'amd64'
            }
            when {
                branch 'main'
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
                cleanImages()
            }
        }

        stage('Create Docker arm64/v8 images for Mongo, Postgres, RabbitMQ and Redis') {
            agent{
                label 'arm64'
            }
            when {
                branch 'main'
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
                cleanImages()
            }
        }

        stage('Create Docker images x86/64 and arm64/v8') {
            agent{
                label 'amd64'
            }
            when {
                branch 'main'
            }
            steps {
                parallel(
                    'LINUX-X86/64': {
                        node('amd64') {
                            unstash name: 'MHPasswordManager'
                            dir("${env.WORKSPACE}/docker/jenkins") {
                                sh "chmod 0755 ./build-x86-64.sh"
                                sh "./build-x86-64.sh ${projectVersion}"
                            }
                            deployImagesToNexusPrivate(projectName as String, projectVersion as String)
                            cleanImages()
                        }
                    },
                    'LINUX-ARM64/V8': {
                        node('arm64') {
                            unstash name: 'MHPasswordManager'
                            dir("${env.WORKSPACE}/docker/jenkins") {
                                sh "chmod 0755 ./build-arm64.sh"
                                sh "./build-arm64.sh ${projectVersion}"
                            }
                            deployImagesToNexusPrivate("arm64-${projectName}" as String, projectVersion as String)
                            cleanImages()
                        }
                    }
                )
            }
        }

        stage('Deploy in DEV environment') {
            agent{
                label 'ansible-control'
            }
            when {
                branch 'main'
            }
            steps {
                dir("${env.WORKSPACE}/ansible-playbooks") {
//                    withCredentials([usernamePassword(credentialsId: 'ansible-get-raw', usernameVariable: 'NEXUS3_USER', passwordVariable: 'NEXUS3_PASS')])
//                            {
//                                //runAnsiblePlaybook('deploy-dev.yaml', 'NEXUS3_PASSWORD=$NEXUS3_PASS')
//                            }
                    echo "WILL RUNNING ON FUTURE"
                }
            }
        }
    }
}
