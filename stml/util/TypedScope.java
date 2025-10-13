package stml.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A type-aware implementation of the {@link Scope} interface.
 * <p>
 * {@code TypedScope} represents a hierarchical structure of nested scopes, where
 * each scope node contains its own variable table and a reference to its parent scope.
 * Scopes form a chain (a tree with a single active path) that can be navigated
 * using {@link #make()}.
 * <p>
 * Each {@code TypedScope} maintains:
 * <ul>
 *   <li>A {@link Map} of variable names and their associated types to values in the current scope.</li>
 *   <li>A reference to its {@code parent} scope (or {@code null} if global).</li>
 * </ul>
 * <p>
 * This implementation enforces type constraints, allowing multiple variables
 * with the same name but different types to coexist in the same scope.
 * Each variable is uniquely identified by its name and type pair.
 * Variables can hold values of their declared type or {@code null} to represent uninitialized variables.
 * <p>
 * The implementation is not thread-safe; concurrent access must be externally synchronized.
 * <p>
 * Variable resolution and assignments traverse upward through the parent chain,
 * ensuring lexical-style variable visibility consistent with most programming languages.
 * @see Scope
 */
public class TypedScope implements Scope {
    // type pair class
    /**
     * A simple record to hold a variable name and its type.
     *
     * @param name the variable name
     * @param type the variable type
     */
    private record TypePair(String name, Class<?> type) {
        /**
         * Override equals to use name and type
         *
         * @param obj the object to compare
         * @return true if the name and type are equal
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TypePair other = (TypePair) obj;
            return name.equals(other.name) && type.equals(other.type);
        }
    }
    private final Scope parent;
    private final Map<TypePair, Object> variables;
    /**
     * Constructs a new child {@code TypedScope} with the specified parent.
     * This constructor is private and used internally by {@link #make()}.
     *
     * @param parent the parent scope of this new scope.
     */
    private TypedScope (Scope parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
    }
    /**
     * Constructs a new global {@code TypedScope} with no parent.
     * <p>
     * This instance starts as both the global and current scope.
     * The returned instance has property that calling {@link #isGlobal()} will return {@code true}.
     */
    public TypedScope() {
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
        return new TypedScope(this);
    }
    /**
     * {@inheritDoc}
     * @implNote The local variables is implemented with a {@code HashMap<String, Object>}, in which
     * each unique variable name maps to its current value. This implementation enforces type constraints,
     * so if any variable with the given name exists in the current scope, regardless of its type,
     * this method returns {@code true}. Otherwise, it returns {@code false}.
     */
    @Override
    public boolean containsLocal(String name) {
        // Search in variable map
        for (TypePair pair : variables.keySet()) {
            if (pair.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks whether a variable with the specified name and type is strictly defined
     * in the current scope.
     *
     * @param clazz the expected class type of the variable.
     * @param name the variable name to check.
     * @return {@code true} if the variable with the given name and type exists in the current scope,
     *         {@code false} otherwise.
     */
    public boolean containsLocal(Class<?> clazz, String name) {
        return variables.containsKey(new TypePair(name, clazz));
    }
    /**
     * {@inheritDoc}
     * <p>
     * It is recommended to use {@link #define(Class, String)} to define variables with specific types,
     * as this method does not actually enforce any type constraints.
     * @implNote The implementation of this method in {@code TypedScope} adds the variable to the local
     * {@code variables} map with an initial value of {@code null} to indicate it is uninitialized.
     * If the variable already exists in the current scope, this call has no effect.
     */
    @Override
    public void define(String name) {
        if (!containsLocal(name)) {
            variables.put(new TypePair(name, Object.class), null); // Initialize with null to indicate uninitialized variable
        }
    }
    /**
     * Defines a new variable with the specified name and type in the current scope.
     * <p>
     * If a variable with the same name and type exists in the <em>current</em> scope, this call has no effect.
     * If a variable with the same name exists in a <em>parent</em> scope, it is shadowed locally.
     * If a variable with the same name but different type exists in the current scope,
     * both variable can coexist, as they are treated as distinct due to their differing types.
     *
     * @param clazz the class type of the variable to define.
     * @param name  the name of the variable to define.
     * @throws IllegalArgumentException if a variable with the same name but different type
     *         already exists in the current scope.
     */
    public void define(Class<?> clazz, String name) {
        if (!containsLocal(clazz, name)) {
            variables.put(new TypePair(name, clazz), null); // Initialize with null to indicate uninitialized variable
        }
    }
    /**
     * {@inheritDoc}
     * @implNote The implementation of this method in {@code TypedScope} first checks if the variable
     * is defined in the current scope using {@link #containsLocal(String)}. If found, it updates the value in the local
     * {@code variables} map. If not found locally, it recursively calls {@code assign} on the parent scope.
     * If the variable is not found in any scope, it throws a {@code NoSuchElementException}.
     */
    @Override
    public void assign(String name, Object value) {
        // First check local scope
        for (TypePair pair : variables.keySet()) {
            if (pair.name.equals(name)) {
                // Check type compatibility
                if (pair.type.isInstance(value)) {
                    // Type matches, assign value
                    variables.put(pair, value);
                    return;
                }
            }
        }
        // Not found locally, check parent
        if (parent != null) {
            parent.assign(name, value);
            return;
        }
        throw new java.util.NoSuchElementException("Variable '" + name + "' is not defined in the current scope or any ancestor.");
    }
    /**
     * Assigns a value to an existing variable with the specified name and type
     * in the current scope or any ancestor scope.
     * <p>
     * If no variable with the specified name and type exists in the scope hierarchy,
     * an exception is thrown.
     *
     * @param clazz the expected class type of the variable.
     * @param name  the name of the variable to assign.
     * @param value the value to assign.
     * @throws IllegalArgumentException if a variable with the given name exists
     *         but its type does not match {@code clazz}.
     * @throws java.util.NoSuchElementException if no variable with {@code name}
     *         and type {@code clazz} exists in the current scope or any ancestor.
     */
    public void assign(Class<?> clazz, String name, Object value) {
        TypePair key = new TypePair(name, clazz);
        if (variables.containsKey(key)) {
            // Check type compatibility
            if (clazz.isInstance(value)) {
                // Type matches, assign value
                variables.put(key, value);
                return;
            } else {
                throw new IllegalArgumentException("Type mismatch: variable '" + name + "' expects type " + clazz.getName());
            }
        }
        // Not found locally, check parent
        if (parent != null) {
            parent.assign(name, value);
            return;
        }
        throw new java.util.NoSuchElementException("Variable '" + name + "' is not defined in the current scope or any ancestor.");
    }
    /**
     * {@inheritDoc}
     * <p>
     * This will destroy all variables with the given name, regardless of their type.
     * If no variable with the specified name exists in the current scope or any ancestor,
     * this call has no effect.
     */
    @Override
    public void destroy(String name) {
        // Remove all variables with the given name in the current scope
        variables.keySet().removeIf(pair -> pair.name.equals(name));
        // Also attempt to destroy in parent scope
        if (parent != null && parent.contains(name)) {
            parent.destroy(name);
        }
    }
    /**
     * Removes a variable with the specified name and type from the current scope only.
     * <p>
     * If no variable with the given name and type exists in the current scope,
     * this call has no effect. It does not affect variables in parent scopes.
     *
     * @param clazz the expected class type of the variable to remove.
     * @param name  the name of the variable to remove.
     */
    public void destroy(Class<?> clazz, String name) {
        TypePair key = new TypePair(name, clazz);
        variables.remove(key);
    }
    /**
     * {@inheritDoc}
     * @implNote The implementation of this method in {@code TypedScope} first checks if the variable
     * is defined in the current scope using {@link #containsLocal(String)}. If found, it returns the value from the local
     * {@code variables} map. If not found locally, it recursively calls {@code lookup} on the parent scope.
     * If the variable is not found in any scope, it throws a {@code NoSuchElementException}.
     * <p>
     * The behavior for this method is actually undefined if multiple variables with the same name
     * but different types exist in the current scope. In such cases, it will return the value
     * of an arbitrary variable with the matching name, without any guarantees about which one.
     * To avoid ambiguity, it is recommended to use {@link #lookup(Class, String)}
     * when multiple types for the same variable name may exist.
     */
    public Object lookup(String name) {
        // First check local scope
        for (TypePair pair : variables.keySet()) {
            if (pair.name.equals(name)) {
                return variables.get(pair);
            }
        }
        // Not found locally, check parent
        if (parent != null) {
            return parent.lookup(name);
        }
        throw new java.util.NoSuchElementException("Variable '" + name + "' is not defined in the current scope or any ancestor.");
    }
    /**
     * Retrieves the value of a variable with the specified name and type
     * from the current scope or one of its ancestors.
     * <p>
     * Lookup proceeds outward through the parent chain until a variable
     * with the matching name and type is found.
     *
     * @param clazz the expected class type of the variable.
     * @param name  the name of the variable to look up.
     * @return the value of the variable.
     * @throws java.util.NoSuchElementException if the variable of the correct type is not found in the current
     *         or any ancestor scope.
     */
    public Object lookup(Class<?> clazz, String name) {
        TypePair key = new TypePair(name, clazz);
        if (variables.containsKey(key)) {
            return variables.get(key);
        }
        // Not found locally, check parent
        if (parent != null) {
            return parent.lookup(name);
        }
        throw new java.util.NoSuchElementException("Variable '" + name + "' with type " + clazz.getName() + " is not defined in the current scope or any ancestor.");
    }
}
