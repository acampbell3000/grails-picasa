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

import groovy.xml.MarkupBuilder

import org.apache.commons.lang.StringUtils

/**
 * Picasa Tag Library.
 *
 * Set of tags specifically for the Grails Picasa plug-in.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaTagLib {
    // Own namespace
    static namespace = 'picasa'

    // Static default values
    public static final def GOOGLE_STATIC_MAP_API = "http://maps.google.com/maps/api/staticmap"
    public static final def GOOGLE_MAP_WIDTH_DEFAULT = "250"
    public static final def GOOGLE_MAP_HEIGHT_DEFAULT = "250"
    public static final def GOOGLE_MAP_ZOOM_DEFAULT = "14"
    public static final def GOOGLE_MAP_DESCRIPTION_DEFAULT = ""

	/**
	 * Display a Google map tile for the provided geo locations. This tag will
     * display nothing is invalid longitude and latitude parameters are provided.
     *
     * @param latitude Map latitude geo point.
     * @param longitude Map longitude geo point.
     * @param description Location description.
     * @param width Google map tile width.
     * @param height Google map tile height.
	 */
	def map = { attrs ->
        // Collect supported parameters
		def longitude = "${attrs.remove("longitude")}"
		def latitude = "${attrs.remove("latitude")}"
		def description = "${attrs.remove("description")}"
		def zoom = "${attrs.remove("zoom")}"
		def width = "${attrs.remove("width")}"
		def height = "${attrs.remove("height")}"

        // Validate attributes
        longitude = (longitude && longitude.matches("^[\\-\\d\\.]+\$")) ? longitude : ""
        latitude = (latitude && latitude.matches("^[\\-\\d\\.]+\$")) ? latitude : ""
        description = description ?: GOOGLE_MAP_DESCRIPTION_DEFAULT
        zoom = (zoom && StringUtils.isNumeric(zoom)) ? zoom : GOOGLE_MAP_ZOOM_DEFAULT
        width = (width && StringUtils.isNumeric(width)) ? width : GOOGLE_MAP_WIDTH_DEFAULT
        height = (height && StringUtils.isNumeric(height)) ? height : GOOGLE_MAP_HEIGHT_DEFAULT

        // Do we have a longitude and latitude we can work with?
        if (StringUtils.isNotEmpty(longitude) && StringUtils.isNotEmpty(latitude)) {
            // Initialise builder
            final StringWriter writer = new StringWriter()
            final def builder = new MarkupBuilder(writer)

            // Output required JavaScript
            builder.script(type: "text/javascript") {
                builder.yield("\nfunction initialise() {\n", false)
                builder.yield("\tvar myLatlng = new google.maps.LatLng('$latitude', '$longitude');\n\n", false)

                builder.yield("\tvar myOptions = {\n", false)
                builder.yield("\t\tzoom: $zoom,\n", false)
                builder.yield("\t\tcenter: myLatlng,\n", false)
                builder.yield("\t\tmapTypeId: google.maps.MapTypeId.ROADMAP\n", false)
                builder.yield("\t}\n\n", false)

                builder.yield("\tvar map = new google.maps.Map(document.getElementById('mapTiles'), myOptions);\n\n", false)

                builder.yield("\tvar marker = new google.maps.Marker({\n", false)
                builder.yield("\t\tposition: myLatlng,\n", false)
                builder.yield("\t\tmap: map,\n", false)
                builder.yield("\t\ttitle: '$description'\n", false)
                builder.yield("\t});\n", false)
                
                builder.yield("}\n\n", false)

                builder.yield("function loadScript() {\n", false)
                builder.yield("\tvar script = document.createElement('script');\n", false)
                builder.yield("\tscript.type = 'text/javascript';\n", false)
                builder.yield("\tscript.src = 'http://maps.google.com/maps/api/js?callback=initialise&sensor=false';\n", false)
                builder.yield("\tdocument.body.appendChild(script);\n", false)
                builder.yield("}\n", false)
            }

            // Output Google Map tiles
            builder.div(id: "mapBackground") {
                div(id: "mapTiles") {
                    p {
                        img(src: "$GOOGLE_STATIC_MAP_API?center=${latitude},${longitude}&zoom=${zoom}&size=${width}x${height}&markers=color:red|${latitude},${longitude}&sensor=false",
                            width: "$width", height: "$height", alt: "$description", onclick: "initialise()")
                    }
                }
            }

            // Flush buffer
            writer.flush()

            // Output writer
            out << writer.toString()
            
        } else {
            // Return nothing
            out << ""
        }
    }
}
