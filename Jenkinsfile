#!groovy

// Don't test plugin compatibility to other Jenkins versions
// Allow failing tests to retry execution
// buildPlugin(failFast: false)

// Test plugin compatbility to latest Jenkins LTS
// Allow failing tests to retry execution
buildPlugin(jenkinsVersions: [null, '2.60.2'], failFast: false)
