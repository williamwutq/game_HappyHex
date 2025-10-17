/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package stml.struct;

import stml.StmlFieldProperty;
import stml.util.TreeScope;
import stml.util.Scope;
import stml.util.ScopeView;

import java.util.ArrayList;
import java.util.List;

public class StmlScope {
    // The key for the property object. This can be used because variables cannot start with assignment operators.
    // This key is guaranteed to not conflict with any user-defined keys.
    private static final String propertyKey = "=p";
    // The key for the array object. When something is popped from view, it is stored here.
    private static final String arrayKey = "=a";

    private Scope scope; // The underlying scope
    private final ScopeView view; // The view of the scope
    private String[] currentContent; // The current content
    private boolean wasUsingArray = false; // Whether the last operation was using the array mechanism

    /**
     * Creates a new StmlScope with an empty TreeScope.
     * <p>
     * This initializes the {@code StmlScope} field with a new global {@code TreeScope} representing
     * the underlying scope, and a {@code ScopeView} for accessing the scope set to global, and
     * opens the object for all writing and adding new fields.
     */
    public StmlScope() {
        this.scope = new TreeScope();
        this.view = new ScopeView(this.scope);
        // Always put a property object and array object in the root scope
        this.scope.lazyAssign(propertyKey, StmlFieldProperty.DEFAULT);
        this.scope.lazyAssign(arrayKey, new ArrayList<>());
    }

    /**
     * Gets the property of this scope.
     * <p>
     * This looks up the property object in the underlying scope, and returns it. If it is not found,
     * it returns the default property object, which give all permissions.
     * @return The property of this scope.
     */
    public StmlFieldProperty property() {
        Object obj = this.view.lookup(propertyKey);
        if (obj instanceof StmlFieldProperty) {
            return (StmlFieldProperty) obj;
        } else return StmlFieldProperty.DEFAULT;
    }

    /**
     * Sets the property of this scope to final if not set.
     * <p>
     * After this method is called, existing variables in the scope will be final and cannot
     * be changed later. However, if the variables reference macros or other variables, and the
     * referenced variables are not final, the variables maybe changed in the background.
     * <p>
     * This does not impact the ability to add new variables. This does not force the parser to parse
     * entered variables.
     */
    public void finalized() {
	    // Get property
    	Object obj = this.view.lookup(propertyKey);
    	if (obj instanceof StmlFieldProperty p) {
    	    this.view.assignLocal(propertyKey, p.finalized());
    	} // This should always happen. If it does not happen, we do not deal with it either
    }

    /**
     * Sets the property of this scope to closed if not set.
     * <p>
     * After this method is called, this scope will no longer accept new variables, and calling functions
     * for new entries will result in exceptions being thrown. However, variables can still be modified and
     * overwritten.
     * <p>
     * Since unnamed scope depend on list operation (expansion), when this scope is set to closed, it may no
     * longer accept unnamed scope operations, if the closed scope is the parent scope.
     * <p>
     * This does not impact the ability to modify variables through any means. This does not force the parser
     * to parse any variables.
     */
    public void closed() {
	    // Get property
	    Object obj = this.view.lookup(propertyKey);
	    if (obj instanceof StmlFieldProperty p) {
	        this.view.assignLocal(propertyKey, p.closed());
	    } // This should always happen. If it does not happen, we do not deal with it either
    }

    /**
     * Sets the property of the named scope to final if not set.
     * <p>
     * After this method is called, existing variables in the named scope will be final and cannot
     * be changed later. However, if the variables reference macros or other variables, and the
     * referenced variables are not final, the variables maybe changed in the background.
     * <p>
     * This does not impact the ability to add new variables. This does not force the parser to parse
     * entered variables.
     *
     * @param name The name of the scope to finalize. This can be a dot-separated path to a nested scope.
     * @throws IllegalArgumentException if the name is invalid or does not refer to a scope
     */
    public void finalized(String name) {
        // Get object
        Object obj = this.lookUpRecursive(name);
        if (obj instanceof ScopeView svo) {
            // Get property
            Object propObj = svo.lookup(propertyKey);
            if (propObj instanceof StmlFieldProperty p) {
                svo.assignLocal(propertyKey, p.finalized());
            } // This should always happen. If it does not happen, we do not deal with it either
        } else {
            throw new IllegalArgumentException("Cannot finalize non-scope object " + name);
        }
    }

