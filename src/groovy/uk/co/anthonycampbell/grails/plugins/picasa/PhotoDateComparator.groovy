package uk.co.anthonycampbell.grails.plugins.picasa

import uk.co.anthonycampbell.grails.plugins.picasa.Photo

/**
 * Photo Date Comparator
 *
 * Comparator which allows an photo to be sorted by the date attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PhotoDateComparator implements Comparator<Photo> {

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
            Date date1 = photo1?.dateCreated
            Date date2 = photo2?.dateCreated

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