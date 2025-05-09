package special.Thanksgiving;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Calculates the date of {@link #getDate Thanksgiving Day} for a given year in the United States.
 * <p>
 * Thanksgiving is observed on the fourth Thursday of November.
 * <p>
 * This class is static final and not intended to be instantiated.
 */
public final class ThanksgivingDate {
    /**
     * Calculates the date of Thanksgiving Day for a given year in the United States.
     * <p>
     * Thanksgiving is observed on the fourth Thursday of November.
     *
     * @param year the year for which to calculate Thanksgiving
     * @return a {@link LocalDate} representing the date of Thanksgiving for the given year
     */
    public static LocalDate getDate(int year) {
        // Start with November 1st
        LocalDate date = LocalDate.of(year, 11, 1);
        // Find the first Thursday of November
        while (date.getDayOfWeek() != DayOfWeek.THURSDAY) {
            date = date.plusDays(1);
        }
        // Add 3 weeks to get the fourth Thursday
        return date.plusWeeks(3);
    }
}
