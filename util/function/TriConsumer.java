package util.function;

/**
 * Represents an operation that accepts three input arguments and returns no result.
 * This is the three-arity specialization of {@link java.util.function.Consumer}.
 * Unlike most other functional interfaces, {@code TriConsumer} is expected to operate via side-effects.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 */
@FunctionalInterface
public interface TriConsumer <T, U, V> {
    void accept(T t, U u, V v);
}
