package util.test;

import java.io.PrintStream;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A simple testing utility class for assertions with colored output.
 * Supports assertions for boolean conditions, object equality, null checks, and exception handling.
 * Outputs results to standard output with color coding for pass/fail.
 * Keeps track of total tests and passed tests, and can print a summary.
 * Usage example:
 * <pre>{@code
 * Test.assertTrue(1 + 1 == 2, "Basic arithmetic");
 * Test.assertEquals("hello", "hello", "String equality");
 * Test.assertNull(null, "Null check");
 * Test.assertExcept(() -> { throw new IllegalArgumentException(); }, IllegalArgumentException.class, "Exception check");
 * Test.printSummary();
 * }</pre>
 * Or use runAll to execute multiple tests:
 * <pre>{@code
 * Test.runAll(
 *     () -> Test.assertTrue(1 + 1 == 2, "Basic arithmetic"),
 *     () -> Test.assertEquals("hello", "hello", "String equality"),
 *     () -> Test.assertNull(null, "Null check"),
 *     () -> Test.assertExcept(() -> { throw new IllegalArgumentException(); }, IllegalArgumentException.class, "Exception check")
 * );
 * }</pre>
 */
public final class Test {
    public static PrintStream OUT = System.out;
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static int testCount = 0;
    private static int passCount = 0;
    private Test() {
        // Prevent instantiation
    }
    /**
     * Assert that a condition is true.
     * @param condition the condition to check
     * @param message the message to display on failure
     */
    public static void assertTrue(boolean condition, String message) {
        testCount ++;
        message = processMessage(message);
        if (!condition) {
            OUT.println(RED + "Test failed" + RESET + ": Expect true, get false" + message);
        } else {
            OUT.println(BLUE + "Test passed" + RESET);
            passCount ++;
        }
    }
    /**
     * Assert that a condition is false.
     * @param condition the condition to check
     * @param message the message to display on failure
     */
    public static void assertFalse(boolean condition, String message) {
        testCount ++;
        message = processMessage(message);
        if (condition) {
            OUT.println(RED + "Test failed" + RESET + ": Expect false, get true" + message);
        } else {
            OUT.println(BLUE + "Test passed" + RESET);
            passCount ++;
        }
    }
    /**
     * Assert that two objects are equal, handling nulls.
     * @param expected the expected value
     * @param actual the actual value
     * @param message the message to display on failure
     */
    public static void assertEquals(Object expected, Object actual, String message) {
        testCount ++;
        message = processMessage(message);
        if (!compare(expected, actual)) {
            OUT.print(RED + "Test failed" + RESET + ": Expect ");
            print(expected);
            OUT.print(", get ");
            print(actual);
            OUT.println(", " + message);
        } else {
            OUT.println(BLUE + "Test passed" + RESET);
            passCount ++;
        }
    }
    /**
     * Assert that a condition is true.
     * @param condition the condition to check
     * @param message the message to display on failure
     */
    public static void assertTrue(BooleanSupplier condition, String message) {
        try {
            assertTrue(condition.getAsBoolean(), message);
        } catch (Exception e) {
            testCount ++;
            OUT.println(RED + "Test failed" + RESET + ": Expect true, get exception " + e.getClass().getName());
            e.printStackTrace(OUT);
            message = message.strip();
            if (message.startsWith(",") || message.startsWith(";")) {
                message = message.substring(1);
            }
            OUT.println(message.strip());
        }
    }
    /**
     * Assert that a condition is false.
     * @param condition the condition to check
     * @param message the message to display on failure
     */
    public static void assertFalse(BooleanSupplier condition, String message) {
        try {
            assertFalse(condition.getAsBoolean(), message);
        } catch (Exception e) {
            testCount ++;
            OUT.println(RED + "Test failed" + RESET + ": Expect false, get exception " + e.getClass().getName());
            e.printStackTrace(OUT);
            message = message.strip();
            if (message.startsWith(",") || message.startsWith(";")) {
                message = message.substring(1);
            }
            OUT.println(message.strip());
        }
    }
    /**
     * Assert that two objects are equal, handling nulls.
     * @param expected the expected value
     * @param actual the actual value
     * @param message the message to display on failure
     */
    public static void assertEquals(Supplier<?> expected, Supplier<?> actual, String message) {
        Object exp = null;
        Object act = null;
        try {
            exp = expected.get();
        } catch (Exception e) {
            OUT.println(RED + "Test failed" + RESET + ": Expect expected value, get exception " + e.getClass().getName());
            e.printStackTrace(OUT);
            message = message.strip();
            if (message.startsWith(",") || message.startsWith(";")) {
                message = message.substring(1);
            }
            OUT.println(message.strip());
            testCount ++;
            return;
        }
        try {
            act = actual.get();
        } catch (Exception e) {
            OUT.println(RED + "Test failed" + RESET + ": Expect actual value, get exception " + e.getClass().getName());
            e.printStackTrace(OUT);
            message = message.strip();
            if (message.startsWith(",") || message.startsWith(";")) {
                message = message.substring(1);
            }
            OUT.println(message.strip());
            testCount ++;
            return;
        }
        assertEquals(exp, act, message);
    }
    /**
     * Assert that an object is null.
     * @param obj the object to check
     * @param message the message to display on failure
     */
    public static void assertNull(Object obj, String message) {
        testCount ++;
        message = processMessage(message);
        if (obj != null) {
            OUT.print(RED + "Test failed" + RESET + ": Expect null, get ");
            print(obj);
            OUT.println(message);
        } else {
            OUT.println(BLUE + "Test passed" + RESET);
            passCount ++;
        }
    }
    /**
     * Assert that an object is not null.
     * @param obj the object to check
     * @param message the message to display on failure
     */
    public static void assertNotNull(Object obj, String message) {
        testCount ++;
        message = processMessage(message);
        if (obj == null) {
            OUT.println(RED + "Test failed" + RESET + ": Expect not null, get null" + message);
        } else {
            OUT.println(BLUE + "Test passed" + RESET);
            passCount ++;
        }
    }
    /**
     * Assert that an object is null.
     * @param obj the object to check
     * @param message the message to display on failure
     */
    public static void assertNull(Supplier<?> obj, String message) {
        Object o = null;
        try {
            o = obj.get();
        } catch (Exception e) {
            testCount ++;
            OUT.println(RED + "Test failed" + RESET + ": Expect null, get exception " + e.getClass().getName());
            e.printStackTrace(OUT);
            message = message.strip();
            if (message.startsWith(",") || message.startsWith(";")) {
                message = message.substring(1);
            }
            OUT.println(message.strip());
        }
        assertNull(o, message);
    }
    /**
     * Assert that an object is not null.
     * @param obj the object to check
     * @param message the message to display on failure
     */
    public static void assertNotNull(Supplier<?> obj, String message) {
        Object o = null;
        try {
            o = obj.get();
        } catch (Exception e) {
            testCount ++;
            OUT.println(RED + "Test failed" + RESET + ": Expect not null, get exception " + e.getClass().getName());
            e.printStackTrace(OUT);
            OUT.println(message);
            message = message.strip();
            if (message.startsWith(",") || message.startsWith(";")) {
                message = message.substring(1);
            }
            OUT.println(message.strip());
        }
        assertNotNull(o, message);
    }
    /**
     * Assert that a runnable throws an exception of the expected type.
     * @param runnable the runnable to execute
     * @param expectedException the expected exception class
     * @param message the message to display on failure
     */
    public static void assertExcept(Runnable runnable, Class<? extends Exception> expectedException, String message) {
        message = processMessage(message);
        testCount ++;
        try {
            runnable.run();
            OUT.println(RED + "Test failed" + RESET + ": Expect exception " + expectedException.getName() + ", get no exception" + message);
        } catch (Exception e) {
            if (expectedException.isInstance(e)) {
                OUT.println(BLUE + "Test passed" + RESET);
                passCount ++;
            } else {
                OUT.println(RED + "Test failed" + RESET + ": Expect exception " + expectedException.getName() + ", get exception " + e.getClass().getName() + message);
                e.printStackTrace(OUT);
            }
        }
    }
    /**
     * Assert that a runnable throws an exception of the expected type.
     * @param runnable the runnable to execute
     * @param expectedException the expected exception class
     */
    public static void assertExcept(Runnable runnable, Class<? extends Exception> expectedException) {
        assertExcept(runnable, expectedException, "");
    }
    /**
     * Assert that a runnable throws an exception.
     * @param runnable the runnable to execute
     * @param message the message to display on failure
     */
    public static void assertExcept(Runnable runnable, String message) {
        assertExcept(runnable, Exception.class, message);
    }
    /**
     * Assert that a runnable throws an exception.
     * @param runnable the runnable to execute
     */
    public static void assertExcept(Runnable runnable) {
        assertExcept(runnable, Exception.class, "");
    }

