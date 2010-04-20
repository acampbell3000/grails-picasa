package uk.co.anthonycampbell.grails.plugins.picasa

/**
 * Picasa plug-in Person domain class.
 *
 * This class is used to persist the main properties of a person
 * entry returned through the Google API.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Person {

    // Declare album properties
    String name = ""
    String email = ""
    String uri = ""

    // Declare constraints
    static constraints = {
        name(blank:false)
        email(blank:false, email:true)
        uri(blank:false)
    }
}