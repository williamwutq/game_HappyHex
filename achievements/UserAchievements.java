package achievements;

import io.JsonConvertible;
import io.Username;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code UserAchievements} class represents a collection of game achievements associated with a specific user.
 * It provides methods to manage and retrieve the user's achievements, as well as to serialize and deserialize
 * the achievements to and from JSON format, implementing {@link JsonConvertible}.
 * <p>
 * Each instance of this class is tied to a single {@link Username}, ensuring that the achievements are
 * user-specific. The class maintains an internal list of {@link GameAchievement} objects, which can be
 * modified through provided methods.
 * <p>
 * The class is designed to be thread-safe for certain operations, particularly those that involve reading
 * the user's achievements. However, methods that modify the achievements list are not thread-safe and should
 * be called from the appropriate thread context (e.g., the AUT thread) to ensure data integrity.
 * <p>
 * This class does not allow direct modification of the achievements list from outside; instead, it provides
 * methods to add individual or multiple achievements, as well as to deserialize achievements from JSON.
 * Deletion of achievements is not supported to maintain a complete record of the user's achievements.
 * <p>
 * Note: The integrity of the achievement data is maintained by ensuring that only achievements belonging
 * to the associated user can be added to the list.
 *
 * @see GameAchievement
 * @see Username
 * @see javax.json.JsonArray
 * @see javax.json.JsonObject
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class UserAchievements implements JsonConvertible {
    private final Username user;
    private final List<GameAchievement> achievements;

    /**
     * Constructs a UserAchievements object for the specified user.
     * The achievements list is initialized as an empty list.
     * <p>
     * The constructor is thread safe as it only initializes final fields.
     * @param user the Username of the user whose achievements are being tracked
     */
    public UserAchievements(Username user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.achievements = new ArrayList<GameAchievement>();
        this.user = user;
    }
    /**
     * Returns the Username of the user associated with these achievements.
     * <p>
     * The method is thread safe as the Username object is immutable.
     * @return the Username of the user
     */
    public Username getUser() {
        return user;
    }
    /**
     * Returns an unmodifiable list of the user's achievements. Used by the AUT thread.
     * The returned list cannot be modified to ensure the integrity of the achievement data.
     * <p>
     * However, the items are mutable, as they do not have direct setters, but can only be
     * updated through update methods.
     * <p>
     * However, use the resulting achievements list outside the AUT thread is not recommended,
     * as the achievements may be updated in the AUT thread. If you want a copy of the list,
     * run the method in the AUT thread with {@link GameAchievement#invokeLater} and cache the result.
     *
     * @return an unmodifiable list of GameAchievement objects representing the user's achievements.
     */
    public List<GameAchievement> getAchievements() {
        return List.copyOf(achievements);
    }
    /**
     * Converts the user's achievements to a JsonArrayBuilder.
     * Each achievement is represented as a JsonObject within the array.
     * <p>
     * This method is thread safe as it uses {@link GameAchievement#invokeLater(Runnable)}
     * to ensure that the conversion is done in a thread-safe manner.
     * @return a JsonArrayBuilder containing the user's achievements in JSON format
     */
    public JsonArrayBuilder toJsonArrayBuilder() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        GameAchievement.invokeLater(() -> {
            for (GameAchievement ga : achievements) {
                jab.add(ga.toJsonObject());
            }
        });
        return jab;
    }
    /**
     * Converts the UserAchievements object to a JsonObjectBuilder.
     * The resulting JSON object contains the user's username and an array of their achievements.
     * <p>
     * This method is thread safe as it uses {@link GameAchievement#invokeLater(Runnable)}
     * to ensure that the conversion is done in a thread-safe manner.
     * @return a JsonObjectBuilder representing the UserAchievements in JSON format
     */
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("user", user.toString());
        job.add("achievements", toJsonArrayBuilder());
        return job;
    }
    /**
     * Adds a new achievement to the user's list of achievements.
     * If the achievement is null or does not belong to the user, an IllegalArgumentException is thrown.
     * If the achievement already exists in the list, it is not added again.
     * <p>
     * This method is not thread safe. It should be called from the AUT thread.
     * If you want to call it from another thread, use {@link GameAchievement#invokeLater(Runnable)}
     * to ensure thread safety.
     * @param achievement the GameAchievement to be added
     * @throws IllegalArgumentException if the achievement is null or does not belong to the user
     */
    public void addAchievement(GameAchievement achievement) {
        if (achievement == null) {
            throw new IllegalArgumentException("Achievement cannot be null");
        }
        if (!achievement.getUser().equals(this.user)) {
            throw new IllegalArgumentException("Achievement user does not match");
        }
        if (!achievements.contains(achievement)) {
            achievements.add(achievement);
        }
    }
    /**
     * Adds multiple achievements to the user's list of achievements.
     * Each achievement is added using the addAchievement method, ensuring that
     * null values and user mismatches are handled appropriately.
     * <p>
     * If an achievement cannot be added due to an IllegalArgumentException,
     * it is ignored and the method continues to add the remaining achievements.
     * <p>
     * This method is not thread safe. It should be called from the AUT thread.
     * If you want to call it from another thread, use {@link GameAchievement#invokeLater(Runnable)}
     * to ensure thread safety.
     * @see #addAchievement(GameAchievement)
     * @param achievements a list of GameAchievement objects to be added
     */
    public void addAllAchievements(List<GameAchievement> achievements) {
        for (GameAchievement ga : achievements) {
            try{addAchievement(ga);}
            catch (IllegalArgumentException ignored){}
        }
    }
    /**
     * Deserializes a JsonArray into the user's list of achievements.
     * This method populates it with achievements created from the provided JsonArray.
     * <p>
     * This method is unsafe as it does not validate the achievements being added.
     * It is assumed that the JsonArray contains valid achievement data.
     * <p>
     * This method is thread safe as it uses {@link GameAchievement#invokeLater(Runnable)}
     * to ensure that the modification of the achievements list is done in a thread-safe manner.
     *
     * @param ja the JsonArray containing achievement data
     * @throws DataSerializationException if there is an error during deserialization
     */
    public void unsafeDeserializeAchievements(JsonArray ja) throws DataSerializationException {
        List<GameAchievement> temp = new ArrayList<>();
        for (int i = 0; i < ja.size(); i++) {
            temp.add(GameAchievement.fromJsonObject(ja.getJsonObject(i)));
        }
        GameAchievement.invokeLater(() -> achievements.addAll(temp));
    }
    /**
     * Deserializes a JsonArray into the user's list of achievements.
     * This method creates a temporary list of achievements from the provided JsonArray
     * and only adds them to the user's list if all achievements are successfully deserialized.
     * <p>
     * This method is safe as it ensures that either all achievements are added or none are,
     * maintaining the integrity of the user's achievement data.
     * <p>
     * This method is thread safe as it uses {@link GameAchievement#invokeLater(Runnable)}
     * to ensure that the modification of the achievements list is done in a thread-safe manner.
     *
     * @param ja the JsonArray containing achievement data
     * @throws DataSerializationException if there is an error during deserialization
     */
    public void deserializeAchievements(JsonArray ja) throws DataSerializationException {
        List<GameAchievement> temp = new ArrayList<>();
        for (int i = 0; i < ja.size(); i++) {
            temp.add(GameAchievement.fromJsonObject(ja.getJsonObject(i)));
        }
        // If all deserialized successfully, add them
        addAllAchievements(temp);
    }
}