package util.tuple;

/**
 * A simple generic Quartet class to hold four related objects.
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 * @param <C> The type of the third object.
 * @param <D> The type of the fourth object.
 */
public class Quartet <A, B, C, D> {
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
}
