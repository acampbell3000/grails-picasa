package uk.co.anthonycampbell.grails.picasa.comparator

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

import uk.co.anthonycampbell.grails.picasa.Album

/**
 * Album Description Comparator
 *
 * Comparator which allows an album to be sorted by the description attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class AlbumDescriptionComparator implements Comparator<Album> {

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
            final String description1 = album1?.description
            final String description2 = album2?.description

            if (!description1 && !description2) { return 0 }

            if (!description1) {
                return -1
            } else if (!description2) {
                return 1
            } else {
                return description1.compareTo(description2)
            }
        }

        return 0
    }
}