pipeline {
	agent none

	triggers {
		pollSCM 'H/10 * * * *'
	}

	options {
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '14'))
	}

	stages {
		stage("Test: baseline (jdk17)") {
			agent {
				docker {
					image 'eclipse-temurin:17-jdk'
					args '-v $HOME/.m2:/root/.m2'
				}
			}
			environment {
				ARTIFACTORY = credentials('02bd1690-b54f-4c9f-819d-a77cb7a9822c')
			}
			steps {
				sh "PROFILES=none ci/test.sh"
			}
		}

		stage("Test other configurations") {
		    parallel {
		        stage("Test: snapshots") {
                    agent {
                        docker {
                            image 'eclipse-temurin:17-jdk'
                            args '-v $HOME/.m2:/root/.m2'
                        }
                    }
                    environment {
                        ARTIFACTORY = credentials('02bd1690-b54f-4c9f-819d-a77cb7a9822c')
                    }
                    steps {
                        sh "PROFILES=snapshots ci/test.sh"
                    }
		        }

		        stage("Test: baseline (jdk19)") {
                    agent {
                        docker {
                            image 'eclipse-temurin:17-jdk'
                            args '-v $HOME/.m2:/root/.m2'
                        }
                    }
                    environment {
                        ARTIFACTORY = credentials('02bd1690-b54f-4c9f-819d-a77cb7a9822c')
                    }
                    steps {
                        sh "PROFILES=none ci/test.sh"
                    }
		        }
		    }
		}
	}

	post {
		changed {
			script {
				slackSend(
						color: (currentBuild.currentResult == 'SUCCESS') ? 'good' : 'danger',
						channel: '#spring-ws',
						message: "${currentBuild.fullDisplayName} - `${currentBuild.currentResult}`\n${env.BUILD_URL}")
				emailext(
						subject: "[${currentBuild.fullDisplayName}] ${currentBuild.currentResult}",
						mimeType: 'text/html',
						recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']],
						body: "<a href=\"${env.BUILD_URL}\">${currentBuild.fullDisplayName} is reported as ${currentBuild.currentResult}</a>")
			}
		}
	}
}
