package uk.co.anthonycampbell.grails.plugins.picasa.listener

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

import javax.servlet.http.HttpSessionListener
import javax.servlet.http.HttpSessionEvent

import org.slf4j.LoggerFactory

import org.springframework.web.context.support.WebApplicationContextUtils

import uk.co.anthonycampbell.grails.plugins.picasa.session.SessionLifecycle

/**
 * Simply delegates to the 'sessionMonitor' bean in the
 * application context.
 *
 * This is registered by PicasaGrailsPlugin.
 * 
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class SessionLifecycleListener implements HttpSessionListener {

    /** LOG */
	private static final log = LoggerFactory.getLogger(SessionLifecycleListener.class)
    
    /**
     * Constructor.
     */
	SessionLifecycleListener() {
		if (log.infoEnabled) {
            log?.info "Initialising the ${this.getClass().getSimpleName()}"
        }
	}

    @Override
	void sessionCreated(final HttpSessionEvent httpSessionEvent) {
		final def session = httpSessionEvent.session
		final def monitors = getMonitors(session)
        
        // Iterate through registered monitors
        final Iterator<Map.Entry> iterator = monitors?.entrySet()?.iterator()
        while (iterator?.hasNext()) {
            iterator?.next()?.getValue()?.sessionCreated(session)
        }
	}

    @Override
	void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
		final def session = httpSessionEvent.session
		final def monitors = getMonitors(session)

        // Iterate through registered monitors
        final Iterator<Map.Entry> iterator = monitors?.entrySet()?.iterator()
        while (iterator?.hasNext()) {
            iterator?.next()?.getValue()?.sessionDestroyed(session)
        }
	}

    /**
     * Get the session monitor.
     *
     * @param the current session.
     * @return the session monitor.
     */
	protected getMonitors(final def session) {
		final def rootContext = WebApplicationContextUtils.getWebApplicationContext(
            session.servletContext)
		final def grailsApplication = rootContext?.getBean('grailsApplication')
        grailsApplication?.mainContext?.getBeansOfType(SessionLifecycle.class)
	}
}