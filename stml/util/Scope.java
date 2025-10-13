package stml.util;

/**
 * Represents a hierarchical variable scope system supporting nested and global scopes.
 * <p>
 * A {@code Scope} manages variable definitions, lookups, assignments, and lifetime
 * across multiple nested contexts (e.g., blocks, functions, or environments).
 * Each scope can define its own variables, inherit from a parent, and spawn child scopes.
 * <p>
 * Conceptually, a scope forms a tree structure:
 * <ul>
 *   <li>The <b>global scope</b> is the root; it has no parent and cannot be exited.</li>
 *   <li>Each newly made scope becomes a <b>child</b> of the current scope.</li>
 * </ul>
 * <p>
 * Variables defined in a scope are accessible within that scope and all its descendants,
 * but not in ancestor or sibling scopes. If a variable is defined in multiple scopes,
 * the most local definition takes precedence (shadowing).
 * <p>
 * This interface does not enforce any specific type system or variable storage mechanism.
 * Implementations may choose to support typed variables, dynamic typing, or other semantics.
 * <p>
 * Implementations must ensure that scopes are acyclic and that parent-child relationships
 * are maintained correctly. The behavior of methods when the scope hierarchy is modified
 * (e.g., destroying scopes) is implementation-defined, but should be documented.
 * <p>
 * Implementations should be mindful of performance implications, especially for deep
 * scope hierarchies or frequent variable lookups. Caching strategies or optimized data
 * structures may be employed to improve efficiency.
 * <p>
 * In general, accessing of variables in this interface is not thread-safe. If multiple
 * threads may access the same {@code Scope} instance concurrently, external synchronization
 * is required. More generally, structure modifications (e.g., creating or destroying scopes)
 * should always be externally synchronized because there is no way to ensure thread-safety
 * without external synchronization.
 */
