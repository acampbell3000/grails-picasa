package uk.co.anthonycampbell.grails.plugins.picasa

/**
 * Picasa plug-in Tag domain class.
 *
 * This class is used to persist any keywords of a Photo
 * item returned through the Google API.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Tag {

    // Declare tag properties
    String tagId = ""
    String keyword = ""

    // Declare tag constraints
    static constraints = {
        tagId(blank:false)
        keyword(blank:false, size:2..250)
    }
}
