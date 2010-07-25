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

import uk.co.anthonycampbell.grails.picasa.Photo

/**
 * Photo Name Comparator
 *
 * Comparator which allows an photo to be sorted by the name attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PhotoNameComparator implements Comparator<Photo> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Photo photo1, final Photo photo2) {
        if (!photo1 && !photo2) { return 0 }

        if (!photo1) {
            return -1
        } else if (!photo2) {
            return 1
        } else {
            final String name1 = photo1?.name
            final String name2 = photo2?.name

            if (!name1 && !name2) { return 0 }

            if (!name1) {
                return -1
            } else if (!name2) {
                return 1
            } else {
                return name1.compareTo(name2)
            }
        }

        return 0
    }
}