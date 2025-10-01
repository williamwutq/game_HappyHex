package util.tuple;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An interface representing an ordered tuple of objects.
 * <p>
 * This interface provides methods to access the first and last elements of the tuple,
 * as well as methods to convert the tuple to a list, set, or array. It also extends
 * the Iterable interface to allow iteration over the elements of the tuple.
 *
 * @param <F> The type of the first object.
 * @param <L> The type of the last object.
 */
public interface OrderedTuple <F, L> extends Iterable<Object> {
    /**
     * Get the first object of the tuple.
     * @return The first object.
     */
    F getFirst();
    /**
     * Get the last object of the tuple.
     * @return The last object.
     */
    L getLast();
    /**
     * Get the size of the tuple.
     * @return The size of the tuple.
     */
    int size();
    /**
     * Convert the tuple to a list.
     * @return The tuple as a list.
     */
    List<Object> asList();
    /**
     * Convert the tuple to a set.
     * @return The tuple as a set.
     */
    default Set<Object> asSet(){
        return Set.copyOf(asList());
    }
    /**
     * Convert the tuple to an array.
     * @return The tuple as an array.
     */
    default Object[] asArray(){
        return asList().toArray();
    }
    /**
     * Get an iterator over the elements of the tuple.
     * @return An iterator over the elements of the tuple.
     */
    default Iterator<Object> iterator(){
        return asList().iterator();
    }
}
