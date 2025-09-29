package util.tuple;

/**
 * A generic mutable container class that holds a single value of type T.
 * This class allows the value to be changed after the object is created.
 * <p>
 * When used in tuples, since tuples are immutable, this class allows for mutable behavior.
 * For example, a tuple can contain a Mutable<Integer> to allow changing the integer value.
 * <p>
 * All methods that access or modify the value are synchronized to ensure thread safety.
 *
 * @param <T> the type of the contained value
 */
public class Mutable<T> {
    public T value;
    /**
     * Constructs a Mutable object with the specified initial value.
     * @param value the initial value to set
     */
    public Mutable(T value) {
        this.value = value;
    }
    /**
     * Returns the contained value.
     * @return the contained value
     */
    synchronized public T get() {
        return value;
    }
    /**
     * Sets the contained value to the specified value.
     * @param value the new value to set
     */
    synchronized public void set(T value) {
        this.value = value;
    }
    /**
     * Returns the string representation of the contained value.
     * If the value is null, returns "null".
     * @return the string representation of the contained value
     */
    @Override
    synchronized public String toString() {
        return value == null ? "null" : value.toString();
    }
    /**
     * Compares this Mutable object to another object for equality.
     * If the other object is also a Mutable, compares their contained values.
     * If the other object is of the same type as the contained value, compares directly.
     * @param obj the object to compare with
     * @return true if both objects are equal, false otherwise
     */
    @Override
    synchronized public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (obj instanceof Mutable<?> other) {
            return (value == null && other.value == null) || (value != null && value.equals(other.value));
        } else {
            // If the other object is of the same type as value, compare directly
            return value != null && value.equals(obj);
        }
    }
    /**
     * Returns the hash code of the contained value, or 0 if the value is null.
     * @return the hash code of the contained value
     */
    @Override
    synchronized public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }
}
