
grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsPlugins()
        grailsHome()
        mavenCentral()
    }

    dependencies {
        test 'org.mockito:mockito-all:1.8.4'
        //test 'junit:junit:4.0'
    }
}

