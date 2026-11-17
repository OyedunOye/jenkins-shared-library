# Jenkins Shared Library

A Jenkins [shared library](https://www.jenkins.io/doc/book/pipeline/shared-libraries/) providing reusable pipeline steps for building, testing, and containerizing a Java/Maven application.

## Structure

```
.
├── src/com/example/
│   └── Docker.groovy       # Shared logic for Docker build/login/push operations
└── vars/
    ├── buildJar.groovy     # Builds the Maven project (mvn clean package)
    ├── testSourceCode.groovy  # Runs the test suite (mvn test)
    ├── buildImage.groovy   # Builds a Docker image
    ├── dockerLogin.groovy  # Logs in to the Docker registry
    └── pushImage.groovy    # Pushes a Docker image to the registry
```

- **`vars/`** contains the global pipeline steps (the public API) callable directly from a `Jenkinsfile`.
- **`src/com/example/`** contains the `Docker` class, which holds the underlying implementation shared by the Docker-related steps.

## Available steps

| Step | Description |
|---|---|
| `buildJar()` | Runs `mvn clean package` to build the application JAR for the current branch. |
| `testSourceCode()` | Runs `mvn test` against the current branch. |
| `buildImage(String imageName)` | Builds a Docker image tagged `imageName` from the `Dockerfile` in the workspace root. |
| `dockerLogin()` | Logs in to Docker using the `docker-credentials` Jenkins credentials (username/password). |
| `pushImage(String imageName)` | Pushes the Docker image tagged `imageName` to the configured Docker registry. |

All steps rely on the `BRANCH_NAME` environment variable (available in Jenkins multibranch pipelines) for logging context.

## Usage

### 1. Register the library in Jenkins

In **Manage Jenkins → System → Global Pipeline Libraries**, add this repository as a library (e.g. named `jenkins-shared-library`).

### 2. Reference it from a `Jenkinsfile`

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
                testSourceCode()
            }
        }

        stage('Build') {
            steps {
                buildJar()
            }
        }

        stage('Docker Build') {
            steps {
                buildImage('my-app:latest')
            }
        }

        stage('Docker Login') {
            steps {
                dockerLogin()
            }
        }

        stage('Docker Push') {
            steps {
                pushImage('my-app:latest')
            }
        }
    }
}
```

## Requirements

- Jenkins with the [Pipeline: Shared Groovy Libraries](https://plugins.jenkins.io/workflow-cjs-plugin/) plugin.
- Maven available on the Jenkins agent (`mvn`).
- Docker available on the Jenkins agent (`docker`).
- A Jenkins credentials entry with ID `docker-credentials` (username/password type) for `dockerLogin()`.

## Local development

This project uses Groovy 5.0.8 (bundled under `lib/`) for local editing/IDE support. Note that `vars/*.groovy` and `src/com/example/Docker.groovy` run inside the Jenkins Pipeline CPS execution engine and are not intended to be run as standalone Groovy scripts outside of Jenkins.
