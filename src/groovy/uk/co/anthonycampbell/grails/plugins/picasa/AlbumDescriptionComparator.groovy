package uk.co.anthonycampbell.grails.plugins.picasa

import uk.co.anthonycampbell.grails.plugins.picasa.Album

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