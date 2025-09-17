package util.tuple;

/**
 * A simple generic Pair class to hold two related objects.
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 */
public class Pair <A, B> {
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
    public B getLast(){return b;}
}
