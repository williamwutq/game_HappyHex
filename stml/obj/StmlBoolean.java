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
 * Represents a boolean value in STML.
 * This class is immutable, singleton, and thread-safe.
 *
 * @see StmlValue
 * @see StmlObject
 * @author William Wu
 * @version STML 1.0
 * @since STML 1.0
 */
public class StmlBoolean implements StmlValue<Boolean>, Comparable<StmlValue<Boolean>>, Cloneable {
    public static final StmlBoolean TRUE = new StmlBoolean(true);
    public static final StmlBoolean FALSE = new StmlBoolean(false);
    private final boolean value;
    private StmlBoolean(boolean value) {
        this.value = value;
    }
    /**
     * Get the StmlBoolean instance for the given boolean value.
     * @param value The boolean value.
     * @return The StmlBoolean instance.
     */
    public static StmlBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }
    /**
     * Get the StmlBoolean instance for the given string value.
     * The string must be "true" or "false" (case-insensitive).
     * @param value The string value.
     * @return The StmlBoolean instance.
     * @throws IllegalArgumentException if the string is not "true" or "false".
     */
    public static StmlBoolean of(String value) {
        if (value.equalsIgnoreCase("true")) {
            return TRUE;
        } else if (value.equalsIgnoreCase("false")) {
            return FALSE;
        } else {
            throw new IllegalArgumentException("Invalid boolean string: " + value);
        }
    }
    /**
     * {@inheritDoc}
     * @implNote return either "true" or "false"
     * @return "true" or "false"
     */
    @Override
    public String toString() {
        return value ? "true" : "false";
    }
    /**
     * {@inheritDoc}
     * @implNote return the value as java boolean
     * @return java boolean
     */
    @Override
    public Boolean getValue() {
        return value;
    }
    /**
     * {@inheritDoc}
     * @implNote return ValueType.BOOLEAN
     * @return ValueType.BOOLEAN
     */
    @Override
    public ValueType getType() {
        return ValueType.BOOLEAN;
    }
    /**
     * {@inheritDoc}
     * @implNote Two StmlBoolean are equal if their values are equal
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StmlBoolean that = (StmlBoolean) obj;
        return value == that.value;
    }
    /**
     * {@inheritDoc}
     * @implNote The hash code is based on the boolean value
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }
    /**
     * {@inheritDoc}
     * @implNote Compare based on the boolean value, false < true
     * @param o The object to compare with
     * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
     * @throws NullPointerException if the specified object is null
     * @throws IllegalArgumentException if the specified object's type is not BOOLEAN
     */
    @Override
    public int compareTo(StmlValue<Boolean> o) {
        if (o == null) throw new NullPointerException("Cannot compare to null");
        if (o.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Cannot compare StmlBoolean to " + o.getType());
        return Boolean.compare(this.value, o.getValue());
    }
    /**
     * {@inheritDoc}
     * @implNote Return the same instance for true and false
     * @return A clone of this StmlBoolean
     */
    @Override
    public StmlValue<Boolean> clone() {
        if (this.value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
}
