package uk.co.anthonycampbell.grails.plugins.picasa

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
 * Picasa plug-in Person domain class.
 *
 * This class is used to persist the main properties of a person
 * entry returned through the Google API.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class Person {
    // Declare person properties
    String name = ""
    String email = ""
    String uri = ""

    // Declare constraints
    static constraints = {
        name(blank: false)
        email(blank: false, email: true)
        uri(blank: false)
    }
}