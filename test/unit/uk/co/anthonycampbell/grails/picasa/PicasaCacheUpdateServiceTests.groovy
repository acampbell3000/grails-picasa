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

import uk.co.anthonycampbell.grails.picasa.event.*

import groovy.util.ConfigObject

import grails.test.*

import org.junit.*

/**
 * Set of unit tests for the picasa cache update service.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaCacheUpdateServiceTests extends GrailsUnitTestCase {

    // Declare test properties
    PicasaCacheUpdateService picasaCacheUpdateService

    // Declare test values
    final def TEST_EVENT = new PicasaUpdateEvent()
    
    /**
     * Set up the test suite.
     */
    protected void setUp() {
        super.setUp()

        // Initialise locking
        mockLogging(PicasaCacheUpdateService, true)
        
        // Setup config
        final def mockedConfig = new ConfigObject()
        mockedConfig.picasa.maxComments = 50
        GrailsApplication.metaClass.getConfig = { -> mockedConfig }

        // Initialise service
        picasaCacheUpdateService = PicasaCacheUpdateService.newInstance()
    }

    /**
     * Tear down the test suite.
     */
    protected void tearDown() {
        super.tearDown()
    }

    /**
     * Unit test for the {@link PicasaCacheUpdateService#onApplicationEvent}
     * method.
     */
    void testOnApplicationEvent() {
        picasaCacheUpdateService.onApplicationEvent(TEST_EVENT)
    }
}
