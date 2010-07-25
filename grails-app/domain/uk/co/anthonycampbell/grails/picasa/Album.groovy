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
    static hasMany = [ tags: Tag ]

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
