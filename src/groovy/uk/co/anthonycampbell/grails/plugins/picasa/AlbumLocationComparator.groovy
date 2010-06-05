package uk.co.anthonycampbell.grails.plugins.picasa

import uk.co.anthonycampbell.grails.plugins.picasa.Album

/**
 * Album Location Comparator
 *
 * Comparator which allows an album to be sorted by the location attribute.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class AlbumLocationComparator implements Comparator<Album> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Album album1, Album album2) {
        if (!album1 && !album2) { return 0 }

        if (!album1) {
            return -1
        } else if (!album2) {
            return 1
        } else {
            String location1 = album1?.location
            String location2 = album2?.location

            if (!location1 && !location2) { return 0 }

            if (!location1) {
                return -1
            } else if (!location2) {
                return 1
            } else {
                return location1.compareTo(location2)
            }
        }

        return 0
    }
}