    /**
     * Assert that a condition is true.
     * @param condition the condition to check
    */
    public static void assertTrue(boolean condition) {
        assertTrue(condition, "");
    }
    /**
     * Assert that a condition is false.
     * @param condition the condition to check
     */
    public static void assertFalse(boolean condition) {
        assertFalse(condition, "");
    }
    /**
     * Assert that two objects are equal, handling nulls.
     * @param expected the expected value
     * @param actual the actual value
     */
    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "");
    }
    /**
     * Assert that a condition is true.
     * @param condition the condition to check
     */
    public static void assertTrue(BooleanSupplier condition) {
        assertTrue(condition, "");
    }
    /**
     * Assert that a condition is false.
     * @param condition the condition to check
     */
    public static void assertFalse(BooleanSupplier condition) {
        assertFalse(condition, "");
    }
    /**
     * Assert that two objects are equal, handling nulls.
     * @param expected the expected value
     * @param actual the actual value
     */
    public static void assertEquals(Supplier<?> expected, Supplier<?> actual) {
        assertEquals(expected, actual, "");
    }
    /**
     * Assert that an object is null.
     * @param obj the object to check
     */
    public static void assertNull(Object obj) {
        assertNull(obj, "");
    }
    /**
     * Assert that an object is not null.
     * @param obj the object to check
     */
    public static void assertNotNull(Object obj) {
        assertNotNull(obj, "");
    }

    /**
     * Compare two objects for equality, handling nulls.
     * @param a the first object
     * @param b the second object
     * @return true if both are null or a.equals(b), false otherwise
     */
    private static boolean compare(Object a, Object b) {
        if (a == b) {
            return true;
        }
        return a != null && a.equals(b);
    }
    /**
     * Print an object to standard output, handling nulls and arrays.
     * @param o the object to print
     */
    private static void print(Object o) {
        if (o == null) {
            OUT.print("null");
        } else if (o.getClass().isArray()) {
            // Handle array
            // Handle primitive arrays
            if (o instanceof int[] arr) {
                OUT.print("[");
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) {
                        OUT.print(", ");
                    }
                    OUT.print(arr[i]);
                }
                OUT.print("]");
                return;
            } else if (o instanceof long[] arr) {
                OUT.print("[");
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) {
                        OUT.print(", ");
                    }
                    OUT.print(arr[i]);
                }
                OUT.print("]");
                return;
            } else if (o instanceof double[] arr) {
                OUT.print("[");
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) {
                        OUT.print(", ");
                    }
                    OUT.print(arr[i]);
                }
                OUT.print("]");
                return;
            } else if (o instanceof float[] arr) {
                OUT.print("[");
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) {
                        OUT.print(", ");
                    }
                    OUT.print(arr[i]);
                }
                OUT.print("]");
                return;
            } else if (o instanceof boolean[] arr) {
                OUT.print("[");
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) {
                        OUT.print(", ");
                    }
                    OUT.print(arr[i]);
                }
                OUT.print("]");
                return;
            } else if (o instanceof char[] arr) {
                OUT.print("[");
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) {
                        OUT.print(", ");
                    }
                    OUT.print(arr[i]);
                }
                OUT.print("]");
                return;
            } else if (o instanceof byte[] arr) {
                OUT.print("[");
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) {
                        OUT.print(", ");
                    }
                    OUT.print(arr[i]);
                }
                OUT.print("]");
                return;
            } else if (o instanceof short[] arr) {
                OUT.print("[");
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) {
                        OUT.print(", ");
                    }
                    OUT.print(arr[i]);
                }
                OUT.print("]");
                return;
            }
            // Handle object arrays
            Object[] arr = (Object[]) o;
            OUT.print("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) {
                    OUT.print(", ");
                }
                print(arr[i]);
            }
            OUT.print("]");
        } else {
            OUT.print(o.toString());
        }
    }
    /**
     * Process a message by trimming whitespace and ensuring it starts with a comma or semicolon.
     * @param message the message to process
     * @return the processed message
     */
    private static String processMessage(String message) {
        if (message == null) {
            return "";
        }
        message = message.trim();
        if (!message.isEmpty()) {
            if (!message.startsWith(",") && ! message.startsWith(";")) {
                message = ", " + message;
            }
        }
        return message;
    }
    /**
     * Print a summary of the test results.
     */
    public static void printSummary() {
        OUT.println("Test summary: " + passCount + "/" + testCount + " tests passed");
        if (testCount != 0) {
            OUT.printf("Pass rate: %.2f%%\n", (double) passCount / testCount * 100);
        }
    }
    /**
     * Clear the test and pass counts. This can be used to reset the counts between different test suites.
     */
    public static void clearCount() {
        testCount = 0;
        passCount = 0;
    }
    /**
     * Run multiple tests and print a summary at the end.
     * @param tests the tests to run
     */
    public static void runAll(Runnable... tests) {
        clearCount();
        System.out.println("Running " + tests.length + " tests...");
        for (Runnable test : tests) {
            OUT.print("Test " + (testCount + 1) + ": ");
            try {
                test.run();
            } catch (Exception e) {
                testCount ++;
                OUT.println(RED + "Test failed" + RESET + ": Uncaught exception " + e.getClass().getName());
                e.printStackTrace(OUT);
            }
        }
        printSummary();
    }
}
