package achievements.staticimpl;

import achievements.AchievementJsonSerializer;
import achievements.DataSerializationException;
import achievements.GameAchievementTemplate;
import achievements.icon.AchievementIcon;
import achievements.icon.AchievementIconSerialHelper;
import hex.GameState;
import io.JsonConvertible;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.HashMap;

/**
 * The {@code StaticAchievement} class is a convenient base class for static game achievements.
 * Static achievements are those that are defined by fixed criteria and cannot be configured dynamically.
 * Implementations of this class represent specific static achievements that can be achieved in the game.
 * Implementations must have a constructor with the signature
 * {@code (String name, String description, AchievementIcon icon)} to facilitate initialization and deserialization.}
 * and the name must be unique among all static achievements.
 * Conveniently, you may use this line of code:
 * <pre>{@code
 *     public YourAchievement(String n, String d, AchievementIcon i) {super(n, d, i);}
 * }</pre>
 * They must also register themselves synchronized in the {@link #IMPLEMENTATIONS} map with their unique name as the key,
 * and provide the implementation to the {@link #test(GameState)} method to determine if the achievement has been achieved.
 * Conveniently, for registration, you must use this line of code in a function named {@code load}:
 * <pre>{@code
 *     public static void load() {
 *         register("Your Achievement", YourAchievement.class);
 *     }
 * }</pre>
 * If any of the above conditions are not met, deserialization will fail, which will cause the achievement system to fail
 * initialization.
 * <p>
 * This class serves as a base class for specific static achievements that can be defined in the game.
 * It provides methods to retrieve the achievement's name, description, and icon, as well as methods for
 * serialization and deserialization to and from JSON format.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed. This ensures
 * that the achievement information remains consistent throughout its lifecycle.
 * <p>
 * The class provides serialization and deserialization methods to convert the achievement to and from JSON format.
 * This allows for easy storage and retrieval of achievement data.
 *
 * @see GameAchievementTemplate
 * @see JsonConvertible
 * @see AchievementIcon
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public abstract class StaticAchievement implements GameAchievementTemplate, JsonConvertible {
    protected String name;
    protected String description;
    protected AchievementIcon icon;
    static final protected HashMap<String, Class<? extends StaticAchievement>> IMPLEMENTATIONS = new HashMap<>();
    public static void load()  {
        AchievementJsonSerializer.registerAchievementClass("JavaBuildIn", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException("Failed to deserialize EliminationAchievement.", e);
            }
        });
        // Load all built-in achievements
        IdenticalQueueAchievement.load();
        AccumulatedEliminationAchievement.load();
        EnginePerfectFitAchievement.load();
        EngineAllPerfectFitAchievement.load();
    }
    /**
     * Creates a new StaticAchievement with the specified name
     * @param name the name of the achievement
     */
    public StaticAchievement(String name, String description, AchievementIcon icon){
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement
     */
    @Override
    public String name() {
        return name;
    }
    /**
     * {@inheritDoc}
     * @return the description of the achievement
     */
    @Override
    public String description() {
        return description;
    }
    /**
     * {@inheritDoc}
     * @return the icon of the achievement
     */
    @Override
    public AchievementIcon icon() {
        return icon;
    }
    /**
     * Two StaticAchievements are considered equal if they have the same name.
     * @param obj The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StaticAchievement that = (StaticAchievement) obj;
        return name.equals(that.name);
    }
    /**
     * The hash code of a StaticAchievement is based on its name, manipulated to reduce collisions.
     * @return The hash code of the StaticAchievement.
     */
    @Override
    public int hashCode() {
        return name.hashCode() - 31;
    }
    /**
     * Serialize the StaticAchievement to a JsonObject.
     * @return The JsonObject representation of the StaticAchievement.
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "JavaBuildIn");
        builder.add("name", name);
        builder.add("description", description);
        builder.add("icon", AchievementIconSerialHelper.serialize(icon));
        return builder.build();
    }
    /**
     * Serialize the StaticAchievement to a JsonObjectBuilder.
     * @return The JsonObjectBuilder representation of the StaticAchievement.
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "JavaBuildIn");
        builder.add("name", name);
        builder.add("description", description);
        builder.add("icon", AchievementIconSerialHelper.serialize(icon));
        return builder;
    }
    /**
     * Deserialize a StaticAchievement from a JsonObject.
     * @param json The JsonObject to deserialize from.
     * @return The deserialized StaticAchievement.
     * @throws DataSerializationException if deserialization fails.
     */
    public static GameAchievementTemplate fromJsonObject(JsonObject json) throws DataSerializationException {
        try {
            String name = json.getString("name");
            String description = json.getString("description");
            AchievementIcon icon = AchievementIconSerialHelper.deserialize(json);
            // Search for the correct implementation class
            synchronized (IMPLEMENTATIONS){
                Class<? extends StaticAchievement> implClass = IMPLEMENTATIONS.get(name);
                if (implClass != null) {
                    return implClass.getConstructor(String.class, String.class, AchievementIcon.class)
                            .newInstance(name, description, icon);
                } else {
                    throw new DataSerializationException("Unknown StaticAchievement type: " + name);
                }
            }
        } catch (Exception e) {
            throw new DataSerializationException("Failed to deserialize StaticAchievement", e);
        }
    }
    /**
     * Register a StaticAchievement implementation class with a unique name.
     * This method must be called in a synchronized block to ensure thread safety.
     * @param name The unique name of the achievement.
     * @param implClass The implementation class of the achievement.
     * @throws IllegalArgumentException if an implementation with the same name is already registered.
     */
    public static void register(String name, Class<? extends StaticAchievement> implClass){
        synchronized (IMPLEMENTATIONS){
            if (IMPLEMENTATIONS.containsKey(name)){
                throw new IllegalArgumentException("An implementation with the name '" + name + "' is already registered.");
            }
            IMPLEMENTATIONS.put(name, implClass);
        }
    }
}
