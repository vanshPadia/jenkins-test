pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    withFolderProperties {
                        checkout([$class: 'GitSCM',
                            branches: [[name: env.gitBranch ?: 'master']],
                            userRemoteConfigs: [[url: env.repositoryUrl]]
                        ])
                    }
                }
            }
        }
        
        stage('Deploy DNS Stack') {
            steps {
                script {
                    withFolderProperties {
                        withAWS(generateAwsCreds(env.environment)) {
                            cfnUpdate(
                                stack: "${env.environment}-dns-acm",
                                file: 'Route53-acm.yaml',
                                params: [
                                    DomainName: env.domainName
                                ],
                                timeoutInMinutes: 30,
                                capabilities: ['CAPABILITY_IAM']
                            )
                        }
                    }
                }
            }
        }
        
        stage('Deploy CDN Stack') {
            steps {
                script {
                    withFolderProperties {
                        withAWS(generateAwsCreds(env.environment)) {
                            cfnUpdate(
                                stack: "${env.environment}-s3-cdn",
                                file: 's3-cdn.yaml',
                                timeoutInMinutes: 30,
                                capabilities: ['CAPABILITY_IAM']
                            )
                        }
                    }
                }
            }
        }
        
        stage('Deploy Content') {
            steps {
                script {
                    withFolderProperties {
                        withAWS(generateAwsCreds(env.environment)) {
                            def s3Bucket = sh(
                                script: """
                                    aws cloudformation describe-stacks \
                                        --stack-name ${env.environment}-s3-cdn \
                                        --query 'Stacks[0].Outputs[?OutputKey==`S3BucketName`].OutputValue' \
                                        --output text
                                """,
                                returnStdout: true
                            ).trim()
                            
                            sh "aws s3 cp index.html s3://${s3Bucket}/"
                        }
                    }
                }
            }
        }
        
        stage('Verify') {
            steps {
                script {
                    withFolderProperties {
                        echo "Website deployed to: https://${env.domainName}"
                    }
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            script {
                withFolderProperties {
                    echo "Pipeline completed successfully for ${env.environment} environment!"
                }
            }
        }
        failure {
            script {
                withFolderProperties {
                    echo "Pipeline failed for ${env.environment} environment!"
                }
            }
        }
    }
}