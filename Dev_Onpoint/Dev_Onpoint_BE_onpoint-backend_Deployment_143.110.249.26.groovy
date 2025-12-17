pipeline {
    agent {
        label 'dev-rvsingh'
    }

    environment {
        REPONAME              = 'onpoint-backend'
        GIT_CREDENTIALS_ID    = 'Jenkins-Git-Cred'
        GIT_REPO              = "https://github.com/FlyNaut-Dev/onpoint-backend.git"
        GIT_BRANCH            = 'development'

        DOCKER_HUB_REPO       = 'hub.flynautstaging.com/onpoint-dev/onpoint-backend'
        DOCKER_IMAGE_TAG      = 'latest'
        DOCKER_REGISTRY_URL   = 'hub.flynautstaging.com'
        DOCKER_CREDENTIALS_ID = 'DockerHub'

        DEPLOY_SERVER         = '10.122.0.6'
        DEPLOY_USER           = 'ravinderpalsingh'
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

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY_URL}", "${DOCKER_CREDENTIALS_ID}") {
                        docker.image("${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                        docker.image("${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}").push("${BUILD_ID}")
                    }
                }
            }
        }

        stage('Cleanup Local Docker Images') {
            steps {
                sh 'docker image prune -f'
            }
        }

        stage('Docker Login + Deploy on Remote') {
            steps {
                sshagent(['DEV_CREDENTIAL']) {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'DockerHub',
                            usernameVariable: 'REG_USER',
                            passwordVariable: 'REG_PASS'
                        )
                    ]) {
                        sh '''
                        ssh -o StrictHostKeyChecking=no -p ${DEPLOY_PORT} ${DEPLOY_USER}@${DEPLOY_SERVER} "
                        docker compose -f ${COMPOSE_PATH}/docker-compose.yml pull ${REPONAME}
                        docker compose -f ${COMPOSE_PATH}/docker-compose.yml up -d --build ${REPONAME}
                        "
                        '''
                    }
                }
            }
        }

        stage('Remove Unused & Dangling Images on Remote Server') {
            steps {
                sshagent(['DEV_CREDENTIAL']) {
                    script {
                         sh '''
                          ssh -o StrictHostKeyChecking=no -p ${DEPLOY_PORT} ${DEPLOY_USER}@${DEPLOY_SERVER} "docker system prune -a -f"
                         '''
                    }
                }
            }
        }           
        
    }
}

