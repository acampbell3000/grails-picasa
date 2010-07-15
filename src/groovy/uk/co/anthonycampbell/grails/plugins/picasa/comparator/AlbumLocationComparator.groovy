package uk.co.anthonycampbell.grails.plugins.picasa.comparator

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

import uk.co.anthonycampbell.grails.plugins.picasa.Album

/**
 * Album Location Comparator
 *
 * Comparator which allows an album to be sorted by the location attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class AlbumLocationComparator implements Comparator<Album> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Album album1, final Album album2) {
        if (!album1 && !album2) { return 0 }

        if (!album1) {
            return -1
        } else if (!album2) {
            return 1
        } else {
            final String location1 = album1?.location
            final String location2 = album2?.location

            if (!location1 && !location2) { return 0 }

            if (!location1) {
                return -1
            } else if (!location2) {
                return 1
            } else {
                return location1.compareTo(location2)
            }
        }

        return 0
    }
}