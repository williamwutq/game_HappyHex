package util.function;

/**
 * Represents an operation that accepts two input arguments and returns no result,
 * but can throw a checked exception.
 * @param <T> the type of the first input to the operation
 * @param <U> the type of the second input to the operation
 * @param <E> the type of exception that may be thrown
 */
@FunctionalInterface
public interface FailableBiConsumer <T, U, E extends Exception> {
    void accept(T t, U u) throws E;
}
