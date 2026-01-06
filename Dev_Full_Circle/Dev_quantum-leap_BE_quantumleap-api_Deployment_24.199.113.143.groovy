pipeline {
     //agent any
    agent {
        label 'dev-rvsingh'
    }

    environment {
        REPONAME              = 'quantumleap-api'
        GIT_CREDENTIALS_ID    = 'Jenkins-Git-Cred'
        GIT_REPO              = "https://github.com/FlyNaut-Dev/quantumleap-api.git"
        GIT_BRANCH            = 'Hotfix'

        DOCKER_HUB_REPO       = 'hub.flynautstaging.com/quantumleap-dev/quantumleap-api'
        DOCKER_IMAGE_TAG      = 'latest'
        DOCKER_REGISTRY_URL   = 'hub.flynautstaging.com'
        DOCKER_CREDENTIALS_ID = 'DockerHub'

        DEPLOY_SERVER         =  '24.199.113.143'
        DEPLOY_USER           = 'root'
        DEPLOY_PORT           = '22'
        COMPOSE_PATH          = '/var/www/dev-project'
        CONTAINER_NAME        = "${REPONAME}"
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Clone Repository') {
            steps {
                git branch: "${GIT_BRANCH}",
                    credentialsId: "${GIT_CREDENTIALS_ID}",
                    url: "${GIT_REPO}"
            }
        }


        // stage('Docker Login + Deploy on Remote') {
        //     steps {
        //         sshagent(['Development-Cred']) {
        //             sh '''
        //                 ssh -o StrictHostKeyChecking=no -p ${DEPLOY_PORT} ${DEPLOY_USER}@${DEPLOY_SERVER} "
        //                cd  ${COMPOSE_PATH} && mkdir ravinderpalsinghdheri-2026
        //               //  echo '${REG_PASS}' | docker login ${DOCKER_REGISTRY_URL} -u '${REG_USER}' --password-stdin &&
        //                 // docker compose -f ${COMPOSE_PATH}/docker-compose.yml pull ${REPONAME} &&
        //                 // docker compose -f ${COMPOSE_PATH}/docker-compose.yml up -d ${REPONAME}
        //                 "
        //             '''

        //         }
        //     }
        // }
         
        
    }
}
