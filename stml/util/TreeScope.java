package stml.util;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A tree-based implementation of the {@link Scope} interface.
 * <p>
 * {@code TreeScope} represents a hierarchical structure of nested scopes, where
 * each scope node contains its own variable table and a reference to its parent scope.
 * Scopes form a chain (a tree with a single active path) that can be navigated
 * using {@link #make()}.
 * <p>
 * Each {@code TreeScope} maintains:
 * <ul>
 *   <li>A {@link Map} of variable names to values in the current scope.</li>
 *   <li>A reference to its {@code parent} scope (or {@code null} if global).</li>
 * </ul>
 * <p>
 * This implementation does not enforce type constraints, allowing any variable
 * to hold values of any type, including {@code null} to represent uninitialized variables.
 * This means all names are unique, and it is not possible to have multiple variables
 * with the same name in the same scope.
 * <p>
 * The implementation is not thread-safe; concurrent access must be externally synchronized.
 * <p>
 * Variable resolution and assignments traverse upward through the parent chain,
 * ensuring lexical-style variable visibility consistent with most programming languages.
 * @see Scope
 */
public class TreeScope implements Scope {
    private final Scope parent;
    private final Map<String, Object> variables;
    /**
     * Constructs a new child {@code TreeScope} with the specified parent.
     * This constructor is private and used internally by {@link #make()}.
     *
     * @param parent the parent scope of this new scope.
     */
    private TreeScope(Scope parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
    }
    /**
     * Constructs a new global {@code TreeScope} with no parent.
     * <p>
     * This instance starts as both the global and current scope.
     * The returned instance has property that calling {@link #isGlobal()} will return {@code true}.
     */
    public TreeScope() {
        this.parent = null;
        this.variables = new HashMap<>();
    }
    /** {@inheritDoc} */
    @Override
    public Scope parent() {
        return parent;
    }
    /**
     * {@inheritDoc}
     * <p>
     * If the child scope modify variables in the parent scope, the changes will be visible in this scope.
     */
    @Override
    public Scope make() {
        return new TreeScope(this);
    }
    /**
     * {@inheritDoc}
     * @implNote The local variables is implemented with a {@code HashMap<String, Object>}, in which
     * each unique variable name maps to its current value. This implementation does not enforce type constraints,
     * allowing any variable to hold values of any type, including {@code null} to represent uninitialized variables.
     */
    @Override
    public boolean containsLocal(String name) {
        return variables.containsKey(name);
    }
    /**
     * {@inheritDoc}
     * @implNote The implementation of this method in {@code TreeScope} adds the variable to the local
     * {@code variables} map with an initial value of {@code null} to indicate it is uninitialized.
     * If the variable already exists in the current scope, this call has no effect.
     */
    @Override
    public void define(String name) {
        if (!containsLocal(name)) {
            variables.put(name, null); // Initialize with null to indicate uninitialized variable
        }
    }
    /**
     * {@inheritDoc}
     * @implNote The implementation of this method in {@code TreeScope} first checks if the variable
     * is defined in the current scope using {@link #containsLocal(String)}. If found, it updates the value in the local
     * {@code variables} map. If not found locally, it recursively calls {@code assign} on the parent scope.
     * If the variable is not found in any scope, it throws a {@code NoSuchElementException}.
     */
    @Override
    public void assign(String name, Object value) {
        if (!isValidName(name)) {
            throw new NoSuchElementException("Variable with invalid name " + name + " is not present in current or ancestor scopes.");
        }
        if (containsLocal(name)) {
            variables.put(name, value);
        } else if (parent != null && parent.contains(name)) {
            parent.assign(name, value);
        } else {
            throw new NoSuchElementException("Variable '" + name + "' not found in current or ancestor scopes.");
        }
    }
    /**
     * {@inheritDoc}
     * @return {@code true} if any variables were removed, {@code false} otherwise.
     */
    @Override
    public boolean destroyLocal(String name) {
        if (isValidName(name)) {
            if (containsLocal(name)) {
                variables.remove(name);
                return true;
            }
        }
        return false;
    }
    /**
     * {@inheritDoc}
     * @implNote The implementation of this method in {@code TreeScope} first checks if the variable
     * is defined in the current scope using {@link #containsLocal(String)}. If found, it returns the value from the local
     * {@code variables} map. If not found locally, it recursively calls {@code lookup} on the parent scope.
     * If the variable is not found in any scope, it throws a {@code NoSuchElementException}.
     * <p>
     * Although this implementation does not enforce type constraints, because type information is not lost, the returned
     * value can be safely cast to the last type {@link #assign(String, Object)} was called with, or to the type
     * expected by the caller. If the variable was defined but never assigned a value, it returns {@code null}.
     */
    @Override
    public Object lookup(String name) {
        if (!isValidName(name)) {
            throw new NoSuchElementException("Variable with invalid name " + name + " is not present in current or ancestor scopes.");
        }
        if (containsLocal(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.lookup(name);
        } else {
            throw new NoSuchElementException("Variable '" + name + "' not found in current or ancestor scopes.");
        }
    }
    /**
     * Validates that the given name is a valid variable name.
     * A valid name, in this definition, is a non-null, non-blank string, for the convenience of use in maps.
     * @param name the variable name to validate.
     * @return {@code true} if the name is valid, {@code false} otherwise.
     */
    private static boolean isValidName(String name) {
        return name != null && !name.isBlank();
    }
    /**
     * Return a string representation of the scope for debugging purposes.
     * This includes the variables defined in the current scope and a recursive
     * representation of the parent scope.
     * <p>
     * The format is:
     * <pre>
     * TreeScope{variables={var1=value1, var2=value2, ...}, parent=TypedScope{...}}
     * </pre>
     * If the scope is global (has no parent), it indicates "global" instead of
     * showing a parent scope.
     * @return a string representation of the scope.
     */
    @Override
    public String toString() {
        return "TreeScope{variables=" + variables.toString() + ", " + (parent == null ? "global" : "parent=" + parent.toString()) + '}';
    }
}
