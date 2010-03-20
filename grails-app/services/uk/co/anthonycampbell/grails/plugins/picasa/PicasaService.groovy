package uk.co.anthonycampbell.grails.plugins.picasa

import java.net.URL;

import com.google.gdata.client.photos.*;
import com.google.gdata.data.photos.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.RedirectRequiredException;
import com.google.gdata.util.ServiceException;

import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger;
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

    // Declare dependencies
    PicasawebService picasaWebService
    def grailsApplication
    def picasaUsername
    def picasaPassword
    def picasaApplicationName
    def picasaImgmax
    def picasaThumbsize

    // Declare logger to be used "after" properties set
    def logger = Logger.getLogger(this.getClass());

    /**
     * Initialise config properties.
     */
    @Override
    void afterPropertiesSet() {
        // Get configuration from Config.groovy
        this.picasaUsername = grailsApplication.config.picasa.username
        this.picasaPassword = grailsApplication.config.picasa.password
        this.picasaApplicationName = this.getClass().getPackage().getName() +
            "-" + grailsApplication.metadata['app.name'] +
            "-" + grailsApplication.metadata['app.version']
        this.picasaImgmax = grailsApplication.config.picasa.imgmax
        this.picasaThumbsize = grailsApplication.config.picasa.thumbsize

        // Lets be optimistic
        def configValid = true

        // Validate properties
        if (!isConfigValid(this.picasaUsername)) {
            logger.error("Unable to connect to Google Picasa Web Albums - invalid username. Please " +
                "ensure you have declared the property picasa.domainname in your application's config.")
            configValid = false
        }
        if (!isConfigValid(this.picasaPassword)) {
            logger.error("Unable to connect to Google Picasa Web Albums - invalid password. Please " +
                "ensure you have declared the property picasa.domainname in your application's config.")
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
    }

    /**
     * List the available albums for the configured Google Picasa account.
     *
     * @return list of albums from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available albums.
     */
    def List<Album> listAlbums() throws PicasaServiceException {
        if(serviceInitialised) {
            try {
                // Initialise result
                List<Album> albumListing = new ArrayList<Album>()

                // Declare feed
                URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "?kind=album&thumbsize=" + this.picasaThumbsize +
                    "&imgmax=" + this.picasaImgmax)

                log.debug("FeedUrl: " + feedUrl)

                // Get user feed
                UserFeed userFeed = picasaWebService.getFeed(feedUrl, UserFeed.class)

                for (AlbumEntry entry : userFeed.getAlbumEntries()) {
                    // Transfer entry into domain class
                    Album album = convertToAlbumDomain(entry)

                    // If we have a valid public entry add to listing
                    if (!album.hasErrors() && album.isPublic) {
                        albumListing.add(album)
                    }
                }

                // Return result
                return albumListing

            } catch (Exception ex) {
                def errorMessage = "Unable to list your Google Picasa Web Albums. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ")"

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
     * @return the retrieved album from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the selected album album.
     */
    def Album getAlbum(String albumId) throws PicasaServiceException {
        if(serviceInitialised) {
            try {
                // Validate ID
                if (!albumId || StringUtils.isEmpty(albumId)) {
                    def errorMessage = "Unable to retrieve your Google Picasa Web Album. The provided " +
                        "ID was invalid. (albumId=" + albumId + ")"

                    log.error(errorMessage, ex)
                    throw new PicasaServiceException(errorMessage, ex)
                }

                // Initialise result
                Album album = null

                // Declare feed
                URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "?thumbsize=" +
                    this.picasaThumbsize + "&imgmax=" + this.picasaImgmax)

                log.debug("FeedUrl: " + feedUrl)

                // Get album feed
                AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class);

                // Transfer feed into domain class
                Album domain = convertToAlbumDomain(albumFeed)

                // If we have a valid public entry add to listing
                if (!domain.hasErrors() && domain.isPublic) {
                    album = domain
                }

                // Return result
                return album

            } catch (Exception ex) {
                def errorMessage = "Unable to retrieve your Google Picasa Web Album. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", albumId=" + albumId + ")"

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
     * @return list of photos for the provided Google Picasa web service album.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available photos.
     */
    def List<Photo> listPhotosForAlbum(String albumId) throws PicasaServiceException {
        if(serviceInitialised) {
            try {
                // Validate ID
                if (!albumId || StringUtils.isEmpty(albumId)) {
                    def errorMessage = "Unable to retrieve your Google Picasa Web Album Photos. The " +
                        "provided ID was invalid. (albumId=" + albumId + ")"

                    log.error(errorMessage, ex)
                    throw new PicasaServiceException(errorMessage, ex)
                }
                
                // Initialise result
                List<Photo> photoListing = new ArrayList<Photo>()

                // Declare feed
                URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "?thumbsize=" +
                    this.picasaThumbsize + "&imgmax=" + this.picasaImgmax)

                log.debug("FeedUrl: " + feedUrl)

                // Get album feed
                AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class)

                for (PhotoEntry entry : albumFeed.getPhotoEntries()) {
                    // Transfer entry into domain class
                    Photo photo = convertToPhotoDomain(entry)

                    // If we have a valid public entry add to listing
                    if (!photo.hasErrors() && photo.isPublic) {
                        photoListing.add(photo)
                    }
                }

                // Return result
                return photoListing

            } catch (Exception ex) {
                def errorMessage = "Unable to list your Google Picasa Web Album Photos. A problem occurred " +
                    "when making the request through the Google Data API. (username=" +
                    this.picasaUsername + ", albumId=" + albumId + ")"

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
     * Get the Photo for the provided IDs through the Google Picasa web service.
     *
     * @param albumId the provided album ID.
     * @param photoId the provided photo ID.
     * @return the retrieved photo from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the selected photo.
     */
    def Photo getPhoto(String albumId, String photoId) throws PicasaServiceException {
        if(serviceInitialised) {
            try {
                // Validate IDs
                if (!albumId || StringUtils.isEmpty(albumId) || !photoId || StringUtils.isEmpty(photoId)) {
                    def errorMessage = "Unable to retrieve your Google Picasa Web Album Photo. The " +
                        "provided IDs were invalid. (albumId=" + albumId + ", photoId=" + photoId + ")"

                    log.error(errorMessage, ex)
                    throw new PicasaServiceException(errorMessage, ex)
                }

                // Initialise result
                Photo photo = null

                // Declare feed
                URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                    this.picasaUsername + "/albumid/" + albumId + "/photoid/" + photoId +
                    "?thumbsize=" + this.picasaThumbsize + "&imgmax=" + this.picasaImgmax)

                log.debug("FeedUrl: " + feedUrl)

                // Get album feed
                PhotoFeed photoFeed = picasaWebService.getFeed(feedUrl, PhotoFeed.class) 

                // Transfer feed into domain class
                Photo domain = convertToPhotoDomain(photoFeed)

                // If we have a valid public entry add to listing
                if (!domain.hasErrors() && domain.isPublic) {
                    photo = domain
                }


                // Check we have a photo to work with
                if (photo) {
                    // Second call to find out navigation based on position in album
                    feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
                        this.picasaUsername + "/albumid/" + albumId + "?thumbsize=" +
                        this.picasaThumbsize + "&imgmax=" + this.picasaImgmax)

                    log.debug("FeedUrl: " + feedUrl)

                    // Get album feed
                    AlbumFeed albumFeed = picasaWebService.getFeed(feedUrl, AlbumFeed.class)

                    // Initialise search variables
                    boolean found = false
                    String previous = ""
                    String current = ""
                    String next = ""

                    // Find photo and store previous and subsequent IDs (if available)
                    for (PhotoEntry entry : albumFeed.getPhotoEntries()) {
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
                    this.picasaUsername + ", albumId=" + albumId + ", photoId=" + photoId + ")"

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
     * Check whether the provided config reference is valid and set.
     *
     * @param the configuration value to validate.
     * @return whether the current configuration value is valid and set.
     */
    private boolean isConfigValid(def setting) {
        // Initialise result
        def result = false

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
    private Album convertToAlbumDomain(def item) {
        // Initialise result
        Album album = new Album()

        // Process ID
        String id = item?.getId()
        album.albumId = id?.substring(id?.lastIndexOf('/') + 1, id?.length())

        // Attempt to persist geo location data
        def geoPoint = new GeoPoint()
        geoPoint.latitude = item?.getGeoLocation()?.getLatitude()
        geoPoint.longitude = item?.getGeoLocation()?.getLongitude()
        album.geoLocation = (!geoPoint.hasErrors()) ? geoPoint : null

        // Check whether album has thumbails
        def thumbnails = item?.getMediaThumbnails()
        if (thumbnails?.size() > 0) {
            album.image = thumbnails?.get(thumbnails?.size()-1)?.getUrl()
            album.width = thumbnails?.get(thumbnails?.size()-1)?.getWidth()
            album.height = thumbnails?.get(thumbnails?.size()-1)?.getHeight()
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
    private Photo convertToPhotoDomain(def item) {
        // Initialise result
        Photo photo = new Photo()

        // Process ID
        String id = item?.getId()
        photo.photoId = id?.substring(id?.lastIndexOf('/') + 1, id?.length())

        // Attempt to persist geo location data
        def geoPoint = new GeoPoint()
        geoPoint.latitude = item?.getGeoLocation()?.getLatitude()
        geoPoint.longitude = item?.getGeoLocation()?.getLongitude()
        photo.geoLocation = (!geoPoint.hasErrors()) ? geoPoint : null

        // Check whether photo has thumbails
        def thumbnails = item?.getMediaThumbnails()
        if (thumbnails?.size() > 0) {
            photo.thumbnailImage = thumbnails?.get(thumbnails?.size()-1)?.getUrl()
            photo.thumbnailWidth = thumbnails?.get(thumbnails?.size()-1)?.getWidth()
            photo.thumbnailHeight = thumbnails?.get(thumbnails?.size()-1)?.getHeight()
        }

        // Check whether photo has content
        def content = item?.getMediaContents()
        if (content?.size() > 0) {
            photo.image = content?.get(content?.size()-1)?.getUrl()
            photo.width = content?.get(content?.size()-1)?.getWidth()
            photo.height = content?.get(content?.size()-1)?.getHeight()
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
}
