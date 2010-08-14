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
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.test.*
import org.junit.*

import com.google.gdata.data.photos.*

/**
 * Set of unit tests for the picasa cache update service.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaCacheUpdateServiceTests extends GrailsUnitTestCase {

    // Declare test properties
    PicasaCacheUpdateService picasaCacheUpdateService

    // Declare test properties
    def TEST_EVENT
    def TEST_SOURCE
    def TEST_PHOTO_ENTRY_A
    def TEST_PHOTO_ENTRY_B
    def TEST_PHOTO_ENTRY_C
    def TEST_PHOTO_ENTRY_D
    def TEST_PHOTO_ENTRY_E
    def TEST_PHOTO_ENTRIES

    // Event test values
    final def TEST_ALBUM_ID_A = "123456"
    final def TEST_ALBUM_ID_B = "234561"
    final def TEST_ALBUM_ID_C = "345612"
    final def TEST_ALBUM_ID_D = "456123"
    final def TEST_ALBUM_ID_E = "561234"
    final def TEST_PHOTO_ID_A = "654321"
    final def TEST_PHOTO_ID_B = "543216"
    final def TEST_PHOTO_ID_C = "432165"
    final def TEST_PHOTO_ID_D = "321654"
    final def TEST_PHOTO_ID_E = "216543"
    final def TEST_SHOW_ALL = true
    
    /**
     * Set up the test suite.
     */
    protected void setUp() {
        super.setUp()

        // Initialise locking
        mockLogging(PicasaCacheUpdateService, true)
        mockLogging(PicasaService, true)
        
        // Setup config
        final def mockedConfig = new ConfigObject()
        mockedConfig.picasa.backgroundRetrieveLimit = 2
        GrailsApplication.metaClass.getConfig = { -> mockedConfig }

        // Prepare photo entries
        TEST_PHOTO_ENTRY_A = new PhotoEntry()
        TEST_PHOTO_ENTRY_A.setAlbumId(TEST_ALBUM_ID_A)
        TEST_PHOTO_ENTRY_A.setId(TEST_PHOTO_ID_A)
        TEST_PHOTO_ENTRY_B = new PhotoEntry()
        TEST_PHOTO_ENTRY_B.setAlbumId(TEST_ALBUM_ID_B)
        TEST_PHOTO_ENTRY_B.setId(TEST_PHOTO_ID_B)
        TEST_PHOTO_ENTRY_C = new PhotoEntry()
        TEST_PHOTO_ENTRY_C.setAlbumId(TEST_ALBUM_ID_C)
        TEST_PHOTO_ENTRY_C.setId(TEST_PHOTO_ID_C)
        TEST_PHOTO_ENTRY_D = new PhotoEntry()
        TEST_PHOTO_ENTRY_D.setAlbumId(TEST_ALBUM_ID_D)
        TEST_PHOTO_ENTRY_D.setId(TEST_PHOTO_ID_D)
        TEST_PHOTO_ENTRY_E = new PhotoEntry()
        TEST_PHOTO_ENTRY_E.setAlbumId(TEST_ALBUM_ID_E)
        TEST_PHOTO_ENTRY_E.setId(TEST_PHOTO_ID_E)

        // Insert photo entries into list
        TEST_PHOTO_ENTRIES = new ArrayList<PhotoEntry>()
        TEST_PHOTO_ENTRIES.add(TEST_PHOTO_ENTRY_A)
        TEST_PHOTO_ENTRIES.add(TEST_PHOTO_ENTRY_B)
        TEST_PHOTO_ENTRIES.add(TEST_PHOTO_ENTRY_C)
        TEST_PHOTO_ENTRIES.add(TEST_PHOTO_ENTRY_D)
        TEST_PHOTO_ENTRIES.add(TEST_PHOTO_ENTRY_E)

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
        // Select source
        TEST_SOURCE = picasaService(4)

        // Initialise test events
        TEST_EVENT = new PicasaUpdateEvent(TEST_SOURCE, TEST_ALBUM_ID_A, TEST_PHOTO_ID_A, TEST_SHOW_ALL,
            TEST_PHOTO_ENTRIES)

        // Run test
        picasaCacheUpdateService.onApplicationEvent(TEST_EVENT)
    }

    /**
     * Unit test for the {@link PicasaCacheUpdateService#onApplicationEvent}
     * method.
     */
    void testOnApplicationEvent_MiddlePhoto() {
        // Select source
        TEST_SOURCE = picasaService(4)

        // Initialise test events
        TEST_EVENT = new PicasaUpdateEvent(TEST_SOURCE, TEST_ALBUM_ID_C, TEST_PHOTO_ID_C, TEST_SHOW_ALL,
            TEST_PHOTO_ENTRIES)

        // Run test
        picasaCacheUpdateService.onApplicationEvent(TEST_EVENT)
    }

    /**
     * Unit test for the {@link PicasaCacheUpdateService#onApplicationEvent}
     * method.
     */
    void testOnApplicationEvent_LastPhoto() {
        // Select source
        TEST_SOURCE = picasaService(4)

        // Initialise test events
        TEST_EVENT = new PicasaUpdateEvent(TEST_SOURCE, TEST_ALBUM_ID_E, TEST_PHOTO_ID_E, TEST_SHOW_ALL,
            TEST_PHOTO_ENTRIES)

        // Run test
        picasaCacheUpdateService.onApplicationEvent(TEST_EVENT)
    }

    /**
     * Unit test for the {@link PicasaCacheUpdateService#onApplicationEvent}
     * method.
     */
    void testOnApplicationEvent_EmptyList() {
        // Select source
        TEST_SOURCE = picasaService(0)

        // Initialise test events
        TEST_EVENT = new PicasaUpdateEvent(TEST_SOURCE, TEST_ALBUM_ID_A, TEST_PHOTO_ID_A, TEST_SHOW_ALL,
            Collections.emptyList())

        // Run test
        picasaCacheUpdateService.onApplicationEvent(TEST_EVENT)
    }

    /**
     * Unit test for the {@link PicasaCacheUpdateService#onApplicationEvent}
     * method.
     */
    void testOnApplicationEvent_SingleList() {
        // Select source
        TEST_SOURCE = picasaService(0)

        // Initialise test events
        TEST_EVENT = new PicasaUpdateEvent(TEST_SOURCE, TEST_ALBUM_ID_A, TEST_PHOTO_ID_A, TEST_SHOW_ALL,
            Collections.singletonList(TEST_PHOTO_ENTRY_A))

        // Run test
        picasaCacheUpdateService.onApplicationEvent(TEST_EVENT)
    }

    /**
     * Unit test for the {@link PicasaCacheUpdateService#onApplicationEvent}
     * method.
     */
    void testOnApplicationEvent_NullPhotoEntries() {
        // Select source
        TEST_SOURCE = picasaService(0)

        // Initialise test events
        TEST_EVENT = new PicasaUpdateEvent(TEST_SOURCE, TEST_ALBUM_ID_A, TEST_PHOTO_ID_A, TEST_SHOW_ALL,
            null)

        // Run test
        picasaCacheUpdateService.onApplicationEvent(TEST_EVENT)
    }

    /**
     * Unit test for the {@link PicasaCacheUpdateService#onApplicationEvent}
     * method.
     */
    void testOnApplicationEvent_SmallQueues() {
        // Select source
        TEST_SOURCE = picasaService(4)

        // Initialise test events
        TEST_EVENT = new PicasaUpdateEvent(TEST_SOURCE, TEST_ALBUM_ID_D, TEST_PHOTO_ID_D, TEST_SHOW_ALL,
            TEST_PHOTO_ENTRIES)

        // Run test
        picasaCacheUpdateService.afterPropertiesSet()
        picasaCacheUpdateService.onApplicationEvent(TEST_EVENT)
    }

    /**
     * Mock a standard picasa service.
     *
     * @param count max number of calls expected to getPhoto.
     * @return mocked PicasaService.
     */
    private def picasaService(final def count = 1) {
        // Save message
        final def serviceFactory = mockFor(PicasaService, true)
        serviceFactory.demand.getPhoto(0..count) { def albumId, def photoId, def showAll,
                def updatePhotoStream -> }

        // Initialise mock
        serviceFactory.createMock()
    }
}
