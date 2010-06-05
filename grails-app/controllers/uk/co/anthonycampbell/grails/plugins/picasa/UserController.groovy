package uk.co.anthonycampbell.grails.plugins.picasa

import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * User controller
 *
 * Controller which handles all of the common actions for the User
 * domain class. In addition, the class also provides support for ajax
 * requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class UserController {
    
    // Declare dependencies
    def messageSource
    
    // Only accept POST requests
	static allowedMethods = [login: 'POST', ajaxLogin: 'POST']

    /**
     * Re-direct index requests to list view
     */
    def index = {
        redirect(controller: "album", action: "index", params: params)
	}

    /**
     * Invoke non-ajax login method
     */
    def login = {
        return doLogin(false)
    }

    /**
     * Invoke non-ajax login method
     */
    def ajaxLogin = {
        return doLogin(true)
    }

    /*
     * Validate an individual field
     */
    def validate = {
        // Initialise domain instance and error message
        final def userInstance = new User(params)
        def errorMessage = ""
        def field = ""

        // Get selected field
        for (param in params) {
            if (param.key != null && !param.key.equals("action")
                && !param.key.equals("controller")) {
                field = param.key
                break
            }
        }

		log.debug("Validating field: " + field)

        // Check whether provided field has errors
        if (!userInstance.validate() && userInstance.errors.hasFieldErrors(field)) {
			// Get error message value
            errorMessage = messageSource.getMessage(
                userInstance.errors.getFieldError(field),
                RCU.getLocale(request)
            )

			log.debug("Error message: " + errorMessage)
        }

        // Render error message
        render(errorMessage)
    }
    
    /**
     * Attempt to login the user through the Google OAuth API.
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doLogin(final boolean isAjax) {
        log.debug("Begin login...")

        final def userInstance = new User(params)
        def showView = "show"
        def createView = "create"
        if (isAjax) {
            showView = "_show"
            createView = "_create"
        }

        if (userInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])}"
            render(view: showView, model: [userInstance: userInstance])
        } else {
            render(view: createView, model: [userInstance: userInstance])
        }
    }
}
