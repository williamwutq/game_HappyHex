package util.test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import util.function.FailableFunction;

/**
 * A utility class that intercepts calls to a function, recording the arguments, result, and exception (if any) for each call.
 * This can be useful for testing and debugging purposes, allowing to analyze the behavior of the function over multiple calls.
 * <p>
 * The intercepted function must conform to the {@link FailableFunction} interface, which allows for functions that can throw checked exceptions.
 * After wrapping a function with this interceptor, calls to the function should be made through the interceptor's {@link #apply(Object[])} method,
 * effectively replacing direct calls to the original function. If the function does not conform to this interface, it must be adapted accordingly.
 * The functional library provides {@link util.function.FunctionPackager} to adapt all kinds of functions to {@link FailableFunction}.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * FailableFunction<Object[], Object, Exception> myFunction = args -> {
 *     // Function implementation
 * };
 * FunctionInterceptor interceptor = new FunctionInterceptor(myFunction);
 * try {
 *     interceptor.apply(new Object[]{arg1, arg2});
 *     // Use the interceptor to call the function
 *     // If the interceptor is not present, the function call would be:
 *     // myFunction.apply(new Object[]{arg1, arg2});
 * } catch (Exception e) {
 *     // Handle exception
 * }
 * List<FunctionInterceptor.FunctionCall> calls = interceptor.getCalls();
 * }
 * <p>
 * The class also provides various statistical methods to analyze the recorded calls, such as pass rate, null rate,
 * exception rate, entropy calculations, and checks for determinism. These methods can help in understanding the
 * reliability and behavior of the function under test.
 * <p>
 * Note: This class is not thread-safe. If the intercepted function is called from multiple threads,
 * external synchronization is required to ensure correct behavior.
 */
