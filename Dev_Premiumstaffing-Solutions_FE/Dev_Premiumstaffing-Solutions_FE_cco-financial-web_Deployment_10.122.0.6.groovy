pipeline {
 agent {
        label 'dev-rvsingh'
    }
 environment {
  GIT_CREDENTIALS_ID = 'New-Github-Jenkins-Cred-2025'
  GIT_REPO = 'https://github.com/FlyNaut-Dev/cco-financial-web.git'
  GIT_BRANCH = 'Development'
  DOCKER_HUB_REPO = 'hub.flynautstaging.com/cco-dev/test'
  DOCKER_IMAGE_TAG = 'latest'
  DOCKER_REGISTRY_URL = 'https://hub.flynautstaging.com'
  DOCKER_CREDENTIALS_ID = 'DockerHub'
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

    stage('Build Docker Image') {
         steps {
             script {
                def image = docker.build("${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}")
             }
          }
      }

         stage('Push Docker Image to Docker Registry') {
            steps {
                script {
                    docker.withRegistry("${DOCKER_REGISTRY_URL}", "${DOCKER_CREDENTIALS_ID}") {
                        def image = docker.image("${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}")
                        image.push()
                    }
                }
            }
        }


 
}
