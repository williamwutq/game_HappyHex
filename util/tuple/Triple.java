package util.tuple;

import java.util.List;
import java.util.Objects;

/**
 * A simple generic Triple class to hold three related objects.
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 * @param <C> The type of the third object.
 */
public class Triple <A, B, C> implements OrderedTuple<A, C> {
    private final A a;
    private final B b;
    private final C c;
    /**
     * Constructor to initialize the triple with three objects.
     * @param a The first object.
     * @param b The second object.
     * @param c The third object.
     */
    public Triple(A a, B b, C c){
        this.a = a;
        this.b = b;
        this.c = c;
    }
    /**
     * Get the first object of the triple.
     * @return The first object.
     */
    public A getFirst(){return a;}
    /**
     * Get the second object of the triple.
     * @return The second object.
     */
    public B getSecond(){return b;}
    /**
     * Get the third object of the triple.
     * @return The third object.
     */
    public C getThird(){return c;}
    /**
     * Get the third object of the triple (alias for getThird).
     * @return The third object.
     */
    public C getLast(){return c;}
    /**
     * Returns a String representation of the triple.
     * @return A string in the format (a, b, c).
     */
    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ")";
    }
    /**
     * Checks if this triple is equal to another object.
     * @param other The object to compare with.
     * @return True if the other object is a Triple with equal elements, false otherwise.
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Triple<?,?,?> o)) return false;
        return (Objects.equals(a, o.a)) && (Objects.equals(b, o.b)) && (Objects.equals(c, o.c));
    }
    /**
     * Returns the hash code of the triple.
     * @return The hash code computed from the three elements.
     */
    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }
    /**
     * Returns the size of the triple, which is always 3.
     * @return The size of the triple.
     */
    @Override
    public int size() {
        return 3;
    }
    /**
     * Returns the elements of the triple as a list.
     * @return A list containing the three elements of the triple.
     */
    @Override
    public List<Object> asList() {
        return List.of(a, b, c);
    }
}
