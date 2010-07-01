package uk.co.anthonycampbell.grails.plugins.picasa

import uk.co.anthonycampbell.grails.plugins.picasa.Comment

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