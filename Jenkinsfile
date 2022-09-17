// Jenkinsfile for the DBC-assertions project

/* 
 * Copyright 2018,2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */
 
pipeline { 
    agent {
        dockerfile {
            filename 'Jenkins.Dockerfile'
            args '-v $HOME/.m2:/root/.m2 --network="host"'
        }
    }
    triggers {
        pollSCM('H */4 * * *')
    }
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-amd64'
    }
    stages {
        stage('Clean') { 
            steps {
               sh 'rm -rf "$WORKSPACE/target"'
            }
        }
        stage('Build, verify and deploy') {
            steps {
                configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]){ 
                    sh 'mvn -B -s $MAVEN_SETTINGS deploy'
                }
            }
        }
    }
    post {
        always {// We ESPECIALLY want the reports on failure
            script {
                recordIssues tools: [
                	java(),
                	javaDoc(),
                	mavenConsole(),
                	pmdParser(pattern: '**/target/pmd.xml'),
					spotBugs(pattern: '**/target/spotbugsXml.xml')
					]
            }
            junit 'target/*-reports/**/TEST-*.xml' 
        }
        success {
            archiveArtifacts artifacts: 'target/DBC-assertions-*.jar', fingerprint: true
        }
    }
}