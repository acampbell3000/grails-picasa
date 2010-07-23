package uk.co.anthonycampbell.grails.plugins.picasa

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
    @Mock PicasaService mockPicasaService
    
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
        //grailsApplication?.config?.picasa.maxComments

        // Apply properties
        //controller.grailsApplication = grailsApplication
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
    void testList() {
        controller.list()
        assertEquals "bar", controller.response.contentAsString
    }
}
