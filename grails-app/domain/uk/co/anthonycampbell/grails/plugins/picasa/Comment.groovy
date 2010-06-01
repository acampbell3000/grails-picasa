package uk.co.anthonycampbell.grails.plugins.picasa

/**
 * Picasa plug-in Comment domain class.
 *
 * This class is used to persist the main properties of a comment
 * entry returned through the Google API.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Comment {

    // Declare comment properties
    String commentId = ""
    String albumId = ""
    String photoId = ""
    String message = ""
    Date dateCreated = new Date()
    Person author = null

    // Declare constraints
    static constraints = {
        commentId(blank: false)
        albumId(blank: false)
        photoId(blank: false)
        message(blank: false, size: 2..1000)
        author(nullable: false)
    }
}
