package uk.co.anthonycampbell.grails.plugins.picasa

/**
 * Picasa plug-in Album domain class.
 *
 * This class is used to persist the main properties of an album
 * item returned through the Google API.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Album {

    // Declare album properties
    String albumId = ""
    String name = ""
    String description = ""
    String location = ""
    GeoPoint geoLocation
    String image = ""
    String width = ""
    String height = ""
    int photoCount = 0
    List tags
    Date dateCreated = new Date()
    boolean isPublic = false

    // Declare relationships
    static hasMany = [ tags : Tag ]

    // Declare constraints
    static constraints = {
        albumId(blank: false)
        name(blank: false, size: 2..250)
        description(nullable: true)
        location(nullable: true)
        geoLocation(nullable: true)
        image(blank: false, url: true)
        width(blank: false)
        height(blank: false)
    }
}
