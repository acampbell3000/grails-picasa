package uk.co.anthonycampbell.grails.picasa.listener

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

import uk.co.anthonycampbell.grails.picasa.event.PicasaUpdateStreamEvent

import org.slf4j.LoggerFactory

import org.springframework.context.ApplicationListener

/**
 * Listener used to invoke calls to the picasa service to retrieve
 * further photo details from the provided steam.
 *
 * This is registered by PicasaGrailsPlugin.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaUpdateStreamListener implements ApplicationListener<PicasaUpdateStreamEvent> {

    /** LOG */
	private static final log = LoggerFactory.getLogger(PicasaUpdateStreamListener.class)

    /** Must ensure bean is not transactional to ensure events get caught (GRAILS-6466) */
    boolean transactional = false
    
    /**
     * Constructor.
     */
    PicasaUpdateStreamListener() {
		if (log.infoEnabled) {
            log?.info "Initialising the ${this.getClass().getSimpleName()}"
        }
    }

    @Override
    void onApplicationEvent(final PicasaUpdateStreamEvent event) {
        println "onApplicationEvent: ${PicasaUpdateStreamListener}"
        println this?.getClass()?.getSimpleName()
    }
}
