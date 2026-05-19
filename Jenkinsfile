@Library('java') _
pipelineSimpleMavenJavaProject('marcoshssilva/password-manager', 'jdk-17',
        [
        'ENABLE_SONARQUBE_CHECK': 'true',
        'AGENT_EXTRA_LABELS': 'node-builder',
        'DEPLOY': 'DOKKU',
        'APP_NAME': 'password-manager'
        ])
