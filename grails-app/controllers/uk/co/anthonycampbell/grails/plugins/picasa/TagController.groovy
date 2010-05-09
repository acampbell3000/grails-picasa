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
     * Get photos for the selected tag through the Picasa service and
     * render the view.
     */
    def show = {
        return doShow(false)
    }

    /**
     * Get photos for the selected tag through the Picasa service and
     * render the ajax view.
     */
    def ajaxShow = {
        return doShow(true)
    }

    /**
     * Request list of tags through the Picasa web service.
     * Sort and prepare response to be displayed in the view.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return list of photos to display.
     */
    private doList(boolean isAjax) {
        // Initialise lists
        List<Tag> tagList = new ArrayList<Tag>()
        List<Tag> displayList = new ArrayList<Tag>()

        // Prepare display values
        int offset = new Integer(((params.offset) ? params.offset : 0)).intValue()
        int max = new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.maxKeywords) ? grailsApplication.config.picasa.maxKeywordsCon : 10))).intValue()
        def listView = "list"
        if(isAjax) listView = "_list"
        flash.message = ""

        log.debug("Attempting to list tags through the Picasa web service")

        // Get photo list from picasa service
        try {
            tagList.addAll(picasaService.listAllTags())

            log.debug("Success...")

        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Tag.list.not.available')}"
        }

        // Sort tags
        Collections.sort(tagList, new TagKeywordComparator())

        // If required, reverse list
        if (params.order == "asc") {
            Collections.reverse(tagList)
        }

        log.debug("Convert response into display list")

        // Convert to array to allow easy display preparation
        Tag[] tagArray = tagList.toArray()
        if (tagArray) {
            // Prepare display list
            tagArray = Arrays.copyOfRange(tagArray, offset,
                ((offset + max) > tagArray.length ? tagArray.length : (offset + max)))
            if (tagArray) {
                // Update display list
                displayList.addAll(Arrays.asList(tagArray))
            }
        }

        log.debug("Display list with " + listView + " view")

        render(view: listView, model: [tagInstanceList: displayList,
                tagInstanceTotal: (tagList?.size() ? tagList.size() : 0)])
    }

    /**
     * Request list of photos through the Picasa web service.
     * Sort and prepare response to be displayed in the view.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return list of photos to display.
     */
    private doShow(boolean isAjax) {
        // Initialise lists
        List<Photo> photoList = new ArrayList<Photo>()
        List<Photo> displayList = new ArrayList<Photo>()

        // Prepare display values
        def showPrivate = (grailsApplication.config.picasa.showPrivatePhotos != null) ? grailsApplication.config.picasa.showPrivatePhotos : false
        int offset = new Integer(((params.offset) ? params.offset : 0)).intValue()
        int max = new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10))).intValue()
        def listView = "show"
        if(isAjax) listView = "_show"
        flash.message = ""

        log.debug("Attempting to list photos for the selected tag through the Picasa web service " +
                "(tagKeyword=" + params.id + ")")

        // Get photo list from picasa service
        try {
            photoList.addAll(picasaService.listPhotosForTag(params.id, showPrivate))

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
        } else {
            Collections.sort(photoList, new PhotoDateComparator())
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

        log.debug("Display list with " + listView + " view and keyword " + params.id)

        render(view: listView, model: [photoInstanceList: displayList,
                photoInstanceTotal: (photoList?.size() ? photoList.size() : 0),
                tagKeyword: params.id])
    }
}
