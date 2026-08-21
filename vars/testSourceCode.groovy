#!/user/bin/env groovy

def call() {
    echo 'testing the application...'
    sh 'mvn test'
}