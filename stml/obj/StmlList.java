package stml.obj;

import java.util.*;

public class StmlList extends ArrayList<StmlObject> implements StmlObject, StmlValue<List<StmlObject>>, Cloneable {
    /**
     * Create a new empty StmlList instance.
     */
    public StmlList() {
        super();
    }
    /**
     * Create a new StmlList instance with the given initial capacity.
     * @param initialCapacity The initial capacity of the list.
     */
    public StmlList(int initialCapacity) {
        super(initialCapacity);
    }
    /**
     * Create a new StmlList instance with the given collection of elements.
     * @param c The collection of elements to add to the list.
     */
    public StmlList(Collection<? extends StmlObject> c) {
        super(c);
    }
    /**
     * {@inheritDoc}
     * @implNote return the value as java List
     * @return java List
     */
    @Override
    public List<StmlObject> getValue() {
        return Collections.unmodifiableList(this);
    }
    /**
     * {@inheritDoc}
     * @implNote return ValueType.LIST
     * @return ValueType.LIST
     */
    @Override
    public ValueType getType() {
        return ValueType.LIST;
    }
    /**
     * {@inheritDoc}
     * @implNote return a shallow copy of this list
     * @return a shallow copy of this list
     */
    @Override
    public StmlList clone() {
        return (StmlList) super.clone();
    }
    /**
     * {@inheritDoc}
     * @implNote return the string representation of the list in STML format
     * @return the string representation of the list in STML format
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size(); i++) {
            sb.append(get(i).toString());
            if (i < size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    /**
     * {@inheritDoc}
     * @implNote return true if the lists are equal
     * @param obj The object to compare to
     * @return true if the lists are equal
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    /**
     * {@inheritDoc}
     * @implNote return the hash code of the list
     * @return the hash code of the list
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    /**
     * Split a string representation of a list into its individual elements.
     * This method takes care of nested lists and string literals.
     * @param str The string representation of the list (without the surrounding brackets).
     * @return An array of strings, each representing an element of the list.
     */
    public static String[] split(String str) {
        // We need to pay attention to string literals and nested lists
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        int nestedListLevel = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\"' || c == '\'') {
                inString = !inString;
                current.append(c);
            } else if (c == '[' && !inString) {
                nestedListLevel++;
                current.append(c);
            } else if (c == ']' && !inString) {
                nestedListLevel--;
                current.append(c);
            } else if (c == ',' && !inString && nestedListLevel == 0) {
                parts.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (!current.isEmpty()) {
            parts.add(current.toString().trim());
        }
        return parts.toArray(new String[0]);
    }
}
