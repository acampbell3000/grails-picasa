package uk.co.anthonycampbell.grails.plugins.picasa

import grails.converters.JSON
import grails.converters.XML

import org.apache.commons.lang.StringUtils
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
    
    // Declare feed types
    public static final String RSS_FEED = "rss"
    public static final String XML_FEED = "xml"
    public static final String JSON_FEED = "json"

    // Declare dependencies
    def grailsApplication
    def picasaService
    def messageSource

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
    
    /*
     * Validate an individual comment field
     */
    def validate = {
        // Initialise domain instance and error message
        final def commentInstance = new Comment(params)
        def errorMessage = ""
        def field = ""

        // Get selected field
        for (param in params) {
            if (param?.key && !param.key.equals("action")
                    && !param.key.equals("controller")) {
                field = param.key
                break
            }
        }

		log.debug "Validating field: $field"

        // Check whether provided field has errors
        if (!commentInstance.validate() && commentInstance.errors.hasFieldErrors(field)) {
			// Get error message value
            errorMessage = messageSource.getMessage(
                commentInstance.errors.getFieldError(field),
                RCU.getLocale(request)
            )

			log.debug "Error message: $errorMessage"
        }

        // Render error message
        render(errorMessage)
    }

    /**
     * Request list of photos through the Picasa web service.
     * Sort and prepare response to be displayed in the view.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return list of photos to display.
     */
    private doList(final boolean isAjax) {
        // Initialise lists
        final List<Photo> photoList = new ArrayList<Photo>()
        final List<Photo> displayList = new ArrayList<Photo>()
        final List<Tag> tagList = new ArrayList<Tag>()

        // Check type of request
        final String feed = params.feed ?: ""
        
        // Prepare display values
        final String paramAlbumId = (params.albumId && StringUtils.isNumeric(params?.albumId)) ? params.albumId : ""
        final boolean showPrivate = grailsApplication.config?.picasa?.showPrivatePhotos ?: false
        final int offset = params.int("offset") ?: 0
        final int max = Math.min(new Integer(params.int("max") ?:
                (grailsApplication.config?.picasa?.max ?: 10)).intValue(), 500)
        final String listView = isAjax ? "_list" : "list"
        flash.message = ""

        log.debug "Attempting to list photos and tags through the Picasa web service " +
                "(albumId=$paramAlbumId)"

        // Get photo list from picasa service
        try {
            photoList.addAll(picasaService.listPhotosForAlbum(paramAlbumId, showPrivate))
            tagList.addAll(picasaService.listTagsForAlbum(paramAlbumId))

            log.debug "Success..."
            
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Photo.list.not.available', default: 'The photo listing is currently not available. Please try again later.')}"
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

        // Render correct feed
        if (feed == RSS_FEED) {
            log.debug "Display list with the $feed feed"

            // Get album information for feed
            final Album album
            try {
                album = picasaService.getAlbum(paramAlbumId, showPrivate)

            } catch (PicasaServiceException pse) { }

            // Begin RSS ouput
            render(contentType: "application/rss+xml", encoding: "UTF-8") {
                rss(version: "2.0", "xmlns:atom": "http://www.w3.org/2005/Atom") {
                    channel {
                        "atom:link"(href:"${createLink(controller: "photo", action: "list", id: paramAlbumId, absolute: true)}/feed/rss", rel: "self", type: "application/rss+xml")
                        title(album?.name ?: "")
                        link(createLink(controller: "photo", action: "list", id: paramAlbumId, absolute: "true"))
                        description(album?.description ?:
                            "${message(code: "uk.co.anthonycampbell.grails.plugins.picasa.Photo.rss.description", default: "RSS feed for the photo listing")}")
                        generator("Grails Picasa Plug-in " + grailsApplication.metadata['app.version'])
                        lastBuildDate(album?.dateCreated?.format(DateUtil.RFC_822) ?: "")

                        if (grailsApplication.config?.picasa?.rssManagingEditor instanceof String) {
                            managingEditor(grailsApplication.config.picasa.rssManagingEditor ?: "")
                        }

                        if (album?.image) {
                            image {
                                url(album.image ?: "")
                                title(album.name ?: "")
                                link(createLink(controller: "photo", action: "list", id: paramAlbumId,
                                        absolute: "true"))
                            }
                        }

                        for (p in photoList) {
                            item {
                                guid(isPermaLink: "false", p.photoId)
                                pubDate(p?.dateCreated?.format(DateUtil.RFC_822))
                                "atom:updated"(DateUtil.formatDateRfc3339(p?.dateCreated))
                                title(p?.title)
                                description(p?.description)
                                link(createLink(controller: "photo", action: "show", id: paramAlbumId +
                                    "/" + p?.photoId, absolute: "true"))

                                if (p?.image) {
                                    enclosure(type: "image/jpeg", url: p?.image, length: "0")
                                }
                            }   
                        }
                    }
                }
            }
        } else if (feed == XML_FEED || feed == JSON_FEED) {
            log.debug "Display list with $feed feed"
            
            // Declare possible feed render types
            final def xmlType = [contentType: "text/xml", encoding: "UTF-8"]
            final def jsonType = [builder: "json", encoding: "UTF-8"]

            // Determine correct type
            final def type = (feed == XML_FEED ? xmlType : jsonType)

            // Begin ouput
            render(type) {
                photos {
                    for (p in photoList) {
                        photo {
                            // Main attributes
                            photoId(p?.photoId)
                            albumId(p?.albumId)
                            title(p?.title)
                            description(p?.description)
                            cameraModel(p?.cameraModel)
                            geoLocation {
                                latitude(p?.geoLocation?.latitude)
                                longitude(p?.geoLocation?.longitude)
                            }
                            thumbnailImage(p?.thumbnailImage)
                            thumbnailWidth(p?.thumbnailWidth)
                            thumbnailHeight(p?.thumbnailHeight)
                            image(p?.image)
                            width(p?.width)
                            height(p?.height)
                            previousPhotoId(p?.previousPhotoId)
                            nextPhotoId(p?.nextPhotoId)
                            dateCreated(p?.dateCreated?.format(DateUtil.RFC_822))
                            isPublic(p?.isPublic)

                            // Tags
                            tags {
                                for(t in p?.tags) {
                                    tag(t?.keyword)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            log.debug "Convert response into display list"

            // Convert to array to allow easy display preparation
            Photo[] photoArray = photoList.toArray()
            if (photoArray) {
                // Prepare display list
                photoArray = Arrays.copyOfRange(photoArray, offset,
                    ((offset + max) > photoArray?.length ? photoArray?.length : (offset + max)))
                if (photoArray) {
                    // Update display list
                    displayList.addAll(Arrays.asList(photoArray))
                }
            }

            log.debug "Display list with $listView view"

            render(view: listView, model: [albumId: paramAlbumId,
                    photoInstanceList: displayList,
                    photoInstanceTotal: (photoList.size() ?: 0),
                    tagInstanceList: tagList])
        }
    }

    /**
     * Request photo for provided IDs through the Picasa web service.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return photo for provided IDs.
     */
    private doShow(final boolean isAjax) {
        // Initialise result
        Photo photoInstance = new Photo()
        final List<Comment> commentList = new ArrayList<Comment>()
        final List<Comment> commentDisplayList = new ArrayList<Comment>()
        final Comment commentInstance = new Comment()
        
        // Check type of request
        final String feed = params.feed ?: ""

        // Prepare display values
        final String albumId = (params.albumId && StringUtils.isNumeric(params.albumId)) ? params.albumId : ""
        final String photoId = (params.photoId && StringUtils.isNumeric(params.photoId)) ? params.photoId : ""
        final boolean showPrivate = grailsApplication.config?.picasa?.showPrivatePhotos ?: false
        int offset = params.int("offset") ?: -1
        final int max = Math.min(new Integer(params.int("max") ?:
                (grailsApplication.config?.picasa?.maxComments ?: 10)).intValue(), 500)
        final String showView = isAjax ? "_show" : "show"
        flash.message = ""

        // Prepare new comment
        commentInstance.albumId = albumId
        commentInstance.albumId = photoId
        commentInstance.message = "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Comment.message.default', default: 'Add a comment...')}"

        log.debug "Attempting to get photo through the Google Picasa web service " +
                "(albumId=$albumId, photoId=$photoId)"

        // Get photo from picasa service
        try {
            photoInstance = picasaService.getPhoto(albumId, photoId, showPrivate)

            log.debug "Success..."
            
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Photo.not.found', default: 'The photo you\'ve selected could not be found. Please ensure the ID is correct and try again.')}"
        }

        // Get comments from photo
        if (photoInstance?.comments) {
            commentList.addAll(photoInstance.comments)
        }

        // Convert to array to allow easy display preparation
        Comment[] commentArray = commentList.toArray()
        if (commentArray) {
            log.debug "Prepare comments for display (offset=$offset, max=$max, " +
                "length=${commentArray?.length})"

            // By default show the last set of comments
            if (offset < 0) {
                final def lastOffset = Math.floor(
                    new Double((commentArray?.length / max) ?: 0.00).doubleValue())
                if (lastOffset) {
                    // Reset offset to allow pagination to be updated correctly
                    offset = params.offset = lastOffset
                }
            }

            // Prepare display list
            commentArray = Arrays.copyOfRange(commentArray, offset,
                ((offset + max) > commentArray?.length ? commentArray?.length : (offset + max)))
            
            if (commentArray) {
                // Update display list
                commentDisplayList.addAll(Arrays.asList(commentArray))
            }
        }

        log.debug "Display photo with $showView view"

        // Display photo
        render(view: showView, model: [albumId: albumId,
                photoId: photoId,
                photoInstance: photoInstance,
                commentInstanceList: commentDisplayList,
                commentInstanceTotal: (commentList?.size() ?: 0),
                commentInstance: commentInstance])
    }
}
