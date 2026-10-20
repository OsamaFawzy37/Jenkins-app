import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement
import hudson.FilePath
import hudson.model.Hudson

def jobDslScript = '''
pipelineJob('Dynamic-K8s-App-Pipeline') {
    description('Pipeline كاملة: بتسحب الكود، تبني الصورة، وترفعها على Kubernetes')
    parameters {
        stringParam('REPO_URL', 'https://github.com/dockersamples/node-bulletin-board.git', 'Git Repository URL')
        stringParam('REGISTRY_URL', 'docker.io', 'Docker Registry URL')
        stringParam('REGISTRY_USER', '', 'Docker Username')
        passwordParam('REGISTRY_PASS', '', 'Docker Password')
        stringParam('K8S_NAMESPACE', 'default', 'Namespace')
        passwordParam('KUBECONFIG_BASE64', '', 'Base64 Kubeconfig')
    }
    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Checkout') {
                            steps {
                                git url: "\\${params.REPO_URL}", branch: 'master'
                            }
                        }
                        stage('Mock Build') {
                            steps {
                                echo "Building... (Docker socket config will be needed for real build)"
                            }
                        }
                    }
                }
            """)
            sandbox()
        }
    }
}
'''
def workspace = new FilePath(Hudson.instance.rootPath)
def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)
new DslScriptLoader(jobManagement).runScript(jobDslScript)
