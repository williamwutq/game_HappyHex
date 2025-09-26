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

package stml.obj;

/**
 * Represents a value in STML.
 * This interface is implemented by all value types in STML, such as strings, integers, floats, booleans, lists, annotations, and nested objects.
 * <p>
 * This interface extends {@link StmlObject}, which represents any object in STML.
 * <p>
 * Implementations of this interface should be immutable and thread-safe.
 *
 * @param <T> The Java type of the value (e.g., String for string values, Integer for integer values, etc.). This is used for retrieval.
 * @see StmlObject
 * @author William Wu
 * @version STML 1.0
 * @since STML 1.0
 */
public interface StmlValue<T> extends StmlObject {
    /**
     * Enumeration of possible STML value types.
     */
    enum ValueType {
        /** String value */
        STRING("string"),
        /** Integer value */
        INTEGER("int"),
        /** Floating-point value */
        FLOAT("float"),
        /** Boolean value */
        BOOLEAN("bool"),
        /** List of values */
        LIST("list"),
        /** Annotation of object */
        ANNOTATION("annotation"),
        /** Nested configuration object */
        OBJECT(""); // Object has no suffix, not annotated
        private final String typeAnnotationString;
        ValueType(String tas) {
            this.typeAnnotationString = tas;
        }
        /**
         * Get the type annotation string for this value type.
         * <p>
         * This is the string used in STML to annotate the type of value.
         * For example, for a string value, the annotation is "string".
         * @return the type annotation string
         */
        public String getTypeAnnotationString() {
            return typeAnnotationString;
        }
    }

    /**
     * Retrieve the java value of the STML value
     * @return the java value of the STML value
     */
    T getValue();

    /**
     * Retrieve the type of the STML value as a {@link ValueType}
     * @return the type of the STML value
     */
    ValueType getType();
}