public final class FunctionInterceptor implements FailableFunction<Object[], Object, Exception> {
    private final List<FunctionCall> calls;
    private final FailableFunction<Object[], Object, Exception> function;
    /**
     * A record of function calls, containing the arguments, result, and exception (if any).
     *
     * @param args the arguments passed to the function
     * @param result the result returned by the function
     * @param exception the exception thrown by the function
     */
    public record FunctionCall(Object[] args, Object result, Exception exception) {
        /**
         * Check if the function call was successful.
         * @return true if the function call was successful, false otherwise.
         */
        boolean isSuccess() {
            return exception == null;
        }
        /**
         * Check if the function call has a result.
         * @return true if the function call has a result, false otherwise.
         */
        boolean hasResult() {
            return result != null;
        }
        /**
         * Check if the function call has arguments.
         * @return true if the function call has arguments, false otherwise.
         */
        boolean hasArgs() {
            return args != null && args.length > 0;
        }
        /**
         * Return a string representation of the function call.
         * @return a string representation of the function call.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("FunctionCall{");
            if (hasArgs()) {
                sb.append("args=");
                sb.append('[');
                for (int i = 0; i < args.length; i++) {
                    sb.append(args[i]);
                    if (i < args.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(']');
            } else {
                sb.append("args=[]");
            }
            if (isSuccess()) {
                if (hasResult()) {
                    sb.append(", result=");
                    sb.append(result);
                } else {
                    sb.append(", result=null");
                }
            } else {
                sb.append(", exception=");
                sb.append(exception);
            }
            sb.append('}');
            return sb.toString();
        }
    }
    /**
     * Create a new FunctionInterceptor for the given function.
     * @param function the function to intercept.
     */
    public FunctionInterceptor(FailableFunction<Object[], Object, Exception> function) {
        this.function = function;
        this.calls = new LinkedList<>();
        // LinkedList because no one will care about random access
    }
    /**
     * Apply the function and record the call, including arguments, result, and exception (if any).
     * <p>
     * This method keep faithful to the original function's behavior, rethrowing any exception thrown by the function.
     *
     * @param args the arguments to pass to the function.
     * @return the result of the function call.
     * @throws Exception if the function throws an exception.
     */
    @Override
    public Object apply(Object[] args) throws Exception {
        Object result = null;
        Exception exception = null;
        try {
            result = function.apply(args);
        } catch (Exception e) {
            exception = e;
        }
        calls.add(new FunctionCall(args, result, exception));
        if (exception != null) {
            throw exception;
        }
        return result;
    }
    /**
     * Get the list of recorded function calls. For each call, the arguments, result, and exception (if any) are recorded.
     * To get only the results, arguments, or exceptions, use
     * {@link #getResults()}, {@link #getArgs()}, or {@link #getExceptions()} respectively.
     * @return the list of recorded function calls.
     */
    public List<FunctionCall> getCalls() {
        return calls;
    }
    /**
     * Get the list of results from the recorded function calls. Resulting list may contain nulls but have consistent size.
     * @return the list of results from the recorded function calls.
     * @see #getCalls()
     */
    public List<Object> getResults() {
        return calls.stream().map(FunctionCall::result).toList();
    }
    /**
     * Get the list of non-null results from the successful recorded function calls. Resulting list may be smaller than the number of calls.
     * @return the list of non-null results from the successful recorded function calls.
     * @see #getResults()
     */
    public List<Object> filteredResults() {
        return calls.stream().filter(FunctionCall::isSuccess).map(FunctionCall::result).filter(Objects::nonNull).toList();
    }
    /**
     * Get the list of arguments from the recorded function calls. Resulting list may contain nulls but have consistent size.
     * @return the list of arguments from the recorded function calls.
     * @see #getCalls()
     */
    public List<Object[]> getArgs() {
        return calls.stream().map(FunctionCall::args).toList();
    }
    /**
     * Get the list of arguments from the successful recorded function calls. Resulting list may be smaller than the number of calls.
     * @return the list of arguments from the successful recorded function calls.
     * @see #getArgs()
     */
    public List<Object[]> filteredArgs() {
        return calls.stream().filter(FunctionCall::isSuccess).filter(FunctionCall::hasArgs).map(FunctionCall::args).toList();
    }
    /**
     * Get the list of exceptions from the recorded function calls. Resulting list may contain nulls but have consistent size.
     * @return the list of exceptions from the recorded function calls.
     * @see #getCalls()
     */
    public List<Exception> getExceptions() {
        return calls.stream().map(FunctionCall::exception).toList();
    }
    /**
     * Get the list of exceptions from the failed recorded function calls. Resulting list may be smaller than the number of calls.
     * @return the list of exceptions from the failed recorded function calls.
     * @see #getExceptions()
     */
    public List<Exception> filteredExceptions() {
        return calls.stream().filter(call -> !call.isSuccess()).map(FunctionCall::exception).filter(Objects::nonNull).toList();
    }
    /**
     * Get the pass rate of the function calls, defined as the ratio of successful calls to total calls.
     * If no calls have been made, the pass rate is defined as 0.0.
     * @return the pass rate of the function calls.
     */
    public double passRate() {
        if (calls.isEmpty()) {
            return 0.0;
        }
        long successCount = calls.stream().filter(FunctionCall::isSuccess).count();
        return (double) successCount / calls.size();
    }
    /**
     * Get the average number of arguments passed to the function across all calls.
     * If no calls have been made, the average argument count is defined as 0.0.
     * @return the average number of arguments passed to the function.
     */
    public double averageArgumentCount() {
        if (calls.isEmpty()) {
            return 0.0;
        }
        long totalArgs = calls.stream().mapToLong(call -> call.args() != null ? call.args().length : 0).sum();
        return (double) totalArgs / calls.size();
    }
    /**
     * Get the null result rate of the function calls, defined as the ratio of calls that returned null to total calls.
     * Calls that threw exceptions are not counted as returning null.
     * If no calls have been made, the null result rate is defined as 0.0.
     * @return the null result rate of the function calls.
     */
    public double nullRate() {
        if (calls.isEmpty()) {
            return 0.0;
        }
        long nullCount = calls.stream().filter(FunctionCall::isSuccess).filter(call -> call.result() == null).count();
        return (double) nullCount / calls.size();
    }
    /**
     * Get the exception rate of the function calls for a specific exception class,
     * defined as the ratio of calls that threw the specified exception to total calls.
     * If no calls have been made, the exception rate is defined as 0.0.
     * @param exceptionClass the class of the exception to calculate the rate for.
     * @return the exception rate of the function calls for the specified exception class.
     */
    public double exceptionRate(Class<? extends Exception> exceptionClass) {
        if (calls.isEmpty()) {
            return 0.0;
        }
        long exceptionCount = calls.stream()
                .filter(call -> !call.isSuccess())
                .filter(call -> exceptionClass.isInstance(call.exception()))
                .count();
        return (double) exceptionCount / calls.size();
    }
    /**
     * Calculate the entropy of the results from successful function calls.
     * Entropy is a measure of uncertainty or randomness in the results.
     * If no successful calls with non-null results have been made, the entropy is defined as 0.0.
     * @return the entropy of the results from successful function calls.
     */
    public double resultEntropy() {
        if (calls.isEmpty()) {
            return 0.0;
        }
        var freqMap = calls.stream()
                .filter(FunctionCall::isSuccess)
                .map(FunctionCall::result)
                .filter(Objects::nonNull)
                // Count frequency of each unique result
                .collect(java.util.stream.Collectors.groupingBy(r -> r, java.util.stream.Collectors.counting()));
        double entropy = 0.0;
        long total = freqMap.values().stream().mapToLong(Long::longValue).sum();
        for (long count : freqMap.values()) {
            double p = (double) count / total;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }
    /**
     * Calculate the entropy of the arguments from successful function calls.
     * Entropy is a measure of uncertainty or randomness in the arguments.
     * If no successful calls with non-null arguments have been made, the entropy is defined as 0.0.
     * @return the entropy of the arguments from successful function calls.
     */
    public double argumentEntropy() {
        if (calls.isEmpty()) {
            return 0.0;
        }
        var freqMap = calls.stream()
                .filter(FunctionCall::isSuccess)
                .map(FunctionCall::args)
                .filter(Objects::nonNull)
                .flatMap(java.util.Arrays::stream)
                // Count frequency of each unique argument
                .collect(java.util.stream.Collectors.groupingBy(r -> r, java.util.stream.Collectors.counting()));
        double entropy = 0.0;
        long total = freqMap.values().stream().mapToLong(Long::longValue).sum();
        for (long count : freqMap.values()) {
            double p = (double) count / total;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }
    /**
     * Calculate the entropy of the exceptions from failed function calls.
     * Entropy is a measure of uncertainty or randomness in the exceptions.
     * If no failed calls with non-null exceptions have been made, the entropy is defined as 0.0.
     * @return the entropy of the exceptions from failed function calls.
     */
    public double exceptionEntropy() {
        if (calls.isEmpty()) {
            return 0.0;
        }
        var freqMap = calls.stream()
                .filter(call -> !call.isSuccess())
                .map(FunctionCall::exception)
                .filter(Objects::nonNull)
                // Count frequency of each unique exception
                .collect(java.util.stream.Collectors.groupingBy(r -> r, java.util.stream.Collectors.counting()));
        double entropy = 0.0;
        long total = freqMap.values().stream().mapToLong(Long::longValue).sum();
        for (long count : freqMap.values()) {
            double p = (double) count / total;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }
    /**
     * Check if the function is deterministic based on the recorded calls.
     * A function is considered deterministic if the same arguments always produce the same result.
     * If no calls have been made, the function is considered deterministic.
     * <p>
     * Note: This method does not consider exceptions in its determination of determinism,
     * and may give false positives for small datasets.
     * @return true if the function is deterministic, false otherwise.
     */
    public boolean isDeterministic() {
        if (calls.isEmpty()) {
            return true;
        }
        var mapping = new java.util.HashMap<List<Object>, Object>();
        for (FunctionCall call : calls) {
            List<Object> argsList = call.args() != null ? java.util.Arrays.asList(call.args()) : java.util.Collections.emptyList();
            if (mapping.containsKey(argsList)) {
                Object existingResult = mapping.get(argsList);
                if (!Objects.equals(existingResult, call.result())) {
                    return false; // Same args map to different results
                }
            } else {
                mapping.put(argsList, call.result());
            }
        }
        return true;
    }
    /**
     * Get the arguments of the last function call.
     * If no calls have been made, returns null.
     * @return the arguments of the last function call, or null if no calls have been made.
     */
    public Object[] getLastArgs() {
        if (calls.isEmpty()) {
            return null;
        }
        return calls.getLast().args();
    }
    /**
     * Get the result of the last function call.
     * If no calls have been made, returns null.
     * @return the result of the last function call, or null if no calls have been made.
     */
    public Object getLastResult() {
        if (calls.isEmpty()) {
            return null;
        }
        return calls.getLast().result();
    }
    /**
     * Get the exception throw by the last function call.
     * If no calls have been made, returns null.
     * @return the exception throw by the last function call, or null if no calls have been made.
     */
    public Exception getLastException() {
        if (calls.isEmpty()) {
            return null;
        }
        return calls.getLast().exception();
    }
    /**
     * Get the most common arguments passed to the function across all calls.
     * If no calls have been made, returns empty array.
     * In case of a tie, one of the most common argument sets is returned arbitrarily.
     * @return the most common arguments passed to the function, or null if no calls have been made.
     */
    public Object[] getMostCommonArgs() {
        if (calls.isEmpty()) {
            return new Object[0];
        }
        var freqMap = calls.stream()
                .map(FunctionCall::args)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.groupingBy(Arrays::asList, java.util.stream.Collectors.counting()));
        return freqMap.entrySet().stream().max(java.util.Map.Entry.comparingByValue()).get().getKey().toArray();
    }
    /**
     * Get the most common result returned by the function across all calls.
     * If no calls have been made, returns null.
     * In case of a tie, one of the most common results is returned arbitrarily.
     * @return the most common result returned by the function, or null if no calls have been made.
     */
    public Object getMostCommonResult() {
        if (calls.isEmpty()) {
            return null;
        }
        var freqMap = calls.stream()
                .map(FunctionCall::result)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.groupingBy(r -> r, java.util.stream.Collectors.counting()));
        return freqMap.entrySet().stream().max(java.util.Map.Entry.comparingByValue()).get().getKey();
    }
    /**
     * Get the most common exception thrown by the function across all calls.
     * If no calls have been made, returns null.
     * In case of a tie, one of the most common exceptions is returned arbitrarily.
     * @return the most common exception thrown by the function, or null if no calls have been made.
     */
    public Exception getMostCommonException() {
        if (calls.isEmpty()) {
            return null;
        }
        var freqMap = calls.stream()
                .map(FunctionCall::exception)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.groupingBy(r -> r, java.util.stream.Collectors.counting()));
        return freqMap.entrySet().stream().max(java.util.Map.Entry.comparingByValue()).get().getKey();
    }
    /**
     * Get the most recent function call record, including arguments, result, and exception (if any).
     * If no calls have been made, returns null.
     * @return the most recent function call record, or null if no calls have been made.
     */
    public FunctionCall getMostRecentCall() {
        if (calls.isEmpty()) {
            return null;
        }
        return calls.getLast();
    }
    /**
     * Filter the recorded function calls based on the provided criteria.
     * Any of the criteria can be null, in which case that criterion is ignored.
     *
     * @param results an array of results to filter by, or null to ignore this criterion.
     * @param args an array of argument arrays to filter by, or null to ignore this criterion.
     * @param exceptions an array of exception classes to filter by, or null to ignore this criterion.
     * @return an array of FunctionCall records that match all provided criteria.
     */
    public FunctionCall[] filter(Object[] results, Object[] args, Class<? extends Exception>[] exceptions) {
        return calls.stream().filter(call -> {
            boolean resultMatch = results == null || Arrays.asList(results).contains(call.result());
            boolean argsMatch = args == null || Arrays.asList(args).contains(call.args());
            boolean exceptionMatch = exceptions == null || (call.exception() != null && Arrays.asList(exceptions).contains(call.exception().getClass()));
            return resultMatch && argsMatch && exceptionMatch;
        }).toArray(FunctionCall[]::new);
    }


