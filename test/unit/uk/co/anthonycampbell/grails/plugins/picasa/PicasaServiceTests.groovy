package uk.co.anthonycampbell.grails.plugins.picasa

import java.io.File
import java.net.URL

import com.google.gdata.client.*
import com.google.gdata.client.photos.*
import com.google.gdata.data.*
import com.google.gdata.data.geo.impl.W3CPoint
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
    @Mock MediaThumbnail mockMediaThumbnail
    @Mock MediaKeywords mockMediaKeywords
    @Mock TextConstruct mockTextConstruct

    // Declare test items
    List<AlbumEntry> listOfAlbumEntries

    // Declare some test config values
    static final String USERNAME = "username"
    static final String PASSWORD = "password"
    static final String APP_NAME = "uk.co.anthonycampbell.grails.plugins.picasa.PicasaServiceTests"
    static final String IMG_MAX = "123"
    static final String THUMBSIZE = "123"
    static final String MAX_RESULTS = "123"

    // Declare some test values
    static final String TEST_ALBUM_ID = "123"
    static final W3CPoint TEST_W3C_POINT = new W3CPoint(0.00, 0.00)
    static final String TEST_MEDIA_THUMBNAIL_URL = ""
    static final int TEST_MEDIA_THUMBNAIL_WIDTH = 100
    static final int TEST_MEDIA_THUMBNAIL_HEIGHT = 100
    static final String TEST_TITLE = "Test title"
    static final String TEST_DESCRIPTION = "Test description"
    static final String TEST_LOCATION = "Test location"
    static final Integer TEST_PHOTO_COUNT = 10
    static final Date TEST_DATE = new Date()
    static final String TEST_ACCESS_PUBLIC = GphotoAccess.Value.PUBLIC

    // Declare test feeds
    static final URL USER_FEED_URL = new URL(
            "http://picasaweb.google.com/data/feed/api/user/" +
            USERNAME + "?kind=album&thumbsize=" + THUMBSIZE +
            "&imgmax=" + IMG_MAX)

    /**
     * Set up test properties and values for the unit tests.
     */
    protected void setUp() {
        super.setUp()

        // Initialise all mocks
        MockitoAnnotations.initMocks(this)
        mockLogging(PicasaService, true)

        // Add validation methods
        mockForConstraintsTests(Album)
        mockForConstraintsTests(GeoPoint)
        mockForConstraintsTests(Tag)

        // Add addition meta program methods
        mockDomain(Album, [])

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

        // Mock media thumbnail
        when(mockMediaThumbnail.getUrl()).thenReturn(TEST_MEDIA_THUMBNAIL_URL)
        when(mockMediaThumbnail.getWidth()).thenReturn(TEST_MEDIA_THUMBNAIL_WIDTH)
        when(mockMediaThumbnail.getHeight()).thenReturn(TEST_MEDIA_THUMBNAIL_HEIGHT)
        final List<MediaThumbnail> THUMBNAIL_LIST = new ArrayList<MediaThumbnail>()
        THUMBNAIL_LIST.add(mockMediaThumbnail)
        THUMBNAIL_LIST.add(mockMediaThumbnail)
        THUMBNAIL_LIST.add(mockMediaThumbnail)

        // Mock media keywords
        final List<String> KEYWORD_LIST = new ArrayList<String>()
        KEYWORD_LIST.add("Keyword One")
        KEYWORD_LIST.add("Keyword Two")
        KEYWORD_LIST.add("Keyword Three")
        when(mockMediaKeywords.getKeywords()).thenReturn(KEYWORD_LIST)

        // Mock text construct for title and description
        when(mockTextConstruct.getPlainText())
            .thenReturn(TEST_TITLE)
            .thenReturn(TEST_DESCRIPTION)
        
        // Mock test album
        when(mockAlbumEntry.getId()).thenReturn(TEST_ALBUM_ID)
        when(mockAlbumEntry.getGeoLocation()).thenReturn(TEST_W3C_POINT)
        when(mockAlbumEntry.getMediaThumbnails()).thenReturn(THUMBNAIL_LIST)
        when(mockAlbumEntry.getMediaKeywords()).thenReturn(mockMediaKeywords)
        when(mockAlbumEntry.getTitle()).thenReturn(mockTextConstruct)
        when(mockAlbumEntry.getDescription()).thenReturn(mockTextConstruct)
        when(mockAlbumEntry.getLocation()).thenReturn(TEST_LOCATION)
        when(mockAlbumEntry.getPhotosUsed()).thenReturn(TEST_PHOTO_COUNT)
        when(mockAlbumEntry.getDate()).thenReturn(TEST_DATE)
        when(mockAlbumEntry.getAccess()).thenReturn(TEST_ACCESS_PUBLIC)

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

        // Prepare mock user feed
        when(mockUserFeed.getAlbumEntries()).thenReturn(listOfAlbumEntries)

        // Get user feed
        when(mockPicasaWebService.getFeed(USER_FEED_URL, UserFeed.class)).thenReturn(mockUserFeed)

        // Run test
        List<Album> albumList = picasaService.listAlbums()

        // Check result
        assertNotNull("Expected an instantiated album list to be returned!", albumList)
        assertEquals("Unexpected album list size returned!",
            listOfAlbumEntries.size(), albumList.size())
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
