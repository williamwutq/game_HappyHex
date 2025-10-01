package util.function;

/**
 * Represents an operation that accepts four input arguments and returns no result.
 * This is the four-arity specialization of {@link java.util.function.Consumer} that can throw a checked exception.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 * @param <W> the type of the fourth argument to the operation
 * @param <E> the type of exception that may be thrown
 */
@FunctionalInterface
public interface FailableQuadConsumer <T, U, V, W, E extends Exception> {
    void accept(T t, U u, V v, W w) throws E;
}
