package uk.co.anthonycampbell.grails.plugins.picasa

import uk.co.anthonycampbell.grails.plugins.picasa.Photo

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
    public int compare(Photo photo1, Photo photo2) {
        if (!photo1 && !photo2) { return 0 }

        if (!photo1) {
            return -1
        } else if (!photo2) {
            return 1
        } else {
            String name1 = photo1?.name
            String name2 = photo2?.name

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