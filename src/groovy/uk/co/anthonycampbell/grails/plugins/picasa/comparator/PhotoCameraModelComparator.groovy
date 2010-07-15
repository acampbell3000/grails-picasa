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

import uk.co.anthonycampbell.grails.plugins.picasa.Photo

/**
 * Photo Camera Model Comparator
 *
 * Comparator which allows an photo to be sorted by the camera model attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PhotoCameraModelComparator implements Comparator<Photo> {

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
            final String model1 = photo1?.cameraModel
            final String model2 = photo2?.cameraModel

            if (!model1 && !model2) { return 0 }

            if (!model1) {
                return -1
            } else if (!model2) {
                return 1
            } else {
                return model1.compareTo(model2)
            }
        }

        return 0
    }
}