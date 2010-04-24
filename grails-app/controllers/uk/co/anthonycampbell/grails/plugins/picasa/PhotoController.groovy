package uk.co.anthonycampbell.grails.plugins.picasa

import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * Photo controller
 *
 * Controller which handles all of the common actions for the Photo
 * domain class. In addition, the class also provides support for ajax
 * requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PhotoController {

    // Declare dependencies
    def grailsApplication
    def picasaService

    // Declare cache (used to reduce Google API calls)
    private static Map<String, List> tagCache = new HashMap<String, List>()

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
     * Get the selected photo through the Picasa service and
     * render the view.
     */
    def show = {
        return doShow(false)
    }

    /**
     * Get the selected photo through the Picasa service and
     * render the ajax view.
     */
    def ajaxShow = {
        return doShow(true)
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
        List<Tag> tagList = new ArrayList<Tag>()

        // Prepare display values
        def showPrivate = (grailsApplication.config.picasa.showPrivatePhotos != null) ? grailsApplication.config.picasa.showPrivatePhotos : false
        int offset = new Integer(((params.offset) ? params.offset : 0)).intValue()
        int max = new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10))).intValue()
        def listView = "list"
        if(isAjax) listView = "_list"
        flash.message = ""

        log.debug("Attempting to list photos and tags through the Picasa web service " +
                "(albumId=" + params.albumId + ")")

        // Get photo list from picasa service
        try {
            photoList.addAll(picasaService.listPhotosForAlbum(params.albumId, showPrivate))

            // Check whether tag list already exists in cache
            if (!tagCache.containsKey(params.albumId)) {
                tagList.addAll(picasaService.listTagsForAlbum(params.albumId))

                // Update cache
                tagCache.put(params.albumId, tagList)
            }

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

        // Always sort Tags alphabetically
        Collections.sort(tagList, new TagKeywordComparator())

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
                albumId: params.albumId, tagInstanceList: tagCache.get(params.albumId)])
    }

    /**
     * Request photo for provided IDs through the Picasa web service.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return photo for provided IDs.
     */
    private doShow(boolean isAjax) {

        // Initialise result
        Photo photoInstance = new Photo()

        // Prepare display values
        def showPrivate = (grailsApplication.config.picasa.showPrivatePhotos != null) ? grailsApplication.config.picasa.showPrivatePhotos : false
        def showView = "show"
        if(isAjax) showView = "_show"
        flash.message = ""

        log.debug("Attempting to get photo through the Google Picasa web service " +
                "(albumId=" + params.albumId + ", photoId=" + params.photoId + ")")

        // Get photo from picasa service
        try {
            photoInstance = picasaService.getPhoto(params.albumId, params.photoId, showPrivate)

            log.debug("Success...")
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Photo.not.found')}"
        }

        log.debug("Display photo with " + showView + " view")

        // Display photo
        render(view: showView, model: [photoInstance: photoInstance])
    }
}
