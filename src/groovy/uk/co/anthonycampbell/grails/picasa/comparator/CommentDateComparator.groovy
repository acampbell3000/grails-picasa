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

import uk.co.anthonycampbell.grails.picasa.Comment

/**
 * Comment Date Comparator
 *
 * Comparator which allows an comment to be sorted by the date attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class CommentDateComparator implements Comparator<Comment> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Comment comment1, final Comment comment2) {
        if (!comment1 && !comment2) { return 0 }

        if (!comment1) {
            return -1
        } else if (!comment2) {
            return 1
        } else {
            final Date date1 = comment1?.dateCreated
            final Date date2 = comment2?.dateCreated

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