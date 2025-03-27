package Launcher.IO;

import javax.json.*;

public final class Version implements Comparable<Version>, JsonConvertible{
    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch){
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    public static Version parse(String version){
        try {
            String[] parts = version.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public int compareTo(Version other){
        if (this.major != other.major){
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor){
            return Integer.compare(this.minor, other.minor);
        }
        return Integer.compare(this.patch, other.patch);
    }
    public boolean equals(Version other){
        return this.major == other.major && this.minor == other.minor && this.patch == other.patch;
    }
    public int hashCode(){
        int hash = 97;
        hash = hash * 31 + major;
        hash = hash * 31 + minor;
        hash = hash * 31 + patch;
        return hash;
    }
    public String toString(){
        return major + "." + minor + "." + patch;
    }

    public JsonObject toJsonObject(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Major", major);
        builder.add("Minor", minor);
        builder.add("Patch", patch);
        return builder.build();
    }

    // Getters
    public int major(){return major;}
    public int minor(){return minor;}
    public int patch(){return patch;}
}
