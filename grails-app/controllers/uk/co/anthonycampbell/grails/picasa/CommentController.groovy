package uk.co.anthonycampbell.grails.picasa

/**
 * Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import uk.co.anthonycampbell.grails.picasa.comparator.*

import grails.converters.JSON
import grails.converters.XML

import org.apache.commons.lang.StringUtils

/**
 * Comment controller
 *
 * Controller which handles all of the common actions for the Comment
 * domain class. In addition, the class also provides support for ajax
 * requests.
 */
class CommentController {

    // Declare feed types
    public static final String RSS_FEED = "rss"
    public static final String XML_FEED = "xml"
    public static final String JSON_FEED = "json"

    // Placeholder for the album / photo ID separator
    public static final String ID_SEPARATOR = ":"

    // Declare dependencies
    def grailsApplication
    def picasaService
    def picasaCommentService
    
    // Delete, save and update actions only accept POST requests
	static allowedMethods = [delete: 'POST', save: 'POST', ajaxSave: 'POST']
    
    // Check user authorisation before adding any new comments
    def beforeInterceptor = [action: this.&checkUser,
        except: ['index', 'list', 'ajaxList', 'login', 'ajaxLogin', 'logout', 'ajaxLogout']]

    /**
     * Re-direct index requests to list view
     */
    def index = {
        redirect(controller: "comment", action: "list", params: params)
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
     * Apply OAuth access token returned to the Picasa service and
     * re-direct to the original photo.
     */
    def login = {
        doLogin()
    }

    /**
     * Invoke logout method.
     */
    def logout = {
        doLogout(false)
    }

    /**
     * Invoke ajax logout method.
     */
    def ajaxLogout = {
        doLogout(true)
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
     * Request list of comments through the Picasa web service.
     * Sort and prepare response to be displayed in the view.
     *
     * @param isAjax whether the request is from an Ajax call.
     * @return list of photos to display.
     */
    private doList(final boolean isAjax) {
        // Initialise lists
        final List<Comment> commentList = new ArrayList<Comment>()
        final List<Comment> displayList = new ArrayList<Comment>()

        // Check type of request
        final String feed = "${params.feed}" ?: ""

        // Prepare display values
        final String paramAlbumId = (params.albumId && StringUtils.isNumeric(params.albumId)) ? "${params.albumId}" : ""
        final String paramPhotoId = (params.photoId && StringUtils.isNumeric(params.photoId)) ? "${params.photoId}" : ""
        int offset = (params.int("offset") == null) ? -1 : params.int("offset")
        final int max = Math.min(new Integer(params.int("max") ?:
                (grailsApplication?.config?.picasa?.maxComments ?: 10)).intValue(), 500)
        final String listView = isAjax ? "_list" : "list"
        flash.message = ""
        flash.oauthError = ""
        
        log.debug "Attempting to get comments through the Google Picasa web service " +
            "(albumId = $paramAlbumId, photoId = $paramPhotoId)"

        // Get comment list from picasa service
        try {
            if (paramAlbumId && paramPhotoId) {
                commentList.addAll(picasaService.listCommentsForPhoto(paramAlbumId, paramPhotoId))
            } else {
                commentList.addAll(picasaService.listAllComments())
            }
            
            log.debug "Success..."

        } catch (PicasaServiceException pse) {
            log.error("Unable to get comments through the Google Picasa web service " +
                "(albumId = $paramAlbumId, photoId = $paramPhotoId)", pse)

            flash.message = message(code: 'uk.co.anthonycampbell.grails.picasa.Comment.list.not.available',
                default: 'The comment listing is currently not available. Please try again later.')
        }

        // If required, reverse list
        if (params.order == "asc") {
            Collections.reverse(commentList)
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
                        "atom:link"(href:"${createLink(controller: "comment", action: "list", absolute: true)}/feed/rss", rel: "self", type: "application/rss+xml")
                        title(message(code: "uk.co.anthonycampbell.grails.picasa.Comment.legend",
                                default: "Photo Comments"))
                        link(createLink(controller: "comment", action: "list", absolute: "true"))
                        description(message(code: "uk.co.anthonycampbell.grails.picasa.Comment.rss.description",
                                default: "RSS feed for the photo comment listing"))
                        generator("Grails Picasa Plug-in " + grailsApplication.metadata['app.version'])

                        if (grailsApplication?.config?.picasa?.rssManagingEditor instanceof String) {
                            managingEditor(grailsApplication?.config?.picasa.rssManagingEditor ?: "")
                        }

                        for (c in commentList) {
                            item {
                                guid(isPermaLink: "false", c?.commentId)
                                pubDate(c?.dateCreated?.format(DateUtil.RFC_822))
                                "atom:updated"(DateUtil.formatDateRfc3339(c?.dateCreated))
                                
                                if (c?.author?.name) {
                                    title(message(code: "uk.co.anthonycampbell.grails.picasa.Comment.rss.title",
                                            args: [c?.author?.name], default: "New comment"))
                                } else {
                                    title(message(code: "uk.co.anthonycampbell.grails.picasa.Comment.rss.title.simple",
                                            default: "New comment"))
                                }
                                
                                description(c?.message)
                                link(createLink(controller: "comment", action: "list", absolute: "true"))
                            }

                            if (!latestBuildDate || latestBuildDate?.compareTo(c?.dateCreated) < 0) {
                                latestBuildDate = c?.dateCreated
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
                comments {
                    for (c in commentList) {
                        comment {
                            // Main attributes
                            commentId(c?.commentId)
                            albumId(c?.albumId)
                            photoId(c?.photoId)
                            message(c?.message)
                            dateCreated(c?.dateCreated?.format(DateUtil.RFC_822))
                            author {
                                name(c?.author?.name)
                                email(c?.author?.email)
                                uri(c?.author?.uri)
                            }
                        }
                    }
                }
            }
        } else {            
            // Convert to array to allow easy display preparation
            Comment[] commentArray = commentList.toArray()
            if (commentArray) {
                log.debug "Convert response into display list (offset=$offset, max=$max, " +
                    "length=${commentArray?.length})"

                // By default show the last set of comments
                if (offset < 0) {
                    final def lastOffset = Math.floor(
                        new Double((commentArray?.length / max) ?: 0.00).doubleValue())
                    if (lastOffset != null) {
                        // Reset offset to allow pagination to be updated correctly
                        offset = params.offset = (lastOffset * max)
                    }
                }

                // Prepare display list
                commentArray = Arrays.copyOfRange(commentArray, offset,
                    ((offset + max) > commentArray?.length ? commentArray?.length : (offset + max)))

                if (commentArray) {
                    // Update display list
                    displayList.addAll(Arrays.asList(commentArray))
                }
            }

            log.debug "Display list with $listView view"

            render(view: listView, model: [albumId: paramAlbumId,
                    photoId: paramPhotoId,
                    commentInstanceList: displayList,
                    commentInstanceTotal: (commentList?.size() ?: 0)])
        }
    }
    
    /**
     * Attempt to post the provided comment instance.
     * 
     * In addition, render the correct view depending on whether the
     * call is Ajax or not.
     *
     * @param isAjax whether the request is from an Ajax call.
     */
    private doSave(final boolean isAjax) {
        // Initialise lists and prepare comment
        final Comment commentInstance = new Comment(params)
        final List<Comment> commentList = new ArrayList<Comment>()
        final List<Comment> displayList = new ArrayList<Comment>()

        // Prepare display values
        final String albumId = (params.albumId && StringUtils.isNumeric(params.albumId)) ? "${params.albumId}" : ""
        final String photoId = (params.photoId && StringUtils.isNumeric(params.photoId)) ? "${params.photoId}" : ""
        final int offset = params.int("offset") ?: 0
        final int max = Math.min(new Integer(params.int("max") ?:
                (grailsApplication?.config?.picasa?.maxComments ?: 10)).intValue(), 500)
        final String createView = isAjax ? "_comments" : "comments"
        flash.message = ""
        flash.oauthError = ""

		log.debug "Attempting to post a new comment (message=${commentInstance?.message}, isAjax=" +
            isAjax + ")"

        try {
            // Post comment through the Picasa comment service
            picasaCommentService.postComment(commentInstance)

            log.debug "Success..."

        } catch (PicasaCommentServiceException pse) {
            log.error("Unable to post a new comment through the Google Picasa web service " +
                "(message=${commentInstance?.message}, isAjax=$isAjax)", pse)

            flash.oauthError = message(code: "uk.co.anthonycampbell.grails.picasa.Comment.error.post",
                default: "A problem was encountered when trying to post your comment. Please ensure that all the fields are complete and try again.")
        }

        log.debug "Attempting to get comments through the Google Picasa web service " +
                "(albumId=$albumId, photoId=$photoId)"

        try {
            // Get updated comment list
            commentList.addAll(picasaService.listCommentsForPhoto(albumId, photoId))

            log.debug "Success..."

        } catch (PicasaServiceException pse) {
            log.error("Unable to get comments through the Google Picasa web service " +
                "(albumId=$albumId, photoId=$photoId)", pse)
            
            flash.message = message(code: "uk.co.anthonycampbell.grails.picasa.Comment.list.not.available",
                default: "The comment listing is currently not available. Please try again later.")
        }

        // If required, reverse list
        if (params.order == "asc") {
            Collections.reverse(commentList)
        }

        log.debug "Convert response into display list"

        // Convert to array to allow easy display preparation
        Comment[] commentArray = commentList.toArray()
        if (commentArray) {
            // Prepare display list
            commentArray = Arrays.copyOfRange(commentArray, offset,
                ((offset + max) > commentArray?.length ? commentArray?.length : (offset + max)))
            
            if (commentArray) {
                // Update display list
                displayList.addAll(Arrays.asList(commentArray))
            }
        }

        log.debug "Display list with $createView view"

        render(view: createView, model: [albumId: albumId,
                photoId: photoId,
                commentInstance: commentInstance,
                commentInstanceList: displayList,
                commentInstanceTotal: (commentList?.size() ?: 0)])
    }

    /**
     * Update picasa service with OAuth token and re-direct to photo.
     */
    private doLogin() {
        // Get session and request parameters
        final def oAuthTokenKey = session?.oauthToken?.key
        final def oAuthTokenSecret = session?.oauthToken?.secret
        final def ids = params?.id?.tokenize(ID_SEPARATOR)
        final def albumId = "${ids?.get(0)}"
        final def photoId = "${ids?.get(1)}"
        flash.message = ""
        flash.oauthError = ""

        log.debug "Updating service to apply OAuth access (oAuthTokenKey=$oAuthTokenKey " +
            ", oAuthTokenSecret=$oAuthTokenSecret)"
        
        try {
            // Update service and session
            picasaCommentService.applyOAuthAccess(session?.oauthToken?.key, session?.oauthToken?.secret)

            log.debug "Success..."

        } catch (PicasaCommentServiceException pse) {
            log.error("Unable to update service to apply OAuth access (oAuthTokenKey=$oAuthTokenKey " +
                ", oAuthTokenSecret=$oAuthTokenSecret)", pse)
            
            flash.oauthError = message(code: "uk.co.anthonycampbell.grails.picasa.Comment.error.login",
                default: "A problem was encountered when trying to connect to your Google Picasa Web Albums account. Please try again later.")
        }
        
        log.debug "Re-directing to photo (albumId=$albumId, photoId=$photoId)"

        // Re-direct to photo
        redirect(controller: "photo", action: "show", params: [albumId: albumId, photoId: photoId])
    }

    /**
     * Update picasa service with OAuth token and re-direct to photo.
     */
    private doLogout(final boolean isAjax) {
        // Get request parameters
        final def ids = params?.id?.tokenize(ID_SEPARATOR)
        final def albumId = ids?.get(0)
        final def photoId = ids?.get(1)
        final String showView = isAjax ? "_create" : "show"

        log.debug "Updating service to remove OAuth access"

        // Update service and session
        picasaCommentService.removeOAuthAccess()

        log.debug "Success..."

        if (isAjax) {
            log.debug "Render comment $showView view for photo (albumId=$albumId, photoId=$photoId)"

            // Just render create comment form
            render(view: showView, model: [albumId: albumId, photoId: photoId])
        } else {
            log.debug "Re-directing to photo with $showView view (albumId=$albumId, photoId=$photoId)"

            // Re-direct to photo
            redirect(controller: "photo", action: showView, params: [albumId: albumId,
                    photoId: photoId])
        }
    }

    /**
     * Check whether there is a session available for the user.
     *
     * @return whether the user is logged in.
     */
    private boolean checkUser() {
        // Initialise result
        boolean loggedIn

        log.debug "Check whether the current user is logged in..."

        // Check whether user is logged in
        if (!session?.oAuthLoggedIn) {
            log.debug "User is NOT logged in."
            loggedIn = false
        } else {
            log.debug "User IS logged in."
            loggedIn = true
        }

        return loggedIn
    }
}
