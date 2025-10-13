package stml.util;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * A stack-based implementation of the {@link Scope} interface.
 * <p>
 * {@code StackScope} represents a hierarchical structure of nested scopes using a stack of maps,
 * where each map corresponds to a scope level. The current scope is represented by the top map
 * on the stack, and each scope can have its own set of variables while still being able to
 * access variables from its ancestor scopes.
 * <p>
 * Each {@code StackScope} maintains:
 * <ul>
 *   <li>A {@link Stack} of {@link Map} objects, where each map holds variable names to values for a scope level.</li>
 *   <li>An index indicating the current scope level in the stack.</li>
 * </ul>
 * <p>
 * The stack implementation is strictly linear, meaning that each scope level is represented
 * by a single map in the stack. When a new scope is created, a new map is pushed onto the stack.
 * When exiting a scope, the top map is popped off the stack, reverting to the previous scope level.
 * When a new scope is created from an existing one, any scopes that were children of the existing
 * scope are invalidated, as the stack is truncated to the current scope level before pushing
 * the new scope's map. This ensures that the scope hierarchy remains consistent and prevents dangling references
 * to invalidated scopes.
 * <p>
 * Compare to implementations with a full tree structure, this stack-based approach
 * is more memory efficient and simpler to manage, as it avoids the overhead of maintaining
 * multiple references and pointers between scope nodes. This implementation also have faster hierarchy methods.
 * However, it does mean that once a scope is exited, it cannot be re-entered, as the stack only maintains a
 * single active path of scopes.
 * <p>
 * This implementation does not enforce type constraints, allowing any variable
 * to hold values of any type, including {@code null} to represent uninitialized variables.
 * This means all names are unique, and it is not possible to have multiple variables
 * with the same name in the same scope.
 * <p>
 * The implementation is not thread-safe; concurrent access must be externally synchronized.
 * <p>
 * Variable resolution and assignments traverse upward through the stack,
 * ensuring lexical-style variable visibility consistent with most programming languages.
 *
 * @see Scope
 */
