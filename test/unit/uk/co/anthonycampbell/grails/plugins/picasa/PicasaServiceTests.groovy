package uk.co.anthonycampbell.grails.plugins.picasa

import java.io.File
import java.net.URL

import com.google.gdata.client.*
import com.google.gdata.client.photos.*
import com.google.gdata.data.*
import com.google.gdata.data.media.*
import com.google.gdata.data.media.mediarss.*
import com.google.gdata.data.photos.*

import org.apache.log4j.Logger

import grails.test.*
import org.junit.*
import org.mockito.*
import static org.mockito.Mockito.*

/**
 * Set of unit tests for the Picasa service tests.
 */
class PicasaServiceTests extends GrailsUnitTestCase {

    // Declare test properties
    PicasaService picasaService
    @Mock PicasawebService mockPicasaWebService
    @Mock AlbumEntry mockAlbumEntry
    @Mock UserFeed mockUserFeed

    // Declare test items
    List<AlbumEntry> listOfAlbumEntries

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

        // Initialise all mocks
        MockitoAnnotations.initMocks(this)
        mockLogging(PicasaService, true)

        // Initialise service
        picasaService = PicasaService.newInstance()
        picasaService.picasaWebService = mockPicasaWebService
        
        // Prepare dummy data
        picasaService.picasaUsername = USERNAME
        picasaService.picasaPassword = PASSWORD
        picasaService.picasaApplicationName = APP_NAME
        picasaService.picasaImgmax = IMG_MAX
        picasaService.picasaThumbsize = THUMBSIZE
        picasaService.picasaMaxResults = MAX_RESULTS
        
        // Build test keywords
        MediaKeywords keywords = new MediaKeywords()
        ArrayList<String> keywordList = new ArrayList<String>()
        keywordList.add("Keyword One")
        keywordList.add("Keyword Two")
        keywordList.add("Keyword Three")
        keywords.addKeywords(keywordList)
        
        /*
        def thumbnails = item?.getMediaThumbnails()
        if (thumbnails?.size() > 0) {
            album.image = thumbnails?.get(thumbnails?.size()-1)?.getUrl()
            album.width = thumbnails?.get(thumbnails?.size()-1)?.getWidth()
            album.height = thumbnails?.get(thumbnails?.size()-1)?.getHeight()
        }
        */

        // Build test album
        /*
        mockAlbumEntry = mock(AlbumEntry)
        mockAlbumEntry.getId().returns("123")
        mockAlbumEntry.getGeoLocation().returns(new com.google.gdata.data.geo.impl.W3CPoint(0.00, 0.00))
        mockAlbumEntry.getMediaThumbnails().returns(null)
        mockAlbumEntry.getMediaKeywords().returns(keywords)
        mockAlbumEntry.getTitle().returns(TextConstruct.plainText("Test title"))
        mockAlbumEntry.getDescription().returns(TextConstruct.plainText("Test description"))
        mockAlbumEntry.getLocation().returns("Test location")
        mockAlbumEntry.getPhotosUsed().returns(new Integer(10))
        mockAlbumEntry.getDate().returns(new Date())
        mockAlbumEntry.getAccess().returns(GphotoAccess.Value.PUBLIC)
        */

        // Prepare a valid result set
        listOfAlbumEntries = new ArrayList<AlbumEntry>()
        listOfAlbumEntries.add(mockAlbumEntry)
        listOfAlbumEntries.add(mockAlbumEntry)
        listOfAlbumEntries.add(mockAlbumEntry)
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
        def result = picasaService.connect(USERNAME, PASSWORD, APP_NAME,
            IMG_MAX, THUMBSIZE, MAX_RESULTS)

        // Check result
        assertFalse(result)
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
        when(mockUserFeed.getAlbumEntries()).thenReturn(listOfAlbumEntries)

        // Get user feed
        when(mockPicasaWebService.getFeed(feedUrl, UserFeed.class)).thenReturn(mockUserFeed)

        // 

        // Run test
        List<Album> albumList = picasaService.listAlbums()

        // Check result
        assertNotNull("Expected an instantiated album list to be returned!", albumList)
        assertEquals("Expected album list to be empty!", 0, albumList.size())
    }

    /**
     * Test the PicasaService.listAlbums() method.
     */
    void testListAlbumsWithServiceNotInitialised() {
        // Ensure service is NOT initialised
        picasaService.serviceInitialised = false

        // Run test
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
