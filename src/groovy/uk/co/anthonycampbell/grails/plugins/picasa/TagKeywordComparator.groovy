package uk.co.anthonycampbell.grails.plugins.picasa

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
    public int compare(Tag tag1, Tag tag2) {
        if (!tag1 && !tag2) { return 0 }

        if (!tag1) {
            return -1
        } else if (!tag2) {
            return 1
        } else {
            String keyword1 = tag1?.keyword
            String keyword2 = tag2?.keyword

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