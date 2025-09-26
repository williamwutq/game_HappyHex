package util.function;

/**
 * Represents a function that accepts no arguments and produces no result,
 * but can throw a checked exception.
 * @param <E> the type of exception that may be thrown
 */
@FunctionalInterface
public interface Failable<E extends Exception> {
    void run() throws E;
}
