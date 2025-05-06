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

package io;

import javax.json.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Represents a snapshot of the current game time, including the date, time, and time zone.
 * <p>
 * This class is immutable and thread-safe, with fields initialized either to the system's
 * current time or via explicit values passed to the constructor.
 * <p>
 * Implements {@link JsonConvertible}, enabling the conversion of the object into
 * a {@link JsonObject} or {@link JsonObjectBuilder} for use in JSON serialization.
 *
 * @see JsonConvertible
 * @see LocalDate
 * @see LocalTime
 * @see ZoneId
 * @see DateTimeFormatter
 * @since 1.0
 * @author William Wu
 * @version 1.1
 */
public final class GameTime implements JsonConvertible{
    /** The date in ISO-8601 format (yyyy-MM-dd). */
    private final String date;
    /** The time in 24-hour format (HH:mm:ss). */
    private final String time;
    /** The system's default time zone ID (e.g., "America/New_York"). */
    private final String zone;

    /**
     * Constructs a new {@code GameTime} object using the current system date, time, and time zone.
     * <ul>
     *     <li>The date is formatted using {@link DateTimeFormatter#ISO_LOCAL_DATE}.</li>
     *     <li>The time is formatted using {@link DateTimeFormatter#ofPattern(String)} with "HH:mm:ss".</li>
     *     <li>The time zone is retrieved using {@link ZoneId#systemDefault()}.</li>
     * </ul>
     */
    public GameTime(){
        this.date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.zone = ZoneId.systemDefault().toString();
    }
    /**
     * Constructs a new {@code GameTime} object with the specified date, time, and zone.
     *
     * @param date the date string (expected format: yyyy-MM-dd)
     * @param time the time string (expected format: HH:mm:ss)
     * @param zone the time zone ID (e.g., "UTC", "America/Los_Angeles")
     */
    public GameTime(String date, String time, String zone){
        this.date = date;
        this.time = time;
        this.zone = zone;
    }

    /**
     * Returns a string representation of the {@code GameTime}, including the date, time, and zone.
     * @return a formatted string separated by space in the form {@code "date time zone"}
     */
    public String toString(){return date + " " + time + " " + zone;}
    /**
     * The date portion of this {@code GameTime}.
     * @return the date as a string (format: yyyy-MM-dd)
     */
    public String getDate(){return date;}
    /**
     * The time portion of this {@code GameTime}.
     * @return the time as a string (format: HH:mm:ss)
     */
    public String getTime(){return time;}
    /**
     * The time zone ID of this {@code GameTime}.
     * @return the time zone as a string (e.g., "America/Los Angeles")
     */
    public String getZone(){return zone;}

     /**
     * Generates a string representing the current date and time in a simple format.
     * <p>
     * The format returned is: {@code yyyy-MM-dd HH:mm:ss.SSS}, where:
     * <ul>
     *   <li>{@code yyyy-MM-dd} is the current date in ISO-8601 format</li>
     *   <li>{@code HH:mm:ss.SSS} is the current time in 24-hour format with milliseconds</li>
     * </ul>
     *
     * @return a string representing the current date and time in the format {@code yyyy-MM-dd HH:mm:ss.SSS}
     */
    public static String generateSimpleTime(){
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + " " +
               LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    /**
     * Converts this {@code GameTime} object into a {@link JsonObjectBuilder}, with keys:
     * <ul>
     *     <li>{@code "Date"} - the ISO-8601 date</li>
     *     <li>{@code "Time"} - the formatted time</li>
     *     <li>{@code "Zone"} - the time zone ID</li>
     * </ul>
     * @return a JSON object builder representing this game time
     */
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Date", date);
        builder.add("Time", time);
        builder.add("Zone", zone);
        return builder;
    }
}
