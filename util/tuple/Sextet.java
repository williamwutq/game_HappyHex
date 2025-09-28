package util.tuple;

import java.util.List;
import java.util.Objects;

/**
 * A simple generic Sextet class to hold six related objects.
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 * @param <C> The type of the third object.
 * @param <D> The type of the fourth object.
 * @param <E> The type of the fifth object.
 * @param <F> The type of the sixth object.
 */
public class Sextet <A, B, C, D, E, F> implements OrderedTuple<A, F> {
    private final A a;
    private final B b;
    private final C c;
    private final D d;
    private final E e;
    private final F f;
    /**
     * Constructor to initialize the sextet with six objects.
     * @param a The first object.
     * @param b The second object.
     * @param c The third object.
     * @param d The fourth object.
     * @param e The fifth object.
     * @param f The sixth object.
     */
    public Sextet(A a, B b, C c, D d, E e, F f){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }
    /**
     * Get the first object of the sextet.
     * @return The first object.
     */
    @Override
    public A getFirst(){return a;}
    /**
     * Get the second object of the sextet.
     * @return The second object.
     */
    public B getSecond(){return b;}
    /**
     * Get the third object of the sextet.
     * @return The third object.
     */
    public C getThird(){return c;}
    /**
     * Get the fourth object of the sextet.
     * @return The fourth object.
     */
    public D getFourth(){return d;}
    /**
     * Get the fifth object of the sextet.
     * @return The fifth object.
     */
    public E getFifth(){return e;}
    /**
     * Get the sixth object of the sextet.
     * @return The sixth object.
     */
    public F getSixth(){return f;}
    /**
     * Get the sixth object of the sextet (alias for getSixth).
     * @return The sixth object.
     */
    @Override
    public F getLast(){return f;}
    /**
     * Returns a String representation of the sextet.
     * @return A string in the format (a, b, c, d, e, f).
     */
    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ", " + d + ", " + e + ", " + f + ")";
    }
    /**
     * Checks if this sextet is equal to another object.
     * @param other The object to compare with.
     * @return True if the other object is a Sextet with equal elements, false otherwise.
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Sextet<?,?,?,?,?,?> o)) return false;
        return (Objects.equals(a, o.a)) && (Objects.equals(b, o.b)) && (Objects.equals(c, o.c)) &&
                (Objects.equals(d, o.d)) && (Objects.equals(e, o.e)) && (Objects.equals(f, o.f));
    }
    /**
     * Returns the hash code of the sextet.
     * @return The hash code computed from the six elements.
     */
    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d, e, f);
    }
    /**
     * Get the size of the sextet.
     * @return The size of the sextet, which is always 6.
     */
    @Override
    public int size() {
        return 6;
    }
    /**
     * Returns the elements of the sextet as a list.
     * @return An unmodifiable list containing the six elements.
     */
    @Override
    public List<Object> asList() {
        return List.of(a, b, c, d, e, f);
    }
}
