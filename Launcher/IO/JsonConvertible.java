package Launcher.IO;

import javax.json.*;

/**
 * An object that can have a JSON representation
 * @see JsonObject
 */
public interface JsonConvertible {
    abstract JsonObjectBuilder toJsonObjectBuilder();
    default JsonObject toJsonObject(){
        return toJsonObjectBuilder().build();
    }
}