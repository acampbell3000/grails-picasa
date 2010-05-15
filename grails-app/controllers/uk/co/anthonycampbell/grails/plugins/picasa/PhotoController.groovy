package uk.co.anthonycampbell.grails.plugins.picasa

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
     * Get the selected photo comments through the Picasa service and
     * render the ajax view.
     */
    def comments = {
        return doComments()
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
        final String feed = (StringUtils.isNotEmpty(params.feed)) ? params.feed : ""
        
        // Prepare display values
        final String paramAlbumId = (StringUtils.isNotEmpty(params.albumId) && StringUtils.isNumeric(params.albumId)) ? params.albumId : ""
        final boolean showPrivate = (grailsApplication.config.picasa.showPrivatePhotos != null) ? grailsApplication.config.picasa.showPrivatePhotos : false
        final int offset = new Integer(((params.offset) ? params.offset : 0)).intValue()
        final int max = Math.min(new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10))).intValue(), 500)
        String listView = "list"
        if (isAjax) listView = "_list"
        flash.message = ""

        log.debug("Attempting to list photos and tags through the Picasa web service " +
                "(albumId=" + paramAlbumId + ")")

        // Get photo list from picasa service
        try {
            photoList.addAll(picasaService.listPhotosForAlbum(paramAlbumId, showPrivate))
            tagList.addAll(picasaService.listTagsForAlbum(paramAlbumId))

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

        // Render correct feed
        if (feed == RSS_FEED) {
            log.debug("Display list with the " + feed + " feed")

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
                        title((album != null && StringUtils.isNotEmpty(album.name)) ? album.name : "")
                        link(createLink(controller: "photo", action: "list", id: paramAlbumId, absolute: "true"))
                        description((album != null && StringUtils.isNotEmpty(album.description)) ? album.description : "")
                        generator("Grails Picasa Plug-in " + grailsApplication.metadata['app.version'])
                        lastBuildDate((album != null && album.dateCreated != null) ? album.dateCreated?.format(DateUtil.RFC_822) : "")

                        if (!grailsApplication.config.picasa.rssManagingEditor instanceof String) {
                            managingEditor(StringUtils.isNotEmpty(grailsApplication.config.picasa.rssManagingEditor) ? grailsApplication.config.picasa.rssManagingEditor : "")
                        }

                        if (album != null && StringUtils.isNotEmpty(album.image)) {
                            image {
                                url(StringUtils.isNotEmpty(album.image) ? album.image : "")
                                title(StringUtils.isNotEmpty(album.name) ? album.name : "")
                                link(createLink(controller: "photo", action: "list", id: paramAlbumId, absolute: "true"))
                            }
                        }

                        for (p in photoList) {
                            item {
                                guid(isPermaLink: "false", p.photoId)
                                pubDate(p.dateCreated?.format(DateUtil.RFC_822))
                                "atom:updated"(DateUtil.formatDateRfc3339(p.dateCreated))
                                title(p.title)
                                description(p.description)
                                link(createLink(controller: "photo", action: "show", id: paramAlbumId + "/" + p.photoId, absolute: "true"))

                                if (StringUtils.isNotEmpty(p.image)) {
                                    enclosure(type: "image/jpeg", url: p.image, length: "0")
                                }
                            }   
                        }
                    }
                }
            }
        } else if (feed == XML_FEED || feed == JSON_FEED) {
            log.debug("Display list with " + feed + " feed")
            
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
                            photoId(p.photoId)
                            albumId(p.albumId)
                            title(p.title)
                            description(p.description)
                            cameraModel(p.cameraModel)
                            geoLocation {
                                latitude(p.geoLocation?.latitude)
                                longitude(p.geoLocation?.longitude)
                            }
                            thumbnailImage(p.thumbnailImage)
                            thumbnailWidth(p.thumbnailWidth)
                            thumbnailHeight(p.thumbnailHeight)
                            image(p.image)
                            width(p.width)
                            height(p.height)
                            previousPhotoId(p.previousPhotoId)
                            nextPhotoId(p.nextPhotoId)
                            dateCreated(p.dateCreated?.format(DateUtil.RFC_822))
                            isPublic(p.isPublic)

                            // Tags
                            if (tagList?.size > 0) {
                                tags {
                                    for(t in tagList) {
                                        tag(t?.keyword)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            log.debug("Convert response into display list")

            // Convert to array to allow easy display preparation
            Photo[] photoArray = photoList.toArray()
            if (photoArray != null) {
                // Prepare display list
                photoArray = Arrays.copyOfRange(photoArray, offset,
                    ((offset + max) > photoArray.length ? photoArray.length : (offset + max)))
                if (photoArray) {
                    // Update display list
                    displayList.addAll(Arrays.asList(photoArray))
                }
            }

            log.debug("Display list with " + listView + " view")

            render(view: listView, model: [albumId: paramAlbumId,
                    photoInstanceList: displayList,
                    photoInstanceTotal: (photoList?.size() ? photoList.size() : 0),
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

        // Check type of request
        final String feed = (StringUtils.isNotEmpty(params.feed)) ? params.feed : ""

        // Prepare display values
        final String albumId = (StringUtils.isNotEmpty(params.albumId) && StringUtils.isNumeric(params.albumId)) ? params.albumId : null
        final String photoId = (StringUtils.isNotEmpty(params.photoId) && StringUtils.isNumeric(params.photoId)) ? params.photoId : null
        final boolean showPrivate = (grailsApplication.config.picasa.showPrivatePhotos != null) ? grailsApplication.config.picasa.showPrivatePhotos : false
        final int offset = new Integer(((params.offset) ? params.offset : 0)).intValue()
        final int max = Math.min(new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.maxComments) ? grailsApplication.config.picasa.maxComments : 10))).intValue(), 500)
        String showView = "show"
        if (isAjax) showView = "_show"
        flash.message = ""

        log.debug("Attempting to get photo through the Google Picasa web service " +
                "(albumId=" + albumId + ", photoId=" + photoId + ")")

        // Get photo from picasa service
        try {
            photoInstance = picasaService.getPhoto(albumId, photoId, showPrivate)

            log.debug("Success...")
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Photo.not.found')}"
        }

        // Get comments from photo
        if (photoInstance.comments != null) {
            commentList.addAll(photoInstance.comments)
        }

        log.debug("Prepare comments for display")

        // Convert to array to allow easy display preparation
        Comment[] commentArray = commentList.toArray()
        if (commentArray != null) {
            // Prepare display list
            commentArray = Arrays.copyOfRange(commentArray, offset,
                ((offset + max) > commentArray.length ? commentArray.length : (offset + max)))
            if (commentArray) {
                // Update display list
                commentDisplayList.addAll(Arrays.asList(commentArray))
            }
        }

        log.debug("Display photo with " + showView + " view")

        // Display photo
        render(view: showView, model: [albumId: params.albumId,
                photoId: params.photoId,
                photoInstance: photoInstance,
                photoComments: commentDisplayList,
                photoCommentTotal: (commentList?.size() ? commentList.size() : 0)])
    }

    /**
     * Request photo comments for provided IDs through the Picasa web service.
     * 
     * @return comments for provided IDs.
     */
    private doComments() {

        // Initialise result
        final List<Comment> commentList = new ArrayList<Comment>()
        final List<Comment> commentDisplayList = new ArrayList<Comment>()

        // Check type of request
        final String feed = (params.feed != null && !params.feed == "") ? params.feed : ""

        // Prepare display values
        final String albumId = (StringUtils.isNotEmpty(params.albumId) && StringUtils.isNumeric(params.albumId)) ? params.albumId : null
        final String photoId = (StringUtils.isNotEmpty(params.photoId) && StringUtils.isNumeric(params.photoId)) ? params.photoId : null
        final int offset = new Integer(((params.offset) ? params.offset : 0)).intValue()
        final int max = Math.min(new Integer(((params.max) ? params.max : ((grailsApplication.config.picasa.maxComments) ? grailsApplication.config.picasa.maxComments : 10))).intValue(), 500)
        final String showView = "_comments"
        flash.message = ""

        log.debug("Attempting to get comments through the Google Picasa web service " +
                "(albumId=" + albumId + ", photoId=" + photoId + ")")

        // Get photo from picasa service
        try {
            commentList.addAll(picasaService.listCommentsForPhoto(albumId, photoId))

            log.debug("Success...")
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Comment.not.found')}"
        }

        log.debug("Prepare comments for display")

        // Convert to array to allow easy display preparation
        Comment[] commentArray = commentList.toArray()
        if (commentArray != null) {
            // Prepare display list
            commentArray = Arrays.copyOfRange(commentArray, offset,
                ((offset + max) > commentArray.length ? commentArray.length : (offset + max)))
            if (commentArray) {
                // Update display list
                commentDisplayList.addAll(Arrays.asList(commentArray))
            }
        }

        log.debug("Display comments with " + showView + " view")

        // Display photo
        render(view: showView, model: [albumId: albumId,
                photoId: photoId,
                photoComments: commentDisplayList,
                photoCommentTotal: (commentList?.size() ? commentList.size() : 0)])
    }
}
