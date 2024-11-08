// vars/generateAwsCreds.groovy
def call(String environment) {
    def roleArn = "arn:aws:iam::891377218197:role/try-2"
    def sessionName = "jenkins-session-${UUID.randomUUID()}"

    // Assume the AWS role and retrieve credentials
    def assumeRoleResult = sh(
        script: """
            aws sts assume-role \
                --role-arn ${roleArn} \
                --role-session-name ${sessionName} \
                --query 'Credentials.[AccessKeyId, SecretAccessKey, SessionToken]' \
                --output text
        """,
        returnStdout: true
    ).trim().split('\n')

    def (accessKey, secretKey, sessionToken) = assumeRoleResult

    // Configure AWS credentials
    withAWS(accessKeyId: accessKey, secretKey: secretKey, sessionToken: sessionToken)
}
