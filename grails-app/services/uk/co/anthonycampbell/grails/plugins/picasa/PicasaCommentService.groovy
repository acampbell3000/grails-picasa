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

import javax.servlet.http.HttpSession

import com.google.gdata.client.Query
import com.google.gdata.client.photos.*
import com.google.gdata.client.authn.oauth.*
import com.google.gdata.data.PlainTextConstruct
import com.google.gdata.data.photos.*

import org.springframework.beans.factory.InitializingBean
import org.springframework.web.context.request.RequestContextHolder

/**
 * Grails service to expose the required Picasa API methods to the
 * application.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaCommentService implements InitializingBean {

    // Declare service properties
    boolean transactional = false
    boolean serviceInitialised = false

    // Declare service scope
    static scope = "session"

    // URL to the Google Picasa GDATA API
    private static final String GOOGLE_GDATA_API_URL = "http://picasaweb.google.com/data/feed/api"

    // Service properties
    private PicasawebService picasaCommentsWebService
    private def picasaApplicationName
    private def picasaConsumerKey
    private def picasaConsumerSecret
    private def allowComments

    // Service dependencies
    def grailsApplication

    /**
     * Initialise config properties.
     */
    @Override
    void afterPropertiesSet() {
        log?.info "Initialising the ${this.getClass().getSimpleName()}..."
        reset()
    }

    /*
     * Attempt to re-connect to the Picasa web service using the new provided
     * configuration details.
     * 
     * @param picasaApplicationName the application's name.
     * @param picasaConsumerKey the picasa OAuth consumer key.
     * @param picasaConsumerSecret the picasa OAuth consumer secret.
     * @param allowComments whether to allow comments to be posted.
     * @return whether a new connection was successfully made.
     */
    boolean connect(final String picasaApplicationName, final String picasaConsumerKey,
            final String picasaConsumerSecret, final String allowComments) {
        log?.info "Setting the ${this.getClass().getSimpleName()} configuration..."

        this.picasaApplicationName = picasaApplicationName
        this.picasaConsumerKey = picasaConsumerKey
        this.picasaConsumerSecret = picasaConsumerSecret
        this.allowComments = allowComments

        // Validate properties and attempt to initialise the service
        return validateAndInitialiseService()
    }

    /*
     * Attempt to re-connect to the Picasa web service using the provided
     * connection details available in the grails-app/conf/Config.groovy file.
     *
     * @return whether a new connection was successfully made.
     */
    boolean reset() {
        log?.info "Resetting ${this.getClass().getSimpleName()} configuration..."

        // Get configuration from Config.groovy
        this.picasaApplicationName = this.getClass().getPackage().getName() +
            "-" + grailsApplication.metadata['app.name'] +
            "-" + grailsApplication.metadata['app.version']
        this.allowComments = grailsApplication.config?.picasa?.allowComments

        // Collect oauth config
        this.picasaConsumerKey = grailsApplication.config?.oauth?.picasa?.consumer.key
        this.picasaConsumerSecret = grailsApplication.config?.oauth?.picasa?.consumer.secret

        // Remove any existing OAuth access
        removeOAuthAccess()
        
        // Validate properties and attempt to initialise the service
        return validateAndInitialiseService()
    }

    /**
     * Apply the provided access token to the Picasa service.
     *
     * @param the access token.
     * @param the access token secret.
     * @throw PicasaCommentServiceException Unable to apply OAuth access.
     */
    def applyOAuthAccess(final String token, final String secret)
            throws PicasaCommentServiceException {

        // Check wheher service is initialised
        if (serviceInitialised) {
            // Check we are allowed to post comments
            if (this.allowComments) {
                // Prepare parameters
                final GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters()
                oauthParameters.setOAuthConsumerKey(this.picasaConsumerKey)
                oauthParameters.setOAuthConsumerSecret(this.picasaConsumerSecret)
                oauthParameters.setOAuthToken(token)
                oauthParameters.setOAuthTokenSecret(secret)

                // Get current session
                final HttpSession session = getSession()

                // Attempt OAuth connection
                try {
                    log.info "Attempting OAuth connection..."

                    // Initialise Picasa Web Service
                    picasaCommentsWebService = new PicasawebService(this.picasaApplicationName)
                    picasaCommentsWebService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer())
                    session?.oAuthLoggedIn = true

                    log.info "Successfully connected to the Google Picasa web service."
                    log.info "Update session with user details..."

                    // Declare feed
                    final URL feedUrl = new URL("$GOOGLE_GDATA_API_URL/user/default")

                    log.debug "FeedUrl: $feedUrl"

                    // Get user feed
                    final UserFeed userFeed = picasaCommentsWebService.getFeed(feedUrl, UserFeed.class)

                    // Get details from feed
                    final String nickname = userFeed?.getNickname()
                    final String username = userFeed?.getUsername()
                    final String thumbnail = userFeed?.getThumbnail()

                    log.debug "User details: nickname=$nickname, username=$username, " +
                        "thumbnail=$thumbnail"

                    // Update session
                    session?.oAuthNickname = nickname
                    session?.oAuthUsername = username
                    session?.oAuthThumbail = thumbnail

                    log.info "Successfully updated session with user details from the Google Picasa web " +
                        "service."

                } catch (Exception ex) {
                    session?.oAuthLoggedIn = false

                    final def errorMessage = "Unable to connect to Google Picasa Web Albums. " +
                        "Invalid OAuth access token and secret!"

                    log.error(errorMessage, ex)
                    throw new PicasaCommentServiceException(errorMessage)
                }
            } else {
                session?.oAuthLoggedIn = false

                final def errorMessage = "Unable to apply acccess token to Google Picasa Web " +
                    "Albums service. Photo comments are disabled."

                log.error(errorMessage)
                throw new PicasaCommentServiceException(errorMessage)
            }
        } else {
            final def errorMessage = "Unable to apply access token to Google Picasa Web Albums " +
                "service. Some of the plug-in configuration is missing. Please refer to the " +
                "documentation and ensure you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaCommentServiceException(errorMessage)
        }
    }

    /**
     * Reset the picasa comments web service and update session.
     */
    def removeOAuthAccess() {
        log.info "Attempting to remove any existing OAuth access..."

        // Get current session
        final HttpSession session = getSession()

        // Initialise Picasa Web Service
        picasaCommentsWebService = null
        session?.oAuthLoggedIn = false
        session?.oAuthNickname = null
        session?.oAuthUsername = null
        session?.oAuthThumbail = null

        log.info "OAuth access successfully removed"
    }

    /**
     * Post the provided comment through the Google Picasa web service.
     *
     * @param comment the provided comment to post.
     * @throw PicasaCommentServiceException when there's been a problem posting
     *      the provided comment.
     */
    def postComment(final Comment comment) throws PicasaCommentServiceException {
        // Check whether service is initialised
        if (serviceInitialised) {
            if (allowComments) {
                // Validate IDs
                if (!comment || !comment.validate()) {
                    final def errorMessage = "Unable to post your Google Picasa Web Album Comment. " +
                        "The provided comment was invalid. (commentId=${comment?.commentId}, " +
                        "albumId=${comment?.albumId}, photoId=${comment?.photoId}, " +
                        "message=${comment?.message})"

                    log.error(errorMessage)
                    throw new PicasaCommentServiceException(errorMessage)
                }

                // Get photo properties
                final def albumId = comment?.albumId
                final def photoId = comment?.photoId

                try {
                    // Declare feed
                    final URL feedUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                        "${this.picasaUsername}/albumid/$albumId/photoid/$photoId")

                    log.debug "FeedUrl: $feedUrl"

                    // Prepare comment
                    final CommentEntry newComment = new CommentEntry()
                    newComment.setContent(new PlainTextConstruct(comment?.message))

                    // Post comment
                    picasaCommentsWebService.insert(feedUrl, newComment)

                } catch (Exception ex) {
                    final def errorMessage = "Unable to post your Google Picasa Web Album Comment. " +
                        "A problem occurred when making the request through the Google Data API. " +
                        "(username=${this.picasaUsername}, albumId=$albumId, photoId=$photoId)"

                    log.error(errorMessage, ex)
                    throw new PicasaCommentServiceException(errorMessage, ex)
                }
            } else {
                final def errorMessage = "Unable to post your Google Picasa Web Album Comment. " +
                    "Comments are currently disabled."

                log.error(errorMessage)
                throw new PicasaCommentServiceException(errorMessage)
            }
        } else {
            final def errorMessage = "Unable to post your Google Picasa Web Album Comment. Some of " +
                "the plug-in configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaCommentServiceException(errorMessage)
        }
    }
    
    /**
     * Validate the service properties and attempt to initialise.
     *
     * @return whether the service has been successfully initialised.
     */
    private boolean validateAndInitialiseService() {
        // Lets be optimistic
        boolean configValid = true

        log?.info "Begin ${this.getClass().getSimpleName()} configuration validation..."

        if (!isConfigValid(this.picasaApplicationName)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid application name. This " +
                "plug-in's application.properties file may have been tampered with. Please re-install " +
                "the Grails Picasa plug-in."
            configValid = false
        }
        if (!isConfigValid(this.allowComments)) {
            log?.error "Unable to allow users to post comments on your Google Picasa Web Albums " +
                "photos. Ensure you have declared the property picasa.allowComments in your " +
                "application's config."
            configValid = false
        }
        if (!isConfigValid(this.picasaConsumerKey)) {
            log?.error "Unable to allow users to post comments on your Google Picasa Web Albums " +
                "photos. Ensure you have declared the property oauth.picasa.consumer.key in your " +
                "application's config."
            configValid = false
        }
        if (!isConfigValid(this.picasaConsumerSecret)) {
            log?.error "Unable to allow users to post comments on your Google Picasa Web Albums " +
                "photos. Ensure you have declared the property oauth.picasa.consumer.secret in your " +
                "application's config."
            configValid = false
        }

        if (configValid) {
            log?.info "${this.getClass().getSimpleName()} configuration valid"
        }

        // Only initialise the service if the configuration is valid
        this.serviceInitialised = configValid

        // Return initialisation result
        return serviceInitialised
    }
    
    /**
     * Check whether the provided config reference is valid and set.
     *
     * @param setting the configuration value to validate.
     * @return whether the current configuration value is valid and set.
     */
    private boolean isConfigValid(final def setting) {
        // Initialise result
        boolean result = false

        // Validate
        if (setting && !(setting instanceof ConfigObject)) {
            if (setting instanceof String && setting) {
                // Non empty string
                result = true
            } else if (!(setting instanceof String)) {
                // Non string, e.g. number
                result = true
            }
        }

        // Return result
        return result
    }

    /**
     * Return the current HTTP session.
     *
     * @result the current HTTP session.
     */
    private HttpSession getSession() {
        return RequestContextHolder.currentRequestAttributes().getSession()
    }
}
