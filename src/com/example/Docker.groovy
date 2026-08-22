#!/user/bin/env groovy
package com.example

class Docker implements Serializable {
    def script

    Docker(script) {
        this.script = script
    }

    def buildDockerImage (String imageName) {
        script.echo "building the docker image for branch '${script.BRANCH_NAME}'..."
        script.sh "docker build -t $imageName ."
    }

    def dockerLogin() {
        script.echo "logging in to docker..."
        script.withCredentials([script.usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
            script.sh "echo '${script.PASS}' | docker login -u '${script.USER}' --password-stdin"
        }
    }

    def dockerPushImage(String imageName) {
        script.echo "pushing the docker image for branch '${script.BRANCH_NAME}' to docker private repository..."
        script.sh "docker build -t $imageName ."
    }
}