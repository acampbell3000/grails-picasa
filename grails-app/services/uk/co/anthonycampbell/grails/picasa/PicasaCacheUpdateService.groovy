package uk.co.anthonycampbell.grails.picasa

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

import uk.co.anthonycampbell.grails.picasa.cache.*
import uk.co.anthonycampbell.grails.picasa.event.*

import org.apache.commons.lang.StringUtils

class PicasaCacheUpdateService implements ApplicationListener<PicasaUpdateEvent> {
    // Must not be transactional to allow full event detection
    static transactional = false
    
    // Declare service scope
    static scope = "singleton"

    /**
     * Initialise config properties.
     */
    @Override
    void afterPropertiesSet() {
        log?.info "Initialising the ${this.getClass().getSimpleName()}..."
    }

    @Override
    void onApplicationEvent(final PicasaUpdateEvent event) {
        println "onApplicationEvent: ${PicasaUpdateStreamListener}"
        println this?.getClass()?.getSimpleName()
    }
}
