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
 * Represents a null value in STML.
 * This class is immutable, singleton, and thread-safe.
 *
 * @see StmlValue
 * @see StmlObject
 * @author William Wu
 * @version STML 1.0
 * @since STML 1.0
 */
public class StmlNull implements StmlValue<Void>, Cloneable {
    public static final StmlNull INSTANCE = new StmlNull();
    private StmlNull() {}
    /**
     * {@inheritDoc}
     * @implNote return "null"
     * @return "null"
     */
    @Override
    public String toString() {
        return "null";
    }
    /**
     * {@inheritDoc}
     * @implNote return null
     * @return null
     */
    @Override
    public Void getValue() {
        return null;
    }
    /**
     * {@inheritDoc}
     * @implNote return ValueType.NULL
     * @return ValueType.NULL
     */
    @Override
    public ValueType getType() {
        return ValueType.NULL;
    }
    /**
     * {@inheritDoc}
     * @implNote return the hash code of the class
     * @return the hash code of the class
     */
    @Override
    public int hashCode() {
        return 0; // All instances are the same, and the hash code for null is 0
    }
    /**
     * {@inheritDoc}
     * @implNote All instances of StmlNull are equal
     * @param obj The object to compare with
     * @return true if the object is an instance of StmlNull, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof StmlNull;
    }
    /**
     * {@inheritDoc}
     * @implNote return the singleton instance
     * @return the singleton instance
     */
    @Override
    protected Object clone(){
        return INSTANCE;
    }
}
