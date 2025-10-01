/**
 * This package contains functional interfaces and utility classes for functional programming in Java.
 * This is an extension of the standard `java.util.function` package, providing additional
 * functional interfaces and utilities to facilitate functional programming paradigms.
 * <p>
 * All interfaces in this package are designed to be used with lambda expressions and method references.
 * All consumers have a {@code accept} method, all suppliers have a {@code get} method,
 * all predicates have a {@code test} method, and all functions have an {@code apply} method.
 * <p>
 * The {@code Failable} interfaces are designed to allow for checked exceptions in functional programming.
 * They extend the standard functional interfaces and add a {@code throws Exception} clause to their methods.
 * <p>
 * For functions with multiple arguments types and return types, the last type parameter is the return type.
 * <p>
 * Natively, this package does not support returning multiple values from functions. To do so, consider using
 * The {@link util.tuple} package to create tuples that can hold multiple values.
 */

package util.function;