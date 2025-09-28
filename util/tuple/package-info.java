/**
 * This package contains utility classes for working with tuples of various sizes.
 * <p>
 * The main interface is {@link util.tuple.OrderedTuple}, which defines methods for accessing
 * the first and last elements of a tuple.
 * <p>
 * In the detailed implementations, get methods for all elements are provided. They are named
 * getFirst, getSecond, getThird, ... getNth, and there is also an alias getLast for the last element.
 * In each detailed implementation, the toString, equals, and hashCode methods are overridden for better usability.
 * The string representation of tuples are in the format (a, b, c, ...).
 * <p>
 * In the {@link util.tuple.Pair} class, a special method to convert a {@code Map.Entry} to a Pair is provided.
 *
 * @see util.tuple.OrderedTuple
 */
package util.tuple;