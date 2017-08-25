#!groovy

// Don't test plugin compatibility to other Jenkins versions
// Allow failing tests to retry execution
// buildPlugin(failFast: false)

// Test plugin compatbility to latest Jenkins LTS
// Allow failing tests to retry execution
// Run findbugs and save the output, mark unstable on any findbugs warning
buildPlugin(jenkinsVersions: [null, '2.60.3'],
            checkstyle: [run:true, archive:true, unstableTotalAll: '0'],
            findbugs: [run:true, archive:true, unstableTotalAll: '0'],
            failFast: false
           )

// See https://github.com/jenkins-infra/pipeline-library/blob/master/README.adoc
// for detailed description of the arguments available with buildPlugin
