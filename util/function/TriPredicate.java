package util.function;

/**
 * Represents a predicate (boolean-valued function) of three arguments.
 * This is the three-arity specialization of {@link java.util.function.Predicate}.
 *
 * @param <T> the type of the first argument to the predicate
 * @param <U> the type of the second argument to the predicate
 * @param <V> the type of the third argument to the predicate
 */
@FunctionalInterface
public interface TriPredicate <T, U, V> {
    boolean test(T t, U u, V v);
}
