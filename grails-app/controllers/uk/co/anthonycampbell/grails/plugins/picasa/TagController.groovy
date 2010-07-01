package uk.co.anthonycampbell.grails.plugins.picasa

import org.apache.commons.lang.StringUtils

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
        redirect(controller: "tag", action: "list", params: params)
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
        final List<Tag> tagList = new ArrayList<Tag>()
        final List<Tag> displayList = new ArrayList<Tag>()

        // Check type of request
        final String feed = params.feed ?: ""

        // Prepare display values
        final int offset = params.int("offset") ?: 0
        final int max = Math.min(new Integer(params.int("max") ?:
                (grailsApplication?.config?.picasa?.maxKeywords ?: 10)).intValue(), 500)
        final String listView = isAjax ? "_list" : "list"
        flash.message = ""

        log.debug "Attempting to list tags through the Google Picasa web service"

        // Get photo list from picasa service
        try {
            tagList.addAll(picasaService?.listAllTags())

            log.debug "Success..."

        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Tag.list.not.available', default: 'The tag listing is currently not available. Please try again later.')}"
        }

        // Sort tags
        Collections.sort(tagList, new TagKeywordComparator())

        // If required, reverse list
        if (params.order == "asc") {
            Collections.reverse(tagList)
        }

        // Render correct feed
        if (feed == RSS_FEED) {
            log.debug "Display list with the $feed feed"

            // Last build date
            final Date date = new Date()

            // Begin RSS ouput
            render(contentType: "application/rss+xml", encoding: "UTF-8") {
                rss(version: "2.0", "xmlns:atom": "http://www.w3.org/2005/Atom") {
                    channel {
                        "atom:link"(href: "${createLink(controller: "tag", action: "list", absolute: true)}/feed/rss", rel: "self", type: "application/rss+xml")
                        title(message(code: "uk.co.anthonycampbell.grails.plugins.picasa.Tag.legend",
                                default: "Tag Listing"))
                        link(createLink(controller: "tag", action: "list", absolute: "true"))
                        description(message(code: "uk.co.anthonycampbell.grails.plugins.picasa.Tag.rss.description",
                                default: "RSS feed for the tag listing"))
                        generator("Grails Picasa Plug-in " + grailsApplication.metadata['app.version'])
                        lastBuildDate(date.format(DateUtil.RFC_822))

                        if (grailsApplication.config?.picasa?.rssManagingEditor instanceof String) {
                            managingEditor(grailsApplication.config?.picasa?.rssManagingEditor ?: "")
                        }

                        for (t in tagList) {
                            item {
                                guid(isPermaLink: "false", t?.keyword)
                                title(t?.keyword)
                                description(t?.keyword)
                                link(createLink(controller: "tag", action: "show", id: t?.keyword,
                                        absolute: "true"))
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
                tags {
                    for (t in tagList) {
                        tag {
                            keyword(t?.keyword)
                            weight(t?.weight)
                            displayWeight(t?.displayWeight)
                        }
                    }
                }
            }
        } else {
            log.debug "Convert response into display list"

            // Convert to array to allow easy display preparation
            Tag[] tagArray = tagList.toArray()
            if (tagArray) {
                // Prepare display list
                tagArray = Arrays.copyOfRange(tagArray, offset,
                    ((offset + max) > tagArray?.length ? tagArray?.length : (offset + max)))
                if (tagArray) {
                    // Update display list
                    displayList.addAll(Arrays.asList(tagArray))
                }
            }
            
            log.debug "Display list with $listView view"

            render(view: listView, model: [tagInstanceList: displayList,
                    tagInstanceTotal: (tagList?.size() ?: 0)])
        }
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
        final List<Photo> photoList = new ArrayList<Photo>()
        final List<Photo> displayList = new ArrayList<Photo>()

        // Check type of request
        final String feed = params.feed ?: ""

        // Prepare display values
        final String paramTagId = params.id ?: ""
        final boolean showPrivate = grailsApplication.config?.picasa?.showPrivatePhotos ?: false
        final int offset = params.int("offset") ?: 0
        final int max = Math.min(new Integer(params.int("max") ?:
                (grailsApplication.config?.picasa?.max ?: 10)).intValue(), 500)
        final String listView = isAjax ? "_show" : "show"
        flash.message = ""

        log.debug("Attempting to list photos for the selected tag through the Picasa web service " +
                "(tagKeyword=$paramTagId)")

        // Get photo list from picasa service
        try {
            photoList.addAll(picasaService.listPhotosForTag(paramTagId, showPrivate))

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
        } else {
            Collections.sort(photoList, new PhotoDateComparator())
        }

        // If required, reverse list
        if (params.order == "asc") {
            Collections.reverse(photoList)
        }
        
        // Render correct feed
        if (feed == RSS_FEED) {
            log.debug "Display list with the $feed feed"

            // Keep track of latest album date
            def latestBuildDate

            // Begin RSS ouput
            render(contentType: "application/rss+xml", encoding: "UTF-8") {
                rss(version: "2.0", "xmlns:atom": "http://www.w3.org/2005/Atom") {
                    channel {
                        "atom:link"(href: "${createLink(controller: "tag", action: "show", id: paramTagId, absolute: true)}/feed/rss", rel: "self", type: "application/rss+xml")
                        title(message(code: "uk.co.anthonycampbell.grails.plugins.picasa.Tag.details.legend",
                                default: "Tag Listing", args: [paramTagId]))
                        link(createLink(controller: "tag", action: "show", id: paramTagId, absolute: "true"))
                        description(message(code: "uk.co.anthonycampbell.grails.plugins.picasa.Tag.rss.description",
                                default: "RSS feed for the tag listing"))
                        generator("Grails Picasa Plug-in " + grailsApplication.metadata['app.version'])
                        
                        if (grailsApplication.config?.picasa?.rssManagingEditor instanceof String) {
                            managingEditor(grailsApplication.config.picasa.rssManagingEditor ?: "")
                        }

                        for (p in photoList) {
                            item {
                                guid(isPermaLink: "false", p?.photoId)
                                pubDate(p?.dateCreated?.format(DateUtil.RFC_822))
                                "atom:updated"(DateUtil.formatDateRfc3339(p?.dateCreated))
                                title(p?.title)
                                description(p?.description)
                                link(createLink(controller: "tag", action: "show",
                                        id: p?.albumId + "/" + p?.photoId, absolute: "true"))

                                if (p?.image) {
                                    enclosure(type: "image/jpeg", url: p?.image, length: "0")
                                }
                            }

                            if (!latestBuildDate || latestBuildDate?.compareTo(p?.dateCreated) < 0) {
                                latestBuildDate = p?.dateCreated
                            }
                        }

                        lastBuildDate(latestBuildDate?.format(DateUtil.RFC_822) ?: "")
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
                                    tag {
                                        keyword(t?.keyword)
                                        weight(t?.weight)
                                        displayWeight(t?.displayWeight)
                                    }
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

            log.debug "Display list with $listView view and keyword $paramTagId"

            render(view: listView, model: [photoInstanceList: displayList,
                    photoInstanceTotal: (photoList?.size() ?: 0),
                    tagKeyword: paramTagId])
        }
    }
}
