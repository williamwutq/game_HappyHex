package achievements;

import hex.GameState;
import io.JsonConvertible;
import io.Username;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameAchievement implements JsonConvertible {
    private static final Set<GameAchievementTemplate> TEMPLATES = new HashSet<GameAchievementTemplate>();
    private static final List<GameAchievement> activeAchievements = new ArrayList<GameAchievement>();
    private static Username activeUser = null;
    private static final int AUT_DELAY = 60;
    private static ExecutorService autExecutor = Executors.newSingleThreadExecutor(); // Achievement Update Thread Executor
    private static Thread autClockThread = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(AUT_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            // TO DO: implement call to update all active achievements
        }
    });
    private final GameAchievementTemplate template;
    private final Username user;
    private boolean achieved;
    /**
     * Constructs a GameAchievement with the specified template and user.
     * The achieved status is set to false by default.
     * @param template the GameAchievementTemplate associated with this achievement
     * @param user the Username of the user who has this achievement
     */
    public GameAchievement(GameAchievementTemplate template, Username user){
        if (template == null || user == null) {
            throw new IllegalArgumentException("Template and user cannot be null");
        }
        synchronized (TEMPLATES) {
            // Add the template if it doesn't already exist by name
            if (!template.equals(getTemplateByName(template.name()))) {
                TEMPLATES.add(template);
            }
        }
        this.template = template;
        this.user = user;
        this.achieved = false;
    }
    /**
     * Constructs a GameAchievement with the specified template, user, and achieved status.
     * @param template the GameAchievementTemplate associated with this achievement
     * @param user the Username of the user who has this achievement
     * @param achieved a boolean indicating whether the achievement has been achieved
     */
    public GameAchievement(GameAchievementTemplate template, Username user, boolean achieved){
        if (template == null || user == null) {
            throw new IllegalArgumentException("Template and user cannot be null");
        }
        synchronized (TEMPLATES) {
            // Add the template if it doesn't already exist by name
            if (!template.equals(getTemplateByName(template.name()))) {
                TEMPLATES.add(template);
            }
        }
        this.template = template;
        this.user = user;
        this.achieved = achieved;
    }

    /**
     * {@inheritDoc}
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GameAchievement other)) return false;
        return template.equals(other.template) && user.equals(other.user);
    }
    /**
     * {@inheritDoc}
     * @return the hash code of the achievement
     */
    @Override
    public int hashCode() {
        return Objects.hash(template, user);
    }

    // Getters
    /**
     * Returns the {@link GameAchievementTemplate} associated with this achievement.
     * @return the GameAchievementTemplate
     */
    public GameAchievementTemplate getTemplate(){
        return template;
    }
    /**
     * Returns the {@link Username} of the user who has this achievement.
     * @return the Username of the user
     */
    public Username getUser(){
        return user;
    }
    /**
     * Returns whether the achievement has been achieved.
     * @return {@code true} if the achievement has been achieved, {@code false} otherwise
     */
    public boolean isAchieved(){
        return achieved;
    }
    /**
     * Updates the achieved status based on the provided game state.
     * If the achievement is already achieved, it will stay achieved.
     * <p>
     * This method should be run on the achievement system's update thread to ensure thread safety.
     * @see GameAchievementTemplate#test(GameState)
     * @param state the current game state to test against the achievement criteria
     */
    protected void updateAchieved(GameState state) {
        if (!achieved){
            // Update only if not already achieved
            achieved = template.test(state);
        }
    }

    // Serialization and deserialization
    /**
     * Converts the achievement to a JSON object builder.
     * @return a {@link JsonObjectBuilder} representing the achievement
     */
    public JsonObjectBuilder toJsonObjectBuilder(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        return builder
                .add("name", template.name())
                .add("user", user.toString())
                .add("achieved", achieved);
    }
    /**
     * Deserializes a GameAchievement from a JSON object.
     * @param obj the JSON object to deserialize
     * @return a GameAchievement instance
     * @throws DataSerializationException if the JSON object is invalid or if the template is not found
     */
    public static GameAchievement fromJsonObject(JsonObject obj) throws DataSerializationException{
        String name;
        Username user;
        boolean achieved;
        try {
            name = obj.getString("name");
            user = new Username(obj.getString("user"));
            achieved = obj.getBoolean("achieved");
        } catch (Exception e){
            throw new DataSerializationException("Invalid JSON object for GameAchievement", e);
        }
        GameAchievementTemplate template = getTemplateByName(name);
        if (template == null) {
            throw new DataSerializationException("No template found for name: " + name + " potentially due to unloaded templates");
        }
        return new GameAchievement(template, user, achieved);
    }
    /**
     * Retrieves the set of all registered GameAchievementTemplate instances.
     * This set is unmodifiable to prevent external modification.
     * @return a set of GameAchievementTemplate instances
     */
    public static Set<GameAchievementTemplate> getTemplates(){
        synchronized (TEMPLATES) {
            return Collections.unmodifiableSet(TEMPLATES);
        }
    }
    /**
     * Registers a new GameAchievementTemplate.
     * If a template with the same name already exists, the registration fails.
     * @param template the GameAchievementTemplate to register
     * @return {@code true} if the template was successfully registered or already exist,
     *         {@code false} if a template with the same name already exists or if the template is null
     */
    public static boolean registerTemplate(GameAchievementTemplate template){
        if (template == null) {
            return false;
        }
        synchronized (TEMPLATES) {
            if (TEMPLATES.contains(template)) {
                return true;
            }
            if (getTemplateByName(template.name()) != null) {
                return false;
            }
            return TEMPLATES.add(template);
        }
    }
    /**
     * Retrieves a GameAchievementTemplate by its name.
     * @param name the name of the achievement template
     * @return the GameAchievementTemplate with the specified name, or null if not found
     */
    public static GameAchievementTemplate getTemplateByName(String name){
        synchronized (TEMPLATES) {
            for (GameAchievementTemplate template : TEMPLATES) {
                if (template.name().equals(name)) {
                    return template;
                }
            }
        }
        return null;
    }

    // AUT
    /**
     * Schedules a task to be executed in the achievement update thread.
     * This method is thread-safe and can be called from any thread.
     * @param task the Runnable task to be executed
     */
    public static void invokeLater(Runnable task){
        autExecutor.submit(task);
    }
    /**
     * Sets the active user for achievement tracking.
     * This will clear the current list of active achievements.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * @param user the Username of the active user, or null to clear the active user
     */
    public static void setActiveUser(Username user){
        autExecutor.submit(() -> {
            activeUser = user;
            activeAchievements.clear();
        });
    }

}
