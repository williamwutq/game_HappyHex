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

import java.math.BigDecimal;

/**
 * Represents a floating-point value in STML, implemented using BigDecimal.
 * This class is immutable and thread-safe.
 */
public class StmlFloat extends Number implements StmlValue<BigDecimal>, Comparable<StmlValue<BigDecimal>>, Cloneable {
    private final BigDecimal value;
    /**
     * Create a new StmlFloat instance with a BigDecimal value.
     * @param value The BigDecimal value.
     */
    public StmlFloat(BigDecimal value) {
        this.value = value;
    }
    /**
     * Create a new StmlFloat instance with a string value.
     * The string must be a valid floating-point representation.
     * @param value The string value.
     * @throws NumberFormatException if the string is not a valid floating-point representation.
     */
    public StmlFloat(String value) {
        this.value = new BigDecimal(value);
    }
    /**
     * Create a new StmlFloat instance with a double value.
     * @param value The double value.
     */
    public StmlFloat(double value) {
        this.value = BigDecimal.valueOf(value);
    }
    /**
     * Create a new StmlFloat instance with a float value.
     * @param value The float value.
     */
    public StmlFloat(float value) {
        this.value = BigDecimal.valueOf(value);
    }
    /**
     * Get the java value of the STML float value
     * @implNote return the BigDecimal value
     * @return the java value of the STML float value
     */
    @Override
    public BigDecimal getValue() {
        return value;
    }
    /**
     * Get the type of the STML value as a {@link ValueType}
     * @implNote return ValueType.FLOAT
     * @return the type of the STML value
     */
    @Override
    public ValueType getType() {
        return ValueType.FLOAT;
    }
    /**
     * {@inheritDoc}
     * @implNote return the value as String
     * @return String representation of the value
     */
    @Override
    public String toString() {
        return value.toString();
    }
    /**
     * {@inheritDoc}
     * @implNote Two StmlFloat objects are considered equal if their values are equal.
     * Additionally, it can be considered equal to a BigDecimal or any Number with the same value.
     * @param obj The object to compare with.
     * @return true if the objects are considered equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return switch (obj) {
            case StmlFloat stmlFloat -> this.value.equals(stmlFloat.value);
            case BigDecimal bigDecimal -> this.value.equals(bigDecimal);
            case Number number -> this.value.compareTo(BigDecimal.valueOf(number.doubleValue())) == 0;
            case null, default -> false;
        };
    }
    /**
     * {@inheritDoc}
     * @implNote return the hash code of the value
     * @return the hash code of the value
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    /**
     * Compare this StmlFloat with another StmlFloat.
     * @param o The other StmlFloat to compare to.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(StmlValue<BigDecimal> o) {
        return this.value.compareTo(o.getValue());
    }
    /**
     * Create a clone of this StmlFloat.
     * @return a new StmlFloat instance with the same value.
     */
    @Override
    public StmlFloat clone() {
        return new StmlFloat(this.value);
    }
    /**
     * {@inheritDoc}
     * @implNote Return the numeric value represented by this object after conversion to type {@code int}
     * @return the numeric value represented by this object after conversion to type {@code int}
     */
    @Override
    public int intValue() {
        return value.intValue();
    }
    /**
     * {@inheritDoc}
     * @implNote Return the numeric value represented by this object after conversion to type {@code long}
     * @return the numeric value represented by this object after conversion to type {@code long}
     */
    @Override
    public long longValue() {
        return value.longValue();
    }
    /**
     * {@inheritDoc}
     * @implNote Return the numeric value represented by this object after conversion to type {@code float}
     * @return the numeric value represented by this object after conversion to type {@code float}
     */
    @Override
    public float floatValue() {
        return value.floatValue();
    }
    /**
     * {@inheritDoc}
     * @implNote Return the numeric value represented by this object after conversion to type {@code double}
     * @return the numeric value represented by this object after conversion to type {@code double}
     */
    @Override
    public double doubleValue() {
        return value.doubleValue();
    }
}