    /**
     * Clear all recorded function calls.
     * After calling this method, the interceptor will have no record of any previous calls.
     */
    public void clear() {
        calls.clear();
    }
    /**
     * Trim the number of recorded function calls to the specified size.
     * If the current number of recorded calls exceeds the specified size, the oldest calls are removed.
     * If the specified size is negative, an IllegalArgumentException is thrown.
     * @param size the maximum number of recorded function calls to keep, must be non-negative.
     * @throws IllegalArgumentException if the specified size is negative.
     */
    public void limit(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be non-negative");
        }
        if (size == 0) {
            clear();
        } else while (calls.size() > size) {
            calls.removeFirst();
        }
    }
    /**
     * Spawn a background thread that automatically clears the recorded function calls at a fixed interval.
     * The thread is a daemon thread, so it will not prevent the JVM from exiting.
     * This can be useful to prevent memory bloat in long-running applications where the interceptor is used extensively.
     * <p>
     * The minimum interval is 1000 milliseconds (1 second) to prevent excessive CPU usage.
     * If a smaller interval is provided, it will be set to 1000 milliseconds.
     * <p>
     * Note: This method does not provide a way to stop the background thread. The thread will run until the JVM exits.
     * It is strongly not recommended to call this method multiple times on the same interceptor instance,
     * as this will spawn multiple background threads that all clear the same call list, leading to
     * unpredictable behavior.
     * @param intervalMillis the interval in milliseconds at which to clear the recorded function calls, minimum 1000ms.
     */
    public void spawnAutoClearThread(long intervalMillis) {
        int interval = intervalMillis < 1000 ? 1000 : (int) intervalMillis;
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                clear();
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }
}
