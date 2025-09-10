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

package achievements;

import hex.GameState;
import io.JsonConvertible;
import io.Username;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The {@code GameAchievement} class provides a robust and thread-safe framework for managing user achievements
 * within a gaming system. It encapsulates the logic for creating, tracking, updating, and serializing achievements,
 * ensuring seamless integration with game state and user data. Achievements are defined by a
 * {@link GameAchievementTemplate}, which specifies the criteria for earning an achievement, and are associated
 * with a {@link Username} representing the user. The class implements the {@link JsonConvertible} interface to
 * support JSON serialization and deserialization, enabling persistent storage and retrieval of achievement data.
 * <p>
 * The system maintains a centralized, thread-safe collection of achievement templates ({@code TEMPLATES}) and
 * active achievements ({@code activeAchievements}) for the current user. It employs a dedicated achievement update
 * thread (AUT) managed by a single-threaded {@link ExecutorService} named "Achievement-Update-Thread" and a daemon
 * thread ({@code autClockThread}) that periodically triggers updates every 120 milliseconds (configurable via
 * {@code AUT_DELAY}). This ensures that achievement checks are performed consistently without impacting the main
 * game thread. The AUT is optimized to execute tasks immediately if called from within itself, reducing overhead
 * (see {@link #invokeLater(Runnable)}).
 * <p>
 * Thread safety is a core design principle, achieved through:
 * <ul>
 *   <li>Synchronized access to shared resources like {@code TEMPLATES} and {@code activeAchievements}.</li>
 *   <li>Volatile variables ({@code gameStateSupplier}, {@code gameStateDisableFlag}, {@code autRunning},
 *       {@code activeUser}) to ensure visibility of updates across threads.</li>
 *   <li>Asynchronous task scheduling via {@code autExecutor} for operations that must run on the AUT.</li>
 * </ul>
 * <p>
 * The class supports dynamic game state integration through a {@link Supplier<GameState>} that provides the
 * current game state for evaluating achievement criteria. The game state can be disabled/enabled to control
 * updates, and the supplier can be updated without restarting the system. The class also provides methods to
 * manage the active user, inject achievements, and ensure all registered templates are represented in the
 * active user's achievement list.
 * <p>
 * Key features include:
 * <ul>
 *   <li>Thread-safe creation and management of achievements with validation to prevent null inputs.</li>
 *   <li>Periodic updates of active achievements based on the current game state, with optimizations to avoid
 *       redundant updates for already achieved states.</li>
 *   <li>JSON serialization/deserialization for persistent storage, with robust error handling for invalid data.</li>
 *   <li>Support for registering and retrieving achievement templates, ensuring uniqueness by name.</li>
 *   <li>Flexible user management, allowing switching or clearing the active user and their achievements.</li>
 *   <li>Comprehensive game state management with enable/disable functionality and thread-safe supplier updates.</li>
 * </ul>
 * <p>
 * The class is designed for use in multithreaded gaming environments where achievements must be updated
 * asynchronously without blocking the main game loop. It provides methods like {@link #startAchievementSystem()}
 * and {@link #shutdownAchievementSystem()} for lifecycle management, ensuring proper initialization and cleanup.
 * Developers should initialize the game state supplier and start the achievement system during application startup
 * and shut it down during application termination to prevent resource leaks.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Initialize game state supplier
 * GameAchievement.initializeGameStateSupplier(Game::getCurrentState);
 * // Start the achievement system
 * GameAchievement.startAchievementSystem();
 * // Set active user
 * GameAchievement.setActiveUser(new Username("player"));
 * // Register a template
 * GameAchievement.registerTemplate(template);
 * // Complete active achievements
 * GameAchievement.completeActiveAchievement();
 * // Retrieve user achievements
 * GameAchievement.getActiveUserAchievements().thenAccept(ua -> System.out.println(ua.getAchievements()));
 * // Shutdown system
 * GameAchievement.shutdownAchievementSystem();
 * }</pre>
 *
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class GameAchievement implements JsonConvertible {
    private static final String TEMPLATES_FILE = "achievements/buildin.hpyhexach.json";
    private static final String USER_DIRECTORY = "users/achievements/";
    private static final Set<GameAchievementTemplate> TEMPLATES = new HashSet<GameAchievementTemplate>();
    private static final Set<GameAchievement> activeAchievements = new HashSet<GameAchievement>();
    private static volatile Supplier<GameState> gameStateSupplier = null;
    private static volatile boolean gameStateDisableFlag = false;
    private static volatile boolean autRunning = false;
    private static Username activeUser = null;
    private static final int AUT_DELAY = 120;
    private static final ExecutorService autExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("Achievement-Update-Thread");
        return t;
    }); // Achievement Update Thread Executor
    private static final Thread autClockThread = new Thread(() -> {
        while (autRunning) {
            try {
                Thread.sleep(AUT_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            updateAll();
        }
    });
    private final GameAchievementTemplate template;
    private final Username user;
    private boolean achieved;
    /**
     * Starts the achievement system's update thread.
     * This method ensures that the update thread is running to periodically update achievements.
     * If the thread is already running, this method has no effect.
     * <p>
     * It is recommended to call this method during the initialization phase of the application
     * to ensure that achievements are updated regularly.
     */
    public static void startAchievementSystem() {
        autRunning = true;
        if (!autClockThread.isAlive()) {
            autClockThread.setDaemon(true);
            autClockThread.start();
        }
        // Create directory if it doesn't exist
        java.io.File dir = new java.io.File(USER_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    /**
     * Shuts down the achievement update thread.
     * This method will stop the thread and prevent any further achievement updates.
     * It is recommended to call this method when the application is closing to ensure a clean shutdown.
     */
    public static void shutdownAchievementSystem(){
        autRunning = false;
        autExecutor.shutdownNow();
        autClockThread.interrupt();
    }
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
    public static void loadTemplate() throws IOException {
        GameAchievementTemplate[] templateArr = AchievementJsonSerializer.deserializeAchievementTemplateFile(TEMPLATES_FILE);
        synchronized (TEMPLATES) {
            for (GameAchievementTemplate template : templateArr) {
                registerTemplate(template);
            }
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
     * This method has internal optimizations to run the task immediately if called from the achievement update thread.
     * @see #inAUT()
     * @param task the Runnable task to be executed
     */
    public static void invokeLater(Runnable task){
        if (task == null) {
            return;
        }
        if (inAUT()) {
            task.run();
            return;
        }
        autExecutor.submit(task);
    }
    /**
     * Sets the active user for achievement tracking.
     * This will clear the current list of active achievements.
     * To clear the active user, pass {@code null} as the parameter.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * <p>
     * @param user the Username of the active user, or null to clear the active user
     */
    public static void setActiveUser(Username user){
        if (inAUT()) {
            activeUser = user;
            activeAchievements.clear();
            return;
        }
        autExecutor.submit(() -> {
            activeUser = user;
            activeAchievements.clear();
        });
    }
    /**
     * Sets the active user and their achievements for achievement tracking.
     * This will replace the current list of active achievements with the provided user's achievements.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * @param userAchievements the UserAchievements containing the active user and their achievements
     * @throws IllegalArgumentException if userAchievements is null
     */
    public static void setActiveUser(UserAchievements userAchievements){
        if (userAchievements == null) {
            throw new IllegalArgumentException("UserAchievements cannot be null");
        }
        if (inAUT()) {
            activeUser = userAchievements.getUser();
            activeAchievements.clear();
            activeAchievements.addAll(userAchievements.getAchievements());
            return;
        }
        autExecutor.submit(() -> {
            activeUser = userAchievements.getUser();
            activeAchievements.clear();
            activeAchievements.addAll(userAchievements.getAchievements());
        });
    }
    /**
     * Unloads the currently active user and clears the list of active achievements.
     * This method is thread-safe and will be executed in the achievement update thread.
     * If no user is active, this method will have no effect.
     * <p>
     * After calling this method, there will be no active user until {@link #setActiveUser(Username)}
     * or {@link #setActiveUser(UserAchievements)} is called again.
     * <p>
     * The method has the same effect as calling {@code setActiveUser(null)}.
     */
    public static void unloadActive(){
        if (inAUT()) {
            activeUser = null;
            activeAchievements.clear();
            return;
        }
        autExecutor.submit(() -> {
            activeUser = null;
            activeAchievements.clear();
        });
    }
    /**
     * Serializes the active user's achievements to a JSON file.
     * The file will be saved in the {@code users/achievements/} directory with the filename
     * corresponding to the active user's username.
     * <p>
     * This method is thread-safe and will be executed in the achievement update thread.
     * If no user is active, this method will have no effect.
     * <p>
     * The method blocks currently until the serialization is complete. If this is not desired,
     * wrap this call in {@link CompletableFuture#runAsync(Runnable)} or similar.
     * @throws IOException if an I/O error occurs during serialization
     * @see #setActiveUser(Username)
     * @see #setActiveUser(UserAchievements)
     */
    public static void serializeActiveUserAchievements() throws IOException {
        UserAchievements ua;
        try {
            ua = getActiveUserAchievements().get();
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            throw new IOException("Failed to retrieve active user achievements", e);
        }
        if (ua == null) {
            System.out.println("No active user to serialize achievements for");
            return;
        }
        AchievementJsonSerializer.serializeUserAchievements(ua, USER_DIRECTORY);
    }
    /**
     * Injects a list of achievements into the active achievements for the active user.
     * Only achievements that belong to the active user and are not already present
     * in the active achievements list will be added.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * If no user is active, this method will have no effect.
     * <p>
     * Sequential calls to this method will not create duplicate achievements for the same template.
     *
     * @param achievements the list of GameAchievement instances to inject
     * @see #setActiveUser(Username)
     * @see #setActiveUser(UserAchievements)
     */
    public static void injectActiveAchievements(List<GameAchievement> achievements){
        if (achievements == null || activeUser == null) {
            return;
        }
        if (inAUT()) {
            for (GameAchievement achievement : achievements) {
                if (achievement.getUser().equals(activeUser) && notInAchievements(achievement.getTemplate().name())) {
                    activeAchievements.add(achievement);
                }
            }
            return;
        }
        autExecutor.submit(() -> {
            for (GameAchievement achievement : achievements) {
                if (achievement.getUser().equals(activeUser) && notInAchievements(achievement.getTemplate().name())) {
                    activeAchievements.add(achievement);
                }
            }
        });
    }
    /**
     * Completes the list of active achievements for the active user.
     * This method ensures that all registered achievement templates are represented
     * in the active achievements list for the current active user.
     * If an achievement for a template does not exist, it will be added with a not achieved status.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * If no user is active, this method will have no effect.
     * <p>
     * Sequential calls to this method will not create duplicate achievements for the same template.
     *
     * @see #setActiveUser(Username)
     * @see #setActiveUser(UserAchievements)
     */
    public static void completeActiveAchievement() {
        if (activeUser == null) return;
        invokeLater(() -> {
            Set<String> existingNames = activeAchievements.stream()
                    .map(a -> a.getTemplate().name())
                    .collect(Collectors.toSet());
            synchronized (TEMPLATES) {
                for (GameAchievementTemplate template : TEMPLATES) {
                    if (!existingNames.contains(template.name())) {
                        activeAchievements.add(new GameAchievement(template, activeUser));
                    }
                }
            }
        });
    }
    /**
     * Retrieves the active user's achievements.
     * This method returns a Future that will complete with the UserAchievements of the active user.
     * If no user is active, the UserAchievements will return null.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * @return a Future containing the UserAchievements of the active user
     */
    public static Future<UserAchievements> getActiveUserAchievements(){
        if (inAUT()) {
            if (activeUser == null) {
                return CompletableFuture.completedFuture(null);
            } else {
                UserAchievements ua = new UserAchievements(activeUser);
                ua.addAllAchievements(activeAchievements.stream().toList());
                return CompletableFuture.completedFuture(ua);
            }
        }
        return autExecutor.submit(() -> {
            if (activeUser != null) {
                UserAchievements ua = new UserAchievements(activeUser);
                ua.addAllAchievements(activeAchievements.stream().toList());
                return ua;
            } else {
                return null;
            }
        });
    }
    /**
     * Checks if the active user has an achievement based on the specified template.
     * This method returns true if the active user's achievements contain an achievement
     * with the same template as the provided one.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * If no user is active, this method will return false.
     * <p>
     * The difference between this method and {@link #notInAchievements(GameAchievementTemplate)}
     * is that this method guarantees a boolean return value of true only if the achievement is
     * definitely present, while the other method returns true if the achievement is definitely not present.
     * @param template the GameAchievementTemplate to check for
     * @return {@code true} if the active user has the achievement, {@code false} otherwise
     */
    public static boolean inAchievements(GameAchievementTemplate template){
        if (template == null) {
            return false;
        }
        if (inAUT()) {
            if (activeUser == null) {
                return false;
            }
            return activeAchievements.contains(new GameAchievement(template, activeUser));
        }
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] contains = new boolean[1];
        autExecutor.submit(() -> {
            try {
                if (activeUser == null) {
                    contains[0] = false;
                    return;
                }
                contains[0] = activeAchievements.contains(new GameAchievement(template, activeUser));
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return contains[0];
    }
    /**
     * Checks if the active user has an achievement based on the specified template name.
     * This method returns true if the active user's achievements contain an achievement
     * with the same template as the one with the provided name.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * If no user is active, this method will return false.
     * <p>
     * The difference between this method and {@link #notInAchievements(String)}
     * is that this method guarantees a boolean return value of true only if the achievement is
     * definitely present, while the other method returns true if the achievement is definitely not present.
     * @param templateName the name of the GameAchievementTemplate to check for
     * @return {@code true} if the active user has the achievement, {@code false} otherwise
     */
    public static boolean inAchievements(String templateName){
        if (templateName == null) {
            return false;
        }
        GameAchievementTemplate template = getTemplateByName(templateName);
        return inAchievements(template);
    }
    /**
     * Checks if the active user does not have an achievement based on the specified template.
     * This method returns true if the active user's achievements do not contain an achievement
     * with the same template as the provided one.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * If no user is active, this method will return false.
     * <p>
     * The difference between this method and {@link #inAchievements(GameAchievementTemplate)}
     * is that this method guarantees a boolean return value of true only if the achievement is
     * definitely not present, while the other method returns true if the achievement is definitely present.
     * @param template the GameAchievementTemplate to check for
     * @return {@code true} if the active user does not have the achievement, {@code false} otherwise
     */
    public static boolean notInAchievements(GameAchievementTemplate template){
        if (template == null) {
            return false;
        }
        if (inAUT()) {
            if (activeUser == null) {
                return false;
            }
            return !activeAchievements.contains(new GameAchievement(template, activeUser));
        }
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] contains = new boolean[1];
        autExecutor.submit(() -> {
            try {
                if (activeUser == null) {
                    contains[0] = false;
                    return;
                }
                contains[0] = activeAchievements.contains(new GameAchievement(template, activeUser));
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return !contains[0];
    }
    /**
     * Checks if the active user does not have an achievement based on the specified template name.
     * This method returns true if the active user's achievements do not contain an achievement
     * with the same template as the one with the provided name.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * If no user is active, this method will return false.
     * <p>
     * The difference between this method and {@link #inAchievements(String)}
     * is that this method guarantees a boolean return value of true only if the achievement is
     * definitely not present, while the other method returns true if the achievement is definitely present.
     * @param templateName the name of the GameAchievementTemplate to check for
     * @return {@code true} if the active user does not have the achievement, {@code false} otherwise
     */
    public static boolean notInAchievements(String templateName){
        if (templateName == null) {
            return false;
        }
        GameAchievementTemplate template = getTemplateByName(templateName);
        return notInAchievements(template);
    }
    /**
     * Returns the active user for achievement tracking.
     * This method returns a Future that will complete with the Username of the active user.
     * If no user is active, the Future will complete with null.
     * <p>
     * The method is thread-safe and will be executed in the achievement update thread.
     * @return a Future containing the Username of the active user, or null if no user is active
     */
    public static Future<Username> getActiveUser(){
        if (inAUT()) {
            return CompletableFuture.completedFuture(activeUser);
        }
        return autExecutor.submit(() -> {
            return activeUser;
        });
    }
    /**
     * Updates all active achievements based on the current game state.
     * This method retrieves the current game state using the registered supplier
     * and updates each active achievement by calling its {@link #updateAchieved(GameState)} method.
     * <p>
     * The method is thread-safe and can be called from any thread. If called from a non-AUT thread,
     * the update will be scheduled to run in the achievement update thread.
     * <p>
     * If no game state supplier has been set or if the supplier returns null, this method will have no effect.
     * @see #getCurrentGameState()
     * @see #updateAchieved(GameState)
     */
    public static void updateAll(){
        if (inAUT()) {
            GameState state = getCurrentGameState();
            if (state == null) {
                return;
            }
            for (GameAchievement achievement : activeAchievements) {
                achievement.updateAchieved(state);
            }
            return;
        }
        autExecutor.submit(() -> {
            GameState state = getCurrentGameState();
            if (state == null) {
                return;
            }
            for (GameAchievement achievement : activeAchievements) {
                achievement.updateAchieved(state);
            }
        });
    }
    /**
     * Returns whether the current thread is the achievement update thread.
     * This can be used to ensure that certain operations are only performed on the correct thread.
     * @see #invokeLater(Runnable)
     * @return {@code true} if the current thread is the achievement update thread, {@code false} otherwise
     */
    public static boolean inAUT(){
        return Thread.currentThread().getName().equals("Achievement-Update-Thread");
    }

    // Game State
    /**
     * Initializes the supplier for the current game state.
     * This supplier will be used to retrieve the current game state when needed.
     * It is important to ensure that the supplier is thread-safe and can be called from any thread.
     * <p>
     * This method is thread safe because the gameStateSupplier variable is declared as volatile, this
     * make it possible to ensure that the most up-to-date value is always read by the AUT.
     * <p>
     * This method can only be called once to set the initial supplier. To change the supplier later,
     * use {@link #updateGameStateSupplier(Supplier)} instead.
     * @param supplier a Supplier that provides the current GameState
     * @throws IllegalArgumentException if the supplier is null
     * @throws IllegalStateException if the supplier has already been set
     */
    public static synchronized void initializeGameStateSupplier(Supplier<GameState> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("GameState supplier cannot be null");
        }
        if (gameStateSupplier != null) {
            throw new IllegalStateException("GameState supplier has already been set, use updateGameStateSupplier to change it");
        }
        gameStateSupplier = supplier;
    }
    /**
     * Updates the supplier for the current game state.
     * This supplier will be used to retrieve the current game state when needed.
     * It is important to ensure that the supplier is thread-safe and can be called from any thread.
     * <p>
     * This method is thread safe because the gameStateSupplier variable is declared as volatile, this
     * make it possible to ensure that the most up-to-date value is always read by the AUT.
     * @param supplier a Supplier that provides the current GameState
     * @throws IllegalArgumentException if the supplier is null
     */
    public static synchronized void updateGameStateSupplier(Supplier<GameState> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("GameState supplier cannot be null");
        }
        gameStateSupplier = supplier;
    }
    /**
     * Returns the supplier for the current game state.
     * This supplier can be used to retrieve the current game state when needed.
     * It is important to ensure that the supplier is thread-safe and can be called from any thread.
     * @return a Supplier that provides the current GameState, or null if no supplier has been set
     */
    public static Supplier<GameState> getGameStateSupplier() {
        return gameStateSupplier;
    }
    /**
     * Disables the game state functionality.
     * After calling this method, {@link #getCurrentGameState()} will always return null,
     * regardless of the supplier set by {@link #initializeGameStateSupplier(Supplier)}
     * or {@link #updateGameStateSupplier(Supplier)}.
     * <p>
     * This method is thread-safe and can be called from any thread.
     * Once disabled, the game state can be re-enabled through {@link #enableGameState()}.
     * If the game state is already disabled, calling this method will have no effect.
     */
    public static void disableGameState() {
        gameStateDisableFlag = true;
    }
    /**
     * Enables the game state functionality.
     * After calling this method, {@link #getCurrentGameState()} will return the current game state
     * provided by the supplier set by {@link #initializeGameStateSupplier(Supplier)}
     * or {@link #updateGameStateSupplier(Supplier)}.
     * <p>
     * This method is thread-safe and can be called from any thread.
     * If the game state was not previously disabled, calling this method will have no effect.
     */
    public static void enableGameState() {
        gameStateDisableFlag = false;
    }
    /**
     * Retrieves the current game state using the registered supplier.
     * If no supplier has been set or game state is disabled, this method will return null.
     * <p>
     * This method is thread-safe and can be called from any thread.
     * @return the current GameState, or null if no supplier has been set
     */
    public static GameState getCurrentGameState(){
        if (gameStateDisableFlag) {
            return null;
        }
        Supplier<GameState> supplier = gameStateSupplier;
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }
}
