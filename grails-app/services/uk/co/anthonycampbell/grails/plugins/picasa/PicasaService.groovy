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

import uk.co.anthonycampbell.grails.plugins.picasa.cache.*

import java.net.URL
import javax.servlet.http.HttpSession

import com.google.gdata.client.Query
import com.google.gdata.client.photos.*
import com.google.gdata.data.photos.*
import com.google.gdata.data.media.mediarss.MediaKeywords

import org.apache.commons.lang.StringUtils

import org.springframework.beans.factory.InitializingBean

/**
 * Grails service to expose the required Picasa API methods to the
 * application.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaService implements InitializingBean {

    // Declare service properties
    boolean transactional = false
    boolean serviceInitialised = false
    
    // Declare service scope
    static scope = "singleton"

    // URL to the Google Picasa GDATA API
    private static final String GOOGLE_GDATA_API_URL = "http://picasaweb.google.com/data/feed/api"
    
    // Declare cache (used to reduce Google API calls)
    private static ServiceCache CACHE
    private static final byte TAG_WEIGHT_SPLIT_TOTAL = 7

    // Declare picasa properties
    private PicasawebService picasaWebService
    private def picasaUsername
    private def picasaPassword
    private def picasaApplicationName
    private def picasaImgmax
    private def picasaThumbsize
    private def picasaMaxResults
    private def allowCache
    private def cacheTimeout

    // Container properties
    def grailsApplication

    /**
     * Initialise config properties.
     */
    @Override
    void afterPropertiesSet() {
        log?.info "Initialising the ${this.getClass().getSimpleName()}..."
        
        CACHE = PicasaServiceCache.getInstance(grailsApplication.config?.picasa?.cacheTimeout)
        reset()
    }

    /*
     * Attempt to re-connect to the Picasa web service using the new provided
     * configuration details.
     *
     * @param picasaUsername the Picasa account's username.
     * @param picasaPassword the Picasa account's password.
     * @param picasaApplicationName the application's name.
     * @param picasaImgmax the photo size to provide in requests through the Google GData API.
     * @param picasaThumbsize the thumbnail size to provide in requests through the Google GData API.
     * @param picasaMaxResults the maximum number of results to return to the view.
     * @param allowCache whether the cache is enabled for the Picasa service.
     * @param cacheTimeout how long the cache is valid before a purge is made.
     * @return whether a new connection was successfully made.
     */
    boolean connect(final String picasaUsername, final String picasaPassword,
            final String picasaApplicationName, final String picasaImgmax,
            final String picasaThumbsize, final String picasaMaxResults,
            final String allowCache, final String cacheTimeout) {
        log?.info "Setting the ${this.getClass().getSimpleName()} configuration..."

        this.picasaUsername = picasaUsername
        this.picasaPassword = picasaPassword
        this.picasaApplicationName = picasaApplicationName
        this.picasaImgmax = picasaImgmax
        this.picasaThumbsize = picasaThumbsize
        this.picasaMaxResults = picasaMaxResults
        this.allowCache = allowCache
        this.cacheTimeout = cacheTimeout
        
        // Empty cache
        CACHE?.purge()

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
        this.picasaUsername = grailsApplication.config?.picasa?.username
        this.picasaPassword = grailsApplication.config?.picasa?.password
        this.picasaApplicationName = this.getClass().getPackage().getName() +
            "-" + grailsApplication.metadata['app.name'] +
            "-" + grailsApplication.metadata['app.version']
        this.picasaImgmax = grailsApplication.config?.picasa?.imgmax
        this.picasaThumbsize = grailsApplication.config?.picasa?.thumbsize
        this.picasaMaxResults = grailsApplication.config?.picasa?.maxResults
        this.allowCache = grailsApplication.config?.picasa?.allowCache
        this.cacheTimeout = grailsApplication.config?.picasa?.cacheTimeout

        // Empty cache
        CACHE?.purge()

        // Validate properties and attempt to initialise the service
        return validateAndInitialiseService()
    }

    /**
     * Get the Album for the provided ID through the Google Picasa web service.
     *
     * @param albumId the provided album ID.
     * @param showAll whether to include hidden / private albums.
     * @return the retrieved album from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the selected album album.
     */
    def Album getAlbum(final String albumId, final boolean showAll = false) throws PicasaServiceException {
        if (serviceInitialised) {
            // Validate ID
            if (!albumId || !StringUtils.isNumeric(albumId)) {
                final def errorMessage = "Unable to retrieve your Google Picasa Web Album. " +
                    "The provided ID was invalid. (albumId=$albumId, showAll=$showAll)"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }

            // Generate cache key
            final def CACHE_KEY = "${PicasaQuery.GET_ALBUM.getMethod()}-$albumId-$showAll"

            // Check cache
            if (this.allowCache) {
                final Album cacheItem = retrieveCache(CACHE_KEY)
                if (cacheItem) {
                    // Found result in cache so return
                    return cacheItem
                }
            }

            try {
                // Initialise result
                Album album = null

                final URL feedUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                    "${this.picasaUsername}/albumid/$albumId?thumbsize=" +
                    "${this.picasaThumbsize}&imgmax=${this.picasaImgmax}")

                log.debug "FeedUrl: $feedUrl"

                // Get album feed
                final AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class)

                // Declare tag feed
                final URL tagUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                    "${this.picasaUsername}/albumid/$albumId?kind=tag")

                log.debug "TagUrl: $tagUrl"

                // Get all tags for this album
                final AlbumFeed tagResultsFeed = picasaWebService.query(new Query(tagUrl), AlbumFeed.class)

                // Get any existing tags
                MediaKeywords albumTags = albumFeed?.getMediaKeywords()
                if (!albumTags) {
                    albumTags = new MediaKeywords()
                }

                // Update album feed with results
                for (final TagEntry tag : tagResultsFeed?.getTagEntries()) {
                    albumTags.addKeyword(tag?.getTitle()?.getPlainText())
                }
                albumFeed?.setKeywords(albumTags)

                // Transfer feed into domain class
                final Album domain = Converter.convertToAlbumDomain(albumFeed)

                // If we have a valid public entry add to listing
                if (!domain?.hasErrors()) {
                    if (showAll || domain?.isPublic) {
                        album = domain
                    }
                }

                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, album)
                }

                // Return result
                return album

            } catch (Exception ex) {
                final def errorMessage = "Unable to retrieve your Google Picasa Web Album. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername}, albumId=$albumId, showAll=$showAll)"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            final def errorMessage = "Unable to retrieve your Google Picasa Web Album. Some of " +
                "the plug-in configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
    }

    /**
     * Get the Photo for the provided IDs through the Google Picasa web service.
     *
     * @param albumId the provided album ID.
     * @param photoId the provided photo ID.
     * @param showAll whether to include hidden / private photo.
     * @return the retrieved photo from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the selected photo.
     */
    def Photo getPhoto(final String albumId, final String photoId, final boolean showAll = false)
        throws PicasaServiceException {

        if (serviceInitialised) {
            // Validate IDs
            if (!albumId || !StringUtils.isNumeric(albumId) ||
                    !photoId || !StringUtils.isNumeric(photoId)) {
                final def errorMessage = "Unable to retrieve your Google Picasa Web Album Photo. " +
                    "The provided IDs were invalid. (albumId=$albumId, photoId=$photoId, " +
                    "showAll=$showAll)"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            // Generate cache key
            final def CACHE_KEY = "${PicasaQuery.GET_PHOTO.getMethod()}-$albumId-$photoId"

            // Check cache
            if (this.allowCache) {
                final Photo cacheItem = retrieveCache(CACHE_KEY)
                if (cacheItem) {
                    // Found result in cache so return
                    return cacheItem
                }
            }

            try {
                // Initialise result
                Photo photo = null

                // Declare feed
                URL feedUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                    "${this.picasaUsername}/albumid/$albumId/photoid/$photoId" +
                    "?thumbsize=${this.picasaThumbsize}&imgmax=${this.picasaImgmax}")

                log.debug "FeedUrl: $feedUrl"

                // Get album feed
                final PhotoFeed photoFeed = picasaWebService.getFeed(feedUrl, PhotoFeed.class)

                // Transfer feed into domain class
                final Photo domain = Converter.convertToPhotoDomain(photoFeed)

                // If we have a valid public entry add to listing
                if (!domain?.hasErrors()) {
                    if (showAll || domain?.isPublic) {
                        photo = domain
                    }
                }

                // Check we have a photo to work with

                if (photo) {
                    // First get list of any comments
                    for (final CommentEntry commentEntry : photoFeed?.getCommentEntries()) {
                        // Transfer comment into domain class
                        final Comment comment = Converter.convertToCommentDomain(commentEntry)

                        if (!comment?.hasErrors()) {
                            photo.addToComments(comment)
                        }
                    }

                    // Second call to find out navigation based on position in album
                    feedUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                        "${this.picasaUsername}/albumid/$albumId?thumbsize=" +
                        "${this.picasaThumbsize}&imgmax=${this.picasaImgmax}")

                    log.debug "FeedUrl: $feedUrl"

                    // Get album feed
                    final AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class)

                    // Initialise search variables
                    boolean found = false
                    String previous = ""
                    String current = ""
                    String next = ""

                    // Find photo and store previous and subsequent IDs (if available)
                    for (final PhotoEntry entry : albumFeed?.getPhotoEntries()) {
                        // Prepare ID
                        current = entry?.getId()?.substring(entry?.getId()?.lastIndexOf('/') + 1,
                            entry?.getId()?.length())

                        // If already found, store next ID and end search
                        if (found == true) {
                            next = current
                            break
                        }

                        if (current == photoId) {
                            found = true
                        }

                        if (found == false) {
                            previous = current
                        }
                    }

                    // Update photo with previous and next images
                    photo.previousPhotoId = previous
                    photo.nextPhotoId = next
                }

                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, photo)
                }

                // Return result
                return photo

            } catch (Exception ex) {

                final def errorMessage = "Unable to retrieve your Google Picasa Web Album Photo. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername}, albumId=$albumId, photoId=$photoId" +
                    ", showAll=$showAll)"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {

            final def errorMessage = "Unable to retrieve your Google Picasa Web Album Photo. Some of " +
                "the plug-in configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
    }

    /**
     * List the available albums for the configured Google Picasa account.
     *
     * @param showAll whether to include hidden / private albums in the list.
     * @return list of albums from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available albums.
     */
    def List<Album> listAllAlbums(final boolean showAll = false) throws PicasaServiceException {
        if (serviceInitialised) {
            try {
                // Generate cache key
                final def CACHE_KEY = "${PicasaQuery.LIST_ALL_ALBUMS.getMethod()}-$showAll"

                // Check cache
                if (this.allowCache) {
                    final List<Album> cachedListing = retrieveCache(CACHE_KEY)
                    if (cachedListing) {
                        // Found result in cache so return
                        return cachedListing
                    }
                }

                // Initialise result
                final List<Album> albumListing = new ArrayList<Album>()

                // Declare feed
                final URL feedUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                    "${this.picasaUsername}?kind=album&thumbsize=${this.picasaThumbsize}" +
                    "&imgmax=${this.picasaImgmax}")

                log.debug "FeedUrl: $feedUrl"

                // Get user feed
                final UserFeed userFeed = picasaWebService.getFeed(feedUrl, UserFeed.class)

                for (final AlbumEntry entry : userFeed?.getAlbumEntries()) {
                    // Transfer entry into domain class
                    final Album album = Converter.convertToAlbumDomain(entry)

                    // If we have a valid public entry add to listing
                    if (!album?.hasErrors()) {
                        if (showAll || album?.isPublic) {
                            albumListing.add(album)
                        }
                    }
                }

                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, albumListing)
                }

                // Return result
                return albumListing

            } catch (Exception ex) {
                final def errorMessage = "Unable to list your Google Picasa Web Albums. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername}, showAll=$showAll)"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            final def errorMessage = "Unable to list your Google Picasa Web Albums. Some of the plug-in " +
                "configuration is missing. Please refer to the documentation and ensure you have " +
                "declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
    }

    /**
     * List the available tags used by the Google Picasa web album user.
     *
     * @return list of tags for the provided Google Picasa web service user.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available tags.
     */
    def List<Tag> listAllTags() throws PicasaServiceException {
        if (serviceInitialised) {
            // Generate cache key
            final def CACHE_KEY = "${PicasaQuery.LIST_ALL_TAGS.getMethod()}"

            // Check cache
            if (this.allowCache) {
                final List<Tag> cachedListing = retrieveCache(CACHE_KEY)
                if (cachedListing) {
                    // Found result in cache so return
                    return cachedListing
                }
            }

            try {
                // Initialise result
                final List<Tag> tagListing = new ArrayList<Tag>()
                int lowestWeight = Integer.MAX_VALUE
                int highestWeight = 0

                // Declare tag feed
                final URL tagUrl = new URL("$GOOGLE_GDATA_API_URL/user/${this.picasaUsername}?kind=tag")

                log.debug "TagUrl: $tagUrl"

                // Get all tags for this album
                final AlbumFeed tagResultsFeed = picasaWebService.query(new Query(tagUrl), AlbumFeed.class)

                // Update list with results
                for (final TagEntry entry : tagResultsFeed?.getTagEntries()) {
                    // Transfer entry into domain class
                    final Tag tag = Converter.convertToTagDomain(entry)

                    // If we have a valid entry add to listing
                    if (!tag?.hasErrors()) {
                        if (tag?.weight < lowestWeight) {
                            lowestWeight = tag?.weight
                        }
                        if (tag?.weight > highestWeight) {
                            highestWeight = tag?.weight
                        }

                        tagListing.add(tag)
                    }
                }

                // Calculate weighting splits
                final double weightSplit = (highestWeight - lowestWeight) / TAG_WEIGHT_SPLIT_TOTAL

                log.debug "Tag weighting: lowestWeight=$lowestWeight, highestWeight=$highestWeight" +
                    ", split=$weightSplit"

                // Update list with display weight values
                for (final Tag tag : tagListing) {
                    final byte counter = TAG_WEIGHT_SPLIT_TOTAL
                    for (double split = highestWeight; split > lowestWeight; split -= weightSplit) {
                        // If required add tag to weight group
                        if (tag?.weight <= split) {
                            tag?.displayWeight = counter
                        }

                        // Update counter
                        counter--
                    }
                }

                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, tagListing)
                }

                // Return result
                return tagListing

            } catch (Exception ex) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Tags. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername})"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            final def errorMessage = "Unable to list your Google Picasa Web Album Tags. Some of " +
                "the plug-in configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
    }

    /**
     * List the most recent comments for the Google Picasa web album user.
     *
     * @return list of most recent comments for the Google Picasa web album user.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available comments.
     */
    def List<Comment> listAllComments() throws PicasaServiceException {
        if (serviceInitialised) {
            // Generate cache key
            final def CACHE_KEY = "${PicasaQuery.LIST_ALL_COMMENTS.getMethod()}"

            // Check cache
            if (this.allowCache) {
                final List<Comment> cachedListing = retrieveCache(CACHE_KEY)
                if (cachedListing) {
                    // Found result in cache so return
                    return cachedListing
                }
            }

            try {
                // Initialise result
                final List<Comment> commentListing = new ArrayList<Comment>()

                // Declare comment feed
                final URL commentUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                    "${this.picasaUsername}?kind=comment")

                log.debug "CommentUrl: $commentUrl"

                // Get all comments for this user
                final PhotoFeed commentResultsFeed = picasaWebService.getFeed(commentUrl, PhotoFeed.class)

                // Update list with results
                for (final CommentEntry entry : commentResultsFeed?.getCommentEntries()) {
                    // Transfer entry into domain class
                    final Comment comment = Converter.convertToCommentDomain(entry)

                    // If we have a valid entry add to listing
                    if (!comment?.hasErrors()) {
                        commentListing.add(comment)
                    }
                }

                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, commentListing)
                }

                // Return result
                return commentListing

            } catch (Exception ex) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Comments. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername})"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            final def errorMessage = "Unable to list your Google Picasa Web Album Comments. Some of " +
                "the plug-in configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
    }

    /**
     * List the available photos for the provided Google Picasa web album.
     *
     * @param albumId the provided album ID.
     * @param showAll whether to include hidden / private photos in the list.
     * @return list of photos for the provided Google Picasa web service album.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available photos.
     */
    def List<Photo> listPhotosForAlbum(final String albumId, final boolean showAll = false)
            throws PicasaServiceException {

        if (serviceInitialised) {
            // Validate ID
            if (!albumId || !StringUtils.isNumeric(albumId)) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Photos. The " +
                    "provided ID was invalid. (albumId=$albumId, showAll=$showAll)"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            // Generate cache key
            final def CACHE_KEY = "${PicasaQuery.LIST_PHOTOS_FOR_ALBUM.getMethod()}-$albumId-$showAll"

            // Check cache
            if (this.allowCache) {
                final List<Photo> cachedListing = retrieveCache(CACHE_KEY)
                if (cachedListing) {
                    // Found result in cache so return
                    return cachedListing
                }
            }

            try {
                // Initialise result
                final List<Photo> photoListing = new ArrayList<Photo>()

                // Declare feed
                final URL feedUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                    "${this.picasaUsername}/albumid/$albumId?thumbsize=" +
                    "${this.picasaThumbsize}&imgmax=${this.picasaImgmax}")

                log.debug "FeedUrl: " + feedUrl

                // Get album feed
                final AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class)

                for (final PhotoEntry entry : albumFeed?.getPhotoEntries()) {
                    // Transfer entry into domain class
                    final Photo photo = Converter.convertToPhotoDomain(entry)

                    // If we have a valid public entry add to listing
                    if (!photo?.hasErrors()) {
                        if (showAll || photo?.isPublic) {
                            photoListing.add(photo)
                        }
                    }
                }

                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, photoListing)
                }

                // Return result
                return photoListing

            } catch (Exception ex) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Photos. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername}, albumId=$albumId, showAll=$showAll)"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            final def errorMessage = "Unable to list your Google Picasa Web Album Photos. Some of the " +
                "plug-in configuration is missing. Please refer tUo the documentation and ensure you " +
                "have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
    }

    /**
     * List the available photos for the provided Google Picasa web album tag keyword.
     *
     * @param tagKeyword the provided tag keyword.
     * @param showAll whether to include hidden / private photos in the list.
     * @return list of photos for the provided Google Picasa web service album tag.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available photos.
     */
    def List<Photo> listPhotosForTag(final String tagKeyword, final boolean showAll = false)
            throws PicasaServiceException {

        if (serviceInitialised) {
            // Validate ID
            if (!tagKeyword) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Photos. " +
                    "The provided tag keyword was invalid. (tagKeyword=$tagKeyword, showAll=$showAll)"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            // Generate cache key
            final def CACHE_KEY = "${PicasaQuery.LIST_PHOTOS_FOR_TAG.getMethod()}-$tagKeyword-$showAll"

            // Check cache
            if (this.allowCache) {
                final List<Photo> cachedListing = retrieveCache(CACHE_KEY)
                if (cachedListing) {
                    // Found result in cache so return
                    return cachedListing
                }
            }
            
            try {
                // Initialise result
                final List<Photo> photoListing = new ArrayList<Photo>()
                
                // Declare feed
                final URL feedUrl = new URL("$GOOGLE_GDATA_API_URL/user/${this.picasaUsername}")

                log.debug "FeedUrl: $feedUrl"

                final Query tagQuery = new Query(feedUrl)
                tagQuery.setStringCustomParameter("kind", "photo")
                tagQuery.setStringCustomParameter("tag", tagKeyword)
                tagQuery.setStringCustomParameter("thumbsize", "${this.picasaThumbsize}")
                tagQuery.setStringCustomParameter("imgmax", "${this.picasaImgmax}")
                tagQuery.setStringCustomParameter("max-results", "${this.picasaMaxResults}")
                
                // Get album feed
                final AlbumFeed tagSearchResultsFeed = picasaWebService.query(tagQuery, AlbumFeed.class)
                
                for (final PhotoEntry entry : tagSearchResultsFeed?.getPhotoEntries()) {
                    // Transfer entry into domain class
                    final Photo photo = Converter.convertToPhotoDomain(entry)

                    // If we have a valid public entry add to listing
                    if (!photo?.hasErrors()) {
                        if (showAll || photo?.isPublic) {
                            photoListing.add(photo)
                        }
                    }
                }

                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, photoListing)
                }

                // Return result
                return photoListing

            } catch (Exception ex) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Photos. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername}, tagKeyword=$tagKeyword, showAll=$showAll)"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            final def errorMessage = "Unable to list your Google Picasa Web Album Photos. Some of " +
                "the plug-in configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
    }

    /**
     * List the available tags for the provided Google Picasa web album.
     *
     * @param albumId the provided album ID.
     * @return list of tags for the provided Google Picasa web service album.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available tags.
     */
    def List<Tag> listTagsForAlbum(final String albumId) throws PicasaServiceException {
        if (serviceInitialised) {
            // Validate ID
            if (!albumId || !StringUtils.isNumeric(albumId)) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Tags. " +
                    "The provided ID was invalid. (albumId=$albumId)"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            // Generate cache key
            final def CACHE_KEY = "${PicasaQuery.LIST_TAGS_FOR_ALBUM.getMethod()}-$albumId"

            // Check cache
            if (this.allowCache) {
                final List<Tag> cachedListing = retrieveCache(CACHE_KEY)
                if (cachedListing) {
                    // Found result in cache so return
                    return cachedListing
                }
            }

            try {
                // Initialise result
                final List<Tag> tagListing = new ArrayList<Tag>()
                int lowestWeight = Integer.MAX_VALUE
                int highestWeight = 0

                // Declare tag feed
                final URL tagUrl = new URL("$GOOGLE_GDATA_API_URL/user/" +
                    "${this.picasaUsername}/albumid/$albumId?kind=tag")

                log.debug "TagUrl: $tagUrl"

                // Get all tags for this album
                final AlbumFeed tagResultsFeed = picasaWebService.query(new Query(tagUrl), AlbumFeed.class)

                // Update list with results
                for (final TagEntry entry : tagResultsFeed?.getTagEntries()) {
                    // Transfer entry into domain class
                    final Tag tag = Converter.convertToTagDomain(entry)

                    // If we have a valid entry add to listing
                    if (!tag?.hasErrors()) {
                        if (tag?.weight < lowestWeight) {
                            lowestWeight = tag?.weight
                        }
                        if (tag?.weight > highestWeight) {
                            highestWeight = tag?.weight
                        }

                        tagListing.add(tag)
                    }
                }

                // Calculate weighting splits
                final double weightSplit = (highestWeight - lowestWeight) / TAG_WEIGHT_SPLIT_TOTAL

                log.debug "Tag weighting: lowestWeight=$lowestWeight, highestWeight=$highestWeight" +
                    ", split=$weightSplit"
                
                // Update list with display weight values
                for (final Tag tag : tagListing) {
                    final byte counter = TAG_WEIGHT_SPLIT_TOTAL
                    for (double split = highestWeight; split > lowestWeight; split -= weightSplit) {
                        // If required add tag to weight group
                        if (tag?.weight <= split) {
                            tag?.displayWeight = counter
                        }

                        // Update counter
                        counter--
                    }
                }
                
                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, tagListing)
                }

                // Return result
                return tagListing

            } catch (Exception ex) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Tags. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername}, albumId=$albumId)"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            final def errorMessage = "Unable to list your Google Picasa Web Album Tags. Some of " +
                "the plug-in configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
    }

    /**
     * List the available comments for the provided Google Picasa web album photo.
     *
     * @param albumId the provided album ID.
     * @param photoId the provided photo ID.
     * @return list of comments for the provided Google Picasa web album photo.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available comments.
     */
    def List<Comment> listCommentsForPhoto(final String albumId, final String photoId)
            throws PicasaServiceException {

        if (serviceInitialised) {
            // Validate IDs
            if (!albumId || !StringUtils.isNumeric(albumId) ||
                    !photoId || !StringUtils.isNumeric(photoId)) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Comments. The " +
                    "provided IDs were invalid. (albumId=$albumId, photoId=$photoId)"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            // Generate cache key
            final def CACHE_KEY = "${PicasaQuery.LIST_COMMENTS_FOR_PHOTO.getMethod()}-$albumId-$photoId"

            // Check cache
            if (this.allowCache) {
                final List<Comment> cachedListing = retrieveCache(CACHE_KEY)
                if (cachedListing) {
                    // Found result in cache so return
                    return cachedListing
                }
            }
            
            try {
                // Initialise result
                final List<Comment> commentListing = new ArrayList<Comment>()

                // Declare comment feed
                final URL commentUrl = new URL("$GOOGLE_GDATA_API_URL/user/${this.picasaUsername}" +
                    "/albumid/$albumId/photoid/$photoId?kind=comment")

                log.debug "CommentUrl: $commentUrl"

                // Get all comments for this photo
                final PhotoFeed commentResultsFeed = picasaWebService.getFeed(commentUrl, PhotoFeed.class)

                // Update list with results
                for (final CommentEntry entry : commentResultsFeed?.getCommentEntries()) {
                    // Transfer entry into domain class
                    final Comment comment = Converter.convertToCommentDomain(entry)

                    // If we have a valid entry add to listing
                    if (!comment?.hasErrors()) {
                        commentListing.add(comment)
                    }
                }

                // Update cache
                if (this.allowCache) {
                    updateCache(CACHE_KEY, commentListing)
                }

                // Return result
                return commentListing

            } catch (Exception ex) {
                final def errorMessage = "Unable to list your Google Picasa Web Album Comments. " +
                    "A problem occurred when making the request through the Google Data API. " +
                    "(username=${this.picasaUsername}, albumId=$albumId, photoId=$photoId)"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            final def errorMessage = "Unable to list your Google Picasa Web Album Comments. Some of " +
                "the plug-in configuration is missing. Please refer to the documentation and ensure " +
                "you have declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
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

        // Validate properties
        if (!isConfigValid(this.picasaUsername)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid username. Please " +
                "ensure you have declared the property picasa.username in your application's config."
            configValid = false
        }
        if (!isConfigValid(this.picasaPassword)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid password. Please " +
                "ensure you have declared the property picasa.password in your application's config."
            configValid = false
        }
        if (!isConfigValid(this.picasaApplicationName)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid application name. This " +
                "plug-in's application.properties file may have been tampered with. Please re-install " +
                "the Grails Picasa plug-in."
            configValid = false
        }
        if (!isConfigValid(this.picasaImgmax)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid max image size. Please " +
                "ensure you have declared the property picasa.imgmax in your application's config."
            configValid = false
        }
        if (!isConfigValid(this.picasaThumbsize)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid thumbnail size. Please " +
                "ensure you have declared the property picasa.thumbsize in your application's config."
            configValid = false
        }
        if (!isConfigValid(this.picasaMaxResults)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid max search results " +
                "value. Please ensure you have declared the property picasa.maxResults in your " +
                "application's config."
            configValid = false
        }
        if (!isConfigValid(this.allowCache)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid cache preference " +
                "value. Please ensure you have declared the property picasa.allowCache in your " +
                "application's config."
            configValid = false
        }
        if (!isConfigValid(this.cacheTimeout)) {
            log?.error "Unable to connect to Google Picasa Web Albums - invalid cache timeout " +
                "value. Please ensure you have declared the property picasa.cacheTimeout in your " +
                "application's config."
            configValid = false
        }

        // Attempt connection if configuration is valid
        if (configValid) {
            log?.info "${this.getClass().getSimpleName()} configuration valid"

            try {
                log?.info "Attempting connection..."

                // Initialise Picasa Web Service
                picasaWebService = new PicasawebService(this.picasaApplicationName)
                picasaWebService.setUserCredentials(this.picasaUsername, this.picasaPassword)

                log?.info "Successfully connected to the Google Picasa web service."

            } catch (Exception ex) {
                log?.error("Unable to connect to Google Picasa Web Albums. Please ensure the " +
                    "provided Google account details are correct and try again.", ex)
                configValid = false
            }
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
     * Retrieve the provided query from the cache.
     *
     * @param queryName name of the query to retrieve.
     * @return result the query result.
     */
    private def retrieveCache(final String queryName) {
        log?.debug "Attempting to retrieve ${queryName} from cache"

        final def result = CACHE.get(queryName)

        if (result) {
            log?.debug "Success..."
        } else {
            log?.debug "Cache not available"
        }

        return result
    }

    /**
     * Updating the Picasa Service cache with the provided query name and
     * result. In addition, perform any required logging.
     *
     * @param queryName name of the query to cache.
     * @param result the query result.
     */
    private void updateCache(final String queryName, final def result) {
        log?.debug "Updating ${queryName} cache"

        CACHE.put(queryName, result)
    }
}
