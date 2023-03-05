pipeline{
    agent any

    tools {
        maven 'Maven'
    }

    options {
        gitLabConnection('test')
        gitlabBuilds(builds: ['Build', 'Test'])
    }

    post {
        failure {
            updateGitlabCommitStatus name: 'Build', state: 'failed'
            updateGitlabCommitStatus name: 'Test', state: 'failed'
        }
        unstable {
            updateGitlabCommitStatus name: 'Build', state: 'failed'
            updateGitlabCommitStatus name: 'Test', state: 'failed'
        }
        aborted {
            updateGitlabCommitStatus name: 'Build', state: 'canceled'
            updateGitlabCommitStatus name: 'Test', state: 'canceled'
        }
        success {
            updateGitlabCommitStatus name: 'Build', state: 'success'
            updateGitlabCommitStatus name: 'Test', state: 'success'
        }
    }

    stages{
        stage("Build"){
            steps{
                sh 'mvn -B clean install -DskipTests'
                echo 'Building the application...'
            }
        }
        stage("Test"){
            steps{
                sh 'mvn test'
                echo 'Testing the application...'
            }
        }
        stage("Deploy"){
            steps{
                echo 'Deploying the application...'
            }
        }
    }
}