package uk.co.anthonycampbell.grails.plugins.picasa

import java.net.URL

import com.google.gdata.client.Query
import com.google.gdata.client.photos.*
import com.google.gdata.data.media.mediarss.MediaKeywords
import com.google.gdata.data.photos.*
import com.google.gdata.util.AuthenticationException
import com.google.gdata.util.RedirectRequiredException
import com.google.gdata.util.ServiceException

import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.springframework.beans.factory.InitializingBean

/**
 * Grails service to expose the required Picasa API methods to the
 * application.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaService implements InitializingBean {

    // Declare service properties
    boolean transactional = true
    boolean serviceInitialised = false
    
    // Declare service scope
    static scope = "session"
    
    // Declare cache (used to reduce Google API calls)
    private static Map<String, List> tagCache = new HashMap<String, List>()

    // Declare dependencies
    PicasawebService picasaWebService
    def grailsApplication
    def picasaUsername
    def picasaPassword
    def picasaApplicationName
    def picasaImgmax
    def picasaThumbsize
    def picasaMaxResults

    // Declare logger to be used "after" properties set
    def logger = Logger.getLogger(this.getClass());

    /**
     * Initialise config properties.
     */
    @Override
    void afterPropertiesSet() {
        reset()
    }

    /*
     * Attempt to re-connect to the Picasa web service using the new provided
     * configuration details.
     *
     * @return whether a new connection was successfully made.
     */
    boolean connect(String picasaUsername, String picasaPassword,
            String picasaApplicationName, String picasaImgmax, String picasaThumbsize,
            String picasaMaxResults) {
        this.picasaUsername = picasaUsername
        this.picasaPassword = picasaPassword
        this.picasaApplicationName = picasaApplicationName
        this.picasaImgmax = picasaImgmax
        this.picasaThumbsize = picasaThumbsize
        this.picasaMaxResults = picasaMaxResults

        return validateAndInitialiseService()
    }

    /*
     * Attempt to re-connect to the Picasa web service using the provided
     * connection details available in the grails-app/conf/Config.groovy file.
     *
     * @return whether a new connection was successfully made.
     */
    boolean reset() {
        // Get configuration from Config.groovy
        this.picasaUsername = grailsApplication.config.picasa.username
        this.picasaPassword = grailsApplication.config.picasa.password
        this.picasaApplicationName = this.getClass().getPackage().getName() +
            "-" + grailsApplication.metadata['app.name'] +
            "-" + grailsApplication.metadata['app.version']
        this.picasaImgmax = grailsApplication.config.picasa.imgmax
        this.picasaThumbsize = grailsApplication.config.picasa.thumbsize
        this.picasaMaxResults = grailsApplication.config.picasa.maxResults

        // Validate properties and attempt to initialise the service
        return validateAndInitialiseService()
    }

    /**
     * List the available albums for the configured Google Picasa account.
     *
     * @param showAll whether to include hidden / private albums in the list.
     * @return list of albums from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available albums.
     */
    def List<Album> listAlbums(final boolean showAll = false) throws PicasaServiceException {
        if(serviceInitialised) {
            try {
                // Initialise result
                final List<Album> albumListing = new ArrayList<Album>()

                // Declare feed
                final URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "?kind=album&thumbsize=" + this.picasaThumbsize +
                    "&imgmax=" + this.picasaImgmax)

                log.debug("FeedUrl: " + feedUrl)

                // Get user feed
                final UserFeed userFeed = picasaWebService.getFeed(feedUrl, UserFeed.class)

                for (AlbumEntry entry : userFeed?.getAlbumEntries()) {
                    // Transfer entry into domain class
                    final Album album = convertToAlbumDomain(entry)

                    // If we have a valid public entry add to listing
                    if (!album?.hasErrors()) {
                        if (showAll || album?.isPublic) {
                            albumListing.add(album)
                        }
                    }
                }

                // Return result
                return albumListing

            } catch (Exception ex) {
                def errorMessage = "Unable to list your Google Picasa Web Albums. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", showAll=" + showAll + ")"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            def errorMessage = "Unable to list your Google Picasa Web Albums. Some of the plug-in " +
                "configuration is missing. Please refer to the documentation and ensure you have " +
                "declared all of the required configuration."

            log.error(errorMessage)
            throw new PicasaServiceException(errorMessage)
        }
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
        if(serviceInitialised) {
            try {
                // Validate ID
                if (StringUtils.isEmpty(albumId)) {
                    def errorMessage = "Unable to retrieve your Google Picasa Web Album. The provided " +
                        "ID was invalid. (albumId=" + albumId + ", showAll=" + showAll + ")"

                    log.error(errorMessage, ex)
                    throw new PicasaServiceException(errorMessage, ex)
                }

                // Initialise result
                Album album = null

                // Declare feed
                final URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "?thumbsize=" +
                    this.picasaThumbsize + "&imgmax=" + this.picasaImgmax)

                log.debug("FeedUrl: " + feedUrl)

                // Get album feed
                final AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class)

                // Declare tag feed
                final URL tagUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "?kind=tag")

                log.debug("TagUrl: " + tagUrl)

                // Get all tags for this album
                final AlbumFeed tagResultsFeed = picasaWebService.query(new Query(tagUrl), AlbumFeed.class)

                // Get any existing tags
                MediaKeywords albumTags = albumFeed?.getMediaKeywords()
                if (albumTags == null) {
                    albumTags = new MediaKeywords()
                }

                // Update album feed with results
                for (TagEntry tag : tagResultsFeed?.getTagEntries()) {
                    albumTags.addKeyword(tag?.getTitle()?.getPlainText())
                }
                albumFeed?.setKeywords(albumTags)

                // Transfer feed into domain class
                final Album domain = convertToAlbumDomain(albumFeed)

                // If we have a valid public entry add to listing
                if (!domain?.hasErrors()) {
                    if (showAll || domain?.isPublic) {
                        album = domain
                    }
                }

                // Return result
                return album

            } catch (Exception ex) {
                def errorMessage = "Unable to retrieve your Google Picasa Web Album. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", albumId=" + albumId + ", showAll=" + showAll + ")"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            def errorMessage = "Unable to retrieve your Google Picasa Web Album. Some of the plug-in " +
                "configuration is missing. Please refer to the documentation and ensure you have " +
                "declared all of the required configuration."

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
        
        if(serviceInitialised) {
            // Validate ID
            if (StringUtils.isEmpty(albumId)) {
                def errorMessage = "Unable to retrieve your Google Picasa Web Album Photos. The " +
                    "provided ID was invalid. (albumId=" + albumId + ", showAll=" + showAll + ")"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            try {
                // Initialise result
                final List<Photo> photoListing = new ArrayList<Photo>()

                // Declare feed
                final URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "?thumbsize=" +
                    this.picasaThumbsize + "&imgmax=" + this.picasaImgmax)

                log.debug("FeedUrl: " + feedUrl)

                // Get album feed
                final AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class)

                for (PhotoEntry entry : albumFeed?.getPhotoEntries()) {
                    // Transfer entry into domain class
                    final Photo photo = convertToPhotoDomain(entry)

                    // If we have a valid public entry add to listing
                    if (!photo?.hasErrors()) {
                        if (showAll || photo?.isPublic) {
                            photoListing.add(photo)
                        }
                    }
                }

                // Return result
                return photoListing

            } catch (Exception ex) {
                def errorMessage = "Unable to list your Google Picasa Web Album Photos. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", albumId=" + albumId + ", showAll=" + showAll + ")"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            def errorMessage = "Unable to list your Google Picasa Web Album Photos. Some of the plug-in " +
                "configuration is missing. Please refer tUo the documentation and ensure you have " +
                "declared all of the required configuration."

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

        if(serviceInitialised) {
            // Validate ID
            if (StringUtils.isEmpty(tagKeyword)) {
                def errorMessage = "Unable to retrieve your Google Picasa Web Album Photos. The " +
                    "provided tag keyword was invalid. (tagKeyword=" + tagKeyword +
                    ", showAll=" + showAll + ")"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            try {
                // Initialise result
                final List<Photo> photoListing = new ArrayList<Photo>()
                
                // Declare feed
                final URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername)

                log.debug("FeedUrl: " + feedUrl)

                final Query tagQuery = new Query(feedUrl)
                tagQuery.setStringCustomParameter("kind", "photo")
                tagQuery.setStringCustomParameter("tag", tagKeyword)
                tagQuery.setStringCustomParameter("thumbsize", "" + this.picasaThumbsize)
                tagQuery.setStringCustomParameter("imgmax", "" + this.picasaImgmax)
                tagQuery.setStringCustomParameter("max-results", "" + this.picasaMaxResults)
                
                // Get album feed
                final AlbumFeed tagSearchResultsFeed = picasaWebService.query(tagQuery, AlbumFeed.class)

                for (PhotoEntry entry : tagSearchResultsFeed?.getPhotoEntries()) {
                    // Transfer entry into domain class
                    final Photo photo = convertToPhotoDomain(entry)

                    // If we have a valid public entry add to listing
                    if (!photo?.hasErrors()) {
                        if (showAll || photo?.isPublic) {
                            photoListing.add(photo)
                        }
                    }
                }

                // Return result
                return photoListing

            } catch (Exception ex) {
                def errorMessage = "Unable to list your Google Picasa Web Album Photos. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", tagKeyword=" + tagKeyword + ", showAll=" + showAll + ")"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            def errorMessage = "Unable to list your Google Picasa Web Album Photos. Some of the plug-in " +
                "configuration is missing. Please refer to the documentation and ensure you have " +
                "declared all of the required configuration."

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
        if(serviceInitialised) {
            // Validate ID
            if (StringUtils.isEmpty(albumId)) {
                def errorMessage = "Unable to retrieve your Google Picasa Web Album Tags. The " +
                    "provided ID was invalid. (albumId=" + albumId + ")"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            // Check whether cache should be in use
            final boolean useTagCache = (grailsApplication?.config?.picasa?.useTagCache != null) ? grailsApplication.config.picasa.useTagCache : false

            // Check whether cache contains required tag listing
            if (useTagCache && tagCache?.containsKey(albumId)) {
                log.debug("Tag cache enabled...")

                return tagCache.get(albumId)
            }

            try {
                // Initialise result
                final List<Tag> tagListing = new ArrayList<Tag>()

                // Declare tag feed
                final URL tagUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "?kind=tag")

                log.debug("TagUrl: " + tagUrl)

                // Get all tags for this album
                final AlbumFeed tagResultsFeed = picasaWebService.query(new Query(tagUrl), AlbumFeed.class)

                // Update list with results
                for (TagEntry entry : tagResultsFeed?.getTagEntries()) {
                    // Transfer entry into domain class
                    final Tag tag = convertToTagDomain(entry)

                    // If we have a valid entry add to listing
                    if (!tag?.hasErrors()) {
                        tagListing.add(tag)
                    }
                }
                
                // Update cache
                tagCache.put(albumId, tagListing)

                // Return result
                return tagListing

            } catch (Exception ex) {
                def errorMessage = "Unable to list your Google Picasa Web Album Tags. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", albumId=" + albumId + ")"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            def errorMessage = "Unable to list your Google Picasa Web Album Tags. Some of the plug-in " +
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
        if(serviceInitialised) {
            try {
                // Initialise result
                final List<Tag> tagListing = new ArrayList<Tag>()

                // Declare tag feed
                final URL tagUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "?kind=tag")

                log.debug("TagUrl: " + tagUrl)

                // Get all tags for this album
                final AlbumFeed tagResultsFeed = picasaWebService.query(new Query(tagUrl), AlbumFeed.class)

                // Update list with results
                for (TagEntry entry : tagResultsFeed?.getTagEntries()) {
                    // Transfer entry into domain class
                    final Tag tag = convertToTagDomain(entry)

                    // If we have a valid entry add to listing
                    if (!tag?.hasErrors()) {
                        tagListing.add(tag)
                    }
                }

                // Return result
                return tagListing

            } catch (Exception ex) {
                def errorMessage = "Unable to list your Google Picasa Web Album Tags. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ")"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            def errorMessage = "Unable to list your Google Picasa Web Album Tags. Some of the plug-in " +
                "configuration is missing. Please refer to the documentation and ensure you have " +
                "declared all of the required configuration."

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
     *      the list of available tags.
     */
    def List<Comment> listCommentsForPhoto(final String albumId, final String photoId)
        throws PicasaServiceException {

        if(serviceInitialised) {
            // Validate IDs
            if (StringUtils.isEmpty(albumId) || StringUtils.isEmpty(photoId)) {
                def errorMessage = "Unable to retrieve your Google Picasa Web Album Comments. The " +
                    "provided IDs were invalid. (albumId=" + albumId + ", photoId=" + photoId + ")"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            try {
                // Initialise result
                final List<Comment> commentListing = new ArrayList<Comment>()

                // Declare tag feed
                final URL commentUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "/photoid/" + photoId + "?kind=comment")

                log.debug("CommentUrl: " + commentUrl)

                // Get all comments for this photo
                final PhotoFeed commentResultsFeed = picasaWebService.getFeed(commentUrl, PhotoFeed.class);

                // Update list with results
                for (CommentEntry entry : commentResultsFeed?.getCommentEntries()) {
                    // Transfer entry into domain class
                    final Comment comment = convertToCommentDomain(entry)

                    // If we have a valid entry add to listing
                    if (!comment?.hasErrors()) {
                        commentListing.add(comment)
                    }
                }

                // Return result
                return commentListing

            } catch (Exception ex) {
                def errorMessage = "Unable to list your Google Picasa Web Album Comments. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", albumId=" + albumId + ", photoId=" + photoId + ")"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            def errorMessage = "Unable to list your Google Picasa Web Album Comments. Some of the plug-in " +
                "configuration is missing. Please refer to the documentation and ensure you have " +
                "declared all of the required configuration."

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
    def Photo getPhoto(final String albumId, final String photoId, boolean showAll = false)
        throws PicasaServiceException {

        if(serviceInitialised) {
            // Validate IDs
            if (StringUtils.isEmpty(albumId) || StringUtils.isEmpty(photoId)) {
                def errorMessage = "Unable to retrieve your Google Picasa Web Album Photo. The " +
                    "provided IDs were invalid. (albumId=" + albumId + ", photoId=" + photoId +
                    ", showAll=" + showAll + ")"

                log.error(errorMessage)
                throw new PicasaServiceException(errorMessage)
            }

            try {
                // Initialise result
                Photo photo = null

                // Declare feed
                URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "/photoid/" + photoId +
                    "?thumbsize=" + this.picasaThumbsize + "&imgmax=" + this.picasaImgmax)

                log.debug("FeedUrl: " + feedUrl)

                // Get album feed
                final PhotoFeed photoFeed = picasaWebService.getFeed(feedUrl, PhotoFeed.class)

                // Transfer feed into domain class
                final Photo domain = convertToPhotoDomain(photoFeed)

                // If we have a valid public entry add to listing
                if (!domain?.hasErrors()) {
                    if (showAll || domain?.isPublic) {
                        photo = domain
                    }
                }

                // Check we have a photo to work with
                if (photo != null) {
                    // First get list of any comments
                    for (CommentEntry commentEntry : photoFeed?.getCommentEntries()) {
                        // Transfer comment into domain class
                        final Comment comment = convertToCommentDomain(commentEntry)

                        if (!comment?.hasErrors()) {
                            photo.addToComments(comment)
                        }
                    }

                    // Second call to find out navigation based on position in album
                    feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                        this.picasaUsername + "/albumid/" + albumId + "?thumbsize=" +
                        this.picasaThumbsize + "&imgmax=" + this.picasaImgmax)

                    log.debug("FeedUrl: " + feedUrl)

                    // Get album feed
                    final AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class)

                    // Initialise search variables
                    boolean found = false
                    String previous = ""
                    String current = ""
                    String next = ""

                    // Find photo and store previous and subsequent IDs (if available)
                    for (PhotoEntry entry : albumFeed?.getPhotoEntries()) {
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

                // Return result
                return photo

            } catch (Exception ex) {
                def errorMessage = "Unable to retrieve your Google Picasa Web Album Photo. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", albumId=" + albumId + ", photoId=" + photoId +
                    ", showAll=" + showAll + ")"

                log.error(errorMessage, ex)
                throw new PicasaServiceException(errorMessage, ex)
            }
        } else {
            def errorMessage = "Unable to retrieve your Google Picasa Web Album Photo. Some of the plug-in " +
                "configuration is missing. Please refer to the documentation and ensure you have " +
                "declared all of the required configuration."

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

        logger.info("Begin PicasaService configuration validation.")

        // Validate properties
        if (!isConfigValid(this.picasaUsername)) {
            logger.error("Unable to connect to Google Picasa Web Albums - invalid username. Please " +
                "ensure you have declared the property picasa.username in your application's config.")
            configValid = false
        }
        if (!isConfigValid(this.picasaPassword)) {
            logger.error("Unable to connect to Google Picasa Web Albums - invalid password. Please " +
                "ensure you have declared the property picasa.password in your application's config.")
            configValid = false
        }
        if (!isConfigValid(this.picasaApplicationName)) {
            logger.error("Unable to connect to Google Picasa Web Albums - invalid application name. This " +
                "plug-in's application.properties file may have been tampered with. Please re-install " +
                "the Grails Picasa plug-in.")
            configValid = false
        }
        if (!isConfigValid(this.picasaImgmax)) {
            logger.error("Unable to connect to Google Picasa Web Albums - invalid max image size. Please " +
                "ensure you have declared the property picasa.imgmax in your application's config.")
            configValid = false
        }
        if (!isConfigValid(this.picasaThumbsize)) {
            logger.error("Unable to connect to Google Picasa Web Albums - invalid thumbnail size. Please " +
                "ensure you have declared the property picasa.thumbsize in your application's config.")
            configValid = false
        }
        if (!isConfigValid(this.picasaMaxResults)) {
            logger.error("Unable to connect to Google Picasa Web Albums - invalid max search results " +
                "value. Please ensure you have declared the property picasa.maxResults in your " +
                "application's config.")
            configValid = false
        }

        // Attempt connection if configuration is valid
        if (configValid) {
            logger.info("Picasa configuration has been found.")

            try {
                logger.info("Attempting connection...")

                // Initialise Picasa Web Service
                picasaWebService = new PicasawebService(this.picasaApplicationName)
                picasaWebService.setUserCredentials(this.picasaUsername, this.picasaPassword);

                logger.info("Successfully connected to the Google Picasa web service.")

            } catch (Exception ex) {
                logger.error("Unable to connect to Google Picasa Web Albums. Please ensure the " +
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
        if (setting != null && !(setting instanceof ConfigObject)) {
            if (setting instanceof String && !StringUtils.isBlank(setting)) {
                result = true
            } else if (!(setting instanceof String)) {
                result = true
            }
        }

        // Return result
        return result
    }

    /**
     * Convert the provided AlbumFeed or AlbumEntry object into the Album domain class.
     *
     * @param item the AlbumFeed or AlbumEntry to convert.
     * @result the Album domain class.
     */
    private Album convertToAlbumDomain(final def item) {
        // Initialise result
        final Album album = new Album()

        // Process ID
        String id = item?.getId()
        album.albumId = id?.substring(id?.lastIndexOf('/') + 1, id?.length())

        // Attempt to persist geo location data
        def geoPoint = new GeoPoint()
        geoPoint.latitude = item?.getGeoLocation()?.getLatitude()
        geoPoint.longitude = item?.getGeoLocation()?.getLongitude()
        album.geoLocation = (!geoPoint.hasErrors()) ? geoPoint : null

        // Check whether album has thumbail
        def thumbnails = item?.getMediaThumbnails()
        if (thumbnails?.size() > 0) {
            album.image = thumbnails?.get(thumbnails?.size()-1)?.getUrl()
            album.width = thumbnails?.get(thumbnails?.size()-1)?.getWidth()
            album.height = thumbnails?.get(thumbnails?.size()-1)?.getHeight()
        }

        // Check whether photo has any tags
        def keywords = item?.getMediaKeywords()?.getKeywords()
        if (keywords?.size() > 0) {
            // Add all tags
            for (String keyword : keywords) {
                final Tag tag = new Tag()
                tag.keyword = keyword

                if (!tag.hasErrors()) {
                    album.addToTags(tag)
                }
            }
        }

        // Transfer remaining properties over to domain class
        album.name = item?.getTitle()?.getPlainText()
        album.description = item?.getDescription()?.getPlainText()
        album.location = item?.getLocation()
        album.photoCount = item?.getPhotosUsed()
        album.dateCreated = item?.getDate()
        album.isPublic = item?.getAccess()?.equals(GphotoAccess.Value.PUBLIC) ? true : false

        // Return update album
        return album
    }

    /**
     * Convert the provided PhotoFeed or PhotoEntry object into the Photo domain class.
     *
     * @param item the PhotoFeed or PhotoEntry to convert.
     * @result the Photo domain class.
     */
    private Photo convertToPhotoDomain(final def item) {
        // Initialise result
        final Photo photo = new Photo()

        // Process ID
        final String id = item?.getId()
        photo.photoId = id?.substring(id?.lastIndexOf('/') + 1, id?.length())

        // Attempt to persist geo location data
        final def geoPoint = new GeoPoint()
        geoPoint.latitude = item?.getGeoLocation()?.getLatitude()
        geoPoint.longitude = item?.getGeoLocation()?.getLongitude()
        photo.geoLocation = (!geoPoint.hasErrors()) ? geoPoint : null

        // Check whether photo has thumbails
        final def thumbnails = item?.getMediaThumbnails()
        if (thumbnails?.size() > 0) {
            photo.thumbnailImage = thumbnails?.get(thumbnails?.size()-1)?.getUrl()
            photo.thumbnailWidth = thumbnails?.get(thumbnails?.size()-1)?.getWidth()
            photo.thumbnailHeight = thumbnails?.get(thumbnails?.size()-1)?.getHeight()
        }

        // Check whether photo has content
        final def content = item?.getMediaContents()
        if (content?.size() > 0) {
            photo.image = content?.get(content?.size()-1)?.getUrl()
            photo.width = content?.get(content?.size()-1)?.getWidth()
            photo.height = content?.get(content?.size()-1)?.getHeight()
        }

        // Check whether photo has any tags
        final def keywords = item?.getMediaKeywords()?.getKeywords()
        if (keywords?.size() > 0) {
            // Add all tags
            for (String keyword : keywords) {
                final Tag tag = new Tag()
                tag.keyword = keyword

                if (!tag.hasErrors()) {
                    photo.addToTags(tag)
                }
            }
        }

        // Transfer remaining properties over to domain class
        photo.albumId = item?.getAlbumId()
        photo.title = item?.getTitle()?.getPlainText()
        photo.description = item?.getDescription()?.getPlainText()
        photo.cameraModel = item?.getExifTags()?.getCameraModel()
        photo.dateCreated = item?.getTimestamp()
        photo.isPublic = item?.getAlbumAccess()?.equals(GphotoAccess.Value.PUBLIC) ? true : false

        // Return updated photo
        return photo
    }

    /**
     * Convert the provided TagEntry object into the Tag domain class.
     *
     * @param entry the TagEntry to convert.
     * @result the Tag domain class.
     */
    private Tag convertToTagDomain(final TagEntry entry) {
        // Initialise result
        final Tag tag = new Tag()

        // Process keyword
        tag.keyword = entry?.getTitle()?.getPlainText()

        // Return updated tag
        return tag
    }

    /**
     * Convert the provided CommentEntry object into the Comment domain class.
     *
     * @param entry the CommentEntry to convert.
     * @result the Comment domain class.
     */
    private Comment convertToCommentDomain(final CommentEntry entry) {
        // Initialise result
        final Comment comment = new Comment()

        // Process properties
        comment.commentId = entry?.getId()
        comment.albumId = entry?.getAlbumId()
        comment.photoId = entry?.getPhotoId()
        comment.message = entry?.getPlainTextContent()

        // Convert DateTime to java.util.Date
        final Date date = new Date()
        date.setTime(entry?.getUpdated()?.getValue())
        comment.dateCreated = date

        // Add author
        final Person person = convertToPersonDomain(entry?.getAuthors()?.get(0))
        if (!person.hasErrors()) {
            comment.author = person
        }

        // Return updated comment
        return comment
    }
    
    /**
     * Convert the provided com.google.gdata.data.Person object into the
     * Person domain class.
     *
     * @param entry the com.google.gdata.data.Person to convert.
     * @result the Person domain class.
     */
    private Person convertToPersonDomain(final com.google.gdata.data.Person entry) {
        // Initalise result
        final Person person = new Person()

        // Process properties
        person.name = entry?.getName()
        person.email = entry?.getEmail()
        person.uri = entry?.getUri()

        // Return updated person
        return person
    }
}
