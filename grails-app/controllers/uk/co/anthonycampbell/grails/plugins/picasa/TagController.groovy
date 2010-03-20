package uk.co.anthonycampbell.grails.plugins.picasa

import org.springframework.web.servlet.support.RequestContextUtils as RCU;

/**
 * Tag controller
 *
 * Controller which handles all of the common actions for the Tag
 * domain class. In addition, the class also provides support for ajax
 * requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class TagController {
    
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
     * Prepare and render the tag list view
     */
    def list = {
        flash.message = ""
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tagInstanceList: Tag.list(params), tagInstanceTotal: Tag.count()]
    }

    /**
     * Get selected instance and render show view
     */
    def show = {
        def tagInstance = Tag.get(params.id)

        // Check whether tag exists
        if (!tagInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), params.id])}"
            redirect(action: "list")
		} else {
            [tagInstance: tagInstance]
        }
    }
    
    /**
     * Initialise form and render view
     */
    def create = {
        def tagInstance = new Tag()
        tagInstance.properties = params
        flash.message = ""
        return [tagInstance: tagInstance]
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
     * Get selected instance and render edit view
     */
    def edit = {
        def tagInstance = Tag.get(params.id)

        // Check whether tag exists
        if (!tagInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), params.id])}"
            redirect(action: "list")
        } else {
            flash.message = ""
            return [tagInstance: tagInstance]
        }
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
     * Get selected instance and attempt to perform a hard delete
     */
    def delete = {
        def tagInstance = Tag.get(params.id)

        // Check whether tag exists
        if (tagInstance) {
            try {
				// Attempt delete
                tagInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'tag.label', default: 'Tag'), params.id])}"
                redirect(action: "list")

            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'tag.label', default: 'Tag'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), params.id])}"
            redirect(action: "list")
        }
    }

    /*
     * Validate an individual field
     */
    def validate = {
        // Initialise domain instance and error message
        def tagInstance = new Tag(params)
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
        if (!tagInstance.validate() && tagInstance.errors.hasFieldErrors(field)) {
			// Get error message value
            errorMessage = messageSource.getMessage(
                contactFormInstance.errors.getFieldError(field),
                RCU.getLocale(request)
            )

			log.debug("Error message: " + errorMessage)
        }

        // Render error message
        render(errorMessage)
    }

    /**
     * Attempt to update the provided tag instance.
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doUpdate(boolean isAjax) {
        def tagInstance = Tag.get(params.id)
        def editView = "edit"
        def showView = "show"
        if(isAjax) {
            editView = "ajaxEdit"
            showView = "ajaxShow"
        }

		log.debug("Attempting to update an instance of Tag (isAjax = " + isAjax + ")")

        // Check whether tag exists
        if (tagInstance) {
			// Check version has not changed
            if (params.version) {
                def version = params.version.toLong()
                if (tagInstance.version > version) {
                    tagInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
						[message(code: 'tag.label', default: 'Tag')] as Object[],
						"Another user has updated this Tag while you were editing")
                    render(view: editView, model: [tagInstance: tagInstance])
                    return
                }
            }

			// Get updated properties
            tagInstance.properties = params

			// Perform update
            if (!tagInstance.hasErrors() && tagInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'tag.label', default: 'Tag'), tagInstance.id])}"
				render(view: showView, model: [tagInstance: tagInstance])
            } else {
                render(view: editView, model: [tagInstance: tagInstance])
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), params.id])}"
            redirect(action: "list")
        }
    }
    
    /**
     * Attempt to save the provided tag instance.
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doSave(boolean isAjax) {
        def tagInstance = new Tag(params)
        def showView = "show"
        def createView = "create"
        if(isAjax) {
            showView = "ajaxShow"
            createView = "ajaxCreate"
        }

		log.debug("Attempting to save an instance of Tag (isAjax = " + isAjax + ")")

        if (tagInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'tag.label', default: 'Tag'), tagInstance.id])}"
            render(view: showView, model: [tagInstance: tagInstance])
        }
        else {
            render(view: createView, model: [tagInstance: tagInstance])
        }
    }
}
