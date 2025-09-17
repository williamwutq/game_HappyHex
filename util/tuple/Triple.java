package util.tuple;

/**
 * A simple generic Triple class to hold three related objects.
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 * @param <C> The type of the third object.
 */
public class Triple <A, B, C> {
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
}
