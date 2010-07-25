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
