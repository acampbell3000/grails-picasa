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

import uk.co.anthonycampbell.grails.plugins.picasa.Tag

/**
 * Tag Keyword Name Comparator
 *
 * Comparator which allows an tag to be sorted by the keyword attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class TagKeywordComparator implements Comparator<Tag> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Tag tag1, final Tag tag2) {
        if (!tag1 && !tag2) { return 0 }

        if (!tag1) {
            return -1
        } else if (!tag2) {
            return 1
        } else {
            final String keyword1 = tag1?.keyword
            final String keyword2 = tag2?.keyword

            if (!keyword1 && !keyword2) { return 0 }

            if (!keyword1) {
                return -1
            } else if (!keyword2) {
                return 1
            } else {
                return keyword1.compareTo(keyword2)
            }
        }

        return 0
    }
}