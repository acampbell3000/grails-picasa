/*
 * Grails Picasa Plug-in
 *
 * A simple plug-in which provides a photo gallery driven from your Google Picasa
 * Web Albums account.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaGrailsPlugin {
    // The plugin version
    def version = "0.5.0"
    // The version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.1 > *"
    // The other plugins this plugin depends on
    def dependsOn = [ oauth: 0.4 ]
    // Resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // Plug-in details
    def author = "Anthony Campbell"
    def authorEmail = "acampbell3000 [[at] googlemail [dot]] com"
    def title = "Grails Picasa Plug-in"
    def description = '''\\
A simple plug-in which provides a photo gallery driven from your Google Picasa Web Albums account.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/picasa"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
