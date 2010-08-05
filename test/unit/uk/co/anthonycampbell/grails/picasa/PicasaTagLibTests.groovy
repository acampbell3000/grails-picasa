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

import grails.test.*

/**
 * Set of unit tests for the Picasa tag library.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaTagLibTests extends TagLibUnitTestCase {

    // Declare test suite properties
    def picasaTagLib

    // Declare test properties
    public static final def TEST_LONGITUDE = "-0.126236"
    public static final def TEST_LATITUDE = "51.500152"
    public static final def TEST_DESCRIPTION = "Test description"
    public static final def TEST_ZOOM = "10"
    public static final def TEST_WIDTH = "250"
    public static final def TEST_HEIGHT = "250"

    // Declare invalid test properties
    public static final def INVALID_TEST_LONGITUDE = "abc.def"
    public static final def INVALID_TEST_LATITUDE = "def.abc"
    public static final def INVALID_TEST_DESCRIPTION = ""
    public static final def INVALID_TEST_ZOOM = "abc"
    public static final def INVALID_TEST_WIDTH = "def"
    public static final def INVALID_TEST_HEIGHT = "ghi"
    
    /**
     * Setup of the test suite.
     */
    @Override
    protected void setUp() {
        super.setUp()

        // Initialise tag library
        picasaTagLib = new PicasaTagLib()
    }

    /**
     * Tear down the test suite.
     */
    @Override
    protected void tearDown() {
        super.tearDown()
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap() {
        // Run test
        final def response = picasaTagLib?.map([longitude: TEST_LONGITUDE,
            latitude: TEST_LATITUDE, description: TEST_DESCRIPTION,
            zoom: TEST_ZOOM, width: TEST_WIDTH, height: TEST_HEIGHT])?.toString()

        // Check result
        assertTrue "Unexpected response returned!", response?.contains(TEST_LONGITUDE)
        assertTrue "Unexpected response returned!", response?.contains(TEST_LATITUDE)
        assertTrue "Unexpected response returned!", response?.contains(TEST_DESCRIPTION)
        assertTrue "Unexpected response returned!", response?.contains(TEST_ZOOM)
        assertTrue "Unexpected response returned!", response?.contains(TEST_WIDTH)
        assertTrue "Unexpected response returned!", response?.contains(TEST_HEIGHT)
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_OnlyLongitudeLatitude() {
        // Run test
        final def response = picasaTagLib?.map([longitude: TEST_LONGITUDE,
            latitude: TEST_LATITUDE])?.toString()

        // Check result
        assertTrue "Unexpected response returned!", response?.contains(TEST_LONGITUDE)
        assertTrue "Unexpected response returned!", response?.contains(TEST_LATITUDE)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_WIDTH_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_HEIGHT_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_ZOOM_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_DESCRIPTION_DEFAULT)
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_OnlyLongitude() {
        // Run test
        final def response = picasaTagLib?.map([longitude: TEST_LONGITUDE])?.toString()

        // Check result
        assertEquals "Unexpected response returned!", "", response
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_OnlyLatitude() {
        // Run test
        final def response = picasaTagLib?.map([latitude: TEST_LATITUDE])?.toString()

        // Check result
        assertEquals "Unexpected response returned!", "", response
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_InvalidLongitudeLatitude() {
        // Run test
        final def response = picasaTagLib?.map([longitude: INVALID_TEST_LONGITUDE,
            latitude: INVALID_TEST_LATITUDE, description: TEST_DESCRIPTION,
            zoom: TEST_ZOOM, width: TEST_WIDTH, height: TEST_HEIGHT])?.toString()

        // Check result
        assertEquals "Unexpected response returned!", "", response
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_NoLongitudeLatitude() {
        // Run test
        final def response = picasaTagLib?.map([description: TEST_DESCRIPTION,
            zoom: TEST_ZOOM, width: TEST_WIDTH, height: TEST_HEIGHT])?.toString()

        // Check result
        assertEquals "Unexpected response returned!", "", response
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_EmptyLongitudeLatitude() {
        // Run test
        final def response = picasaTagLib?.map([longitude: "", latitude: "",
            description: TEST_DESCRIPTION, zoom: TEST_ZOOM, width: TEST_WIDTH,
            height: TEST_HEIGHT])?.toString()

        // Check result
        assertEquals "Unexpected response returned!", "", response
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_InvalidDescriptionHeightWidthZoom() {
        // Run test
        final def response = picasaTagLib?.map([longitude: TEST_LONGITUDE,
            latitude: TEST_LATITUDE, description: INVALID_TEST_DESCRIPTION,
            zoom: INVALID_TEST_ZOOM, width: INVALID_TEST_WIDTH,
            height: INVALID_TEST_HEIGHT])?.toString()

        // Check result
        assertTrue "Unexpected response returned!", response?.contains(TEST_LONGITUDE)
        assertTrue "Unexpected response returned!", response?.contains(TEST_LATITUDE)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_WIDTH_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_HEIGHT_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_ZOOM_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_DESCRIPTION_DEFAULT)
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_EmptyDescriptionHeightWidthZoom() {
        // Run test
        final def response = picasaTagLib?.map([longitude: TEST_LONGITUDE,
            latitude: TEST_LATITUDE, description: "", zoom: "", width: "",
            height: ""])?.toString()

        // Check result
        assertTrue "Unexpected response returned!", response?.contains(TEST_LONGITUDE)
        assertTrue "Unexpected response returned!", response?.contains(TEST_LATITUDE)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_WIDTH_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_HEIGHT_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_ZOOM_DEFAULT)
        assertTrue "Unexpected response returned!", response?.contains(PicasaTagLib.GOOGLE_MAP_DESCRIPTION_DEFAULT)
    }

    /**
     * Unit test for the tag library map method.
     */
    void testMap_NoArguments() {
        // Run test
        final def response = picasaTagLib?.map([:])?.toString()

        // Check result
        assertEquals "Unexpected response returned!", "", response
    }
}
