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
import com.google.gdata.util.AuthenticationException
import com.google.gdata.util.RedirectRequiredException
import com.google.gdata.util.ServiceException

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
    @Mock PhotoEntry mockPhotoEntry
    @Mock TagEntry mockTagEntry
    @Mock UserFeed mockUserFeed
    @Mock AlbumFeed mockAlbumFeed
    @Mock AlbumFeed mockTagFeed
    @Mock MediaThumbnail mockMediaThumbnail
    @Mock com.google.gdata.data.media.mediarss.MediaContent mockMediaContent
    @Mock MediaKeywords mockMediaKeywords
    @Mock TextConstruct mockTextConstruct
    @Mock ExifTags mockExifTags

    // Declare test items
    List<AlbumEntry> listOfAlbumEntries
    List<TagEntry> listOfTagEntries
    List<PhotoEntry> listOfPhotoEntries
    List<MediaContent> listOfMediaContents

    // Declare some test config values
    static final String USERNAME = "username"
    static final String PASSWORD = "password"
    static final String APP_NAME = "uk.co.anthonycampbell.grails.plugins.picasa.PicasaServiceTests"
    static final String IMG_MAX = "123"
    static final String THUMBSIZE = "123"
    static final String MAX_RESULTS = "123"

    // Declare some test values
    static final String TEST_ALBUM_ID = "123"
    static final String TEST_PHOTO_ID = "456"
    static final String TEST_TAG_KEYWORD = "Keyword"
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
    static final String TEST_CAMERA_MAKE = "Test camera make"
    static final String TEST_CAMERA_MODEL = "Test camera model"
    static final String TEST_CONTENT_URL = "http://localhost"
    static final int TEST_CONTENT_WIDTH = 100
    static final int TEST_CONTENT_HEIGHT = 100
    static final boolean TEST_SHOW_ALL = true

    // Declare test feeds
    static final URL USER_FEED_URL = new URL(
            "http://picasaweb.google.com/data/feed/api/user/" +
            USERNAME + "?kind=album&thumbsize=" + THUMBSIZE +
            "&imgmax=" + IMG_MAX)
    static final URL SIMPLE_USER_FEED_URL = new URL(
            "http://picasaweb.google.com/data/feed/api/user/" + USERNAME)
    static final URL ALBUM_FEED_URL = new URL(
            "http://picasaweb.google.com/data/feed/api/user/" +
            USERNAME + "/albumid/" + TEST_ALBUM_ID + "?thumbsize=" + THUMBSIZE +
            "&imgmax=" + IMG_MAX)
    static final URL ALBUM_TAG_FEED_URL = new URL(
            "http://picasaweb.google.com/data/feed/api/user/" + USERNAME +
            "/albumid/" + TEST_ALBUM_ID + "?kind=tag")

    // Test tag query
    static final Query TEST_QUERY = new Query(SIMPLE_USER_FEED_URL)

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
        mockForConstraintsTests(Photo)
        mockForConstraintsTests(Comment)
        mockForConstraintsTests(Tag)
        mockForConstraintsTests(GeoPoint)

        // Add addition meta program methods
        mockDomain(Album, [])
        mockDomain(Photo, [])
        mockDomain(Comment, [])
        mockDomain(Tag, [])
        mockDomain(GeoPoint, [])

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

        // Exif tags for camera model etc
        when(mockExifTags.getCameraMake()).thenReturn(TEST_CAMERA_MAKE)
        when(mockExifTags.getCameraModel()).thenReturn(TEST_CAMERA_MODEL)

        // Mock media contents
        when(mockMediaContent.getUrl()).thenReturn(TEST_CONTENT_URL)
        when(mockMediaContent.getWidth()).thenReturn(TEST_CONTENT_WIDTH)
        when(mockMediaContent.getHeight()).thenReturn(TEST_CONTENT_HEIGHT)

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

        // Prepare photo list
        listOfMediaContents = new ArrayList<MediaContent>()
        listOfMediaContents.add(mockMediaContent)
        listOfMediaContents.add(mockMediaContent)
        listOfMediaContents.add(mockMediaContent)

        // Mock test photo
        when(mockPhotoEntry.getId()).thenReturn(TEST_PHOTO_ID)
        when(mockPhotoEntry.getAlbumId()).thenReturn(TEST_ALBUM_ID)
        when(mockPhotoEntry.getGeoLocation()).thenReturn(TEST_W3C_POINT)
        when(mockPhotoEntry.getMediaThumbnails()).thenReturn(THUMBNAIL_LIST)
        when(mockPhotoEntry.getMediaContents()).thenReturn(listOfMediaContents)
        when(mockPhotoEntry.getMediaKeywords()).thenReturn(mockMediaKeywords)
        when(mockPhotoEntry.getTitle()).thenReturn(mockTextConstruct)
        when(mockPhotoEntry.getDescription()).thenReturn(mockTextConstruct)
        when(mockPhotoEntry.getExifTags()).thenReturn(mockExifTags)
        when(mockPhotoEntry.getTimestamp()).thenReturn(TEST_DATE)
        //when(mockPhotoEntry.getAccess()).thenReturn(TEST_ACCESS_PUBLIC)

        // Prepare a valid album result set
        listOfPhotoEntries = new ArrayList<PhotoEntry>()
        listOfPhotoEntries.add(mockPhotoEntry)
        listOfPhotoEntries.add(mockPhotoEntry)
        listOfPhotoEntries.add(mockPhotoEntry)

        // Mock test album feed
        when(mockAlbumFeed.getId()).thenReturn(TEST_ALBUM_ID)
        when(mockAlbumFeed.getGeoLocation()).thenReturn(TEST_W3C_POINT)
        when(mockAlbumFeed.getMediaThumbnails()).thenReturn(THUMBNAIL_LIST)
        when(mockAlbumFeed.getMediaKeywords()).thenReturn(mockMediaKeywords)
        //when(mockAlbumFeed.getTitle()).thenReturn(mockTextConstruct)
        when(mockAlbumFeed.getDescription()).thenReturn(mockTextConstruct)
        when(mockAlbumFeed.getLocation()).thenReturn(TEST_LOCATION)
        when(mockAlbumFeed.getPhotosUsed()).thenReturn(TEST_PHOTO_COUNT)
        when(mockAlbumFeed.getDate()).thenReturn(TEST_DATE)
        when(mockAlbumFeed.getAccess()).thenReturn(TEST_ACCESS_PUBLIC)
        when(mockAlbumFeed.getPhotoEntries()).thenReturn(listOfPhotoEntries)

        // Prepare a valid album result set
        listOfAlbumEntries = new ArrayList<AlbumEntry>()
        listOfAlbumEntries.add(mockAlbumEntry)
        listOfAlbumEntries.add(mockAlbumEntry)
        listOfAlbumEntries.add(mockAlbumEntry)

        // Mock test tag
        when(mockTagEntry.getTitle()).thenReturn(mockTextConstruct)

        // Prepare a valid tag result set
        listOfTagEntries = new ArrayList<TagEntry>()
        listOfTagEntries.add(mockTagEntry)
        listOfTagEntries.add(mockTagEntry)
        listOfTagEntries.add(mockTagEntry)

        // Update tag query
        TEST_QUERY.setStringCustomParameter("kind", "photo")
        TEST_QUERY.setStringCustomParameter("tag", TEST_TAG_KEYWORD)
        TEST_QUERY.setStringCustomParameter("thumbsize", "" + THUMBSIZE)
        TEST_QUERY.setStringCustomParameter("imgmax", "" + IMG_MAX)
        TEST_QUERY.setStringCustomParameter("max-results", "" + MAX_RESULTS)
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
    void testListAlbums_WithServiceNotInitialised() {
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
                "you have declared all of the required configuration.",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listAlbums() method.
     */
    void testListAlbums_WithEmptyAlbumList() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        // Prepare mock user feed with empty list
        when(mockUserFeed.getAlbumEntries()).thenReturn(Collections.emptyList())

        // Get user feed
        when(mockPicasaWebService.getFeed(USER_FEED_URL, UserFeed.class)).thenReturn(mockUserFeed)

        // Run test
        List<Album> albumList = picasaService.listAlbums()

        // Check result
        assertNotNull("Expected an instantiated album list to be returned!", albumList)
        assertEquals("Unexpected album list size returned!", 0, albumList.size())
    }

    /**
     * Test the PicasaService.getAlbum() method.
     */
    void testGetAlbum() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        // Get album feed
        when(mockPicasaWebService.getFeed(ALBUM_FEED_URL, AlbumFeed.class)).thenReturn(mockAlbumFeed)

        // Get tag feed
        when(mockPicasaWebService.query(new Query(ALBUM_TAG_FEED_URL), AlbumFeed.class))
            .thenReturn(mockAlbumFeed)

        // Run test
        Album album = picasaService.getAlbum(TEST_ALBUM_ID)

        // Check result
        assertNotNull("Expected an instantiated album to be returned!", album)
        assertEquals("Unexpected album returned!",
            TEST_ALBUM_ID, album.getId())
    }

    /**
     * Test the PicasaService.listPhotosForAlbum() method.
     */
    void testListPhotosForAlbum() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        // Get album feed
        when(mockPicasaWebService.getFeed(ALBUM_FEED_URL, AlbumFeed.class)).thenReturn(mockAlbumFeed)

        // Run test
        List<Photo> photoList = picasaService.listPhotosForAlbum(TEST_ALBUM_ID)

        // Check result
        assertNotNull("Expected an instantiated photo list to be returned!", photoList)
        assertEquals("Unexpected photo list returned!",
            listOfPhotoEntries.size(), photoList.size())
    }

    /**
     * Test the PicasaService.listPhotosForAlbum() method.
     */
    void testListPhotosForAlbum_WithNoPhotos() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        // No photos
        when(mockAlbumFeed.getPhotoEntries()).thenReturn(Collections.emptyList())

        // Get album feed
        when(mockPicasaWebService.getFeed(ALBUM_FEED_URL, AlbumFeed.class)).thenReturn(mockAlbumFeed)

        // Run test
        List<Photo> photoList = picasaService.listPhotosForAlbum(TEST_ALBUM_ID)

        // Check result
        assertNotNull("Expected an instantiated photo list to be returned!", photoList)
        assertEquals("Unexpected number of photos returned!", 0, photoList.size())
    }

    /**
     * Test the PicasaService.listPhotosForAlbum() method.
     */
    void testListPhotosForAlbum_WithServiceNotInitialised() {
        // Ensure service is initialised
        picasaService.serviceInitialised = false

        try {
            // Run test
            picasaService.listPhotosForAlbum(TEST_ALBUM_ID)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to list your Google Picasa Web Album Photos. Some of the plug-in " +
                "configuration is missing. Please refer tUo the documentation and ensure you have " +
                "declared all of the required configuration.",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listPhotosForAlbum() method.
     */
    void testListPhotosForAlbum_WithEmptyAlbumId() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        try {
            // Run test
            picasaService.listPhotosForAlbum("")
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Photos. The " +
                "provided ID was invalid. (albumId=" + "" + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listPhotosForAlbum() method.
     */
    void testListPhotosForAlbum_WithNullAlbumId() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        try {
            // Run test
            picasaService.listPhotosForAlbum(null)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Photos. The " +
                "provided ID was invalid. (albumId=" + null + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listPhotosForAlbum() method.
     */
    void testListPhotosForAlbum_WithFeedException() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        // Get album feed
        when(mockPicasaWebService.getFeed(ALBUM_FEED_URL, AlbumFeed.class))
            .thenThrow(new ServiceException("Test exception"))

        try {
            // Run test
            picasaService.listPhotosForAlbum(TEST_ALBUM_ID)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Photos. The " +
                "provided ID was invalid. (albumId=" + TEST_ALBUM_ID + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listPhotosForTag() method.
     */
    void testListPhotosForTag() {
        picasaService.serviceInitialised = true

        // Get tag feed
        when(mockPicasaWebService.query(TEST_QUERY, AlbumFeed.class)).thenReturn(mockAlbumFeed)

        // Run test
        List<Photo> photoList = picasaService.listPhotosForTag(TEST_TAG_KEYWORD)

        // Check result
        assertNotNull("Expected an instantiated photo list to be returned!", photoList)
        assertEquals("Unexpected photo list returned!",
            listOfPhotoEntries.size(), photoList.size())
    }

    /**
     * Test the PicasaService.listPhotosForTag() method.
     */
    void testListPhotosForTag_WithNoPhotos() {
        picasaService.serviceInitialised = true

        // Get tag feed
        when(mockPicasaWebService.query(TEST_QUERY, AlbumFeed.class))
            .thenReturn(Collections.emptyList())

        // Run test
        List<Photo> photoList = picasaService.listPhotosForTag(TEST_TAG_KEYWORD)

        // Check result
        assertNotNull("Expected an instantiated photo list to be returned!", photoList)
        assertEquals("Unexpected photo list returned!", 0, photoList.size())
    }

    /**
     * Test the PicasaService.listPhotosForTag() method.
     */
    void testListPhotosForTag_WithServiceNotInitialised() {
        // Ensure service is initialised
        picasaService.serviceInitialised = false

        try {
            // Run test
            picasaService.listPhotosForTag(TEST_TAG_KEYWORD)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to list your Google Picasa Web Album Tags. Some of the plug-in " +
                "configuration is missing. Please refer two the documentation and ensure you have " +
                "declared all of the required configuration.",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listPhotosForTag() method.
     */
    void testListPhotosForTag_WithEmptyTagKeyword() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        try {
            // Run test
            picasaService.listPhotosForTag("")
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Tags. The " +
                "provided ID was invalid. (albumId=" + "" + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listPhotosForTag() method.
     */
    void testListPhotosForTag_WithNullTagKeyword() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        try {
            // Run test
            picasaService.listPhotosForTag(null)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Tags. The " +
                "provided ID was invalid. (albumId=" + null + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listPhotosForTag() method.
     */
    void testListPhotosForTag_WithFeedException() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        // Get tag feed
        when(mockPicasaWebService.query(TEST_QUERY, AlbumFeed.class))
            .thenThrow(new ServiceException("Test exception"))

        try {
            // Run test
            picasaService.listPhotosForTag(TEST_TAG_KEYWORD)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Photos. The " +
                "provided ID was invalid. (albumId=" + TEST_TAG_KEYWORD + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listTagsForAlbum() method.
     */
    void testListTagsForAlbum() {
        picasaService.serviceInitialised = true

        // Get tag feed
        when(mockPicasaWebService.query(new Query(ALBUM_TAG_FEED_URL), AlbumFeed.class))
            .thenReturn(mockAlbumFeed)

        // Run test
        List<Photo> photoList = picasaService.listTagsForAlbum(TEST_ALBUM_ID)

        // Check result
        assertNotNull("Expected an instantiated photo list to be returned!", photoList)
        assertEquals("Unexpected photo list returned!",
            listOfPhotoEntries.size(), photoList.size())
    }

    /**
     * Test the PicasaService.listTagsForAlbum() method.
     */
    void testListTagsForAlbum_WithNoPhotos() {
        picasaService.serviceInitialised = true

        // Get tag feed
        when(mockPicasaWebService.query(new Query(ALBUM_TAG_FEED_URL), AlbumFeed.class))
            .thenReturn(Collections.emptyList())

        // Run test
        List<Photo> photoList = picasaService.listTagsForAlbum(TEST_ALBUM_ID)

        // Check result
        assertNotNull("Expected an instantiated photo list to be returned!", photoList)
        assertEquals("Unexpected photo list returned!", 0, photoList.size())
    }

    /**
     * Test the PicasaService.listTagsForAlbum() method.
     */
    void testListTagsForAlbum_WithServiceNotInitialised() {
        // Ensure service is initialised
        picasaService.serviceInitialised = false

        try {
            // Run test
            picasaService.listTagsForAlbum(TEST_ALBUM_ID)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to list your Google Picasa Web Album Tags. Some of the plug-in " +
                "configuration is missing. Please refer two the documentation and ensure you have " +
                "declared all of the required configuration.",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listTagsForAlbum() method.
     */
    void testListTagsForAlbum_WithEmptyAlbumId() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        try {
            // Run test
            picasaService.listTagsForAlbum("")
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Tags. The " +
                "provided ID was invalid. (albumId=" + "" + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listTagsForAlbum() method.
     */
    void testListTagsForAlbum_WithNullAlbumId() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        try {
            // Run test
            picasaService.listTagsForAlbum(null)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Tags. The " +
                "provided ID was invalid. (albumId=" + null + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }

    /**
     * Test the PicasaService.listTagsForAlbum() method.
     */
    void testListTagsForAlbum_WithFeedException() {
        // Ensure service is initialised
        picasaService.serviceInitialised = true

        // Get tag feed
        when(mockPicasaWebService.query(new Query(ALBUM_TAG_FEED_URL), AlbumFeed.class))
            .thenThrow(new ServiceException("Test exception"))

        try {
            // Run test
            picasaService.listTagsForAlbum(TEST_ALBUM_ID)
            fail("Expected PicasaServiceException to be thrown!")

        } catch (PicasaServiceException pse) {
            // Check result
            assertEquals("Unexpected exception has been thrown!",
                "Unable to retrieve your Google Picasa Web Album Photos. The " +
                "provided ID was invalid. (albumId=" + TEST_ALBUM_ID + ", showAll=" + false + ")",
                pse.getMessage())
        }
    }
}
