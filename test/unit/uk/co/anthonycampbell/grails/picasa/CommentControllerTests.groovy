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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collections;

import groovy.util.ConfigObject
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.converters.JSON
import grails.converters.XML
import grails.test.*

import org.junit.*
import org.mockito.*
import static org.mockito.Mockito.*

import uk.co.anthonycampbell.grails.picasa.matchers.IsComment

/**
 * Set of unit tests for the comment controller.
 * 
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class CommentControllerTests extends ControllerUnitTestCase {

    // Declare test dependencies
    def grailsApplication

    // Declare mock dependencies
    @Mock PicasaServiceInterface mockPicasaService
    @Mock PicasaServiceInterface mockPicasaServiceEmpty
    @Mock PicasaServiceInterface mockPicasaServiceException
    @Mock PicasaCommentServiceInterface mockPicasaCommentService
    @Mock PicasaCommentServiceInterface mockPicasaCommentServiceException

    // Test comments
    final Comment TEST_COMMENT_1 = new Comment()
    final Comment TEST_COMMENT_2 = new Comment()
    final Comment TEST_COMMENT_3 = new Comment()

    // Test values
    final def TEST_LIST = [TEST_COMMENT_1, TEST_COMMENT_2, TEST_COMMENT_3]
    final def TEST_EMPTY_LIST = []
    final def TEST_MESSAGE_1 = "Comment 1"
    final def TEST_MESSAGE_2 = "Comment 2"
    final def TEST_MESSAGE_3 = "Comment 3"
    final def TEST_ALBUM_ID = "123456"
    final def TEST_PHOTO_ID = "654321"
    final def TEST_INVALID_ALBUM_ID = "abcdef"
    final def TEST_INVALID_PHOTO_ID = "fedcba"
    final def TEST_OFFSET = "5"
    final def TEST_MAX = "10"
    final def TEST_INVALID_OFFSET = "abc"
    final def TEST_INVALID_MAX = "cba"
    final def TEST_I18N_MESSAGE = "Test message"
    final def TEST_PICASA_SERVICE_EXCEPTION = new PicasaServiceException("Test exception")
    final def TEST_PICASA_COMMENT_SERVICE_EXCEPTION = new PicasaCommentServiceException("Test exception")
    
    /**
     * Set up the test suite.
     */
    protected void setUp() {
        super.setUp()

        // Initialise logging
        mockLogging(CommentController.class, true)

        // Initialise all mocks
        MockitoAnnotations.initMocks(this)
        
        // Setup config
        final def mockedConfig = new ConfigObject()
        mockedConfig.picasa.maxComments = 50
        GrailsApplication.metaClass.getConfig = { -> mockedConfig }

        // Mock i18n
        controller.metaClass.message = { def map -> return TEST_I18N_MESSAGE }
        
        // Prepare test comments
        TEST_COMMENT_1.albumId = TEST_ALBUM_ID
        TEST_COMMENT_1.photoId = TEST_PHOTO_ID
        TEST_COMMENT_1.message = TEST_MESSAGE_1
        TEST_COMMENT_2.albumId = TEST_ALBUM_ID
        TEST_COMMENT_2.photoId = TEST_PHOTO_ID
        TEST_COMMENT_2.message = TEST_MESSAGE_2
        TEST_COMMENT_3.albumId = TEST_ALBUM_ID
        TEST_COMMENT_3.photoId = TEST_PHOTO_ID
        TEST_COMMENT_3.message = TEST_MESSAGE_3

        // Return populated list
        when(mockPicasaService.listAllComments()).thenReturn(TEST_LIST)
        when(mockPicasaService.listCommentsForPhoto(TEST_ALBUM_ID, TEST_PHOTO_ID)).thenReturn(
            TEST_LIST)
        when(mockPicasaService.listCommentsForPhoto("", "")).thenReturn(TEST_EMPTY_LIST)

        // Return empty list
        when(mockPicasaServiceEmpty.listAllComments()).thenReturn(TEST_EMPTY_LIST)
        when(mockPicasaServiceEmpty.listCommentsForPhoto(TEST_ALBUM_ID, TEST_PHOTO_ID)).thenReturn(
            TEST_EMPTY_LIST)

        // Throw exception
        when(mockPicasaServiceException.listAllComments()).thenThrow(TEST_PICASA_SERVICE_EXCEPTION)
        when(mockPicasaServiceException.listCommentsForPhoto(TEST_ALBUM_ID, TEST_PHOTO_ID)).thenThrow(
            TEST_PICASA_SERVICE_EXCEPTION)

        // Throw exception
        when(mockPicasaCommentServiceException.postComment(argThat(new IsComment()))).thenThrow(
            TEST_PICASA_COMMENT_SERVICE_EXCEPTION)
    }

    /**
     * Tear down the test suite.
     */
    protected void tearDown() {
        super.tearDown()
    }

    /**
     * Unit test for the index controller method.
     */
    void testIndex() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Run test
        controller.index()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action

        // Check responses
        assertEquals "Unexpected response returned!", "comment", redirectController
        assertEquals "Unexpected response returned!", "list", redirectAction
    }

    /**
     * Unit test for the index controller method.
     */
    void testIndex_Params() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Apply param
        controller.params.albumId = TEST_ALBUM_ID

        // Run test
        controller.index()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params

        // Check responses
        assertEquals "Unexpected response returned!", "comment", redirectController
        assertEquals "Unexpected response returned!", "list", redirectAction
        assertNotNull "Unexpected response returned!", redirectParams
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, redirectParams.albumId
    }

    /**
     * Unit test for the list controller method.
     */
    void testList() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_Reverse() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.order = "asc"

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_Offset() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.offset = 1

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", TEST_LIST.size()-1,
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_Max() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.max = 1

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_MaxOffset() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.offset = 1
        controller.params.max = 1

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_AlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_InvalidAlbumId() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_InvalidPhotoId() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_InvalidAlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_FeedRss() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.feed = CommentController.RSS_FEED

        // Run test
        controller.list()

        // Retrieve responses
        final def response = controller.response.contentAsString
        final def xmlResult = (response) ? XML.parse(response) : ""

        // Check responses
        assertNotNull "Unexpected response returned!", response
        assertNotNull "Unexpected response returned!", xmlResult
        assertEquals "Unexpected response returned!", "application/rss+xml", xmlResult.contentType
        assertEquals "Unexpected response returned!", "UTF-8", xmlResult.encoding
        assertNotNull "Unexpected response returned!", xmlResult.rss
        assertNotNull "Unexpected response returned!", xmlResult.rss.version
        assertEquals "Unexpected response returned!", "2.0", xmlResult.rss.version
        assertNotNull "Unexpected response returned!", xmlResult.rss.channel
        assertNotNull "Unexpected response returned!", xmlResult.rss.channel.item
        assertEquals "Unexpected response returned!", TEST_LIST.get(0)?.message,
            xmlResult.rss.channel.item[0].description
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_FeedXml() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.feed = CommentController.XML_FEED

        // Run test
        controller.list()

        // Retrieve responses
        final def response = controller.response.contentAsString
        final def xmlResult = (response) ? XML.parse(response) : ""

        // Check responses
        assertNotNull "Unexpected response returned!", response
        assertNotNull "Unexpected response returned!", xmlResult
        assertNotNull "Unexpected response returned!", xmlResult.comments
        assertNotNull "Unexpected response returned!", xmlResult.comments.comment
        assertEquals "Unexpected response returned!", TEST_LIST.get(0)?.message,
            xmlResult.comments.comment[0].description
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_FeedJson() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.feed = CommentController.JSON_FEED

        // Run test
        controller.list()

        // Retrieve responses
        final def response = controller.response.contentAsString
        final def jsonResult = (response) ? JSON.parse(response) : ""

        // Check responses
        assertNotNull "Unexpected response returned!", response
        assertNotNull "Unexpected response returned!", jsonResult
        assertNotNull "Unexpected response returned!", jsonResult.comments
        assertNotNull "Unexpected response returned!", jsonResult.comments.comment
        assertEquals "Unexpected response returned!", TEST_LIST.get(0)?.message,
            jsonResult.comments.comment[0].description
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_InvalidFeed() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.feed = "test"

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_MaxOffset() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.offset = TEST_OFFSET
        controller.params.max = TEST_MAX

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_AlbumPhotoIds() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidAlbumId() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidPhotoId() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidAlbumPhotoIds() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListException() {
        // Apply exception mock service
        controller.picasaService = mockPicasaServiceException

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }


    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_Reverse() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.order = "asc"

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_Offset() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.offset = 1

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", TEST_LIST.size()-1,
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_Max() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.max = 1

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_MaxOffset() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.offset = 1
        controller.params.max = 1

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_AlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_InvalidAlbumId() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_InvalidPhotoId() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_InvalidAlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_FeedRss() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.feed = CommentController.RSS_FEED

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def response = controller.response.contentAsString
        final def xmlResult = (response) ? XML.parse(response) : ""

        // Check responses
        assertNotNull "Unexpected response returned!", response
        assertNotNull "Unexpected response returned!", xmlResult
        assertEquals "Unexpected response returned!", "application/rss+xml", xmlResult.contentType
        assertEquals "Unexpected response returned!", "UTF-8", xmlResult.encoding
        assertNotNull "Unexpected response returned!", xmlResult.rss
        assertNotNull "Unexpected response returned!", xmlResult.rss.version
        assertEquals "Unexpected response returned!", "2.0", xmlResult.rss.version
        assertNotNull "Unexpected response returned!", xmlResult.rss.channel
        assertNotNull "Unexpected response returned!", xmlResult.rss.channel.item
        assertEquals "Unexpected response returned!", TEST_LIST.get(0)?.message,
            xmlResult.rss.channel.item[0].description
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_FeedXml() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.feed = CommentController.JSON_FEED

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def response = controller.response.contentAsString
        final def xmlResult = (response) ? XML.parse(response) : ""

        // Check responses
        assertNotNull "Unexpected response returned!", response
        assertNotNull "Unexpected response returned!", xmlResult
        assertNotNull "Unexpected response returned!", xmlResult.comments
        assertNotNull "Unexpected response returned!", xmlResult.comments.comment
        assertEquals "Unexpected response returned!", TEST_LIST.get(0)?.message,
            xmlResult.comments.comment[0].description
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_FeedJson() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.feed = CommentController.JSON_FEED

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def response = controller.response.contentAsString
        final def jsonResult = (response) ? JSON.parse(response) : ""

        // Check responses
        assertNotNull "Unexpected response returned!", response
        assertNotNull "Unexpected response returned!", jsonResult
        assertNotNull "Unexpected response returned!", jsonResult.comments
        assertNotNull "Unexpected response returned!", jsonResult.comments.comment
        assertEquals "Unexpected response returned!", TEST_LIST.get(0)?.message,
            jsonResult.comments.comment[0].description
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_InvalidFeed() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService

        // Test parameters
        controller.params.feed = "test"

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_MaxOffset() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.offset = TEST_OFFSET
        controller.params.max = TEST_MAX

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_AlbumPhotoIds() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_InvalidAlbumId() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_InvalidPhotoId() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_InvalidAlbumPhotoIds() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaServiceEmpty

        // Test parameters
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListException() {
        // Apply exception mock service
        controller.picasaService = mockPicasaServiceException

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "_list", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService
        controller.picasaCommentService = mockPicasaCommentService

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.save()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "comments", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_MESSAGE_1, model?.commentInstance?.message
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.commentInstance?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.commentInstance?.photoId
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_InvalidAlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService
        controller.picasaCommentService = mockPicasaCommentService

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.save()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "comments", viewName
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_MESSAGE_1, model?.commentInstance?.message
        assertEquals "Unexpected response returned!", TEST_INVALID_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected response returned!", TEST_INVALID_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_MaxOffset() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService
        controller.picasaCommentService = mockPicasaCommentService

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID
        controller.params.offset = 1
        controller.params.max = 1

        // Run test
        controller.save()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "comments", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_MESSAGE_1, model?.commentInstance?.message
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.commentInstance?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.commentInstance?.photoId
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_Reverse() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService
        controller.picasaCommentService = mockPicasaCommentService

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID
        controller.params.order = "asc"

        // Run test
        controller.save()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "comments", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_MESSAGE_1, model?.commentInstance?.message
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.commentInstance?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.commentInstance?.photoId
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_postCommentException() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaService
        controller.picasaCommentService = mockPicasaCommentServiceException

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.save()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "comments", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_MESSAGE_1, model?.commentInstance?.message
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.commentInstance?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.commentInstance?.photoId
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", TEST_I18N_MESSAGE, flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_listCommentsException() {
        // Set the controller to use the mock service
        controller.picasaService = mockPicasaServiceException
        controller.picasaCommentService = mockPicasaCommentService

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.save()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected response returned!", "comments", viewName
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_MESSAGE_1, model?.commentInstance?.message
        assertEquals "Unexpected response returned!", TEST_ALBUM_ID, model?.commentInstance?.albumId
        assertEquals "Unexpected response returned!", TEST_PHOTO_ID, model?.commentInstance?.photoId
        assertEquals "Unexpected response returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }
}
