pipeline {
 agent {
        label 'dev-rvsingh'
    }
 environment {
        GIT_CREDENTIALS_ID = 'New-Github-Jenkins-Cred-2025'
        GIT_REPO = 'https://github.com/ravinderpalflynaut/development.git'
        GIT_BRANCH = 'Development'
    }
    stages {
        stage('Clone Repository') {
            steps {
                script {
                    git branch: "${GIT_BRANCH}", credentialsId: "${GIT_CREDENTIALS_ID}", url: "${GIT_REPO}"
                }
            }
        }
    }
}