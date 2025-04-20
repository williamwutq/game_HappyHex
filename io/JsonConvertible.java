package io;

import javax.json.*;

/**
 * <p> An interface for converting objects into JSON representations.</p>
 * <p> Implementing classes must provide a {@link #toJsonObjectBuilder() method} to convert
 * an object of that class into a {@link javax.json.JsonObjectBuilder}. The interface also
 * provides method to convert into a {@link javax.json.JsonObject}.</p>
 * @see JsonObject
 * @author William Wu
 * @version 1.1
 */
public interface JsonConvertible {
    /**
     * Converts the object into a {@link javax.json.JsonObjectBuilder}.
     * This method should be implemented by classes to define how their data
     * should be serialized into a JSON structure.
     *
     * @return a JsonObjectBuilder containing the object's data
     */
    abstract JsonObjectBuilder toJsonObjectBuilder();
    /**
     * Converts the object into a {@link javax.json.JsonObject} by building the result
     * from the {@link #toJsonObjectBuilder()}.
     *
     * @return a JsonObject representing the object
     */
    default JsonObject toJsonObject(){
        return toJsonObjectBuilder().build();
    }
}