#!/usr/bin/env groovy

node('rhel7') {
	stage('Checkout SCM') {
		deleteDir()
		git url: 'https://github.com/robstryker/org.jboss.tools.ssp.git'
	}

	stage('Build') {
		def mvnHome = tool 'maven3-latest'
		env.PATH="${env.PATH}:${mvnHome}/bin"

		sh 'mvn clean install'
		sh 'mvn clean package -f distribution/pom.xml'

		archiveArtifacts artifacts: 'distribution/target/org.jboss.tools.ssp.distribution-*.zip'	
	}
}
