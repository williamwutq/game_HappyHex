package util.function;

import java.util.function.*;

/**
 * A utility class for packing various types of functions, predicates, and consumers into a single function that takes an Object array as input.
 * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
 * This class supports standard and failable versions of functions, predicates, and consumers with up to four arguments, which should cover
 * all types provided in the {@code util.function} package. For more arguments, use tuples provided by the {@link util.tuple} package.
 * <p>
 * All functions are packaged into a {@link util.function.FailableFunction} that can throw any exception.
 * In this signature, the return type is {@code Object}, and the input is an array of {@code Object}, and the exception type is {@code Exception}.
 * <p>
 * To use, simply call the static {@code packFunc} method with the function to be packed as an argument.
 *
 * @see util.function.FailableFunction
 */
public class FunctionPackager {
    private FunctionPackager() {
        // Private constructor to prevent instantiation
    }
    /**
     * Pack a failable function into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param function the function to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(FailableFunction<Object, Object, Exception> function) {
        return (Object[] args) -> {
            if (args.length != 1) {
                throw new IllegalArgumentException("Argument count mismatch: expected 1, got " + args.length);
            }
            return function.apply(args[0]);
        };
    }
    /**
     * Pack a failable bi-function into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param function the bi-function to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(FailableBiFunction<Object, Object, Object, Exception> function) {
        return (Object[] args) -> {
            if (args.length != 2) {
                throw new IllegalArgumentException("Argument count mismatch: expected 2, got " + args.length);
            }
            return function.apply(args[0], args[1]);
        };
    }
    /**
     * Pack a failable tri-function into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param function the tri-function to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(FailableTriFunction<Object, Object, Object, Object, Exception> function) {
        return (Object[] args) -> {
            if (args.length != 3) {
                throw new IllegalArgumentException("Argument count mismatch: expected 3, got " + args.length);
            }
            return function.apply(args[0], args[1], args[2]);
        };
    }
    /**
     * Pack a failable quad-function into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param function the quad-function to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(FailableQuadFunction<Object, Object, Object, Object, Object, Exception> function) {
        return (Object[] args) -> {
            if (args.length != 4) {
                throw new IllegalArgumentException("Argument count mismatch: expected 4, got " + args.length);
            }
            return function.apply(args[0], args[1], args[2], args[3]);
        };
    }
    /**
     * Pack a standard function into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param function the function to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(Function<Object, Object> function) {
        return (Object[] args) -> {
            if (args.length != 1) {
                throw new IllegalArgumentException("Argument count mismatch: expected 1, got " + args.length);
            }
            return function.apply(args[0]);
        };
    }
    /**
     * Pack a standard bi-function into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param function the bi-function to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(BiFunction<Object, Object, Object> function) {
        return (Object[] args) -> {
            if (args.length != 2) {
                throw new IllegalArgumentException("Argument count mismatch: expected 2, got " + args.length);
            }
            return function.apply(args[0], args[1]);
        };
    }
    /**
     * Pack a standard tri-function into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param function the tri-function to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(TriFunction<Object, Object, Object, Object> function) {
        return (Object[] args) -> {
            if (args.length != 3) {
                throw new IllegalArgumentException("Argument count mismatch: expected 3, got " + args.length);
            }
            return function.apply(args[0], args[1], args[2]);
        };
    }
    /**
     * Pack a standard quad-function into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param function the quad-function to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(QuadFunction<Object, Object, Object, Object, Object> function) {
        return (Object[] args) -> {
            if (args.length != 4) {
                throw new IllegalArgumentException("Argument count mismatch: expected 4, got " + args.length);
            }
            return function.apply(args[0], args[1], args[2], args[3]);
        };
    }
    /**
     * Pack a standard predicate into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param predicate the predicate to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(Predicate<Object> predicate) {
        return (Object[] args) -> {
            if (args.length != 1) {
                throw new IllegalArgumentException("Argument count mismatch: expected 1, got " + args.length);
            }
            return predicate.test(args[0]);
        };
    }
    /**
     * Pack a standard bi-predicate into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param predicate the bi-predicate to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(BiPredicate<Object, Object> predicate) {
        return (Object[] args) -> {
            if (args.length != 2) {
                throw new IllegalArgumentException("Argument count mismatch: expected 2, got " + args.length);
            }
            return predicate.test(args[0], args[1]);
        };
    }
    /**
     * Pack a standard tri-predicate into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param predicate the tri-predicate to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(TriPredicate<Object, Object, Object> predicate) {
        return (Object[] args) -> {
            if (args.length != 3) {
                throw new IllegalArgumentException("Argument count mismatch: expected 3, got " + args.length);
            }
            return predicate.test(args[0], args[1], args[2]);
        };
    }
    /**
     * Pack a standard quad-predicate into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param predicate the quad-predicate to be packed
     * @return a function that takes an Object array and returns an Object
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(QuadPredicate<Object, Object, Object, Object> predicate) {
        return (Object[] args) -> {
            if (args.length != 4) {
                throw new IllegalArgumentException("Argument count mismatch: expected 4, got " + args.length);
            }
            return predicate.test(args[0], args[1], args[2], args[3]);
        };
    }
    /**
     * Pack a standard consumer into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param consumer the consumer to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(Consumer<Object> consumer) {
        return (Object[] args) -> {
            if (args.length != 1) {
                throw new IllegalArgumentException("Argument count mismatch: expected 1, got " + args.length);
            }
            consumer.accept(args[0]);
            return null;
        };
    }
    /**
     * Pack a standard bi-consumer into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param consumer the bi-consumer to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(BiConsumer<Object, Object> consumer) {
        return (Object[] args) -> {
            if (args.length != 2) {
                throw new IllegalArgumentException("Argument count mismatch: expected 2, got " + args.length);
            }
            consumer.accept(args[0], args[1]);
            return null;
        };
    }
    /**
     * Pack a standard tri-consumer into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param consumer the tri-consumer to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(TriConsumer<Object, Object, Object> consumer) {
        return (Object[] args) -> {
            if (args.length != 3) {
                throw new IllegalArgumentException("Argument count mismatch: expected 3, got " + args.length);
            }
            consumer.accept(args[0], args[1], args[2]);
            return null;
        };
    }
    /**
     * Pack a standard quad-consumer into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param consumer the quad-consumer to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(QuadConsumer<Object, Object, Object, Object> consumer) {
        return (Object[] args) -> {
            if (args.length != 4) {
                throw new IllegalArgumentException("Argument count mismatch: expected 4, got " + args.length);
            }
            consumer.accept(args[0], args[1], args[2], args[3]);
            return null;
        };
    }
    /**
     * Pack a failable consumer into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param consumer the consumer to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(FailableConsumer<Object, Exception> consumer) {
        return (Object[] args) -> {
            if (args.length != 1) {
                throw new IllegalArgumentException("Argument count mismatch: expected 1, got " + args.length);
            }
            consumer.accept(args[0]);
            return null;
        };
    }
    /**
     * Pack a failable bi-consumer into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param consumer the bi-consumer to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(FailableBiConsumer<Object, Object, Exception> consumer) {
        return (Object[] args) -> {
            if (args.length != 2) {
                throw new IllegalArgumentException("Argument count mismatch: expected 2, got " + args.length);
            }
            consumer.accept(args[0], args[1]);
            return null;
        };
    }
    /**
     * Pack a failable tri-consumer into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param consumer the tri-consumer to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(FailableTriConsumer<Object, Object, Object, Exception> consumer) {
        return (Object[] args) -> {
            if (args.length != 3) {
                throw new IllegalArgumentException("Argument count mismatch: expected 3, got " + args.length);
            }
            consumer.accept(args[0], args[1], args[2]);
            return null;
        };
    }
    /**
     * Pack a failable quad-consumer into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param consumer the quad-consumer to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(FailableQuadConsumer<Object, Object, Object, Object, Exception> consumer) {
        return (Object[] args) -> {
            if (args.length != 4) {
                throw new IllegalArgumentException("Argument count mismatch: expected 4, got " + args.length);
            }
            consumer.accept(args[0], args[1], args[2], args[3]);
            return null;
        };
    }
    /**
     * Pack a standard runnable into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param runnable the runnable to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(Runnable runnable) {
        return (Object[] args) -> {
            if (args.length != 0) {
                throw new IllegalArgumentException("Argument count mismatch: expected 0, got " + args.length);
            }
            runnable.run();
            return null;
        };
    }
    /**
     * Pack a failable runnable into a single function that takes an Object array.
     * The packed function checks the number of arguments at runtime and throws an IllegalArgumentException if there is a mismatch.
     * @param failable the failable runnable to be packed
     * @return a function that takes an Object array and returns null
     */
    public static FailableFunction<Object[], Object, Exception> packFunc(Failable<Exception> failable) {
        return (Object[] args) -> {
            if (args.length != 0) {
                throw new IllegalArgumentException("Argument count mismatch: expected 0, got " + args.length);
            }
            failable.run();
            return null;
        };
    }
}
