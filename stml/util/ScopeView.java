package stml.util;

/**
 * A utility class that manages a view of nested scopes using a {@link Scope} implementation.
 * <p>
 * {@code ScopeView} provides methods to navigate and manipulate the current scope,
 * including entering and exiting scopes, defining and assigning variables, and
 * looking up variable values. It maintains a reference to the current scope
 * and allows for easy scope management in a hierarchical manner.
 * <p>
 * This class is designed to work with any implementation of the {@link Scope} interface,
 * allowing for flexibility in how scopes are represented and managed.
 * <p>
 * The initial scope is a global scope, and new scopes can be created as children
 * of the current scope. The current scope can be changed by entering or exiting scopes.
 * All operations on variables are performed in the context of the current scope,
 * with support for variable shadowing and scope-based visibility.
 * <p>
 * This implementation is thread-safe, with synchronized methods to ensure
 * consistent access to the current scope.
 *
 * @see Scope
 */
public class ScopeView {
    private Scope current;
    private final Scope root; // Also used as lock
    /**
     * Create a new ScopeView instance with a global scope.
     * The initial current scope is the global scope.
     */
    public ScopeView() {
        this.root = new StackScope(); // We do not even need a full tree here
        synchronized (root) {
            this.current = root;
        }
    }
    /**
     * Create a new ScopeView instance with the provided global scope.
     * The initial current scope is the provided global scope.
     * <p>
     * Please avoid directly modify the passed in scope after passing it to this constructor.
     *
     * @param global The global scope to use as the root scope.
     * @throws IllegalArgumentException if the provided scope is not a global scope.
     */
    public ScopeView(Scope global) {
        if (global == null || !global.isGlobal()) {
            throw new IllegalArgumentException("The provided scope must be a global scope.");
        }
        this.root = global;
        synchronized (root) {
            this.current = root;
        }
    }
    /**
     * Check if the current scope is the global scope.
     * This method returns {@code true} if the current scope
     * is the root scope (global scope); otherwise, it returns {@code false}.
     *
     * @return {@code true} if the current scope is the global scope;
     *         {@code false} otherwise.
     * @see Scope#isGlobal()
     */
    public boolean isGlobal() {
        synchronized (root) {
            return current.isGlobal();
        }
    }
    /**
     * Enter a new child scope of the current scope.
     * This method creates a new child scope of the current scope
     * and sets it as the current scope. All local variables defined
     * in the current scope remain accessible in the new child scope.
     */
    public void enter() {
        synchronized (root) {
            current = current.make();
        }
    }
    /**
     * Exit the current scope and return to the parent scope.
     * If the current scope is the global scope, this method has no effect.
     * This method updates the current scope to its parent scope,
     * effectively discarding any local variables defined in the
     * current scope.
     */
    public void exit() {
        synchronized (root) {
            if (!current.isGlobal()) {
                current = current.parent();
            }
        }
    }
    /**
     * Re-enter the current scope by creating a new child scope.
     * This method creates a new child scope of the current scope
     * and sets it as the current scope. If the current scope is
     * the global scope, this method has no effect. All local
     * variables defined in the current scope are discarded.
     */
    public void reenter() {
        synchronized (root) {
            if (!current.isGlobal()) {
                current = current.parent().make();
            }
        }
    }
    /**
     * Reset the current scope to the global scope.
     * This method sets the current scope to the root scope,
     * effectively discarding any nested scopes that were
     * created after the initial global scope.
     */
    public void toRoot() {
        synchronized (root) {
            current = root;
        }
    }
    /**
     * Check if a variable is defined in the current scope chain.
     * This method searches for the variable starting from the current scope
     * and moving up through parent scopes until it finds the variable or
     * reaches the global scope.
     *
     * @param name The name of the variable to check.
     * @return {@code true} if the variable is defined in any scope in the
     *         current scope chain; {@code false} otherwise.
     * @see Scope#contains(String)
     */
    public boolean contains(String name){
        synchronized (root) {
            return current.contains(name);
        }
    }
    /**
     * Check if a variable is defined in the current scope only.
     * This method does not search parent scopes; it only checks the
     * current scope.
     *
     * @param name The name of the variable to check.
     * @return {@code true} if the variable is defined in the current scope;
     *         {@code false} otherwise.
     * @see Scope#containsLocal(String)
     */
    public boolean containsLocal(String name){
        synchronized (root) {
            return current.containsLocal(name);
        }
    }
    /**
     * Define a new variable by its name in the current scope only.
     * This method does not search parent scopes; it only affects the
     * current scope.
     *
     * @param name The name of the variable to define.
     * @see Scope#define(String)
     */
    public void define(String name) {
        synchronized (root) {
            current.define(name);
        }
    }
    /**
     * Assign a value to a variable by its name in the current scope chain.
     * This method searches for the variable starting from the current scope
     * and moving up through parent scopes until it finds the variable to
     * assign the value. If the variable does not exist in any scope, it is
     * created in the current scope.
     *
     * @param name  The name of the variable to assign.
     * @param value The value to assign to the variable.
     * @see Scope#assign(String, Object)
     * @throws java.util.NoSuchElementException if the variable is not found in any scope and cannot be created.
     */
    public void assign(String name, Object value) {
        synchronized (root) {
            current.assign(name, value);
        }
    }
    /**
     * Assign a value to a variable by its name in the current scope only.
     * If the variable does not exist in the current scope, an exception is thrown.
     * This method does not search parent scopes; it only affects the
     * current scope.
     *
     * @param name  The name of the variable to assign.
     * @param value The value to assign to the variable.
     * @see Scope#lazyAssign(String, Object)
     * @throws java.util.NoSuchElementException if the variable is not found in the current scope.
     */
    public void assignLocal(String name, Object value) {
        synchronized (root) {
            if (current.containsLocal(name)) {
                current.assign(name, value);
            } else {
                throw new java.util.NoSuchElementException("Variable '" + name + "' not found in the current scope.");
            }
        }
    }
    /**
     * Destroy a variable by its name in the current scope only.
     * This method does not search parent scopes; it only affects the
     * current scope.
     *
     * @param name The name of the variable to destroy.
     * @return {@code true} if the variable was found and destroyed in the
     *         current scope; {@code false} if the variable was not found
     *         in the current scope.
     * @see Scope#destroyLocal(String)
     */
    public boolean destroy(String name) {
        synchronized (root) {
            return current.destroyLocal(name);
        }
    }
    /**
     * Look up the value of a variable by its name in the current scope chain.
     * This method searches for the variable starting from the current scope
     * and moving up through parent scopes until it finds the variable or
     * reaches the global scope.
     *
     * @param name The name of the variable to look up.
     * @return The value associated with the variable name, or {@code null} if
     *         the variable is not found in any scope.
     * @see Scope#lookup(String)
     * @throws java.util.NoSuchElementException if the variable is not found in any scope.
     */
    public Object lookup(String name) {
        synchronized (root) {
            return current.lookup(name);
        }
    }
}
