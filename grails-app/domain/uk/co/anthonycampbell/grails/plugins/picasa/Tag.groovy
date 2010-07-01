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
    String keyword = ""
    int weight = 0
    int displayWeight = 0

    // Declare tag constraints
    static constraints = {
        keyword(blank: false, size: 2..250)
        weight(nullable: false)
        displayWeight(nullable: false)
    }
}
