package Launcher.IO;

import javax.json.JsonObject;

/**
 * An object that can have a JSON representation
 * @see JsonObject
 */
public interface JsonConvertible {
    abstract JsonObject toJsonObject();
}