    /**
     * Sets the property of the named scope to closed if not set.
     * <p>
     * After this method is called, the named scope will no longer accept new variables, and calling functions
     * for new entries will result in exceptions being thrown. However, variables can still be modified and
     * overwritten.
     * <p>
     * Since unnamed scope depend on list operation (expansion), when this scope is set to closed, it may no
     * longer accept unnamed scope operations, if the closed scope is the parent scope.
     * <p>
     * This does not impact the ability to modify variables through any means. This does not force the parser
     * to parse any variables.
     *
     * @param name The name of the scope to close. This can be a dot-separated path to a nested scope.
     * @throws IllegalArgumentException if the name is invalid or does not refer to a scope
     */
    public void closed(String name) {
        // Get object
        Object obj = this.lookUpRecursive(name);
        if (obj instanceof ScopeView svo) {
            // Get property
            Object propObj = svo.lookup(propertyKey);
            if (propObj instanceof StmlFieldProperty p) {
                svo.assignLocal(propertyKey, p.closed());
            } // This should always happen. If it does not happen, we do not deal with it either
        } else {
            throw new IllegalArgumentException("Cannot close non-scope object " + name);
        }
    }

    /**
     * Cleans up a name string by replacing leading/trailing/multiple dots with current content.
     * <p>
     * This method processes a name string to replace leading, trailing, and multiple consecutive dots
     * with corresponding elements from the current content array. It also ignores spaces outside
     * quoted segments. Quoted segments (enclosed in single or double quotes) are preserved as-is.
     * <p>
     * Only implied dots at the start of the name string, with the total amount not exceeding the length of
     * the current content array, are replaced. If there are too many leading dots, or if multiple or trailing dots
     * are found after non-dot characters, an exception is thrown. This is to prevent content from being
     * corrupted, leading to accidental overwrites.
     * <p>
     * For example, given the current content array `["x", "y", "z"]`:
     * <ul>
     * <li>`"..a.b"` becomes `"x.a.b"`</li>
     * <li>`"a..b"` throws an exception because multiple dots are not allowed after non-dot characters</li>
     * <li>`"a.b.."` throws an exception because trailing dots are not allowed after non-dot characters</li>
     * <li>`".. ..a.b"` throws an exception because too many leading dots are used</li>
     * <li>`".. .c"` becomes `"x.y.z.c"`</li>
     * </ul>
     * <p>
     * Spaces outside quoted segments are ignored, so `".. ..a. 'b. ' .c"` becomes `"x.y.a.'b. '.c"`.
     * <p>
     * @see #currentContent
     * @param name The name string to clean up.
     * @return The cleaned-up name string.
     */
    public String cleanupNameString(String name){
        StringBuilder sb = new StringBuilder();
        boolean lastWasDot = true; boolean disallowReplacement = false;
        boolean inSingleQuotes = false; boolean inDoubleQuotes = false;
        int dotCount = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (lastWasDot) {
                if (c == ' ') continue;
                if (c == '.') {
                    // Use current content according to dotCount
                    if (disallowReplacement) {
                        throw new IllegalArgumentException("Cannot use leading/trailing/multiple dots after non-dot characters in name \"" + name + "\"");
                    } else if (dotCount < currentContent.length) {
                        sb.append(currentContent[dotCount]);
                    } else {
                        throw new IllegalArgumentException("Too many leading/trailing/multiple dots in name \"" + name + "\"");
                    }
                    dotCount++;
                } else {
                    if (c == '\'') {
                        inSingleQuotes = true;
                    } else if (c == '"') {
                        inDoubleQuotes = true;
                    }
                    lastWasDot = false;
                }
                sb.append(c);
            } else {
                if (inSingleQuotes || inDoubleQuotes) {
                    sb.append(c);
                    if (inSingleQuotes && c == '\'') {
                        inSingleQuotes = false;
                    } else if (inDoubleQuotes && c == '"') {
                        inDoubleQuotes = false;
                    }
                } else if (c != ' ') {
                    disallowReplacement = true;
                    if (c == '.') {
                        lastWasDot = true;
                        dotCount++;
                    } else {
                        if (c == '\'') {
                            inSingleQuotes = true;
                        } else if (c == '"') {
                            inDoubleQuotes = true;
                        }
                    }
                    sb.append(c);
                }
            }
        }
        if (lastWasDot) {
            // The last dot has no check for disallowReplacement because it may indicate the array mechanism,
            // Where an unnamed scope is created instead of a named one.
            if (dotCount < currentContent.length) {
                sb.append(currentContent[dotCount]);
            } else {
                throw new IllegalArgumentException("Too many leading/trailing/multiple dots in name \"" + name + "\"");
            }
        }
        return sb.toString();
    }
    /**
     * Removes surrounding quotes from a quoted string.
     * <p>
     * This method checks if the given string starts and ends with matching single or double quotes.
     * If so, it removes the surrounding quotes and returns the unquoted string. If the string is not
     * properly quoted, it is returned unchanged.
     * <p>
     * For example:
     * <ul>
     * <li>"'example'" becomes 'example'</li>
     * <li>'"example"' becomes "example"</li>
     * <li>"example' remains "example'</li>
     * <li>"'example" becomes 'example</li>
     * </ul>
     * @param quotedString The string to remove quotes from.
     * @return The unquoted string, or the original string if not properly quoted.
     */
    public static String removeQuotes(String quotedString) {
        if ((quotedString.startsWith("'") && quotedString.endsWith("'")) ||
            (quotedString.startsWith("\"") && quotedString.endsWith("\""))) {
            return quotedString.substring(1, quotedString.length() - 1);
        }
        return quotedString;
    }

    /**
     * Creates a named scope in this scope.
     * <p>
     * This creates a new named scope in this scope, creating any intermediate scopes as necessary.
     * If any intermediate scopes are found to be non-scopes, an exception is thrown if they are final,
     * otherwise they are overridden with a new scope.
     * <p>
     * The name can be a dot-separated path to create nested scopes. Leading, trailing, and multiple
     * consecutive dots are replaced with corresponding elements from the current content array.
     * Spaces outside quoted segments are ignored. Quoted segments (enclosed in single or double quotes)
     * are preserved as-is.
     * <p>
     * Only implied dots at the start of the name string, with the total amount not exceeding the length of
     * the current content array, are replaced. If there are too many leading dots, or if multiple or trailing dots
     * are found after non-dot characters, an exception is thrown. This is to prevent content from being
     * corrupted, leading to accidental overwrites
     * <p>
     * After calling this method, the {@code currentContent} array is updated to reflect the
     * cleaned name split by dots.
     *
     * @param name The name of the scope to create. This can be a dot-separated path to create nested scopes.
     * @throws IllegalArgumentException if the name is invalid
     * @throws IllegalStateException if an intermediate scope is final and cannot be overridden
     */
    public void createNamedScope(String name) {
        String cleanName = cleanupNameString(name);
        // If old one was using array, pop it, move view to global
        if (wasUsingArray) {
            wasUsingArray = false;
            popAndMoveToGlobal();
        }
        // Reset current content to the cleaned name split by dots
        currentContent = cleanName.split("\\.");
        createNamedScope(this.view, cleanName);
    }
    /**
     * Creates a named scope in the given scope.
     * <p>
     * This creates a new named scope in the given scope, creating any intermediate scopes as necessary.
     * If any intermediate scopes are found to be non-scopes, an exception is thrown if they are final,
     * otherwise they are overridden with a new scope.
     *
     * @param scopeView The scope view to create the named scope in.
     * @param name The name of the scope to create. This can be a dot-separated path to create nested scopes.
     */
    private void createNamedScope(ScopeView scopeView, String name) {
        Scope scope = scopeView.root();
        // Get the index of the '.' in the name
        int dotIndex = name.indexOf('.');
        if (dotIndex == -1) {
            // No dot, just create a new scope here
            Scope s = new TreeScope();
            ScopeView sv = new ScopeView(scope);
            s.lazyAssign(propertyKey, StmlFieldProperty.DEFAULT);
            s.lazyAssign(arrayKey, new ArrayList<>());
            scope.lazyAssignLocal(removeQuotes(name), sv);
        } else {
            // Get the name before and after the dot
            String beforeDot = removeQuotes(name.substring(0, dotIndex).trim());
            String afterDot = name.substring(dotIndex + 1).trim();
            if (beforeDot.isEmpty()) {
                throw new IllegalArgumentException("Invalid name: empty segment before dot in \"" + name + "\"");
                // Should not happen due to cleanupNameString
            } else if (afterDot.isEmpty()) {
                // This indicates the array mechanism, create an unnamed scope
                createUnnamedScope(scopeView);
                return;
            }
            Scope s;
            ScopeView sv;
            // Get or create the scope before the dot
            if (!this.view.contains(beforeDot)) {
                s = new TreeScope();
                sv = new ScopeView(scope);
                s.lazyAssign(propertyKey, StmlFieldProperty.DEFAULT);
                s.lazyAssign(arrayKey, new ArrayList<>());
                scope.lazyAssignLocal(beforeDot, sv);
            } else {
                Object obj = this.view.lookup(beforeDot);
                if (obj instanceof ScopeView svo) {
                    sv = svo;
                } else {
                    // Loop up property to see if it's final
                    if (this.property().isFinal()) {
                        throw new IllegalStateException("Cannot override final field " + beforeDot);
                    } else {
                        // Override the field
                        s = new TreeScope();
                        sv = new ScopeView(scope);
                        s.lazyAssign(propertyKey, StmlFieldProperty.DEFAULT);
                        s.lazyAssign(arrayKey, new ArrayList<>());
                        scope.lazyAssignLocal(beforeDot, sv);
                    }
                }
            }
            // Now we should have a scope at beforeDot, recurse
            createNamedScope(sv, afterDot);
        }
    }
    /**
     * Creates an unnamed scope in this scope.
     * <p>
     * This creates a new unnamed scope in this scope. If the current scope is global,
     * it enters a new global scope. Otherwise, it reenters (adds to) the current scope.
     * After calling this method, the {@code wasUsingArray} flag is set to true.
     * <p>
     * If a new scope is created, it is added to the array object in the parent scope.
     * @param scopeView The scope view to create the unnamed scope in.
     */
    private void createUnnamedScope(ScopeView scopeView) {
        wasUsingArray = true;
        if (scopeView.isGlobal()) {
            scopeView.enter();
        } else {
            Scope s = scopeView.current();
            // Add to the array object in the parent scope
            Object arrObj = s.parent().lookup(arrayKey);
            if (arrObj instanceof List<?>) {
                try {
                    List<Scope> arr = (List<Scope>) arrObj;
                    arr.add(s);
                } catch (ClassCastException e) {
                    throw new IllegalStateException("Array object is not a list"); // Should not happen
                }
                scopeView.reenter(); // Reenter to update the view
            } else {
                throw new IllegalStateException("Array object is not a list"); // Should not happen
            }
            scopeView.reenter();
        }
    }

    /**
     * Pops the current scope and moves the view to global.
     * <p>
     * This method pops the current scope from the view, and moves the view to the global scope.
     * The popped scope is stored in the array object in the parent scope.
     * If the current scope is already global, this method does nothing.
     * <p>
     * This method is called when switching from using the array mechanism to not using it.
     */
    private void popAndMoveToGlobal() {
        // Get with view
        if (!this.view.isGlobal()){
            // Only pop if not global
            Scope s = this.view.current();
            // Add to the array object in the parent scope
            Object arrObj = s.parent().lookup(arrayKey);
            if (arrObj instanceof List<?>) {
                try {
                    List<Scope> arr = (List<Scope>) arrObj;
                    arr.add(s);
                } catch (ClassCastException e) {
                    throw new IllegalStateException("Array object is not a list"); // Should not happen
                }
                this.view.toRoot(); // Move to global
            } else {
                throw new IllegalStateException("Array object is not a list"); // Should not happen
            }
        }
    }

    /**
     * Recursively looks up a name in this scope.
     * <p>
     * This method looks up a name in this scope, handling dot-separated paths and
     * array access. If the name contains dots, it splits the name at the first dot and looks
     * up the part before the dot in the current scope. If it finds a scope, it recurses into
     * that scope with the part after the dot. If it does not find a scope, it returns the
     * object found or null if not found.
     * <p>
     * If the name does not contain dots, it checks if it is an array access (ends with ']'
     * or contains '['). If so, it looks up the array object in the current scope and tries
     * to parse the index from the name. If successful, it returns a new ScopeView for the
     * scope at that index in the array. If not successful, it throws an exception.
     * <p>
     * If the name is neither dot-separated nor an array access, it simply looks up the name
     * in the current scope and returns the result or null if not found.
     * <p>
     * The behavior of this function is undefined for array access objects during the parsing of
     * said objects. This means parse must not force parse the future objects.
     *
     * @param name The name to look up. This can be a dot-separated path or an array access.
     * @return The object found, or null if not found.
     * @throws IllegalArgumentException if the name is invalid
     * @throws IllegalStateException if an array access is attempted on a non-list object
     */
    public Object lookUpRecursive(String name) {
        String cleanName = cleanupNameString(name);
        // If old one was using array, pop it, move view to global
        if (wasUsingArray) {
            wasUsingArray = false;
            popAndMoveToGlobal();
        }
        return lookupRecursive(this.view, cleanName);
    }
    /**
     * Recursively looks up a name in the given scope view.
     * <p>
     * This method looks up a name in the given scope view, handling dot-separated paths and
     * array access. If the name contains dots, it splits the name at the first dot and looks
     * up the part before the dot in the current scope. If it finds a scope, it recurses into
     * that scope with the part after the dot. If it does not find a scope, it returns the
     * object found or null if not found.
     * <p>
     * If the name does not contain dots, it checks if it is an array access (ends with ']'
     * or contains '['). If so, it looks up the array object in the current scope and tries
     * to parse the index from the name. If successful, it returns a new ScopeView for the
     * scope at that index in the array. If not successful, it throws an exception.
     * <p>
     * If the name is neither dot-separated nor an array access, it simply looks up the name
     * in the current scope and returns the result or null if not found.
     * <p>
     * The behavior of this function is undefined for array access objects during the parsing of
     * said objects. This means parse must not force parse the future objects.
     *
     * @param scopeView The scope view to look up the name in.
     * @param name The name to look up. This can be a dot-separated path or an array access.
     * @return The object found, or null if not found.
     * @throws IllegalArgumentException if the name is invalid
     * @throws IllegalStateException if an array access is attempted on a non-list object
     */
    public Object lookupRecursive(ScopeView scopeView, String name) {
        // Get the index of the '.' in the name
        int dotIndex = name.indexOf('.');
        if (dotIndex == -1) {
            if (name.trim().endsWith("]") || name.contains("[")) {
                // Do array access
                Object arrObj = scopeView.lookup(arrayKey);
                if (arrObj instanceof List<?>) {
                    try {
                        List<Scope> arr = (List<Scope>) arrObj;
                        String indexStr = name.trim();
                        if (indexStr.startsWith("[")) {
                            indexStr = indexStr.substring(1);
                        }
                        if (indexStr.endsWith("]")) {
                            indexStr = indexStr.substring(0, indexStr.length() - 1);
                        }
                        indexStr = indexStr.trim();
                        int index = Integer.parseInt(indexStr);
                        if (index < 0 || index >= arr.size()) {
                            return null; // Out of bounds
                        }
                        Scope s = arr.get(index);
                        return new ScopeView(s);
                    } catch (ClassCastException e) {
                        throw new IllegalStateException("Array object is not a list"); // Should not happen
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid array index: " + name);
                    }
                } else {
                    throw new IllegalStateException("Array object is not a list"); // Should not happen
                }
            }
            // No dot, just property lookup here
            return scopeView.lookup(removeQuotes(name));
        } else {
            // Get the name before and after the dot
            String beforeDot = removeQuotes(name.substring(0, dotIndex).trim());
            String afterDot = name.substring(dotIndex + 1).trim();
            if (beforeDot.isEmpty()) {
                throw new IllegalArgumentException("Invalid name: empty segment before dot in \"" + name + "\"");
                // Should not happen due to cleanupNameString
            } else if (afterDot.isEmpty()) {
                // This indicates the array mechanism, where it should return an unnamed scope.
                // However, unnamed scope are to be accessed by [] and not by dot, so this is invalid.
                throw new IllegalArgumentException("Invalid name: empty segment after dot in \"" + name + "\"");
            }
            // Get the scope if it is a scope
            if (!this.view.contains(beforeDot)) {
                return null; // Not found
            } else {
                Object obj = this.view.lookup(beforeDot);
                if (obj instanceof ScopeView svo) {
                    // Recurse
                    return lookupRecursive(svo, afterDot);
                } else {
                    return obj; // Not a scope, return the object
                }
            }
        }
    }

    /**
     * Recursively assigns a value to a name in this scope.
     * <p>
     * This method assigns a value to a name in this scope, handling dot-separated paths.
     * If the name contains dots, it splits the name at the first dot and looks up the part before
     * the dot in the current scope. If it finds a scope, it recurses into that scope with the part
     * after the dot. If it does not find a scope, it creates a new scope and recurses into it.
     * <p>
     * If the name does not contain dots, it assigns the value to the name in the current scope.
     * If the name already exists, it checks if it is final or closed and throws an exception if
     * so. Otherwise, it assigns the value to the name.
     *
     * @param name The name to assign the value to. This can be a dot-separated path.
     * @param value The value to assign.
     * @throws IllegalArgumentException if the name is invalid
     * @throws IllegalStateException if an intermediate scope is final and cannot be overridden,
     *                               or if trying to override a final field
     */
    public void lazyAssignRecursive(String name, Object value) {
        String cleanName = cleanupNameString(name);
        // If old one was using array, pop it, move view to global
        if (wasUsingArray) {
            wasUsingArray = false;
            popAndMoveToGlobal();
        }
        lazyAssignRecursive(this.view, cleanName, value);
    }
    /**
     * Recursively assigns a value to a name in the given scope view.
     * <p>
     * This method assigns a value to a name in the given scope view, handling dot-separated paths.
     * If the name contains dots, it splits the name at the first dot and looks up the part before
     * the dot in the current scope. If it finds a scope, it recurses into that scope with the part
     * after the dot. If it does not find a scope, it creates a new scope and recurses into it.
     * <p>
     * If the name does not contain dots, it assigns the value to the name in the current scope.
     * If the name already exists, it checks if it is final or closed and throws an exception if
     * so. Otherwise, it assigns the value to the name.
     *
     * @param scopeView The scope view to assign the value in.
     * @param name The name to assign the value to. This can be a dot-separated path.
     * @param value The value to assign.
     * @throws IllegalArgumentException if the name is invalid
     * @throws IllegalStateException if an intermediate scope is final and cannot be overridden,
     *                               or if trying to override a final field
     */
    private void lazyAssignRecursive(ScopeView scopeView, String name, Object value) {
        // Get the index of the '.' in the name
        int dotIndex = name.indexOf('.');
        if (dotIndex == -1) {
            Scope s = scopeView.root();
            if (!s.contains(removeQuotes(name))) {
                // Check access: if closed, cannot add new
                Object obj = s.lookup(propertyKey);
                if (obj instanceof StmlFieldProperty p) {
                    if (p.isClosed()) {
                        throw new IllegalStateException("Cannot override final field " + name);
                    }
                }
                s.lazyAssignLocal(removeQuotes(name), value);
            } else {
                // Check access: if final, cannot override
                Object obj = s.lookup(propertyKey);
                if (obj instanceof StmlFieldProperty p) {
                    if (p.isFinal()) {
                        throw new IllegalStateException("Cannot override final field " + name);
                    }
                } // This should always happen. We allow writing if property is not found
                s.lazyAssignLocal(removeQuotes(name), value);
            }
        } else {
            // Get the name before and after the dot
            String beforeDot = removeQuotes(name.substring(0, dotIndex).trim());
            String afterDot = name.substring(dotIndex + 1).trim();
            if (beforeDot.isEmpty()) {
                throw new IllegalArgumentException("Invalid name: empty segment before dot in \"" + name + "\"");
                // Should not happen due to cleanupNameString
            } else if (afterDot.isEmpty()) {
                throw new IllegalArgumentException("Invalid name: empty segment after dot in \"" + name + "\"");
                // This is because unnamed scopes are final and cannot be assigned to
            }
            Scope s;
            ScopeView sv;
            // Get or create the scope before the dot
            if (!this.view.contains(beforeDot)) {
                s = new TreeScope();
                sv = new ScopeView(scopeView.root());
                s.lazyAssign(propertyKey, StmlFieldProperty.DEFAULT);
                s.lazyAssign(arrayKey, new ArrayList<>());
                scopeView.root().lazyAssignLocal(beforeDot, sv);
            } else {
                Object obj = this.view.lookup(beforeDot);
                if (obj instanceof ScopeView svo) {
                    sv = svo;
                } else {
                    // Loop up property to see if it's final
                    if (this.property().isFinal()) {
                        throw new IllegalStateException("Cannot override final field " + beforeDot);
                    } else {
                        // Override the field
                        s = new TreeScope();
                        sv = new ScopeView(scopeView.root());
                        s.lazyAssign(propertyKey, StmlFieldProperty.DEFAULT);
                        s.lazyAssign(arrayKey, new ArrayList<>());
                        scopeView.root().lazyAssignLocal(beforeDot, sv);
                    }
                }
            }
            // Now we should have a scope at beforeDot, recurse
            lazyAssignRecursive(sv, afterDot, value);
        }
    }

    /**
     * Checks if a character is legal in a scope name.
     * @param c The character to check.
     * @return True if the character is legal, false otherwise.
     */
    public static boolean isLegalScopeNameChar(char c) {
        // Rules: letters, digits, underscore, hyphen, space, single quote, double quote,
        // caret, exclamation mark, question mark, asterisk, plus, pipe, at sign, and any non-ASCII character
        return Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == ' ' || c == '\'' || c == '"'
                || c == '^' || c == '!' || c == '?' || c == '*' || c == '+' || c == '|' || c == '@' || c >= 128;
    }
    /**
     * Checks if a scope name is legal.
     * @param name The scope name to check.
     * @return True if the scope name is legal, false otherwise.
     */
    public static boolean isLegalScopeName(String name) {
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!isLegalScopeNameChar(c)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Checks if a scope name segment is legal.
     * <p>
     * Differs from isLegalScopeName in that dots are not allowed in segments.
     * @param name The scope name segment to check.
     * @return True if the scope name segment is legal, false otherwise.
     */
    public static boolean isLegalScopeNameSegment(String name) {
        // Empty segments are not allowed
        if (name.isEmpty()) return false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!isLegalScopeNameChar(c) || c == '.') {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String name = "......a. 'b. ' .c";
        String[] currentContent = "x.y.z.'k '.p.q.".split("\\.");
        StmlScope stmlScope = new StmlScope();
        stmlScope.currentContent = currentContent;
        System.out.println(stmlScope.cleanupNameString(name));
    }
}