public interface Scope {
    /**
     * Determines whether this scope is the global (root) scope.
     * <p>
     * The global scope is the top scope that has no parent scope and
     * contains variables accessible from all other scopes.
     *
     * @return {@code true} if this scope is the global scope, {@code false} otherwise.
     */
    default boolean isGlobal(){
        return parent() == null;
    }
    /**
     * Return the parent scope of the current scope, or {@code null} if the scope is the
     * global (root) scope.
     * <p>
     * The current scope can access variables defined in its parent scope and
     * all ancestor scopes, but the parent scope cannot access variables defined
     * in the current scope.
     * <p>
     * The following hierarchy properties hold:
     * <ul>
     *   <li>The global scope's parent is {@code null}.</li>
     *   <li>A scope's parent is never {@code null} unless it is the global scope.</li>
     *   <li>A scope cannot be its own parent.</li>
     *   <li>Parent relationships are acyclic; a scope cannot be an ancestor of itself.</li>
     *   <li>If scope A is the parent of scope B, then B is a child of A. This is checked by {@link #isChildOf(Scope)} and {@link #contains(Scope)}.</li>
     *   <li>If scope A is the parent of scope B, then A cannot be a child of B.</li>
     * </ul>
     * 
     * @return the parent scope of this scope, or {@code null} if the scope is the global (root) scope.
     */
    Scope parent();
    /**
     * Enters a new nested (child) scope under the current scope.
     * <p>
     * The newly entered scope has the current scope as its parent.
     * Can define variables visible only within itself and its descendants.
     * <p>
     * The method should always make a new scope, so multiple calls of this
     * method on this method will return different scopes.
     * <p>
     * Calling {@link #parent()} on the returned scope should return a scope that is an alias
     * for the current scope.
     * 
     * @implSpec The implementation of this method is responsible for
     * ensuring the new scope is properly linked to its parent (the current scope).
     * The method may call private constructors to avoid arbitrary creations of scopes.
     * If creation of new scope necessitate invalidation of existing children, the invalidation
     * should be immediately reflected on hierarchy related methods such as {@link #contains(Scope)}
     * and {@link #isChildOf(Scope)}.
     * @return the new child scope that becomes current.
     */
    Scope make();
    /**
     * Checks whether a variable with the specified name is defined
     * in the current scope or any of its {@link #parent} scopes.
     * <p>
     * If {@link Scope#containsLocal(String name)} returns {@code}
     * true, this method should always return true.
     * 
     * @implNote The default implementation return {@code true} if the variable
     * is {@link #containsLocal(String) present locally} or is present in its parent
     * scope, checked by recursively calling {@code contains(String name)}.
     * @param name the variable name to check.
     * @return {@code true} if the variable exists in the current scope or any ancestor,
     *         {@code false} otherwise.
     */
    default boolean contains(String name) {
        if (containsLocal(name)) {
            return true;
        } else {
            Scope parent = parent();
            if (parent != null) {
                return parent().contains(name);
            } return false;
        }
    }
    /**
     * Checks whether a variable with the specified name is strictly defined
     * in the current scope. If the variable is present in a parent scope but not this
     * scope, this returns {@code false}.
     *
     * @param name the variable name to check.
     * @return {@code true} if the variable exists in the current scope,
     *         {@code false} otherwise.
     */
    boolean containsLocal(String name);
    /**
     * Checks whether the specified {@code Scope} object is identical to this scope
     * or appears in its parent chain.
     * <p>
     * This is the inverse of {@link #isChildOf(Scope)}, which means {@code scope1.contains(scope2)}
     * returns {@code true} if and only if {@code scope1.isChildOf(scope2)} returns true.
     * <p>
     * The result of this operation maybe {@code false} if the current scope contained the given
     * scope, but the given scope is removed later. When this happens, hierarchy is invalidated.
     * <p>
     * Scopes are acyclic, so if {@code scope1.contains(scope2)} returns {@code true},
     * it is necessary that {@code scope2.contains(scope1)} returns {@code false}.
     *
     * @implNote the default implementation uses reference equality to check if the scopes
     * are the same, and iteratively traverses the parent chain of the given scope to
     * check for containment.
     * @param scope the scope to test for containment.
     * @return {@code true} if {@code scope} is this scope or one of its ancestors,
     *         {@code false} otherwise.
     */
    default boolean contains(Scope scope){
        if (scope == null) return false;
        do {
            scope = scope.parent();
            if (scope == this) return true; // Exact reference equals used instead of equals
        } while (scope != null);
        return false;
    }
    /**
     * Determines whether this scope is a descendant (child or deeper) of the specified scope.
     * <p>
     * This is the inverse of {@link #contains(Scope)}, which means {@code scope1.isChildOf(scope2)}
     * returns {@code true} if and only if {@code scope1.contains(scope2)} returns true.
     * <p>
     * The result of this operation maybe {@code false} if the given scope contained the current
     * scope, but the current scope is removed later. When this happens, hierarchy is invalidated,
     * but the current scope may still have access to the parent scope.
     * <p>
     * Scopes are acyclic, so if {@code scope1.isChildOf(scope2)} returns {@code true},
     * it is necessary that {@code scope2.isChildOf(scope1)} returns {@code false}.
     *
     * @implNote the default implementation uses reference equality to check if the scopes
     * are the same, and iteratively traverses the parent chain of this scope to
     * check for ancestry.
     * @param scope the scope to test as a potential ancestor.
     * @return {@code true} if this scope is a descendant of {@code scope}, {@code false} otherwise.
     */
    default boolean isChildOf(Scope scope){
        if (scope == null) return false;
        Scope p = parent();
        while (p != null) {
            if (p == scope) return true; // Exact reference equals used instead of equals
            p = p.parent();
        }
        return false;
    }
    /**
     * Defines a new variable in the current scope.
     * <p>
     * If the variable already exists in this scope, this call has no effect.
     * If a variable with the same name exists in a <em>parent</em> scope, it is shadowed locally.
     * <p>
     * When a variable is shadowed, a new variable is declared within this more specific scope,
     * and the variable with the same name in the outer scope is effectively hidden.
     * If this shadowing behavior is not desired, use {@link #lazyDefine(String)} instead.
     *
     * @param name the name of the variable to define.
     */
    void define(String name);
    /**
     * Defines a new variable in the current scope only if it does not already exist
     * in the current scope or any ancestor scope.
     * <p>
     * If the variable with the specified name already exists in the current scope
     * or any ancestor, this call has no effect.
     * If it does not exist, a new variable is defined in the current scope.
     * <p>
     * Different from {@link #lazyDefineLocal(String)} or {@link #define(String)},
     * this method ensures that the variable is defined only if it does not exist
     * in the entire scope chain, preventing shadowing of variables in ancestor scopes.
     *
     * @param name the name of the variable to define.
     * @implNote The default implementation first checks if the variable exists using {@link #contains(String)}.
     * If it exists, it returns {@code false}. If it does not exist, it calls {@link #define(String)}
     * to define the variable in the current scope and returns {@code true}.
     */
    default void lazyDefine(String name){
        if (!contains(name)) {
            define(name);
        }
    }
    /**
     * Defines a new variable in the current scope only if it does not already exist
     * in the current scope.
     * <p>
     * If the variable with the specified name already exists in the current scope,
     * this call has no effect.
     * If it does not exist, a new variable is defined in the current scope.
     *
     * @param name the name of the variable to define.
     * @implNote The default implementation simply calls {@link #define(String)}.
     * Because {@link #define(String)} has no effect if the variable already exists
     * in the current scope, this method effectively ensures the variable is defined
     * locally if it does not already exist locally.
     * @implSpec There is no reason to override this method because it is
     * effectively the same as {@link #define(String)}.
     */
    default void lazyDefineLocal(String name){
        define(name);
    }
    /**
     * Assigns a value to an existing variable in the current scope or any ancestor scope.
     * <p>
     * In general, the variable with the specified name in the most local scope is assigned.
     * If no variable with the specified name exists in the scope hierarchy,
     * an {@code NoSuchElementException} exception is thrown.
     * <p>
     * For lazy assignment that defines the variable if it does not exist,
     * see {@link #lazyAssign(String, Object)}.
     *
     * @param name  the name of the variable to assign.
     * @param value the value to assign.
     * @throws java.util.NoSuchElementException if no variable with {@code name}
     *         exists in the current scope or any ancestor.
     * @throws ClassCastException if the implementation enforces type constraints
     *         and the provided value is incompatible with the variable's type.
     */
    void assign(String name, Object value);
    /**
     * Assigns a value to a variable in the current scope, defining it first if it does not exist.
     * <p>
     * If the variable with the specified name already exists in the current scope or any ancestor,
     * it is assigned the new value. If it does not exist, a new variable is defined in the current
     * scope and then assigned the value.
     * <p>
     * Notice that if a variable with the same name exists in a parent scope but not in the current scope,
     * that variable is modified instead of defining a new variable in the current scope. This may
     * result in dangerous side effects if not intended.
     * <p>
     * For lazy assignment that always defines the variable in the current scope, see
     * {@link #lazyAssignLocal(String, Object)}.
     *
     * @param name  the name of the variable to assign.
     * @param value the value to assign.
     * @implNote The default implementation first checks if the variable exists using {@link #contains(String)}.
     * If it exists, it calls {@link #assign(String, Object)} to assign the value.
     * If it does not exist, it calls {@link #define(String)} to define the variable in the current scope,
     * and then calls {@link #assign(String, Object)} to assign the value.
     * @throws ClassCastException if the implementation enforces type constraints
     *         and the provided value is incompatible with the variable's type.
     */
    default void lazyAssign(String name, Object value){
        if (contains(name)) {
            assign(name, value);
        } else {
            define(name);
            assign(name, value);
        }
    }
    /**
     * Defines a new variable in the current scope and assigns it a value locally, shadowing
     * any variable with the same name in ancestor scopes.
     * <p>
     * If the variable already exists in this scope, it is assigned the new value.
     * If not, a new variable is defined in the current scope and then assigned the value.
     * This method ensures that the variable is always defined in the current scope,
     * regardless of whether a variable with the same name exists in a parent scope.
     * <p>
     * This may lead to unintended shadowing of variables in ancestor scopes, which
     * should be used with caution. If shadowing is not desired, use
     * {@link #lazyAssign(String, Object)} instead.
     *
     * @param name  the name of the variable to define and assign.
     * @param value the value to assign.
     * @implNote The default implementation is a sequence of {@link #define(String)}
     * and {@link #assign(String, Object)}. Because {@link #define(String)} has no effect
     * if the variable already exists in the current scope, this method effectively
     * ensures the variable is defined locally before assignment.
     * @throws ClassCastException if the implementation enforces type constraints
     *          and the provided value is incompatible with the variable's type.
     */
    default void lazyAssignLocal(String name, Object value){
        define(name);
        assign(name, value);
    }
    /**
     * Removes a variable from the current scope only, return whether the variable is removed.
     * <p>
     * If the variable does not exist in the current scope, this call has no effect.
     * Variables with the same name in ancestor scopes are not affected.
     * <p>
     * To recursively remove all variable from all ancestor scopes, see {@link #destroyRecursive(String)}.
     * To remove the most local occurrence of the variable from the entire scope chain,
     * see {@link #destroy(String)}.
     *
     * @param name the name of the variable to remove.
     * @implSpec The implementation of this method is responsible for
     * ensuring that the variable is removed only from the current scope. If the variable does not exist
     * in the current scope, the method should simply return without any action. If the implementation
     * enforces type constraints, it should ensure a variable of the same name but different type can
     * be created later after the existing variable is destroyed.
     * @return {@code true} if the variable was present and removed, {@code false} otherwise.
     */
    boolean destroyLocal(String name);
    /**
     * Removes a variable from the current scope or any ancestor scope, return whether
     * a variable is removed.
     * <p>
     * In general, the variable with the specified name in the most local scope is removed.
     * If no variable with the specified name exists in the scope hierarchy,
     * this call has no effect.
     * <p>
     * For simple scope usage, this might not be the desired behavior. Consider using
     * {@link #destroyLocal(String)} to remove a variable only from the current scope.
     * <p>
     * The method differs from {@link #destroyRecursive(String)} in that it removes
     * only the most local occurrence of the variable, not all occurrences in the entire
     * scope chain. Thus, this is generally safer.
     *
     * @param name the name of the variable to remove.
     * @implNote The default implementation first attempts to remove the variable
     * from the current scope using {@link #destroyLocal(String)}. If that fails,
     * it recursively calls {@code destroy} on the parent scope. If no parent exists,
     * the method simply returns without any action.
     * @return {@code true} if a variable was removed, {@code false} otherwise.
     */
    default boolean destroy(String name){
        if (destroyLocal(name)) {
            return true;
        } else {
            Scope parent = parent();
            if (parent != null) {
                return parent.destroy(name);
            }
        }
        return false;
    }
    /**
     * Removes all variables from the current scope and all ancestor scopes, return whether
     * any variable is removed.
     * <p>
     * All variables with the specified name in the current scope and all ancestor scopes
     * are removed. If no variable with the specified name exists in the scope hierarchy,
     * this call has no effect.
     * <p>
     * For simple scope usage, this might not be the desired behavior. Consider using
     * {@link #destroyLocal(String)} to remove a variable only from the current scope.
     * <p>
     * The method differs from {@link #destroy(String)} in that it removes
     * all occurrences of the variable in the entire scope chain, not just
     * the most local one.
     *
     * @param name the name of the variable to remove.
     * @implNote The default implementation first removes the variable
     * from the current scope using {@link #destroyLocal(String)}. It then
     * recursively calls {@code destroyRecursive} on the parent scope if it exists.
     * @return {@code true} if any variable was removed, {@code false} otherwise.
     */
    default boolean destroyRecursive(String name){
        boolean removed = destroyLocal(name);
        Scope parent = parent();
        if (parent != null) {
            if (parent.destroyRecursive(name)){
                removed = true;
            }
        }
        return removed;
    }
    /**
     * Retrieves the value of a variable from the current scope or one of its ancestors.
     * <p>
     * In general, value of the variable with the specified name in the most local scope is returned.
     * Lookup proceeds outward through the parent chain until the variable is found.
     * If no variable with the specified name exists in the scope hierarchy,
     * an {@code NoSuchElementException} exception is thrown.
     * <p>
     * Uninitialized values should be {@code null}. Null is not a special case.
     * If the value of the variable is {@code null}, return null.
     *
     * @param name the name of the variable to look up.
     * @return the value of the variable.
     * @throws java.util.NoSuchElementException if the variable is not found in the current
     *         or any ancestor scope.
     * @implSpec The implementation of this method is responsible for
     * ensuring that the variable is looked up correctly through the scope hierarchy. If the variable is not found,
     * the method should throw a {@code NoSuchElementException}. If the variable is found but uninitialized,
     * the method should return {@code null}. If the implementation enforces type constraints, it should ensure
     * that the returned value can be safely cast to the expected type by the caller.
     */
    Object lookup(String name);
}
