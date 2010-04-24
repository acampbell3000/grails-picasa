package uk.co.anthonycampbell.grails.plugins.picasa

import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * Album controller
 *
 * Controller which handles all of the common actions for the Album
 * domain class. In addition, the class also provides support for ajax
 * requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class AlbumController {
    
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
     * Prepare and render the album list view.
     */
    def list = {
        return doList(false)
    }

    /**
     * Prepare and render the album list view.
     */
    def ajaxList = {
        return doList(true)
    }

    /**
     * Get the selected album through the Picasa service and
     * render the view.
     */
    def show = {
        return doShow(false)
    }

    /**
     * Get the selected album through the Picasa service and
     * render the ajax view.
     */
    def ajaxShow = {
        return doShow(true)
    }

    /**
     * Request list of albums and tags through the Picasa web service.
     * Sort and prepare response to be displayed in the view.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return list of albums to display.
     */
    private doList(boolean isAjax) {
        // Initialise lists
        List<Album> albumList = new ArrayList<Album>()
        List<Album> displayList = new ArrayList<Album>()
        List<Tag> tagList = new ArrayList<Tag>()

        // Prepare display values
        def showPrivate = (grailsApplication.config.picasa.showPrivateAlbums != null) ? grailsApplication.config.picasa.showPrivateAlbums : false
        int offset = new Integer(((params.offset) ? params.offset : 0)).intValue()
        int max = new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10))).intValue()
        def listView = "list"
        if(isAjax) listView = "_list"
        flash.message = ""

        log.debug("Attempting to list albums (showPrivateAlbums=" + showPrivate +
            ") and tags through the Picasa web service")

        // Get album list from picasa service
        try {
            albumList.addAll(picasaService.listAlbums(showPrivate))
            tagList.addAll(picasaService.listAllTags())

            log.debug("Success...")
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Album.list.not.available')}"
        }

        // If required, sort list
        if (params.sort) {
            switch (params.sort) {
                case "name":
                    Collections.sort(albumList, new AlbumNameComparator())
                    break
                case "description":
                    Collections.sort(albumList, new AlbumDescriptionComparator())
                    break
                case "location":
                    Collections.sort(albumList, new AlbumLocationComparator())
                    break
                case "dateCreated":
                    Collections.sort(albumList, new AlbumDateComparator())
                    break
            }
        }

        // If required, reverse list
        if (params.order == "asc") {
            Collections.reverse(albumList)
        }

        // Always sort Tags alphabetically
        Collections.sort(tagList, new TagKeywordComparator())

        log.debug("Convert response into display list")

        // Convert to array to allow easy display preparation
        Album[] albumArray = albumList.toArray()
        if (albumArray) {
            // Prepare display list
            albumArray = Arrays.copyOfRange(albumArray, offset,
                ((offset + max) > albumArray.length ? albumArray.length : (offset + max)))
            if (albumArray) {
                // Update display list
                displayList.addAll(Arrays.asList(albumArray))
            }
        }

        log.debug("Display list with " + listView + " view")

        render(view: listView, model: [albumInstanceList: displayList,
                albumInstanceTotal: (albumList?.size() ? albumList.size() : 0),
                tagInstanceList: tagList])
    }

    /**
     * Request album for provided ID through the Picasa web service.
     * 
     * @param isAjax whether the request is from an Ajax call.
     * @return album for provided ID.
     */
    private doShow(boolean isAjax) {
        
        // Initialise result
        Album albumInstance = new Album()

        // Prepare display values
        def showPrivate = (grailsApplication.config.picasa.showPrivateAlbums != null) ? grailsApplication.config.picasa.showPrivateAlbums : false
        def showView = "show"
        if(isAjax) showView = "_show"
        flash.message = ""

        log.debug("Attempting to get album ID " + params.id +
            " (showPrivateAlbums=" + showPrivate + ") through the Google Picasa web service")

        // Get album from picasa service
        try {
            albumInstance = picasaService.getAlbum(params.id, showPrivate)

            log.debug("Success...")
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Album.not.found')}"
        }

        log.debug("Display album with " + showView + " view")

        // Display album
        render(view: showView, model: [albumInstance: albumInstance])
    }
}
