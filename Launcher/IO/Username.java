package Launcher.IO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code Username} class represents a username with enforced validation rules.
 * It encapsulates the logic for validating, storing, comparing, and hashing usernames.
 *
 * <p>Usernames must adhere to the following rules:
 * <ul>
 *   <li>Length must be between {@link #MIN_LENGTH} and {@link #MAX_LENGTH} characters.</li>
 *   <li>Cannot begin or end with '-', '_' or a space.</li>
 *   <li>Must contain at least one letter or special character ('-' or '_').</li>
 *   <li>Can only include letters, digits, spaces, '-', or '_'.</li>
 *   <li>Normal usernames cannot be {@link #KEYWORDS}.</li>
 * </ul>
 *
 * <p>The class is immutable after construction and ensures efficient comparison and hashing operations.
 *
 * @author William Wu
 * @version 1.1
 */
public final class Username {
    /** The maximum allowed length of a username, inclusive. */
    public static final int MAX_LENGTH = 24;
    /** The minimum allowed length of a username, inclusive. */
    public static final int MIN_LENGTH = 3;
    /** Internal character array storage of the username (padded to {@link #MAX_LENGTH}). */
    private final char[] arr = new char[MAX_LENGTH];
    /** The actual number of characters used in {@link #arr}. */
    private final int actualLength;
    /**
     * Set of reserved keywords (usernames that are reserved and not allowed for normal purpose).
     * <p>
     * Current keywords include the capitalized and lowercase versions of:
     * <ul>
     *     <li>"player"</li>
     *     <li>"default"</li>
     *     <li>"dev"</li>
     *     <li>"guest"</li>
     *     <li>"host"</li>
     *     <li>"user"</li>
     *     <li>"driver"</li>
     *     <li>"god"</li>
     *     <li>"evil"</li>
     *     <li>"devil"</li>
     *     <li>"hard"</li>
     *     <li>"easy"</li>
     *     <li>"harmony"</li>
     *     <li>"hash"</li>
     *     <li>"code"</li>
     *     <li>"game"</li>
     *     <li>"gamer"</li>
     *     <li>"happyhex"</li>
     *     <li>"hex"</li>
     *     <li>"name"</li>
     *     <li>"club"</li>
     *     <li>"event"</li>
     * </ul>
     */
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "player", "default", "dev", "guest", "host", "user", "harmony", "game", "gamer",
            "happyhex", "hex", "name", "club", "event", "driver", "hash", "code", "easy"
    ));

    /**
     * Constructs a new {@code Username} object from a {@link String}, after validating its format.
     *
     * @param string the input string representing the username
     * @throws IllegalArgumentException if the input string is not a valid username
     * @see #Username(char[])
     */
    public Username(String string) throws IllegalArgumentException {
        this(generateCharArray(string));
    }
    /**
     * Constructs a new {@code Username} from a character array after validating its format.
     *
     * @param characters the character array representing the username
     * @throws IllegalArgumentException if the input character array is not a valid username
     * @see #Username(String)
     */
    public Username(char[] characters) throws IllegalArgumentException {
        if (!isValidFormat(characters)) {
            throw new IllegalArgumentException("Invalid username format.");
        }
        System.arraycopy(characters, 0, arr, 0, characters.length);
        actualLength = characters.length;
    }

    /**
     * Create a {@code Username} from a string.
     * This method will not throw any exceptions and is preferred over {@link #Username direct constructor}.
     * @param string the input string
     * @return a new {@code Username} object, or {@code null} if the format is invalid
     * @see #toString()
     */
    public static Username getUsername(String string) {
        try {
            return new Username(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Checks whether this username is a reserved keyword. Keywords are case-insensitive.
     * @return {@code true} if this username is a keyword, {@code false} otherwise
     */
    public boolean isKeyword() {
        return KEYWORDS.contains(toString().toLowerCase());
    }

    /**
     * The actual length of the username.
     * @return the number of characters in this username
     */
    public int length() {
        return actualLength;
    }

    /**
     * Computes the long hash value for this given username.
     * @return the computed hash value as a {@code long}
     */
    public long toHash() {
        long hash = 7;
        for (int i = 0; i < actualLength; i++) {
            hash = hash * 509 + arr[i];
        }
        return hash;
    }

    /**
     * Computes the standard hash code ({@code int}) representation of the username.
     * @return the hash code for this username
     * @see #toHash()
     */
    @Override
    public int hashCode() {
        return (int) (toHash() ^ (toHash() >>> 32));
    }

    /**
     * Returns the string representation of the username.
     * This value is parseable via the {@link #getUsername(String)} method.
     * @return the username as a string
     */
    @Override
    public String toString() {
        return new String(arr, 0, actualLength);
    }
    /**
     * Compares this username to another {@code Username} object for equality.
     *
     * @param other the {@code Username} to compare with
     * @return {@code true} if both usernames are equal, {@code false} otherwise
     * @see #equals(Object)
     */
    public boolean equals(Username other) {
        if (other == null) return false;
        return this.toString().equals(other.toString());
    }
    /**
     * Compares this username to a {@link String} for equality.
     *
     * @param str the string to compare with
     * @return {@code true} if the string matches the username, {@code false} otherwise
     * @see #equals(Object)
     */
    public boolean equals(String str) {
        if (str == null) return false;
        return this.toString().equals(str);
    }

    /**
     * Validates the character array format for username creation.
     *
     * @param characters the character array to validate
     * @return {@code true} if the format is valid, {@code false} otherwise
     * @see Username Username rules
     */
    private static boolean isValidFormat(char[] characters) {
        if (characters.length < MIN_LENGTH || characters.length > MAX_LENGTH) return false;
        if (characters[0] == '-' || characters[0] == '_' || characters[0] == ' ' ||
                characters[characters.length - 1] == '-' || characters[characters.length - 1] == '_' || characters[characters.length - 1] == ' ') {
            return false;
        }
        boolean hasLetterOrSpecial = false;
        for (char c : characters) {
            if (Character.isLetter(c) || c == '-' || c == '_') {
                hasLetterOrSpecial = true;
            } else if (!Character.isDigit(c) && c != ' ') {
                return false;
            }
        }
        return hasLetterOrSpecial;
    }

    /**
     * Converts the input string into a character array and validates it.
     *
     * @param string the input string
     * @return a character array representing the username
     * @throws IllegalArgumentException if the input string is not a valid username
     */
    private static char[] generateCharArray(String string) {
        char[] chars = string.toCharArray();
        if (!isValidFormat(chars)) {
            throw new IllegalArgumentException("Invalid username format.");
        }
        return chars;
    }
}
