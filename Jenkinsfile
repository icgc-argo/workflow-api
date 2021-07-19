def dockerRepo = "ghcr.io/icgc-argo/workflow-api"
def chartVersion = "0.1.0"
def commit = "UNKNOWN"
def version = "UNKNOWN"

pipeline {
    agent {
        kubernetes {
            label 'workflow-api'
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: jdk
    tty: true
    image: openjdk:11
    env:
      - name: DOCKER_HOST
        value: tcp://localhost:2375
      - name: HOME
        value: /mnt/executor
    volumeMounts:
      - name: docker-home
        mountPath: /mnt/executor
  - name: dind-daemon
    image: docker:18.06-dind
    securityContext:
        privileged: true
    volumeMounts:
      - name: docker-graph-storage
        mountPath: /var/lib/docker
  - name: helm
    image: alpine/helm:2.12.3
    command:
    - cat
    tty: true
  - name: docker
    image: docker:18-git
    tty: true
    env:
      - name: DOCKER_HOST
        value: tcp://localhost:2375
  volumes:
  - name: docker-graph-storage
    emptyDir: {}
  - name: docker-home
    emptyDir: {}
"""
        }
    }
    stages {
            stage('Prepare') {
                steps {
                    script {
                        commit = sh(returnStdout: true, script: 'git describe --always').trim()
                        version = readMavenPom().getVersion()
                    }
                }
            }
            stage('Test') {
                steps {
                    container('jdk') {
                        sh "./mvnw test"
                    }
                }
            }
            stage('Build & Publish Develop') {
                when {
                    branch "develop"
                }
                steps {
                    container('docker') {
                        withCredentials([usernamePassword(credentialsId:'argoContainers', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh 'docker login ghcr.io -u $USERNAME -p $PASSWORD'
                        }

                        // DNS error if --network is default
                        sh "docker build --network=host . -t ${dockerRepo}:edge -t ${dockerRepo}:${version}-${commit}"

                        sh "docker push ${dockerRepo}:${version}-${commit}"
                        sh "docker push ${dockerRepo}:edge"
                    }
                }
            }
            stage('deploy to rdpc-collab-dev') {
                when {
                    branch "develop"
                }
                steps {
                    build(job: "/provision/helm", parameters: [
                        [$class: 'StringParameterValue', name: 'AP_RDPC_ENV', value: 'dev' ],
                        [$class: 'StringParameterValue', name: 'AP_CHART_NAME', value: 'workflow-api'],
                        [$class: 'StringParameterValue', name: 'AP_RELEASE_NAME', value: 'workflow-api'],
                        [$class: 'StringParameterValue', name: 'AP_HELM_CHART_VERSION', value: "${chartVersion}"],
                        [$class: 'StringParameterValue', name: 'AP_ARGS_LINE', value: "--set-string image.tag=${version}-${commit}" ]
                    ])
                    // sleep(time:30,unit:"SECONDS")
                    // build(job: "/provision/rdpc-gateway-restart", parameters: [
                    //     [$class: 'StringParameterValue', name: 'AP_RDPC_ENV', value: 'dev' ],
                    // ])
                }
            }
            stage('Release & Tag') {
                when {
                    branch "master"
                }
                steps {
                    container('docker') {
                        withCredentials([usernamePassword(credentialsId: 'argoGithub', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                            sh "git tag ${version}"
                          sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/icgc-argo/workflow-api --tags"
                        }

                        withCredentials([usernamePassword(credentialsId:'argoContainers', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh 'docker login ghcr.io -u $USERNAME -p $PASSWORD'
                        }

                        // DNS error if --network is default
                        sh "docker build --network=host . -t ${dockerRepo}:latest -t ${dockerRepo}:${version}"

                        sh "docker push ${dockerRepo}:${version}"
                        sh "docker push ${dockerRepo}:latest"
                    }
                }
            }
            stage('deploy to rdpc-collab-qa') {
                when {
                    branch "master"
                }
                steps {
                    build(job: "/provision/helm", parameters: [
                        [$class: 'StringParameterValue', name: 'AP_RDPC_ENV', value: 'qa' ],
                        [$class: 'StringParameterValue', name: 'AP_CHART_NAME', value: 'workflow-api'],
                        [$class: 'StringParameterValue', name: 'AP_RELEASE_NAME', value: 'workflow-api'],
                        [$class: 'StringParameterValue', name: 'AP_HELM_CHART_VERSION', value: "${chartVersion}"],
                        [$class: 'StringParameterValue', name: 'AP_ARGS_LINE', value: "--set-string image.tag=${version}" ]
                    ])
                    // sleep(time:30,unit:"SECONDS")
                    // build(job: "/provision/rdpc-gateway-restart", parameters: [
                    //     [$class: 'StringParameterValue', name: 'AP_RDPC_ENV', value: 'qa' ],
                    // ])
                }
            }
        }
    }