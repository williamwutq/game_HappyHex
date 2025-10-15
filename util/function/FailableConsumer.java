package util.function;

/**
 * Represents an operation that accepts a single input argument and returns no result,
 * but can throw a checked exception.
 * @param <T> the type of the input to the operation
 * @param <E> the type of exception that may be thrown
 */
@FunctionalInterface
public interface FailableConsumer <T, E extends Exception> {
    void accept(T t) throws E;
}
