pipelineUsingJava17AndMaven('marcoshssilva/password-manager',
        [
        'ENABLE_DEPENDENCY_CHECK': 'false',
        'ENABLE_SONARQUBE_CHECK': 'false',
        'DEPLOY': 'DOKKU',
        'APP_NAME': 'password-manager',
        'CLEAN_WORKSPACE': 'true',
        'DOKKU_PROJECTS_PIPELINES': [
            { -> dokkuStepsProject('password-manager-eureka', '.', 'pack') },
            { -> dokkuStepsProject('password-manager-api-gateway', '.', 'pack') },
            { -> dokkuStepsProject('password-manager-configserver', '.', 'pack') },
            { -> dokkuStepsProject('password-manager-api-files', '.', 'pack') },
            { -> dokkuStepsProject('password-manager-api-passwords', '.', 'pack') },
            { -> dokkuStepsProject('password-manager-api-users', '.', 'pack') },
            { -> dokkuStepsProject('password-manager-api-emails', '.', 'pack') },
            { -> dokkuStepsProject('password-manager-oauth2-server', '.', 'pack') },
            ]
        ])
