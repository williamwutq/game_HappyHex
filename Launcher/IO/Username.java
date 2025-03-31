package Launcher.IO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Username {
    private char[] arr = new char[24];
    private int actualLength;
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "player", "default", "dev", "guest", "host", "user", "harmony", "game", "gamer",
            "happyhex", "hex", "name"
    ));

    public Username(String string) throws IllegalArgumentException {
        this(generateCharArray(string));
    }

    public Username(char[] characters) throws IllegalArgumentException {
        if (!isValidFormat(characters)) {
            throw new IllegalArgumentException("Invalid username format.");
        }
        System.arraycopy(characters, 0, arr, 0, characters.length);
        actualLength = characters.length;
    }

    public static Username getUsername(String string) {
        try {
            return new Username(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isKeyword() {
        return KEYWORDS.contains(toString().toLowerCase());
    }

    public int length() {
        return actualLength;
    }

    public long toHash() {
        long hash = 7;
        for (int i = 0; i < actualLength; i++) {
            hash = hash * 509 + arr[i];
        }
        return hash;
    }

    @Override
    public int hashCode() {
        return (int) (toHash() ^ (toHash() >>> 32));
    }

    @Override
    public String toString() {
        return new String(arr, 0, actualLength);
    }

    public boolean equals(Username other) {
        if (other == null) return false;
        return this.toString().equals(other.toString());
    }

    public boolean equals(String str) {
        if (str == null) return false;
        return this.toString().equals(str);
    }

    private static boolean isValidFormat(char[] characters) {
        if (characters.length < 3 || characters.length > 24) return false;
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

    private static char[] generateCharArray(String string) {
        char[] chars = string.toCharArray();
        if (!isValidFormat(chars)) {
            throw new IllegalArgumentException("Invalid username format.");
        }
        return chars;
    }
}
