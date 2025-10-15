package util.function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link java.util.function.Function}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <V> the type of the third argument to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface TriFunction <T, U, V, R> {
    R apply(T t, U u, V v);
}
