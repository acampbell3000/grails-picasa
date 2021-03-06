
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

import uk.co.anthonycampbell.grails.picasa.listener.SessionLifecycleListener
import uk.co.anthonycampbell.grails.picasa.session.SessionMonitor
import uk.co.anthonycampbell.grails.picasa.utils.PicasaUtils

import org.slf4j.LoggerFactory

/*
 * Grails Picasa Plug-in
 *
 * A simple plug-in which provides a photo gallery driven from your
 * Google Picasa Web Albums account.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaGrailsPlugin {
    /** Logger */
	private static final log = LoggerFactory.getLogger(
        "uk.co.anthonycampbell.grails.picasa.PicasaGrailsPlugin")

    // The plugin version
    def version = "0.6.7"
    // The version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.1 > *"
    // The other plugins this plugin depends on"
    def dependsOn = [ oauth: "0.10", jquery: "1.4.2.5", springEvents: "1.0" ]
    // Resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // Plug-in details
    def author = "Anthony Campbell"
    def authorEmail = "acampbell3000 [[at] mail from google"
    def title = "Grails Picasa Plug-in"
    def description = '''\\
A simple plug-in which provides a photo gallery driven from your Google Picasa Web Albums account.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/picasa"

    def doWithWebDescriptor = { webXml ->
        // Check environment support hot reloading
		if (PicasaUtils.isEnvironmentClassReloadable()) {
            if (log?.infoEnabled) {
                log?.info "Registering ${SessionLifecycleListener.class} " +
                    "in web descriptor for session monitor"
            }

            // Register Session lifecycle listener
            final def listeners = webXml.'listener'
            listeners + {
                'listener' {
                    'listener-class'("uk.co.anthonycampbell.grails.picasa.listener.SessionLifecycleListener")
                }
            }
		}
	}

    def doWithApplicationContext = { applicationContext ->
        // Check environment support hot reloading
        if (PicasaUtils.isEnvironmentClassReloadable()) {
			if (log?.infoEnabled) {
				log?.info "Registering ${SessionMonitor.class} in application context"
			}

            // Register session monitor
            applicationContext?.registerSingleton("sessionMonitor", SessionMonitor.class)
            
		} else {
			if (log?.debugEnabled) {
				log?.debug "Unable to register ${SessionMonitor.class} in application context " +
                    "(Environment.current.reloadEnabled=${Environment.current.reloadEnabled})"
			}
		}
    }

    def onConfigChange = { event ->
        // Config change, need to reset the Picasa service
        final def picasaService = event?.ctx?.getBean("picasaService")
        picasaService?.reset()

        // Get sessions from monitor
        final def monitor = event?.ctx?.getBean("sessionMonitor")
        final def sessions = monitor.getSessions()

        // Update Picasa comment service for each session
        for (final def session in sessions) {
            session?.picasaCommentService?.reset()
        }
    }
}
