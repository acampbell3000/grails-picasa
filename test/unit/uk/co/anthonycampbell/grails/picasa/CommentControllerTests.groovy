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

    // Declare test dependencies
    def grailsApplication
    def mockPicasaServiceFactoryEmpty
    def mockPicasaServiceFactoryException

    // Declare mock dependencies
    @Mock PicasaServiceInterface mockPicasaService
    @Mock PicasaServiceInterface mockPicasaServiceException

    // Test properties
    final def TEST_EMPTY_LIST = Collections.emptyList()
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

        // Return empty list
        when(mockPicasaService.listAllComments()).thenReturn(TEST_EMPTY_LIST)
        when(mockPicasaService.listCommentsForPhoto(TEST_ALBUM_ID, TEST_PHOTO_ID)).thenReturn(
            TEST_EMPTY_LIST)

        // Throw exception
        when(mockPicasaServiceException.listAllComments()).thenThrow(TEST_PICASA_SERVICE_EXCEPTION)
        when(mockPicasaServiceException.listCommentsForPhoto(TEST_ALBUM_ID, TEST_PHOTO_ID)).thenThrow(
            TEST_PICASA_SERVICE_EXCEPTION)
    }

    /**
     * Tear down the test suite.
     */
    protected void tearDown() {
        super.tearDown()
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList() {
        // Set the controller to use the empty mock service
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
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", 0, model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_AlbumPhotoIds() {
        // Set the controller to use the empty mock service
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
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", 0, model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidAlbumId() {
        // Set the controller to use the empty mock service
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
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", 0, model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidPhotoId() {
        // Set the controller to use the empty mock service
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
        assertEquals "Unexpected response returned!", "", model?.albumId
        assertEquals "Unexpected response returned!", "", model?.photoId
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", 0, model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_InvalidAlbumPhotoIds() {
        // Set the controller to use the empty mock service
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
        assertEquals "Unexpected response returned!", TEST_EMPTY_LIST, model?.commentInstanceList
        assertEquals "Unexpected response returned!", 0, model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testListEmptyList_OffsetMax() {
        // Set the controller to use the empty mock service
        controller.picasaService = mockPicasaService

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
        assertEquals "Unexpected response returned!", 0, model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", "", flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }

    /**
     * Unit test for the list controller method.
     */
    void testList_PicasaServiceException() {        
        // Apply mock service
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
        assertEquals "Unexpected response returned!", 0, model?.commentInstanceTotal
        assertEquals "Unexpected response returned!", TEST_I18N_MESSAGE, flashMessage
        assertEquals "Unexpected response returned!", "", flashOAuthError
    }
}
