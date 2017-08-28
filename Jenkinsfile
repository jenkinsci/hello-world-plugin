#!groovy

// Don't test plugin compatibility to other Jenkins versions
// Allow failing tests to retry execution
// buildPlugin(failFast: false)

// Test plugin compatbility to pom defined version (null) and latest Jenkins weekly
// Allow failing tests to retry execution
// Run checkstyle and save the output, mark unstable on any checkstyle warning
// Run findbugs and save the output, mark unstable on any findbugs warning
buildPlugin(jenkinsVersions: [null, '2.75'],
            checkstyle: [run:true, archive:true, unstableTotalAll: '0'],
            findbugs: [run:true, archive:true, unstableTotalAll: '0'],
            failFast: false
           )

// See https://github.com/jenkins-infra/pipeline-library/blob/master/README.adoc
// for detailed description of the arguments available with buildPlugin
