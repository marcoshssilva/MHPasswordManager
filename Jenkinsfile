/***
 * <h2>Developed by Marcos Henrique de Santana</h2>
 *
 * This Jenkinsfile is only for multi-branch pipeline. <br/>
 * The real Jenkins pipeline is loaded directly inside Jenkins by library. <br/>
 *
 * <br/>
 * <br/>
 *
 * Date: 29/Jun/2023
 */

def projectName = 'mhpasswordmanager'

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

pipelineSecretMyPasswordManager(projectName, projectVersion, projectFolders)