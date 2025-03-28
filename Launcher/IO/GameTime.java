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

    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Date", date);
        builder.add("Time", time);
        builder.add("Zone", zone);
        return builder;
    }
}
