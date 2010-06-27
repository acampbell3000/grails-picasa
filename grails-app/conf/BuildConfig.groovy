
grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsPlugins()
        grailsHome()
        mavenCentral()
    }

    dependencies {
        runtime 'javax.mail:mail:1.4.3'
        test 'org.mockito:mockito-all:1.8.4'
    }

    plugins {
        runtime 'org.grails.plugins:oauth:0.3'
        test 'org.grails.plugins:code-coverage:latest.integration'
    }
}

