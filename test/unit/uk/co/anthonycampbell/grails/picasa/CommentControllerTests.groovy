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

/**
 * Set of unit tests for the comment controller.
 * 
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class CommentControllerTests extends ControllerUnitTestCase {

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

        // Initialise logging and comment instance
        mockLogging(CommentController, true)
        mockForConstraintsTests(Comment, [])
        
        // Setup config
        final def mockedConfig = new ConfigObject()
        mockedConfig.picasa.maxComments = 50
        GrailsApplication.metaClass.getConfig = { -> mockedConfig }

        // Mock i18n
        controller.metaClass.message = { def map -> TEST_I18N_MESSAGE }
        controller.messageSource = [getMessage: { def map -> TEST_I18N_MESSAGE }]
        
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
        controller.picasaService = populatedService()

        // Run test
        controller.index()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "comment", redirectController
        assertEquals "Unexpected re-direct action returned!", "list", redirectAction
    }

    /**
     * Unit test for the index controller method.
     */
    void testIndex_Params() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

        // Apply param
        controller.params.albumId = TEST_ALBUM_ID

        // Run test
        controller.index()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "comment", redirectController
        assertEquals "Unexpected re-direct action returned!", "list", redirectAction
        assertNotNull "Re-direct parameters were not available!", redirectParams
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, redirectParams.albumId
    }

    /**
     * Unit test for the list controller method.
     */
    void testList() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_Reverse() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

        // Test parameters
        controller.params.order = CommentController.REVERSE

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_Offset() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected list size response returned!", TEST_LIST.size()-1,
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_Max() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_MaxOffset() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_AlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_InvalidAlbumId() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_InvalidPhotoId() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_InvalidAlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_FeedRss() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected content type returned!", "application/rss+xml", xmlResult.contentType
        assertEquals "Unexpected encoding returned!", "UTF-8", xmlResult.encoding
        assertNotNull "RSS element does not exist!", xmlResult.rss
        assertNotNull "RSS version does not exist!", xmlResult.rss.version
        assertEquals "Unexpected RSS version returned!", "2.0", xmlResult.rss.version
        assertNotNull "RSS channel does not exist!", xmlResult.rss.channel
        assertNotNull "RSS channel item does not exist!", xmlResult.rss.channel.item
        assertEquals "Unexpected RSS channel item description returned!", TEST_LIST.get(0)?.message,
            xmlResult.rss.channel.item[0].description
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_FeedXml() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertNotNull "Unexpected response returned!", xmlResult.comment
        assertEquals "Unexpected list size response returned!", TEST_LIST.size(),
            xmlResult.comment.size()
        assertEquals "Unexpected album ID response returned!", TEST_LIST.get(0)?.albumId,
            xmlResult.comment[0].albumId.text()
        assertEquals "Unexpected photo ID response returned!", TEST_LIST.get(0)?.photoId,
            xmlResult.comment[0].photoId.text()
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_FeedJson() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertNotNull "Unexpected response returned!", jsonResult.comment
        assertEquals "Unexpected list size response returned!", TEST_LIST.size(),
            jsonResult.comment.size()
        assertEquals "Unexpected album ID response returned!", TEST_LIST.get(0)?.albumId,
            jsonResult.comment[0].albumId.text()
        assertEquals "Unexpected photo ID response returned!", TEST_LIST.get(0)?.photoId,
            jsonResult.comment[0].photoId.text()
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_InvalidFeed() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_MaxOffset() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_AlbumPhotoIds() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidAlbumId() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidPhotoId() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidAlbumPhotoIds() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListException() {
        // Apply exception mock service
        controller.picasaService = exceptionService()

        // Run test
        controller.list()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }


    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_Reverse() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

        // Test parameters
        controller.params.order = CommentController.REVERSE

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_Offset() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected list size response returned!", TEST_LIST.size()-1,
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_Max() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_MaxOffset() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_AlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_InvalidAlbumId() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_InvalidPhotoId() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_InvalidAlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_FeedRss() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected content type returned!", "application/rss+xml", xmlResult.contentType
        assertEquals "Unexpected encoding returned!", "UTF-8", xmlResult.encoding
        assertNotNull "RSS element does not exist!", xmlResult.rss
        assertNotNull "RSS version does not exist!", xmlResult.rss.version
        assertEquals "Unexpected RSS version returned!", "2.0", xmlResult.rss.version
        assertNotNull "RSS channel does not exist!", xmlResult.rss.channel
        assertNotNull "RSS channel item does not exist!", xmlResult.rss.channel.item
        assertEquals "Unexpected RSS channel item description returned!", TEST_LIST.get(0)?.message,
            xmlResult.rss.channel.item[0].description
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_FeedXml() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

        // Test parameters
        controller.params.feed = CommentController.XML_FEED

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def response = controller.response.contentAsString
        final def xmlResult = (response) ? XML.parse(response) : ""

        // Check responses
        assertNotNull "Unexpected response returned!", response
        assertNotNull "Unexpected response returned!", xmlResult
        assertNotNull "Unexpected response returned!", xmlResult.comment
        assertEquals "Unexpected list size response returned!", TEST_LIST.size(),
            xmlResult.comment.size()
        assertEquals "Unexpected album ID response returned!", TEST_LIST.get(0)?.albumId,
            xmlResult.comment[0].albumId.text()
        assertEquals "Unexpected photo ID response returned!", TEST_LIST.get(0)?.photoId,
            xmlResult.comment[0].photoId.text()
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_FeedJson() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertNotNull "Unexpected response returned!", jsonResult.comment
        assertEquals "Unexpected list size response returned!", TEST_LIST.size(),
            jsonResult.comment.size()
        assertEquals "Unexpected album ID response returned!", TEST_LIST.get(0)?.albumId,
            jsonResult.comment[0].albumId.text()
        assertEquals "Unexpected photo ID response returned!", TEST_LIST.get(0)?.photoId,
            jsonResult.comment[0].photoId.text()
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxList_InvalidFeed() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(), model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_MaxOffset() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_AlbumPhotoIds() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_InvalidAlbumId() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_InvalidPhotoId() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListEmptyList_InvalidAlbumPhotoIds() {
        // Set the controller to use the empty mock service
        controller.picasaService = emptyService()

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
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax list controller method.
     */
    void testAjaxListException() {
        // Apply exception mock service
        controller.picasaService = exceptionService()

        // Run test
        controller.ajaxList()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_list", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected flash message returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = commentService()

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
        assertEquals "Unexpected view name returned!", "comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_InvalidAlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = commentService()

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
        assertEquals "Unexpected view name returned!", "comments", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_INVALID_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_INVALID_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_MaxOffset() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = commentService()

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
        assertEquals "Unexpected view name returned!", "comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size returned!", 1, model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_Reverse() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID
        controller.params.order = CommentController.REVERSE

        // Run test
        controller.save()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_postCommentException() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = exceptionCommentService()

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
        assertEquals "Unexpected view name returned!", "comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", TEST_I18N_MESSAGE, flashOAuthError
    }

    /**
     * Unit test for the save controller method.
     */
    void testSave_listCommentsException() {
        // Set the controller to use the mock service
        controller.picasaService = exceptionService()
        controller.picasaCommentService = commentService()

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
        assertEquals "Unexpected view name returned!", "comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax save controller method.
     */
    void testAjaxSave() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.ajaxSave()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax save controller method.
     */
    void testAjaxSave_InvalidAlbumPhotoIds() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_INVALID_ALBUM_ID
        controller.params.photoId = TEST_INVALID_PHOTO_ID

        // Run test
        controller.ajaxSave()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_comments", viewName
        assertEquals "Unexpected album ID returned!", "", model?.albumId
        assertEquals "Unexpected photo ID returned!", "", model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_INVALID_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_INVALID_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax save controller method.
     */
    void testAjaxSave_MaxOffset() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID
        controller.params.offset = 1
        controller.params.max = 1

        // Run test
        controller.ajaxSave()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size returned!", 1,
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax save controller method.
     */
    void testAjaxSave_Reverse() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID
        controller.params.order = CommentController.REVERSE

        // Run test
        controller.ajaxSave()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected message returned!", TEST_LIST.get(TEST_LIST.size()-1)?.message,
            model?.commentInstanceList?.get(0)?.message
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax save controller method.
     */
    void testAjaxSave_postCommentException() {
        // Set the controller to use the mock service
        controller.picasaService = populatedService()
        controller.picasaCommentService = exceptionCommentService()

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.ajaxSave()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size response returned!", TEST_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected list total returned!", TEST_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", TEST_I18N_MESSAGE, flashOAuthError
    }

    /**
     * Unit test for the ajax save controller method.
     */
    void testAjaxSave_listCommentsException() {
        // Set the controller to use the mock service
        controller.picasaService = exceptionService()
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.ajaxSave()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_comments", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected display list returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceList?.size()
        assertEquals "Unexpected display list size returned!", TEST_EMPTY_LIST.size(),
            model?.commentInstanceTotal
        assertEquals "Unexpected comment message returned!", TEST_MESSAGE_1,
            model?.commentInstance?.message
        assertEquals "Unexpected comment album ID returned!", TEST_ALBUM_ID,
            model?.commentInstance?.albumId
        assertEquals "Unexpected comment photo ID returned!", TEST_PHOTO_ID,
            model?.commentInstance?.photoId
        assertEquals "Unexpected flash message returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the login controller method.
     */
    void testLogin() {
        // Set the controller to use the mock service
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.session.oauthToken = [key: "", secret: ""]
        controller.params.id = TEST_ALBUM_ID + CommentController.ID_SEPARATOR + TEST_PHOTO_ID

        // Run test
        controller.login()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "photo", redirectController
        assertEquals "Unexpected re-direct action returned!", "show", redirectAction
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, redirectParams?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, redirectParams?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the login controller method.
     */
    void testLogin_NoIds() {
        // Set the controller to use the mock service
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.session.oauthToken = [key: "", secret: ""]
        controller.params.id = ""

        // Run test
        controller.login()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "album", redirectController
        assertEquals "Unexpected re-direct action returned!", "index", redirectAction
        assertEquals "Unexpected album ID returned!", "", redirectParams?.albumId
        assertEquals "Unexpected photo ID returned!", "", redirectParams?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
    }

    /**
     * Unit test for the login controller method.
     */
    void testLogin_OnlySeparator() {
        // Set the controller to use the mock service
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.session.oauthToken = [key: "", secret: ""]
        controller.params.id = CommentController.ID_SEPARATOR

        // Run test
        controller.login()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "album", redirectController
        assertEquals "Unexpected re-direct action returned!", "index", redirectAction
        assertEquals "Unexpected album ID returned!", "", redirectParams?.albumId
        assertEquals "Unexpected photo ID returned!", "", redirectParams?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the login controller method.
     */
    void testLogin_ApplyException() {
        // Set the controller to use the mock service
        controller.picasaCommentService = exceptionCommentService()

        // Test parameters
        controller.session.oauthToken = [key: "", secret: ""]
        controller.params.id = TEST_ALBUM_ID + CommentController.ID_SEPARATOR + TEST_PHOTO_ID

        // Run test
        controller.login()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "photo", redirectController
        assertEquals "Unexpected re-direct action returned!", "show", redirectAction
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, redirectParams?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, redirectParams?.photoId
        assertEquals "Unexpected flash message returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the logout controller method.
     */
    void testLogout() {
        // Set the controller to use the mock service
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.id = TEST_ALBUM_ID + CommentController.ID_SEPARATOR + TEST_PHOTO_ID

        // Run test
        controller.logout()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "photo", redirectController
        assertEquals "Unexpected re-direct action returned!", "show", redirectAction
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, redirectParams?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, redirectParams?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the logout controller method.
     */
    void testLogout_NoIds() {
        // Set the controller to use the mock service
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.id = ""

        // Run test
        controller.logout()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "album", redirectController
        assertEquals "Unexpected re-direct action returned!", "index", redirectAction
        assertEquals "Unexpected album ID returned!", "", redirectParams?.albumId
        assertEquals "Unexpected photo ID returned!", "", redirectParams?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the logout controller method.
     */
    void testLogout_RemoveException() {
        // Set the controller to use the mock service
        controller.picasaCommentService = exceptionCommentService()

        // Test parameters
        controller.params.id = TEST_ALBUM_ID + CommentController.ID_SEPARATOR + TEST_PHOTO_ID

        // Run test
        controller.logout()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "photo", redirectController
        assertEquals "Unexpected re-direct action returned!", "show", redirectAction
        assertEquals "Unexpected 1response returned!", TEST_ALBUM_ID, redirectParams?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, redirectParams?.photoId
        assertEquals "Unexpected flash message returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax logout controller method.
     */
    void testAjaxLogout() {
        // Set the controller to use the mock service
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.id = TEST_ALBUM_ID + CommentController.ID_SEPARATOR + TEST_PHOTO_ID

        // Run test
        controller.ajaxLogout()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_create", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax logout controller method.
     */
    void testAjaxLogout_NoIds() {
        // Set the controller to use the mock service
        controller.picasaCommentService = commentService()

        // Test parameters
        controller.params.id = ""

        // Run test
        controller.ajaxLogout()

        // Retrieve responses
        final def redirectController = controller.redirectArgs.controller
        final def redirectAction = controller.redirectArgs.action
        final def redirectParams = controller.redirectArgs.params
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected re-direct controller returned!", "album", redirectController
        assertEquals "Unexpected re-direct action returned!", "index", redirectAction
        assertEquals "Unexpected response returned!", "", redirectParams?.albumId
        assertEquals "Unexpected photo ID returned!", "", redirectParams?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", "", flashOAuthError
    }

    /**
     * Unit test for the ajax logout controller method.
     */
    void testAjaxLogout_RemoveException() {
        // Set the controller to use the mock service
        controller.picasaCommentService = exceptionCommentService()

        // Test parameters
        controller.params.id = TEST_ALBUM_ID + CommentController.ID_SEPARATOR + TEST_PHOTO_ID

        // Run test
        controller.ajaxLogout()

        // Retrieve responses
        final def model = controller.modelAndView.model?.linkedHashMap
        final def viewName = controller.modelAndView.viewName
        final def flashMessage = controller.flash.message
        final def flashOAuthError = controller.flash.oauthError

        // Check responses
        assertEquals "Unexpected view name returned!", "_create", viewName
        assertEquals "Unexpected album ID returned!", TEST_ALBUM_ID, model?.albumId
        assertEquals "Unexpected photo ID returned!", TEST_PHOTO_ID, model?.photoId
        assertEquals "Unexpected flash message returned!", "", flashMessage
        assertEquals "Unexpected flash OAuth message returned!", TEST_I18N_MESSAGE, flashOAuthError
    }

    /**
     * Unit test for the validate ajax controller method.
     */
    void testValidate() {        
        // Test parameters
        controller.params.message = TEST_MESSAGE_1
        controller.params.albumId = TEST_ALBUM_ID
        controller.params.photoId = TEST_PHOTO_ID

        // Run test
        controller.validate()

        // Retrieve response
        final def response = controller.response.contentAsString

        // Check response
        assertEquals "Unexpected response returned!", "", response
    }

    /**
     * Unit test for the validate ajax controller method.
     */
    void testValidate_InvalidComment() {
        // Test parameters
        controller.params.message = TEST_MESSAGE_1

        // Run test
        controller.validate()

        // Retrieve response
        final def response = controller.response.contentAsString

        // Check response
        assertEquals "Unexpected response returned!", TEST_I18N_MESSAGE, response
    }

    /**
     * Unit test for the ajax logout controller method.
     */
    void testCheckUser() {
        // Logged in
        controller.session?.oAuthLoggedIn = true

        // Run test
        def loggedIn = controller.checkUser()

        // Check response
        assertEquals "Unexpected response returned!", true, loggedIn
    }

    /**
     * Unit test for the ajax logout controller method.
     */
    void testCheckUser_LoggedOut() {
        // Logged in
        controller.session?.oAuthLoggedIn = false

        // Run test
        def loggedIn = controller.checkUser()

        // Check response
        assertEquals "Unexpected response returned!", false, loggedIn
    }

    /**
     * Unit test for the ajax logout controller method.
     */
    void testCheckUser_Null() {
        // Run test
        def loggedIn = controller.checkUser()

        // Check response
        assertEquals "Unexpected response returned!", false, loggedIn
    }

    /**
     * Apply populated test lists to the PicasaService metaClass methods.
     *
     * @return GrailsMock with the metaClass for the service updated to
     *      provide populated lists.
     */
    private populatedService() {
        // Return populated list
        final def serviceFactory = mockFor(PicasaService.class, true)
        serviceFactory.demand.listAllComments(0..1) { -> TEST_LIST }
        serviceFactory.demand.listCommentsForPhoto(0..1) { def albumId, def photoId ->
            return (!albumId || !photoId) ? TEST_EMPTY_LIST : TEST_LIST
        }

        // Initialise mock
        serviceFactory.createMock()
    }

    /**
     * Apply empty test lists to the PicasaService metaClass methods.
     *
     * @return GrailsMock with the metaClass for the service updated to
     *      provide empty lists.
     */
    private emptyService() {
        // Return empty list
        final def serviceEmptyFactory = mockFor(PicasaService, true)
        serviceEmptyFactory.demand.listAllComments(0..1) { -> TEST_EMPTY_LIST }
        serviceEmptyFactory.demand.listCommentsForPhoto(0..1) { def albumId, def photoId ->
            TEST_EMPTY_LIST
        }

        // Initialise mock
        serviceEmptyFactory.createMock()
    }

    /**
     * Apply exception test lists to the PicasaService metaClass methods.
     *
     * @return GrailsMock with the metaClass for the service updated to
     *      provide exception lists.
     */
    private exceptionService() {
        // Throw exception
        final def serviceExceptionFactory = mockFor(PicasaService, true)
        serviceExceptionFactory.demand.listAllComments(0..1) { ->
            throw TEST_PICASA_SERVICE_EXCEPTION
        }
        serviceExceptionFactory.demand.listCommentsForPhoto(0..1) { def albumId, def photoId ->
            throw TEST_PICASA_SERVICE_EXCEPTION
        }

        // Initialise mock
        serviceExceptionFactory.createMock()
    }

    /**
     * Apply test behaviour to the PicasaCommentService metaClass.
     *
     * @return GrailsMock with the metaClass for the service updated to
     *      provide standard behaviour.
     */
    private commentService() {
        // Save message
        final def commentServiceFactory = mockFor(PicasaCommentService, true)
        commentServiceFactory.demand.postComment(0..1) { def comment -> }
        commentServiceFactory.demand.applyOAuthAccess(0..1) { def key, def secret -> }
        commentServiceFactory.demand.removeOAuthAccess(0..1) { -> }
        
        // Initialise mock
        commentServiceFactory.createMock()
    }

    /**
     * Apply exception behaviour to the PicasaCommentService metaClass.
     *
     * @return GrailsMock with the metaClass for the service updated to
     *      provide exception lists.
     */
    private exceptionCommentService() {
        // Throw exception
        final def commentExceptionServiceFactory = mockFor(PicasaCommentService, true)
        commentExceptionServiceFactory.demand.postComment(0..1) { def comment ->
            throw TEST_PICASA_COMMENT_SERVICE_EXCEPTION
        }
        commentExceptionServiceFactory.demand.applyOAuthAccess(0..1) { def key, def secret ->
            throw TEST_PICASA_COMMENT_SERVICE_EXCEPTION
        }
        commentExceptionServiceFactory.demand.removeOAuthAccess(0..1) { ->
            throw TEST_PICASA_COMMENT_SERVICE_EXCEPTION
        }

        // Initialise mock
        commentExceptionServiceFactory.createMock()
    }
}