public class StackScope implements Scope {
    private final Stack<Map<String , Object>> stack;
    private final Map<String, Object> map;
    private final int index;
    /**
     * Constructs a new global {@code StackScope} with no parent.
     * <p>
     * This instance starts as both the global and current scope.
     */
    public StackScope() {
        this.stack = new Stack<>();
        this.map = new java.util.HashMap<>();
        this.stack.push(map);
        this.index = 0;
    }
    /**
     * Private constructor to create a new child scope from a parent scope.
     * <p>
     * The new scope shares the same stack as the parent, but has its own map
     * at the next index in the stack.
     * @param parent the parent scope from which to create the new child scope.
     */
    private StackScope(StackScope parent) {
        this.stack = parent.stack; // Exact reference
        int s = this.stack.size();
        if (s > parent.index + 1) {
            this.stack.setSize(parent.index + 1);
        }
        this.map = new java.util.HashMap<>();
        this.stack.push(map);
        this.index = parent.index + 1;
    }
    /**
     * Private constructor to create a scope at a specific index in the stack.
     * <p>
     * Used internally to create parent scopes without modifying the stack.
     * @param stack the stack of scope maps.
     * @param index the index of the map in the stack for this scope.
     */
    private StackScope(Stack<Map<String, Object>> stack, int index) {
        this.stack = stack;
        this.map = stack.get(index);
        this.index = index;
    }
    /**
     * Validates that this scope is still valid within its stack context.
     * <p>
     * A scope becomes invalid if the stack has been modified in a way that
     * removes or replaces the map at this scope's index.
     * <p>
     * Most methods should call this method at the start to ensure the scope
     * is still valid before proceeding.
     *
     * @return {@code true} if the scope is valid, {@code false} otherwise.
     */
    public boolean isValid() {
        return index >= 0 && index < stack.size() && stack.get(index) == map;
    }
    /** {@inheritDoc} */
    @Override
    public boolean isGlobal() {
        return index == 0;
    }
    /** {@inheritDoc} */
    @Override
    public Scope parent() {
        if (isGlobal()) return null;
        return new StackScope(stack, index - 1);
    }
    /**
     * Enters a new nested (child) scope under the current scope.
     * <p>
     * The newly entered scope has the current scope as its parent.
     * Can define variables visible only within itself and its descendants.
     * <p>
     * The method should always make a new scope, so multiple calls of this
     * method on this method will return different scopes.
     * <p>
     * Calling {@link #parent()} on the returned scope should return a view of the parent scope.
     *
     * @implSpec The implementation of this method is responsible for
     * ensuring the new scope is properly linked to its parent (the current scope).
     * The method may call private constructors to avoid arbitrary creations of scopes.
     * If creation of new scope necessitate invalidation of existing children, the invalidation
     * should be immediately reflected on hierarchy related methods such as {@link #contains(Scope)}
     * and {@link #isChildOf(Scope)}.
     * @return the new child scope that becomes current.
     */
    @Override
    public Scope make() {
        return new StackScope(this);
    }
    /**
     * {@inheritDoc}
     *
     * @param name the variable name to check.
     * @return {@code true} if the variable exists in the current scope or any ancestor,
     *         {@code false} otherwise.
     * @implNote This implementation checks each scope in the stack from the current index
     * down to the global scope for the presence of the variable name. If the scope is invalidated,
     * it returns {@code false} immediately, as an invalid scope cannot contain any variables.
     */
    @Override
    public boolean contains(String name) {
        if (!isValid()) return false; // Invalid scope cannot contain anything
        for (int i = index; i >= 0; i--) {
            if (stack.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }
    /**
     * {@inheritDoc}
     * @implNote The local variables is implemented with a {@code HashMap<String, Object>}, in which
     * each unique variable name maps to its current value. This implementation does not enforce type constraints,
     * allowing any variable to hold values of any type, including {@code null} to represent uninitialized variables.
     */
    @Override
    public boolean containsLocal(String name) {
        return stack.get(index).containsKey(name);
    }
    /**
     * {@inheritDoc}
     * @param scope the scope to test for containment.
     * @return {@code true} if this scope contains the given scope in its ancestry, {@code false} otherwise.
     * @implSpec This implementation checks if the given scope is the same instance as this scope
     * or any of its parent scopes by traversing up the stack. If the scope is invalidated,
     * it returns {@code false} immediately, as an invalid scope cannot contain any other scopes.
     */
    @Override
    public boolean contains(Scope scope) {
        if (!isValid()) return false; // Invalid scope cannot contain anything
        if (!(scope instanceof StackScope s)) return false;
        return s.isValid() && s.stack == this.stack && s.index <= this.index;
    }
    /**
     * {@inheritDoc}
     * @param scope the scope to test as a potential ancestor.
     * @return {@code true} if this scope is a descendant of the given scope, {@code false} otherwise.
     * @implSpec This implementation checks if the given scope is the same instance as this scope
     * or any of its parent scopes by traversing up the stack. If the scope is invalidated,
     * it returns {@code false} immediately, as an invalid scope cannot be a child of any scope.
     */
    @Override
    public boolean isChildOf(Scope scope) {
        if (!isValid()) return false; // Invalid scope cannot be child of anything
        if (!(scope instanceof StackScope s)) return false;
        return s.isValid() && s.stack == this.stack && s.index < this.index;
    }
    /**
     * {@inheritDoc}
     * @implNote The implementation of this method in {@code StackScope} adds the variable to the local
     * map at the current index in the stack with an initial value of {@code null} to indicate it is uninitialized.
     * If the scope is invalidated, it throws an {@code IllegalStateException}, as definitions
     * cannot be made in an invalid scope.
     */
    @Override
    public void define(String name) {
        if (!isValid()) {
            throw new IllegalStateException("Cannot define variable in an invalidated scope.");
        }
        stack.get(index).putIfAbsent(name, null); // Initialize with null to indicate uninitialized variable
    }
    /**
     * {@inheritDoc}
     * @param name  the name of the variable to assign.
     * @param value the value to assign.
     * @throws java.util.NoSuchElementException if no variable with {@code name}
     *         exists in the current scope or any ancestor.
     * @implNote This implementation searches each scope in the stack from the current index
     * down to the global scope for the presence of the variable name. If found, it updates
     * the value in that scope's map. If not found in any scope, it throws a {@code NoSuchElementException}.
     * If the scope is invalidated, it throws an {@code IllegalStateException}, as assignments
     * cannot be made in an invalid scope.
     */
    @Override
    public void assign(String name, Object value) {
        if (!isValid()) {
            throw new IllegalStateException("Cannot assign variable in an invalidated scope.");
        }
        for (int i = index; i >= 0; i--) {
            Map<String, Object> currentMap = stack.get(i);
            if (currentMap.containsKey(name)) {
                currentMap.put(name, value);
                return;
            }
        }
        throw new NoSuchElementException("Variable '" + name + "' not defined in current or ancestor scopes.");
    }
    /**
     * {@inheritDoc}
     * @param name the variable name to remove.
     * @implNote This implementation removes the variable from the map at the current index in the stack only.
     * If the scope is invalidated, it throws an {@code IllegalStateException}, as destruction cannot be made in an invalid scope.
     * @return {@code true} if the variable was found and removed, {@code false} otherwise.
     */
    @Override
    public boolean destroyLocal(String name) {
        if (!isValid()) {
            throw new IllegalStateException("Cannot destroy variable in an invalidated scope.");
        }
        return stack.get(index).remove(name) != null;
    }
    /**
     * {@inheritDoc}
     * @param name the name of the variable to look up.
     * @return the value of the variable.
     * @throws java.util.NoSuchElementException if the variable is not found in the current
     *         or any ancestor scope.
     * @implNote This implementation searches each scope in the stack from the current index
     * down to the global scope for the presence of the variable name. If found, it returns
     * the value from that scope's map. If not found in any scope, it throws a {@code NoSuchElementException}.
     * If the scope is invalidated, it throws an {@code IllegalStateException}, as lookups
     * cannot be made in an invalid scope.
     */
    @Override
    public Object lookup(String name) {
        if (!isValid()) {
            throw new IllegalStateException("Cannot lookup variable in an invalidated scope.");
        }
        for (int i = index; i >= 0; i--) {
            Map<String, Object> currentMap = stack.get(i);
            if (currentMap.containsKey(name)) {
                return currentMap.get(name);
            }
        }
        throw new NoSuchElementException("Variable '" + name + "' not defined in current or ancestor scopes.");
    }
    /**
     * Returns a string representation of the current scope for debugging purposes.
     * <p>
     * The string includes the scope's index in the stack, the number of variables
     * defined in the current scope, and a listing of the variable names and their
     * current values.
     * <p>
     * If the scope is invalidated, the string indicates that the scope is invalid.
     * @return a string representation of the current scope.
     */
    @Override
    public String toString() {
        return "StackScope{index=" + index + ", stackSize=" + stack.size() + ", variables=" + map + "}";
    }
}
