def dockerRepo = "ghcr.io/icgc-argo/song-search"
def githubRepo = "icgc-argo/workflow-search"
def chartVersion = "0.2.0"
def commit = "UNKNOWN"
def version = "UNKNOWN"

pipeline {
    agent {
        kubernetes {
            label 'workflow-search'
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
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
      type: File
  - name: docker-graph-storage
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
                        [$class: 'StringParameterValue', name: 'AP_CHART_NAME', value: 'workflow-search'],
                        [$class: 'StringParameterValue', name: 'AP_RELEASE_NAME', value: 'search'],
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
                          sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/icgc-argo/workflow-search --tags"
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
                        [$class: 'StringParameterValue', name: 'AP_CHART_NAME', value: 'workflow-search'],
                        [$class: 'StringParameterValue', name: 'AP_RELEASE_NAME', value: 'search'],
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