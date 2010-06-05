package uk.co.anthonycampbell.grails.plugins.picasa

import uk.co.anthonycampbell.grails.plugins.picasa.Photo

/**
 * Photo Description Comparator
 *
 * Comparator which allows an photo to be sorted by the description attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PhotoDescriptionComparator implements Comparator<Photo> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Photo photo1, Photo photo2) {
        if (!photo1 && !photo2) { return 0 }

        if (!photo1) {
            return -1
        } else if (!photo2) {
            return 1
        } else {
            String description1 = photo1?.description
            String description2 = photo2?.description

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