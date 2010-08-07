
/**
 * Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsPlugins()
        grailsHome()
        mavenCentral()

        // Google's Maven repository
        mavenRepo "http://google-maven-repository.googlecode.com/svn/repository/"
    }

    dependencies {
        //runtime 'com.google.api.client:google-api-client:1.0.9-alpha'
        runtime 'javax.mail:mail:1.4.3'
        test 'org.mockito:mockito-all:1.8.5'
    }

    plugins {
        runtime 'org.grails.plugins:spring-events:1.0'
        runtime 'org.grails.plugins:jquery:1.4.2.5'
        runtime 'org.grails.plugins:oauth:0.6'
        test 'org.grails.plugins:code-coverage:latest.integration'
    }
}

