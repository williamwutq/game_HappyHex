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
    public void finalize() {
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
     * to parse any varaiables.
     */
    public void close() {
	// Get property
	Object obj = this.view.lookup(propertyKey);
	if (obj instanceof StmlFieldProperty p) {
	    this.view.assignLocal(propertyKey, p.closed());
	} // This should always happen. If it does not happen, we do not deal with it either
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
            // TODO
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



    public static void main(String[] args) {
        String name = "......a. 'b. ' .c";
        String[] currentContent = "x.y.z.'k '.p.q.".split("\\.");
        StmlScope stmlScope = new StmlScope();
        stmlScope.currentContent = currentContent;
        System.out.println(stmlScope.cleanupNameString(name));
    }
}
