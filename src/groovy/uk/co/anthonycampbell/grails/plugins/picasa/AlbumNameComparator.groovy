package uk.co.anthonycampbell.grails.plugins.picasa

import uk.co.anthonycampbell.grails.plugins.picasa.Album

/**
 * Album Name Comparator
 *
 * Comparator which allows an album to be sorted by the name attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class AlbumNameComparator implements Comparator<Album> {

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
            final String name1 = album1?.name
            final String name2 = album2?.name

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