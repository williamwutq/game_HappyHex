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

/**
 * <p> An interface for converting objects into JSON representations.</p>
 * <p> Implementing classes must provide a {@link #toJsonObjectBuilder() method} to convert
 * an object of that class into a {@link javax.json.JsonObjectBuilder}. The interface also
 * provides method to convert into a {@link javax.json.JsonObject}.</p>
 * @see JsonObject
 * @since 1.0
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