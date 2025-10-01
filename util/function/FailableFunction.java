package util.function;

/**
 * Represents a function that accepts one argument and produces a result,
 * but can throw a checked exception.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of exception that may be thrown
 */
@FunctionalInterface
public interface FailableFunction<T, R, E extends Exception> {
    R apply(T t) throws E;
}
