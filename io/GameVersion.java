package io;

import javax.json.*;

/**
 * Represents a semantic version of the game in the format {@code major.minor.patch}.
 * Implements {@link Comparable} and {@link JsonConvertible} for sorting and JSON serialization.
 * @author William Wu
 * @version 1.1
 */
public final class GameVersion implements Comparable<GameVersion>, JsonConvertible{
    private final int major;
    private final int minor;
    private final int patch;

    /**
     * Constructs a new GameVersion instance.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     */
    public GameVersion(int major, int minor, int patch){
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    /**
     * Parses a version string in the format "major.minor.patch" and returns a new {@code GameVersion}.
     * @param version the version string to parse
     * @return a {@code GameVersion} instance or {@code null} if the format is invalid
     */
    public static GameVersion parse(String version){
        try {
            String[] parts = version.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            return new GameVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * Compares this version to another version, using semantic versioning order.
     *
     * @param other the other {@code GameVersion} to compare with
     * @return a negative number, zero, or a positive number if this version is less than, equal to,
     *         or greater than the other version
     * @see Comparable#compareTo
     */
    public int compareTo(GameVersion other){
        if (this.major != other.major){
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor){
            return Integer.compare(this.minor, other.minor);
        }
        return Integer.compare(this.patch, other.patch);
    }
    /**
     * Checks whether this version is equal to another {@code GameVersion}.
     *
     * @param other the other version to compare to
     * @return true if the major, minor, and patch versions are all equal
     */
    public boolean equals(GameVersion other){
        return this.major == other.major && this.minor == other.minor && this.patch == other.patch;
    }
    /**
     * Computes the hash code for this version.
     * @return the hash code value
     */
    public int hashCode(){
        int hash = 97;
        hash = hash * 31 + major;
        hash = hash * 31 + minor;
        hash = hash * 31 + patch;
        return hash;
    }
    /**
     * Returns the version as a string in the format {@code major.minor.patch}.
     * This string can be decoded by the {@link #parse(String)} method.
     * @return the string representation of this version
     */
    public String toString(){
        return major + "." + minor + "." + patch;
    }

    /**
     * Converts this version to a JSON object builder.
     * @return the {@code JsonObjectBuilder} containing version fields
     * @see JsonConvertible
     */
    public JsonObjectBuilder toJsonObjectBuilder(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Major", major);
        builder.add("Minor", minor);
        builder.add("Patch", patch);
        return builder;
    }

    // Getters
    /** @return the major version number */
    public int major(){return major;}
    /** @return the minor version number */
    public int minor(){return minor;}
    /** @return the patch version number */
    public int patch(){return patch;}
}
