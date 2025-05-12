/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

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
