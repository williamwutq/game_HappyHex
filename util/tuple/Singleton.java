package util.tuple;

import java.util.List;

/**
 * A simple generic Singleton class to hold one object.
 * @param <T> The type of the object.
 */
public class Singleton <T> implements OrderedTuple<T, T> {
    private final T t;
    /**
     * Constructor to initialize the singleton with one object.
     * @param t The object.
     */
    public Singleton(T t){
        this.t = t;
    }
    /**
     * Get the object of the singleton.
     * @return The object.
     */
    public T getValue(){return t;}
    /**
     * Get the first object of the singleton (alias for getValue).
     * @return The first object.
     */
    @Override
    public T getFirst(){return t;}
    /**
     * Get the last object of the singleton (alias for getValue).
     * @return The last object.
     */
    @Override
    public T getLast(){return t;}
    /**
     * Returns a string representation of the singleton.
     * @return A string in the format (t).
     */
    @Override
    public String toString(){
        return "("+t+")";
    }
    /**
     * Checks if this singleton is equal to another object.
     * @param other The object to compare with.
     * @return True if the other object is a Singleton with an equal element, false otherwise.
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Singleton<?> o)) return false;
        return (t == null && o.t == null) || (t != null && t.equals(o.t));
    }
    /**
     * Returns the hash code of the singleton.
     * @return The hash code of the object, or 0 if the object is null.
     */
    @Override
    public int hashCode(){
        return t == null ? 0 : t.hashCode();
    }
    /**
     * Get the size of the singleton (always 1).
     * @return The size of the singleton.
     */
    @Override
    public int size() {
        return 1;
    }
    /**
     * Convert the singleton to a list containing the single object.
     * @return A list containing the single object.
     */
    @Override
    public List<Object> asList() {
        return List.of(t);
    }
}
