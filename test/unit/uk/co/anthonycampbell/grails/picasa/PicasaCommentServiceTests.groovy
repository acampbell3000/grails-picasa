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

import groovy.util.ConfigObject
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.test.*
import org.junit.*

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockServletContext
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes

/**
 * Set of unit tests for the Picasa comment service tests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaCommentServiceTests extends GrailsUnitTestCase {

    // Declare test properties
    PicasaCommentService picasaCommentService

    // Declare test values
    def final TEST_APPLICATION_NAME = "test.application.name"
    def final TEST_APPLICATION_VERSION = "test.123"
    def final TEST_CONSUMER_KEY = "test.key"
    def final TEST_CONSUMER_SECRET = "test.secret"
    def final TEST_ALLOW_COMMENTS = true

    // Declare invalid test values
    def final INVALID_TEST_APPLICATION_NAME = ""
    def final INVALID_TEST_CONSUMER_KEY = ""
    def final INVALID_TEST_CONSUMER_SECRET = ""
    def final INVALID_TEST_ALLOW_COMMENTS = ""

    // Variable to hold test config
    def config
    def metadata

    /**
     * Set up the test suite.
     */
    protected void setUp() {
        super.setUp()

        // Initialise locking
        mockLogging(PicasaCommentService, true)

        // Setup config
        config = new ConfigObject()
        config.picasa.backgroundRetrieveLimit = 2

        // Setup application metadata
        metadata = [:]
        metadata["app.name"] = TEST_APPLICATION_NAME
        metadata["app.version"] = TEST_APPLICATION_VERSION

        // Prepare a test web request to provide a session
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest()
        //servletRequest.getSession().metaClass.oAuthLoggedIn = { -> null }
        //servletRequest.setParameter("oAuthNickname", "")
        //servletRequest.setParameter("oAuthUsername", "")
        //servletRequest.setParameter("oAuthThumbail", "")
        final GrailsWebRequest grailsWebRequest = new GrailsWebRequest(servletRequest,
            new MockHttpServletResponse(), new MockServletContext())
        grailsWebRequest.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, grailsWebRequest, 0)

        // Apply test request to context
        RequestContextHolder.requestAttributes = grailsWebRequest

        // Initialise service
        picasaCommentService = PicasaCommentService.newInstance()
        picasaCommentService.grailsApplication = { -> config }
        picasaCommentService.grailsApplication = { -> metadata }
    }

    /**
     * Tear down the test suite.
     */
    protected void tearDown() {
        super.tearDown()
    }

    /**
     * Unit test for the {@link PicasaCommentService#connect} method.
     */
    void testConnect() {
        // Run test
        final def result = picasaCommentService.connect(TEST_APPLICATION_NAME, TEST_CONSUMER_KEY,
            TEST_CONSUMER_SECRET, TEST_ALLOW_COMMENTS)

        // Check result
        assertTrue "Unexpected result returned!", result
    }

    /**
     * Unit test for the {@link PicasaCommentService#connect} method.
     */
    void testConnect_Invalid() {
        // Run test
        final def result = picasaCommentService.connect(INVALID_TEST_APPLICATION_NAME,
            INVALID_TEST_CONSUMER_KEY, INVALID_TEST_CONSUMER_SECRET, INVALID_TEST_ALLOW_COMMENTS)

        // Check result
        assertFalse "Unexpected result returned!", result
    }

    /**
     * Unit test for the {@link PicasaCommentService#reset} method.
     */
    void testReset() {
        // Run test
        final def result = picasaCommentService.reset()

        // Check result
        assertTrue "Unexpected result returned!", result
    }
}
