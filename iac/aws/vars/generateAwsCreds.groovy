// vars/generateAwsCreds.groovy
def call(String environment) {
    def roleArn = "arn:aws:iam::891377218197:role/try-2"

    // Use AWS CLI to assume the role and capture credentials
    def sessionName = "jenkins-session-${UUID.randomUUID()}"
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

    // Extract the access key, secret key, and session token
    def (accessKey, secretKey, sessionToken) = assumeRoleResult

    // Return the credentials as a map
    return [
        accessKeyId: accessKey,
        secretAccessKey: secretKey,
        sessionToken: sessionToken
    ]
}
