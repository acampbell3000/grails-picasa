package uk.co.anthonycampbell.grails.plugins.picasa

import java.io.File;
import java.net.URL;

import com.google.gdata.client.*;
import com.google.gdata.client.photos.*;
import com.google.gdata.data.*;
import com.google.gdata.data.media.*;
import com.google.gdata.data.photos.*;

import grails.test.*

/**
 * Set of unit tests for the Picasa service tests.
 */
class PicasaServiceTests extends GrailsUnitTestCase {

    // Declare test properties
    PicasaService picasaService
    def mockPicasaWebService

    /**
     * Set up test properties and values for the unit tests.
     */
    protected void setUp() {
        super.setUp()

        //  Declare mock objects
        mockPicasaWebService = mockFor(PicasawebService.class)

        // Initialise service
        //picasaService = PicasaService.newInstance()

        //picasaService.picasawebService = mockPicasaWebService.createMock();
    }

    /**
     * Clear up test properties and values.
     */
    protected void tearDown() {
        super.tearDown()
    }

    /**
     *
     */
    void testSomething() {
        print "Testing something..."
        def c = "hi"
        def d = "hi"
    }
}
