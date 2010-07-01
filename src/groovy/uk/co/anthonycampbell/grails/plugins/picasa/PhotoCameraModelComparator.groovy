package uk.co.anthonycampbell.grails.plugins.picasa

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