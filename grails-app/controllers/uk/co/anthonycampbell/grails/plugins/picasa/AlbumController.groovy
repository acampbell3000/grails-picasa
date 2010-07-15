package uk.co.anthonycampbell.grails.plugins.picasa

/**
 * Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import uk.co.anthonycampbell.grails.plugins.picasa.comparator.*

import grails.converters.JSON
import grails.converters.XML

import org.apache.commons.lang.StringUtils

/**
 * Album controller
 *
 * Controller which handles all of the common actions for the Album
 * domain class. In addition, the class also provides support for ajax
 * requests.

                log?.debug "End ${PicasaQuery.LIST_ALL_TAGS.getMethod()}\n"

 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class AlbumController {

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
        final List<Album> albumList = new ArrayList<Album>()
        final List<Album> displayList = new ArrayList<Album>()
        final List<Tag> tagList = new ArrayList<Tag>()

        // Check type of request
        final String feed = params.feed ?: ""

        // Prepare display values
        final boolean showPrivate = grailsApplication.config?.picasa?.showPrivateAlbums ?: false
        final int offset = params.int("offset") ?: 0
        final int max = Math.min(new Integer(params.int("max") ?:
                (grailsApplication.config?.picasa?.max ?: 10)).intValue(), 500)
        final String listView = isAjax ? "_list" : "list"
        flash.message = ""

        log.debug "Attempting to list albums (showPrivateAlbums=$showPrivate" +
            ") and tags through the Picasa web service"

        // Get album list from picasa service
        try {
            albumList.addAll(picasaService.listAlbums(showPrivate))
            tagList.addAll(picasaService.listAllTags())


            log.debug "Success..."
            
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Album.list.not.available', default: 'The photo album listing is currently not available. Please try again later.')}"
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
        
        // Render correct feed
        if (feed == RSS_FEED) {
            log.debug "Display list with the $feed feed"

            // Keep track of latest album date
            def latestBuildDate

            // Begin RSS ouput
            render(contentType: "application/rss+xml", encoding: "UTF-8") {
                rss(version: "2.0", "xmlns:atom": "http://www.w3.org/2005/Atom") {
                    channel {
                        "atom:link"(href:"${createLink(controller: "album", action: "list", absolute: true)}/feed/rss", rel: "self", type: "application/rss+xml")
                        title(message(code: "uk.co.anthonycampbell.grails.plugins.picasa.Album.legend",
                                default: "Photo Albums"))
                        link(createLink(controller: "album", action: "list", absolute: "true"))
                        description(message(code: "uk.co.anthonycampbell.grails.plugins.picasa.Album.rss.description",
                                default: "Photo Albums"))
                        generator("Grails Picasa Plug-in " + grailsApplication.metadata['app.version'])

                        if (grailsApplication.config?.picasa?.rssManagingEditor instanceof String) {
                            managingEditor(grailsApplication.config.picasa.rssManagingEditor ?: "")
                        }
                        
                        for (a in albumList) {
                            item {

                                guid(isPermaLink: "false", a?.albumId)
                                pubDate(a?.dateCreated?.format(DateUtil.RFC_822))
                                "atom:updated"(DateUtil.formatDateRfc3339(a?.dateCreated))
                                title(a?.name)
                                description(a?.description)
                                link(createLink(controller: "album", action: "show", id: a?.albumId,
                                        absolute: "true"))
                                
                                if (a?.image) {
                                    enclosure(type: "image/jpeg", url: a?.image, length: "0")
                                }
                            }
                            
                            if (!latestBuildDate || latestBuildDate?.compareTo(a?.dateCreated) < 0) {
                                latestBuildDate = a?.dateCreated
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
                albums {
                    for (a in albumList) {
                        album {
                            // Main attributes
                            albumId(a?.albumId)
                            name(a?.name)
                            description(a?.description)
                            location(a?.location)
                            geoLocation {
                                latitude(a?.geoLocation?.latitude)
                                longitude(a?.geoLocation?.longitude)
                            }
                            image(a?.image)
                            width(a?.width)
                            height(a?.height)
                            dateCreated(a?.dateCreated?.format(DateUtil.RFC_822))
                            isPublic(a?.isPublic)

                            // Tags
                            tags {
                                for(t in a?.tags) {
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
            Album[] albumArray = albumList.toArray()
            if (albumArray) {
                // Prepare display list
                albumArray = Arrays.copyOfRange(albumArray, offset,

                    ((offset + max) > albumArray?.length ? albumArray?.length : (offset + max)))
                if (albumArray) {
                    // Update display list
                    displayList.addAll(Arrays.asList(albumArray))
                }
            }

            log.debug "Display list with $listView view"

            render(view: listView, model: [albumInstanceList: displayList,
                    albumInstanceTotal: (albumList?.size() ?: 0),
                    tagInstanceList: tagList])
        }
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
        final String albumId = params?.id ?: ""
        final boolean showPrivate = grailsApplication.config?.picasa?.showPrivateAlbums ?: false
        final String showView = isAjax ? "_show" : "show"
        flash.message = ""

        log.debug "Attempting to get album ID $albumId" +
            " (showPrivateAlbums=$showPrivate) through the Google Picasa web service"

        // Get album from picasa service
        try {
            albumInstance = picasaService.getAlbum(albumId, showPrivate)

            log.debug "Success..."
            
        } catch (PicasaServiceException pse) {
            flash.message =
                "${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Album.not.found', default: 'The photo album you\'ve selected could not be found. Please ensure the ID is correct and try again.')}"
        }

        log.debug "Display album with $showView view"

        // Display album
        render(view: showView, model: [albumInstance: albumInstance])
    }
}
