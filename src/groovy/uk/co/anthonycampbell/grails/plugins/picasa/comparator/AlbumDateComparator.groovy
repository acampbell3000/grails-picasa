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
 * Album Date Comparator
 *
 * Comparator which allows an album to be sorted by the date attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class AlbumDateComparator implements Comparator<Album> {

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
            final Date date1 = album1?.dateCreated
            final Date date2 = album2?.dateCreated

            if (!date1 && !date2) { return 0 }

            if (!date1) {
                return -1
            } else if (!date2) {
                return 1
            } else {
                return date1.compareTo(date2)
            }
        }

        return 0
    }
}