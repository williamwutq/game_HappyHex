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
 * Represents a string value in STML.
 * This class is immutable and thread-safe.
 *
 * @see StmlValue
 * @see StmlObject
 * @author William Wu
 * @version STML 1.0
 * @since STML 1.0
 */
public class StmlString implements StmlValue<String>, Comparable<StmlValue<String>>, Cloneable {
    private final String value;
    /**
     * Create a new StmlString instance with a String value.
     * @param value The String value.
     */
    public StmlString(String value) {
        this.value = value;
    }
    /**
     * Get the java value of the STML string value
     * @implNote return the String value
     * @return the java value of the STML string value
     */
    @Override
    public String getValue() {
        return value;
    }
    /**
     * Get the type of the STML value as a {@link ValueType}
     * @implNote return ValueType.STRING
     * @return the type of the STML value
     */
    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }
    /**
     * {@inheritDoc}
     * @implNote return the value as String
     * @return String representation of the value
     */
    @Override
    public String toString() {
        return value;
    }
    /**
     * {@inheritDoc}
     * @implNote compare based on the string value
     * @param o The other StmlValue to compare to.
     * @return comparison result based on string values
     */
    @Override
    public int compareTo(StmlValue<String> o) {
        return this.value.compareTo(o.getValue());
    }
    /**
     * {@inheritDoc}
     * @implNote return a new StmlString instance with the same value
     * @return a clone of this StmlString
     */
    @Override
    public StmlString clone() {
        return new StmlString(this.value);
    }
    /**
     * {@inheritDoc}
     * @implNote equality based on the string value
     * @param obj The object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return switch (obj) {
            case StmlString stmlStr -> this.value.equals(stmlStr.value);
            case String str -> this.value.equals(str);
            default -> false;
        };
    };
    /**
     * {@inheritDoc}
     * @return the hash code of the value
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
