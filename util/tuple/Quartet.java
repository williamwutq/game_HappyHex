package util.tuple;

import java.util.List;
import java.util.Objects;

/**
 * A simple generic Quartet class to hold four related objects.
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 * @param <C> The type of the third object.
 * @param <D> The type of the fourth object.
 */
public class Quartet <A, B, C, D> implements OrderedTuple<A, D> {
    private final A a;
    private final B b;
    private final C c;
    private final D d;
    /**
     * Constructor to initialize the quartet with four objects.
     * @param a The first object.
     * @param b The second object.
     * @param c The third object.
     * @param d The fourth object.
     */
    public Quartet(A a, B b, C c, D d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    /**
     * Get the first object of the quartet.
     * @return The first object.
     */
    public A getFirst(){return a;}
    /**
     * Get the second object of the quartet.
     * @return The second object.
     */
    public B getSecond(){return b;}
    /**
     * Get the third object of the quartet.
     * @return The third object.
     */
    public C getThird(){return c;}
    /**
     * Get the fourth object of the quartet.
     * @return The fourth object.
     */
    public D getFourth(){return d;}
    /**
     * Get the fourth object of the quartet (alias for getFourth).
     * @return The fourth object.
     */
    public D getLast(){return d;}
    /**
     * Returns a String representation of the quartet.
     * @return A string in the format (a, b, c, d).
     */
    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ", " + d + ")";
    }
    /**
     * Checks if this quartet is equal to another object.
     * @param other The object to compare with.
     * @return True if the other object is a Quartet with equal elements, false otherwise.
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Quartet<?,?,?,?> o)) return false;
        return (Objects.equals(a, o.a)) && (Objects.equals(b, o.b)) && (Objects.equals(c, o.c)) && (Objects.equals(d, o.d));
    }
    /**
     * Returns the hash code of the quartet.
     * @return The hash code computed from the four elements.
     */
    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d);
    }
    /**
     * Returns the size of the quartet, which is always 4.
     * @return The size of the quartet.
     */
    @Override
    public int size() {
        return 4;
    }
    /**
     * Returns the elements of the quartet as a list.
     * @return A list containing the four elements of the quartet.
     */
    @Override
    public List<Object> asList() {
        return List.of(a, b, c, d);
    }
}
