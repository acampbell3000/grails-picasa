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

    public static final def GOOGLE_STATIC_MAP_API = "http://maps.google.com/maps/api/staticmap"

	/**
	 * Display a Google map tile for the provided geo locations.
     *
     * @param latitude Map latitude geo point.
     * @param longitude Map longitude geo point.
     * @param description Location description.
     * @param zoom Google map tile zoom.
	 */
	def map = { attrs ->
        // Collect parameters
		def latitude = attrs.remove("latitude")
		def longitude = attrs.remove("longitude")
		def description = attrs.remove("description")
		def zoom = attrs.remove("zoom")
		def width = attrs.remove("width")
		def height = attrs.remove("height")

        // Validate attributes
        

        // Initialise builder
        final StringWriter writer = new StringWriter()
        final def builder = new MarkupBuilder(writer)

        // Output required JavaScript
		builder.script(type: "text/javascript") {
			builder.yield("function initialise() {\n", false)
			builder.yield("\tvar myLatlng = new google.maps.LatLng(\"$latitude\",\n", false)
        }

        builder.div(id: "mapBackground") {
            div(id: "mapTiles") {
                p {
                    img(src: "$GOOGLE_STATIC_MAP_API?center=${latitude},${longitude}&amp;zoom=${zoom}&amp;size=${width}x${height}&amp;markers=color:red|${latitude},${longitude}&amp;sensor=false"
                        width: "$width", height: "$height", alt: "$description", onclick: "initialise()")
                }
            }
        }

        // Flush buffer
		writer.flush()

        // Output writer
        out << writer.toString()
    }
/*
<script type="text/javascript">
    function initialise() {
        var myLatlng = new google.maps.LatLng("${photoInstance?.geoLocation?.latitude}",
            "${photoInstance?.geoLocation?.longitude}");
        var myOptions = {
            zoom: 14,
            center: myLatlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        }
        var map = new google.maps.Map(document.getElementById("mapTiles"), myOptions);

        var marker = new google.maps.Marker({
            position: myLatlng,
            map: map,
            title: "${fieldValue(bean: photoInstance, field: "description")}"
        });
    }

    function loadScript() {
        var script = document.createElement("script");
        script.type = "text/javascript";
        script.src = "http://maps.google.com/maps/api/js?callback=initialise&sensor=false";
        document.body.appendChild(script);
    }

    window.onload = loadScript;
</script>

<div id="mapBackground">
    <div id="mapTiles">
                "<p><img src="http://maps.google.com/maps/api/staticmap?center=${photoInstance?.geoLocation?.latitude},${photoInstance?.geoLocation?.longitude}&amp;zoom=14&amp;size=250x250&amp;markers=color:red|${photoInstance?.geoLocation?.latitude},${photoInstance?.geoLocation?.longitude}&amp;sensor=false"
             width="250" height="250" alt="${fieldValue(bean: photoInstance, field: "description")}" onclick="initialise()" /></p>"
    </div>
</div>
*/
}
