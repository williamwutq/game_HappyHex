package util.tuple;

/**
 * A mutable container class that can hold a value of any type, with a specified class type.
 * This class allows the value to be changed after the object is created, while enforcing type constraints.
 * <p>
 * When used in tuples, since tuples are immutable, this class allows for mutable behavior.
 * For example, a tuple can contain a FlexibleTypeMutable to allow changing the value while maintaining type safety.
 * <p>
 * Compare to {@link Mutable}, which is a generic mutable container, this class provides more flexibility by
 * allowing the type to be specified at runtime and changed as needed. However, for stricter type safety,
 * consider using {@code Mutable}.
 * <p>
 * All methods that access or modify the value are synchronized to ensure thread safety.
 */
public class FlexibleTypeMutable implements Cloneable {
    public Object value;
    public Class<?> type;
    /**
     * Constructs a Mutable object with the specified initial value.
     * @param value the initial value to set
     */
    public FlexibleTypeMutable(Object value) {
        this.value = value;
        this.type = value != null ? value.getClass() : Object.class;
    }
    /**
     * Constructs a Mutable object with the specified initial value and type.
     * @param value the initial value to set
     * @param type the class type of the value
     * @throws ClassCastException if the value is not an instance of the specified type
     */
    public FlexibleTypeMutable(Object value, Class<?> type) {
        if (value != null && !type.isInstance(value)) {
            throw new ClassCastException("Value must be of type " + type.getName());
        }
        this.value = value;
        this.type = type;
    }
    /**
     * Constructs a Mutable object with null value and Object type.
     */
    public FlexibleTypeMutable() {
        this.value = null;
        this.type = Object.class;
    }
    /**
     * Constructs a Mutable object by copying the value and type from another Mutable object.
     * @param mutable the Mutable object to copy from
     */
    public <T> FlexibleTypeMutable(Mutable<T> mutable) {
        this.value = mutable.get();
        this.type = value != null ? value.getClass() : Object.class;
    }
    /**
     * Constructs a Mutable object with the specified type and null value.
     * @param type the class type of the value
     */
    public FlexibleTypeMutable(Class<?> type) {
        this.value = null;
        this.type = type;
    }
    /**
     * Returns the contained value.
     * @return the contained value
     */
    synchronized public Object get() {
        return value;
    }
    /**
     * Returns the class type of the contained value.
     * @return the class type of the contained value
     */
    synchronized public Class<?> getType() {
        return type;
    }
    /**
     * Sets the contained value to the specified value.
     * @param value the new value to set
     * @throws ClassCastException if the value is not an instance of the current type
     */
    synchronized public void set(Object value) {
        // Check class type
        if (value != null && !type.isInstance(value)) {
            throw new ClassCastException("Value must be of type " + type.getName());
        }
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
     * If the other object is also a FlexibleTypeMutable, compares their contained values.
     * If the other object is of the same type as the contained value, compares directly.
     * @param obj the object to compare with
     * @return true if both objects are equal, false otherwise
     */
    @Override
    synchronized public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FlexibleTypeMutable other = (FlexibleTypeMutable) obj;
        return (value == null && other.value == null) || (value != null && value.equals(other.value));
    }
    /**
     * Returns the hash code of the contained value, or 0 if the value is null.
     * @return the hash code of the contained value
     */
    @Override
    synchronized public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }
    /**
     * Creates and returns a copy of this object.
     * @return a clone of this instance
     */
    synchronized public FlexibleTypeMutable clone() {
        return new FlexibleTypeMutable(value, type);
    }
    /**
     * Compares the type of this FlexibleTypeMutable with another FlexibleTypeMutable.
     * @param other the other FlexibleTypeMutable to compare with
     * @return true if both have the same type, false otherwise
     */
    synchronized boolean typeEquals(FlexibleTypeMutable other) {
        return this.type == other.type;
    }
    /**
     * Creates and returns a Mutable object with the same contained value.
     * <p>
     * <em>Note: The returned Mutable object will have a generic type of Object.</em>
     * @return a Mutable object containing the same value
     * @see Mutable
     */
    synchronized Mutable<Object> asMutable() {
        return new Mutable<>(value);
    }
    /**
     * Narrows the type of the contained value to the specified new type.
     * If the current value is not an instance of the new type, throws ClassCastException.
     * @param newType the new class type to set
     * @throws ClassCastException if the current value is not an instance of the new type
     * @see #castType(Class)
     */
    synchronized void narrowType(Class<?> newType) {
        if (value != null && !newType.isInstance(value)) {
            throw new ClassCastException("Value must be of type " + newType.getName());
        }
        this.type = newType;
    }
    /**
     * Expands the type of the contained value to the specified new type.
     * The new type must be a supertype of the current type.
     * @param newType the new class type to set
     * @throws ClassCastException if the new type is not a supertype of the current type
     * @see #castType(Class)
     */
    synchronized void expandType(Class<?> newType) {
        if (!newType.isAssignableFrom(this.type)) {
            throw new ClassCastException("New type must be a supertype of the current type");
        }
        this.type = newType;
    }
    /**
     * Casts the contained value to the specified new type without changing the type field.
     * If the current value is not an instance of the new type, throws ClassCastException.
     * @param newType the class type to cast to
     * @throws ClassCastException if the current value is not an instance of the new type
     * @see #narrowType(Class)
     * @see #expandType(Class)
     */
    synchronized void castType(Class<?> newType) {
        // Check class type
        if (value != null && !newType.isInstance(value)) {
            throw new ClassCastException("Value must be of type " + newType.getName());
        }
    }
}
