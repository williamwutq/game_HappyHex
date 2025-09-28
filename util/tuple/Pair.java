package util.tuple;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple generic Pair class to hold two related objects.
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 */
public class Pair <A, B> implements OrderedTuple<A, B> {
    private final A a;
    private final B b;
    /**
     * Constructor to initialize the pair with two objects.
     * @param a The first object.
     * @param b The second object.
     */
    public Pair(A a, B b){
        this.a = a;
        this.b = b;
    }
    /**
     * Get the first object of the pair.
     * @return The first object.
     */
    @Override
    public A getFirst(){return a;}
    /**
     * Get the second object of the pair.
     * @return The second object.
     */
    public B getSecond(){return b;}
    /**
     * Get the second object of the pair (alias for getSecond).
     * @return The second object.
     */
    @Override
    public B getLast(){return b;}
    /**
     * Returns a string representation of the pair.
     * @return A string in the format (a, b).
     */
    @Override
    public String toString(){
        return "("+a+", "+b+")";
    }
    /**
     * Checks if this pair is equal to another object.
     * @param other The object to compare with.
     * @return True if the other object is a Pair with equal elements, false otherwise.
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Pair<?,?> o)) return false;
        return (Objects.equals(a, o.a)) && (Objects.equals(b, o.b));
    }
    /**
     * Returns the hash code of the pair.
     * @return The hash code computed from the two elements.
     */
    @Override
    public int hashCode(){
        return Objects.hash(a, b);
    }
    /**
     * Returns the size of the pair, which is always 2.
     * @return The size of the pair.
     */
    @Override
    public int size() {
        return 2;
    }
    /**
     * Returns the elements of the pair as a list.
     * @return A list containing the two elements of the pair.
     */
    @Override
    public List<Object> asList() {
        return List.of(a, b);
    }
    /**
     * Returns the elements of the pair as a Map.Entry.
     * @return A Map.Entry containing the two elements of the pair.
     */
    public Map.Entry<A, B> asMapEntry() {
        return Map.entry(a, b);
    }
}
