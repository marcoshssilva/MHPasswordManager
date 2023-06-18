pipeline {
    agent none
    steps {
        stages {
            stage('Eureka Server - Compile, Tests and Deploy') {
                agent {
                    label any
                }
                steps {
                    // maven cycle
                    dir("${env.WORKSPACE}/mhpasswordmanager-service-registry") {
                        sh "mvn clean test install"
//                        runSonarQubeWithMavenPlugin 'MHPasswordManager-Service-Discovery'
//                        sh "mvn deploy -Dmaven.test.skip=true"
                    }
                }
            }

            stage('Config-Services - Compile, Tests and Deploy') {
                agent {
                    label any
                }
                steps {
                    // maven cycle
                    dir("${env.WORKSPACE}/mhpasswordmanager-config-services") {
                        sh "mvn clean test install"
//                        runSonarQubeWithMavenPlugin 'MHPasswordManager-ConfigServices'
//                        sh "mvn deploy -Dmaven.test.skip=true"
                    }
                }
            }

            stage('API-Gateway - Compile, Tests and Deploy') {
                agent {
                    label any
                }
                steps {
                    // maven cycle
                    dir("${env.WORKSPACE}/mhpasswordmanager-api-gateway") {
                        sh "mvn clean test install"
//                        runSonarQubeWithMavenPlugin 'MHPasswordManager-API-Gateway'
//                        sh "mvn deploy -Dmaven.test.skip=true"
                    }
                }
            }

            stage('OAuth2-Authorization-Server - Compile, Tests and Deploy') {
                agent {
                    label any
                }
                steps {
                    // maven cycle
                    dir("${env.WORKSPACE}/mhpasswordmanager-oauth2-authorizationserver") {
                        sh "mvn clean test install"
//                        runSonarQubeWithMavenPlugin 'MHPasswordManager-OAuth2-Authorization-Server'
//                        sh "mvn deploy -Dmaven.test.skip=true"
                    }
                }
            }

            stage('User-Service - Compile, Tests and Deploy') {
                agent {
                    label any
                }
                steps {
                    // maven cycle
                    dir("${env.WORKSPACE}/mhpasswordmanager-user-service") {
                        sh "mvn clean test install"
//                        runSonarQubeWithMavenPlugin 'MHPasswordManager-UserService'
//                        sh "mvn deploy -Dmaven.test.skip=true"
                    }
                }
            }

            stage('Password-Service - Compile, Tests and Deploy') {
                agent {
                    label any
                }
                steps {
                    // maven cycle
                    dir("${env.WORKSPACE}/mhpasswordmanager-password-service") {
                        sh "mvn clean test install"
//                        runSonarQubeWithMavenPlugin 'MHPasswordManager-PasswordService'
//                        sh "mvn deploy -Dmaven.test.skip=true"
                    }
                }
            }

            stage('File-Service - Compile, Tests and Deploy') {
                agent {
                    label any
                }
                steps {
                    // maven cycle
                    dir("${env.WORKSPACE}/mhpasswordmanager-file-service") {
                        sh "mvn clean test install"
//                        runSonarQubeWithMavenPlugin 'MHPasswordManager-FileService'
//                        sh "mvn deploy -Dmaven.test.skip=true"
                    }
                }
            }

            stage('Email-Service - Compile, Tests and Deploy') {
                agent {
                    label any
                }
                steps {
                    // maven cycle
                    dir("${env.WORKSPACE}/mhpasswordmanager-email-service") {
                        sh "mvn clean test install"
//                        runSonarQubeWithMavenPlugin 'MHPasswordManager-EmailService'
//                        sh "mvn deploy -Dmaven.test.skip=true"
                    }
                }
            }
        }
    }
}