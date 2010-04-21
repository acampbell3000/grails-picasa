package uk.co.anthonycampbell.grails.plugins.picasa

/**
 * Picasa plug-in Photo domain class.
 *
 * This class is used to persist the main properties of a Photo
 * item returned through the Google API.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Photo {

    // Declare photo properties
    String photoId = ""
    String albumId = ""
    String title = ""
    String description = ""
    String cameraModel = ""
    GeoPoint geoLocation
    String thumbnailImage = ""
    String thumbnailWidth = ""
    String thumbnailHeight = ""
    String image = ""
    String width = ""
    String height = ""
    String previousPhotoId = ""
    String nextPhotoId = ""
    List tags
    List comments
    Date dateCreated = new Date()
    boolean isPublic = false

    // Declare relationships
    static hasMany = [ tags : Tag, comments : Comment ]

    // Declare photo constraints
    static constraints = {
        photoId(blank:false)
        albumId(blank:false)
        title(nullable:true)
        description(nullable:true)
        cameraModel(nullable:true)
        geoLocation(nullable:true)
        thumbnailImage(url: true, blank:false)
        thumbnailWidth(blank:false)
        thumbnailHeight(blank:false)
        image(url: true, blank:false)
        width(blank:false)
        height(blank:false)
        previousPhotoId(nullable:true)
        nextPhotoId(nullable:true)
    }
}
