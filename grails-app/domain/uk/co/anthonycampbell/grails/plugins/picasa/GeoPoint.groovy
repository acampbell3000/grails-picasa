package uk.co.anthonycampbell.grails.plugins.picasa

/**
 * Picasa plug-in Get Point domain class.
 *
 * This class is used to persist the geo properties used
 * throughout the Grails Picasa plug-in.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class GeoPoint {
    // Declare geo point properties
    Double latitude = 0
    Double longitude = 0

    // Declare constraints
    static constraints = {
        latitude(nullable: false)
        longitude(nullable: false)
    }
}
