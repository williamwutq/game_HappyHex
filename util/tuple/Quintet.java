package util.tuple;

import java.util.List;
import java.util.Objects;

/**
 * A simple generic Quintet class to hold five related objects.
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 * @param <C> The type of the third object.
 * @param <D> The type of the fourth object.
 * @param <E> The type of the fifth object.
 */
public class Quintet <A, B, C, D, E> implements OrderedTuple<A, E> {
    private final A a;
    private final B b;
    private final C c;
    private final D d;
    private final E e;
    /**
     * Constructor to initialize the quintet with five objects.
     * @param a The first object.
     * @param b The second object.
     * @param c The third object.
     * @param d The fourth object.
     * @param e The fifth object.
     */
    public Quintet(A a, B b, C c, D d, E e){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }
    /**
     * Get the first object of the quintet.
     * @return The first object.
     */
    public A getFirst(){return a;}
    /**
     * Get the second object of the quintet.
     * @return The second object.
     */
    public B getSecond(){return b;}
    /**
     * Get the third object of the quintet.
     * @return The third object.
     */
    public C getThird(){return c;}
    /**
     * Get the fourth object of the quintet.
     * @return The fourth object.
     */
    public D getFourth(){return d;}
    /**
     * Get the fifth object of the quintet.
     * @return The fifth object.
     */
    public E getFifth(){return e;}
    /**
     * Get the fifth object of the quintet (alias for getFifth).
     * @return The fifth object.
     */
    public E getLast(){return e;}
    /**
     * Returns a String representation of the quintet.
     * @return A string in the format (a, b, c, d, e).
     */
    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ", " + d + ", " + e + ")";
    }
    /**
     * Checks if this quintet is equal to another object.
     * @param other The object to compare with.
     * @return True if the other object is a Quintet with equal elements, false otherwise.
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Quintet<?,?,?,?,?> o)) return false;
        return (Objects.equals(a, o.a)) && (Objects.equals(b, o.b)) && (Objects.equals(c, o.c)) && (Objects.equals(d, o.d)) && (Objects.equals(e, o.e));
    }
    /**
     * Returns the hash code of the quintet.
     * @return The hash code computed from the five elements.
     */
    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d, e);
    }
    /**
     * Returns the size of the quintet, which is always 5.
     * @return The size of the quintet.
     */
    @Override
    public int size() {
        return 5;
    }
    /**
     * Returns the elements of the quintet as a list.
     * @return An unmodifiable list containing the five elements.
     */
    @Override
    public List<Object> asList() {
        return List.of(a, b, c, d, e);
    }
}
