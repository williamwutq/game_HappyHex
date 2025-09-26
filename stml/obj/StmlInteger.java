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

import java.math.BigInteger;

/**
 * Represents an integer value in STML, implemented using BigInteger.
 * This class is immutable and thread-safe.
 *
 * @see StmlValue
 * @see StmlObject
 * @author William Wu
 * @version STML 1.0
 * @since STML 1.0
 */
public class StmlInteger extends Number implements StmlValue<BigInteger>, Comparable<StmlValue<BigInteger>>, Cloneable {
    public static final StmlInteger ZERO = new StmlInteger(0);
    private final BigInteger value;
    /**
     * Create a new StmlInteger instance with a BigInteger value.
     * @param value The BigInteger value.
     */
    public StmlInteger(BigInteger value) {
        this.value = value;
    }
    /**
     * Create a new StmlInteger instance with a string value.
     * The string must be a valid integer representation.
     * @param value The string value.
     * @throws NumberFormatException if the string is not a valid integer representation.
     */
    public StmlInteger(String value) {
        this.value = new BigInteger(value);
    }
    /**
     * Create a new StmlInteger instance with a long value.
     * @param value The long value.
     */
    public StmlInteger(long value) {
        this.value = BigInteger.valueOf(value);
    }
    /**
     * Create a new StmlInteger instance with an int value.
     * @param value The int value.
     */
    public StmlInteger(int value) {
        this.value = BigInteger.valueOf(value);
    }
    /**
     * Create a new StmlInteger instance with another StmlInteger instance.
     * @param o The BigInteger value.
     */
    @Override
    public int compareTo(StmlValue<BigInteger> o) {
        return this.value.compareTo(o.getValue());
    }
    /**
     * {@inheritDoc}
     * @return the numeric value represented by this object after conversion to type {@code int}
     */
    @Override
    public int intValue() {
        return value.intValue();
    }
    /**
     * {@inheritDoc}
     * @return the numeric value represented by this object after conversion to type {@code long}
     */
    @Override
    public long longValue() {
        return value.longValue();
    }
    /**
     * {@inheritDoc}
     * @return the numeric value represented by this object after conversion to type {@code float}
     */
    @Override
    public float floatValue() {
        return value.floatValue();
    }
    /**
     * {@inheritDoc}
     * @return the numeric value represented by this object after conversion to type {@code double}
     */
    @Override
    public double doubleValue() {
        return value.doubleValue();
    }
    /**
     * Get the value of this StmlInteger.
     * @return the value of this StmlInteger as {@code BigInteger}
     */
    @Override
    public BigInteger getValue() {
        return value;
    }
    /**
     * {@inheritDoc}
     * @implNote return the value as string
     * @return the value as string
     */
    @Override
    public ValueType getType() {
        return ValueType.INTEGER;
    }
    /**
     * {@inheritDoc}
     * @implNote return the value as string
     * @return the value as string
     */
    @Override
    public String toString() {
        return value.toString();
    }
    /**
     * {@inheritDoc}
     * @implNote Two Number are equal if their values are equal
     * @param obj The object to compare with
     * @return true if the two Number are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Number other)) return false;
        if (obj instanceof StmlInteger stmlInt) {
            return this.value.equals(stmlInt.value);
        }
        if (obj instanceof BigInteger stmlFloat) {
            return this.value.equals(stmlFloat);
        }
        return this.value.equals(BigInteger.valueOf(other.longValue()));
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
     * {@inheritDoc}
     * @implNote return a clone of this StmlInteger
     * @return a clone of this StmlInteger
     */
    @Override
    public StmlInteger clone() {
        return this;
    }
}
