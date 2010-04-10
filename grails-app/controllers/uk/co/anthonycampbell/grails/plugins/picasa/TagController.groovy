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
    def grailsApplication
    def picasaService

    /**
     * Re-direct index requests to list view.
     */
    def index = {
        redirect(action: "list", params: params)
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
     * Request list of photos through the Picasa web service.
     * Sort and prepare response to be displayed in the view.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return list of photos to display.
     */
    private doList(boolean isAjax) {
        // Initialise lists
        List<Photo> photoList = new ArrayList<Photo>()
        List<Photo> displayList = new ArrayList<Photo>()

        // Prepare display values
        int offset = new Integer(((params.offset) ? params.offset : 0)).intValue()
        int max = new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10))).intValue()
        def listView = "list"
        if(isAjax) listView = "_list"
        flash.message = ""

        log.debug("Attempting to list photos for the selected tag through the Picasa web service " +
                "(tagKeyword=" + params.id + ")")

        // Get photo list from picasa service
        try {
            photoList.addAll(picasaService.listPhotosForTag(params.id))

            log.debug("Success...")

        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Photo.list.not.available')}"
        }

        // If required, sort list
        if (params.sort) {
            switch (params.sort) {
                case "name":
                    Collections.sort(photoList, new PhotoNameComparator())
                    break
                case "description":
                    Collections.sort(photoList, new PhotoDescriptionComparator())
                    break
                case "cameraModel":
                    Collections.sort(photoList, new PhotoCameraModelComparator())
                    break
                case "dateCreated":
                    Collections.sort(photoList, new PhotoDateComparator())
                    break
            }
        }

        // If required, reverse list
        if (params.order == "asc") {
            Collections.reverse(photoList)
        }

        log.debug("Convert response into display list")

        // Convert to array to allow easy display preparation
        Photo[] photoArray = photoList.toArray()
        if (photoArray) {
            // Prepare display list
            photoArray = Arrays.copyOfRange(photoArray, offset,
                ((offset + max) > photoArray.length ? photoArray.length : (offset + max)))
            if (photoArray) {
                // Update display list
                displayList.addAll(Arrays.asList(photoArray))
            }
        }

        log.debug("Display list with " + listView + " view")

        render(view: listView, model: [photoInstanceList: displayList,
                photoInstanceTotal: (photoList?.size() ? photoList.size() : 0),
                tagKeyword: params.id])
    }
}
