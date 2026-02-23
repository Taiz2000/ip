package sagiri.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Utility methods for parsing and validating dates in dd-mm-yy format.
 */
public final class DateTimeUtil {
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}-\\d{2}-\\d{2}");

    private DateTimeUtil() {
    }

    /**
     * Parses a date string in "dd-mm-yy" format to LocalDateTime at midnight.
     *
     * @param dateStr the date string to parse
     * @return LocalDateTime object, or null if parsing fails
     */
    public static LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || !DATE_PATTERN.matcher(dateStr).matches()) {
            return null;
        }
        try {
            String[] parts = dateStr.split("-");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]) + 2000; // yy -> 20yy

            return LocalDate.of(year, month, day).atStartOfDay();
        } catch (NumberFormatException | DateTimeException e) {
            return null;
        }
    }

    /**
     * Validates if a date string is in strict "dd-mm-yy" format and is a real date.
     *
     * @param dateStr the date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateFormat(String dateStr) {
        return parseDate(dateStr) != null;
    }
}