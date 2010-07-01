<%=packageName ? "package ${packageName}\n\n" : ''%>import org.springframework.web.servlet.support.RequestContextUtils as RCU;

/**
 * ${className} controller
 *
 * Controller which handles all of the common actions for the ${className}
 * domain class. In addition, the class also provides support for ajax
 * requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class ${className}Controller {
    <%def lowerCaseName = grails.util.GrailsNameUtils.getPropertyName(className)%>
    // Declare dependencies
    def messageSource
    
    // Delete, save and update actions only accept POST requests
	static allowedMethods = [delete:'POST', save:'POST', ajaxSave:'POST', update:'POST', ajaxUpdate:'POST']

    /**
     * Re-direct index requests to list view
     */
    def index = {
        redirect(action: "list", params: params)
	}

    /**
     * Invoke non-ajax list method
     */
    def list = {
        return doList(false)
    }

    /**
     * Invoke non-ajax list method
     */
    def ajaxList = {
        return doList(true)
    }

    /**
     * Invoke non-ajax show method
     */
    def show = {
        return doShow(false)
    }

    /**
     * Invoke non-ajax show method
     */
    def ajaxShow = {
        return doShow(true)
    }

    /**
     * Invoke non-ajax save method
     */
    def save = {
        doSave(false)
    }

    /**
     * Invoke ajax save method
     */
    def ajaxSave = {
        doSave(true)
    }

    /**
     * Invoke non-ajax update method
     */
    def update = {
        doUpdate(false)
    }

    /**
     * Invoke ajax update method
     */
    def ajaxUpdate = {
        doUpdate(true)
    }

    /**
     * Initialise form and render view
     */
    def create = {
        final def ${propertyName} = new ${className}()
        ${propertyName}.properties = params
        flash.message = ""
        return [${propertyName}: ${propertyName}]
    }

    /**
     * Get selected instance and render edit view
     */
    def edit = {
        final def ${propertyName} = ${className}.get(params.id)

        // Check whether ${lowerCaseName} exists
        if (!${propertyName}) {
            flash.message = "\${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])}"
            redirect(action: "list")
        } else {
            flash.message = ""
            return [${propertyName}: ${propertyName}]
        }
    }

    /**
     * Get selected instance and attempt to perform a hard delete
     */
    def delete = {
        final def ${propertyName} = ${className}.get(params.id)

        // Check whether ${lowerCaseName} exists
        if (${propertyName}) {
            try {
				// Attempt delete
                ${propertyName}.delete(flush: true)
                flash.message = "\${message(code: 'default.deleted.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])}"
                redirect(action: "list")

            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "\${message(code: 'default.not.deleted.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        } else {
            flash.message = "\${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])}"
            redirect(action: "list")
        }
    }

    /*
     * Validate an individual field
     */
    def validate = {
        // Initialise domain instance and error message
        final def ${propertyName} = new ${className}(params)
        def errorMessage = ""
        def field = ""

        // Get selected field
        for (param in params) {
            if (param.key && !param.key.equals("action")
                && !param.key.equals("controller")) {
                field = param.key
                break
            }
        }

		log.debug("Validating field: " + field)

        // Check whether provided field has errors
        if (!${propertyName}.validate() && ${propertyName}.errors.hasFieldErrors(field)) {
			// Get error message value
            errorMessage = messageSource.getMessage(
                ${propertyName}.errors.getFieldError(field),
                RCU.getLocale(request)
            )

			log.debug("Error message: " + errorMessage)
        }

        // Render error message
        render(errorMessage)
    }

    /**
     * Retrieve and render list of ${lowerCaseName}s.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doList(final boolean isAjax) {
        def listView = "list"
        if (isAjax) listView = "_list"

        flash.message = ""
        params.max = Math.min(params.max ? params.int('max') : 10, 500)

        log.debug("Display list with " + listView + " view")

        render(view: listView, model: [${propertyName}List: ${className}.list(params), ${propertyName}Total: ${className}.count()])
    }

    /**
     * Get the selected ${lowerCaseName} and render the view.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doShow(final boolean isAjax) {
        final def ${propertyName} = ${className}.get(params.id)
        def showView = "show"
        if (isAjax) showView = "_show"

        // Check whether ${lowerCaseName} exists
        if (!${propertyName}) {
            flash.message = "\${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])}"
            redirect(action: "list")
		} else {
            log.debug("Display ${lowerCaseName} with " + showView + " view")

            render(view: showView, model: [${propertyName}: ${propertyName}])
        }
    }

    /**
     * Attempt to update the provided ${lowerCaseName} instance.
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doUpdate(final boolean isAjax) {
        final def ${propertyName} = ${className}.get(params.id)
        def editView = "edit"
        def showView = "show"
        if (isAjax) {
            editView = "_edit"
            showView = "_show"
        }

		log.debug("Attempting to update an instance of ${className} (isAjax = " + isAjax + ")")

        // Check whether ${lowerCaseName} exists
        if (${propertyName}) {
			// Check version has not changed
            if (params.version) {
                def version = params.version.toLong()
                if (${propertyName}.version > version) {
                    ${propertyName}.errors.rejectValue("version", "default.optimistic.locking.failure",
						[message(code: '${domainClass.propertyName}.label', default: '${className}')] as Object[],
						"Another user has updated this ${className} while you were editing")
                    render(view: editView, model: [${propertyName}: ${propertyName}])
                    return
                }
            }

			// Get updated properties
            ${propertyName}.properties = params

			// Perform update
            if (!${propertyName}.hasErrors() && ${propertyName}.save(flush: true)) {
                flash.message = "\${message(code: 'default.updated.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])}"
				render(view: showView, model: [${propertyName}: ${propertyName}])
            } else {
                render(view: editView, model: [${propertyName}: ${propertyName}])
            }
        } else {
            flash.message = "\${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])}"
            redirect(action: "list")
        }
    }
    
    /**
     * Attempt to save the provided ${lowerCaseName} instance.
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doSave(final boolean isAjax) {
        final def ${propertyName} = new ${className}(params)
        def showView = "show"
        def createView = "create"
        if (isAjax) {
            showView = "_show"
            createView = "_create"
        }

		log.debug("Attempting to save an instance of ${className} (isAjax = " + isAjax + ")")

        if (${propertyName}.save(flush: true)) {
            flash.message = "\${message(code: 'default.created.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])}"
            render(view: showView, model: [${propertyName}: ${propertyName}])
        } else {
            render(view: createView, model: [${propertyName}: ${propertyName}])
        }
    }
}
