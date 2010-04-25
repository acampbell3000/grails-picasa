package uk.co.anthonycampbell.grails.plugins.picasa

import java.io.File;
import java.net.URL;

import com.google.gdata.client.*;
import com.google.gdata.client.photos.*;
import com.google.gdata.data.*;
import com.google.gdata.data.media.*;
import com.google.gdata.data.photos.*;

import org.apache.log4j.Logger

import grails.test.*
import org.gmock.*

/**
 * Set of unit tests for the Picasa service tests.
 */
@WithGMock
class PicasaServiceTests extends GrailsUnitTestCase {

    // Declare test properties
    PicasaService picasaService
    def mockPicasaWebService
    def mockLogger

    // Declare test items
    def mockAlbumEntry

    // Declare some test values
    static final String USERNAME = "username"
    static final String PASSWORD = "password"
    static final String APP_NAME = "uk.co.anthonycampbell.grails.plugins.picasa.PicasaServiceTests"
    static final String IMG_MAX = "123"
    static final String THUMBSIZE = "123"
    static final String MAX_RESULTS = "123"

    /**
     * Set up test properties and values for the unit tests.
     */
    protected void setUp() {
        super.setUp()

        //  Declare mock objects
        mockPicasaWebService = mock(PicasawebService)

        // Initialise service
        mockLogging(PicasaService, true)
        picasaService = PicasaService.newInstance()
        picasaService.picasaWebService = mockPicasaWebService
        
        // Prepare dummy data
        picasaService.picasaUsername = USERNAME
        picasaService.picasaPassword = PASSWORD
        picasaService.picasaApplicationName = APP_NAME
        picasaService.picasaImgmax = IMG_MAX
        picasaService.picasaThumbsize = THUMBSIZE
        picasaService.picasaMaxResults = MAX_RESULTS

        // Build some test results
        mock

        List<AlbumEntry> listOfAlbumEntries = new ArrayList<AlbumEntry>()
        listOfAlbumEntries.add()

        // Build test geo location


        // Build test album
        mockAlbumEntry = mock(AlbumEntry)
        mockAlbumEntry.getId().returns("123")
        mockAlbumEntry.getGeoLocation().returns(new com.google.gdata.data.geo.impl.W3CPoint(0.00, 0.00))
        mockAlbumEntry.getMediaThumbnails().returns(null)
        mockAlbumEntry.getMediaKeywords().returns(null)
        mockAlbumEntry.getTitle().returns(TextConstruct.plainText("Test title"))
        mockAlbumEntry.getDescription().returns(TextConstruct.plainText("Test description"))
        mockAlbumEntry.getLocation().returns("Test location")
        mockAlbumEntry.getPhotosUsed().returns(new Integer(10))
        mockAlbumEntry.getDate().returns(new Date())
        mockAlbumEntry.getAccess().returns(GphotoAccess.Value.PUBLIC)


        // Check whether album has thumbails
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
                Tag tag = new Tag()
                tag.keyword = keyword

                if (!tag.hasErrors()) {
                    album.addToTags(tag)
                }
            }
        }

    }

    /**
     * Clear up test properties and values.
     */
    protected void tearDown() {
        super.tearDown()
    }

    /**
     * Test the PicasaService.connect() method.
     */
    void testConnectWithTestLoginDetails() {
        // Run test
        play {
            def result = picasaService.connect(USERNAME, PASSWORD, APP_NAME,
                IMG_MAX, THUMBSIZE, MAX_RESULTS)

            // Check result
            assertFalse(result)
        }
    }

    /**
     * Test the PicasaService.listAlbums() method.
     */
    void testListAlbums() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        // Declare feed
        URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/" +
            USERNAME + "?kind=album&thumbsize=" + THUMBSIZE +
            "&imgmax=" + IMG_MAX)

        // Prepare mock user feed
        def mockUserFeed = mock(UserFeed)
        mockUserFeed.getAlbumEntries().returns(new AlbumEntry())

        // Get user feed
        mockPicasaWebService.getFeed(feedUrl, UserFeed.class).returns(mockUserFeed)

        //UserFeed userFeed = 

        // Run test
        play {
            List<Album> albumList = picasaService.listAlbums()

            // Check result
            assertNotNull("Expected an instantiated album list to be returned!", albumList)
            assertEquals("Expected album list to be empty!", 0, albumList.size())
        }
    }

    /**
     * Test the PicasaService.listAlbums() method.
     */
    void testListAlbumsWithServiceNotInitialised() {
        // Ensure service is NOT initialised
        picasaService.serviceInitialised = false

        // Run test
        play {
            try {
                picasaService.listAlbums()
                fail("Expected PicasaServiceException to be thrown!")
                
            } catch (PicasaServiceException pse) {
                // Check result
                assertEquals("Unexpected exception has been thrown!",
                    "Unable to list your Google Picasa Web Albums. Some of the plug-in " +
                    "configuration is missing. Please refer to the documentation and ensure " +
                    "you have declared all of the required configuration.", pse.getMessage())
            }
        }
    }
}
