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

package stml;

/**
 * An enum representing the properties of a field in STML.
 * <p>
 * Fields can be one of four types:
 * <ul>
 * <li>DEFAULT: A normal variable field that can be changed and have new fields added</li>
 * <li>FINAL: A field that cannot be changed, but new fields can be added</li>
 * <li>CLOSED: A field that can be changed, but no new fields can be added</li>
 * <li>IMMUTABLE: A field that cannot be changed, and no new fields can be added</li>
 * </ul>
 * <p>
 * These properties are represented by two boolean values:
 * <ul>
 * <li>isFinal: whether the field is final (as opposed to variable)</li>
 * <li>isClosed: whether the field is closed (as opposed to open)</li>
 * </ul>
 * <p>
 * Note that the property "ordered" is not represented here, as it only applies to list fields.
 * Note that all other custom annotation properties are not represented here, as they do not affect the behavior of the parser.
 * Note that type properties are not represented here, as they are checked during the lazy parsing stage.
 */
public enum StmlFieldProperty {
	/**
	 * A normal variable field that can be changed and have new fields added
	 */
	DEFAULT(false, false),
	/**
	 * A field that cannot be changed, but new fields can be added
	 */
	FINAL(true, false),
	/**
	 * A field that can be changed, but no new fields can be added
	 */
	CLOSED(false, true),
	/**
	 * A field that cannot be changed, and no new fields can be added
	 */
	IMMUTABLE(true, true);

	private final boolean isFinal; // As opposed to variable
	private final boolean isClosed; // As opposed to open
	// Ordered is not mentioned because it only applies to lists

	/**
	 * Construct a StmlFieldProperty with the given properties
	 * @param isFinal whether the field is final
	 * @param isClosed whether the field is closed
	 */
	StmlFieldProperty(boolean isFinal, boolean isClosed) {
		this.isFinal = isFinal;
		this.isClosed = isClosed;
	}

	/**
	 * Check if the field is final
	 * <p>
	 * Final fields cannot be changed.
	 * @return true if the field is final, false otherwise
	 */
	public boolean isFinal() {
		return isFinal;
	}
	/**
	 * Check if the field is closed
	 * <p>
	 * Closed fields cannot have new fields added to them.
	 * @return true if the field is closed, false otherwise
	 */
	public boolean isClosed() {
		return isClosed;
	}
	/**
	 * Check if the field is open
	 * <p>
	 * Open fields can have new fields added to them.
	 * @return true if the field is open, false otherwise
	 */
	public boolean isOpen() {
		return !isClosed;
	}
	/**
	 * Check if the field is a variable
	 * <p>
	 * Variable fields can be changed.
	 * @return true if the field is a variable, false otherwise
	 */
	public boolean isVariable() {
		return !isFinal;
	}
	/**
	 * Check if the field permits any write operations
	 * <p>
	 * A field permits write operations if it is open and variable.
	 * @return true if the field permits any write operations, false otherwise
	 */
	public boolean permitsAnyWrite() {
		return !isFinal && !isClosed;
	}
	/**
	 * Return a finalized version of this {@code StmlFieldProperty}
	 * <p>
	 * If the property is already final, it stays final. Otherwise, the corresponding
	 * field that is final is returned.
	 * <p>
	 * Do not confuse this method with {@link #isFinal()}, which returns <em>whether</em>
	 * the property is final.
	 */
	public StmlFieldProperty finalized() {
	    if (this == DEFAULT || this == FINAL) {
		return FINAL;
	    } else return IMMUTABLE;
	}
	/**
         * Return a closed version of this {@code StmlFieldProperty}
         * <p>
         * If the property is already closed, it stays closed. Otherwise, the corresponding
         * field that is final is returned.
         * <p>
         * Do not confuse this method with {@link #isClosed()}, which returns <em>whether</em>
         * the property is closed.
         */
	public StmlFieldProperty closed() {
	    if (this == DEFAULT || this == CLOSED) {
		return CLOSED;
	    } else return IMMUTABLE;
	}
}
