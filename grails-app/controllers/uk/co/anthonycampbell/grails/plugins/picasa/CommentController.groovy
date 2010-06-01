package uk.co.anthonycampbell.grails.plugins.picasa

import org.springframework.web.servlet.support.RequestContextUtils as RCU;

/**
 * Comment controller
 *
 * Controller which handles all of the common actions for the Comment
 * domain class. In addition, the class also provides support for ajax
 * requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class CommentController {
    
    // Declare dependencies
    def messageSource
    
    // Delete, save and update actions only accept POST requests
	static allowedMethods = [delete:'POST', save:'POST', ajaxSave:'POST', update:'POST', ajaxUpdate:'POST']

    /**
     * Re-direct index requests to list view
     */
    def index = {
        redirect(controller: "album", action: "index", params: params)
	}

    /**
     * Prepare and render the photo list view.
     */
    def list = {
        return doList(false)
    }

    /**
     * Prepare and render the photo list view.
     */
    def ajaxList = {
        return doList(true)
    }

    /**
     * Request list of comments through the Picasa web service.
     * Sort and prepare response to be displayed in the view.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return list of photos to display.
     */
    private doList(boolean isAjax) {
        // Initialise lists
        final List<Comment> commentList = new ArrayList<Comment>()
        final List<Comment> displayList = new ArrayList<Comment>()

        // Prepare display values
        final int offset = params.int("offset") ?: 0
        final int max = Math.min(new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10))).intValue(), 500)
        def listView = "list"
        if (isAjax) listView = "_list"
        flash.message = ""

        log.debug("Attempting to list tags through the Picasa web service")

        // Get comment list from picasa service
        try {
            commentList.addAll(picasaService.listAllComments())

            log.debug("Success...")

        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Comment.list.not.available')}"
        }

        // Sort comments
        Collections.sort(commentList, new CommentDateComparator())

        // If required, reverse list
        if (params.order == "asc") {
            Collections.reverse(commentList)
        }

        log.debug("Convert response into comment list")

        // Convert to array to allow easy display preparation
        Comment[] commentArray = commentList.toArray()
        if (commentArray) {
            // Prepare display list
            commentArray = Arrays.copyOfRange(commentArray, offset,
                ((offset + max) > commentArray.length ? commentArray.length : (offset + max)))
            if (commentArray) {
                // Update display list
                displayList.addAll(Arrays.asList(commentArray))
            }
        }

        log.debug("Display list with " + listView + " view")

        render(view: listView, model: [commentInstanceList: displayList,
                commentInstanceTotal: (commentList?.size() ?: 0)])
    }

    /**
     * Get selected instance and render show view
     */
    def show = {
        def commentInstance = Comment.get(params.id)

        // Check whether comment exists
        if (!commentInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'comment.label', default: 'Comment'), params.id])}"
            redirect(action: "list")
		} else {
            [commentInstance: commentInstance]
        }
    }
    
    /**
     * Initialise form and render view
     */
    def create = {
        def commentInstance = new Comment()
        commentInstance.properties = params
        flash.message = ""
        return [commentInstance: commentInstance]
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
        def commentInstance = Comment.get(params.id)

        // Check whether comment exists
        if (!commentInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'comment.label', default: 'Comment'), params.id])}"
            redirect(action: "list")
        } else {
            flash.message = ""
            return [commentInstance: commentInstance]
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
        def commentInstance = Comment.get(params.id)

        // Check whether comment exists
        if (commentInstance) {
            try {
				// Attempt delete
                commentInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'comment.label', default: 'Comment'), params.id])}"
                redirect(action: "list")

            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'comment.label', default: 'Comment'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'comment.label', default: 'Comment'), params.id])}"
            redirect(action: "list")
        }
    }

    /*
     * Validate an individual field
     */
    def validate = {
        // Initialise domain instance and error message
        def commentInstance = new Comment(params)
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
        if (!commentInstance.validate() && commentInstance.errors.hasFieldErrors(field)) {
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
     * Attempt to update the provided comment instance.
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doUpdate(boolean isAjax) {
        def commentInstance = Comment.get(params.id)
        def editView = "edit"
        def showView = "show"
        if(isAjax) {
            editView = "ajaxEdit"
            showView = "ajaxShow"
        }

		log.debug("Attempting to update an instance of Comment (isAjax = " + isAjax + ")")

        // Check whether comment exists
        if (commentInstance) {
			// Check version has not changed
            if (params.version) {
                def version = params.version.toLong()
                if (commentInstance.version > version) {
                    commentInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
						[message(code: 'comment.label', default: 'Comment')] as Object[],
						"Another user has updated this Comment while you were editing")
                    render(view: editView, model: [commentInstance: commentInstance])
                    return
                }
            }

			// Get updated properties
            commentInstance.properties = params

			// Perform update
            if (!commentInstance.hasErrors() && commentInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'comment.label', default: 'Comment'), commentInstance.id])}"
				render(view: showView, model: [commentInstance: commentInstance])
            } else {
                render(view: editView, model: [commentInstance: commentInstance])
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'comment.label', default: 'Comment'), params.id])}"
            redirect(action: "list")
        }
    }
    
    /**
     * Attempt to save the provided comment instance.
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doSave(boolean isAjax) {
        def commentInstance = new Comment(params)
        def showView = "show"
        def createView = "create"
        if(isAjax) {
            showView = "ajaxShow"
            createView = "ajaxCreate"
        }

		log.debug("Attempting to save an instance of Comment (isAjax = " + isAjax + ")")

        if (commentInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'comment.label', default: 'Comment'), commentInstance.id])}"
            render(view: showView, model: [commentInstance: commentInstance])
        }
        else {
            render(view: createView, model: [commentInstance: commentInstance])
        }
    }
}
