package util.function;

/**
 * Represents a function that accepts three arguments and produces a result,
 * but can throw a checked exception.
 * @param <T> the type of the first input to the function
 * @param <U> the type of the second input to the function
 * @param <V> the type of the third input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of exception that may be thrown
 */
@FunctionalInterface
public interface FailableTriFunction <T, U, V, R, E extends Exception> {
    R apply(T t, U u, V v) throws E;
}
