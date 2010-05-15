package uk.co.anthonycampbell.grails.plugins.picasa

/**
 * Date Utility class.
 *
 * Simple utility class to provide date format helper methods to support the
 * W3C RFC 882 and 3339 standards.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class DateUtil {

    // RFC date formats
    public static final String RFC_822 = "EE, d MMM yyyy HH:mm:ss Z"
    public static final String RFC_3339_NO_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss"
    public static final String RFC_3339_TIMEZONE = "Z"

    /**
     * Formats the provided date to provide a valid RFC 3339 timestamp
     * (i.e. 2002-10-02T10:00:00-05:00). Java formatter strips ":" which
     * goes against the RFC 3339 strict standard.
     *
     * @param date the date to format.
     * @return the formatted date.
     */
    public static String formatDateRfc3339(Date date) {
        // Format two parts of RFC standard
        final String formattedDate = date?.format(RFC_3339_NO_TIMEZONE);
        String timeZone = date?.format(RFC_3339_TIMEZONE);

        // Put colon back in
        if (timeZone != null && timeZone.length() > 2) {
            final String timeZoneHead = timeZone.substring(0, timeZone.length() - 2)
            final String timeZoneTail = timeZone.substring(timeZone.length() - 2, timeZone.length())
            timeZone = timeZoneHead << ":" << timeZoneTail
        } else {
            // RFC 3339 requires a 'Z' to be available if no timezone is available.
            timeZone = "Z";
        }

        return formattedDate << timeZone;
    }
}

