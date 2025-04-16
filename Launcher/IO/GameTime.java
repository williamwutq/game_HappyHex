package Launcher.IO;

import javax.json.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public final class GameTime implements JsonConvertible{
    private final String date;
    private final String time;
    private final String zone;

    public GameTime(){
        this.date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.zone = ZoneId.systemDefault().toString();
    }
    public GameTime(String date, String time, String zone){
        this.date = date;
        this.time = time;
        this.zone = zone;
    }

    public String toString(){return date + " " + time + " " + zone;}
    public String getDate(){return date;}
    public String getTime(){return time;}
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

    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Date", date);
        builder.add("Time", time);
        builder.add("Zone", zone);
        return builder;
    }
}
