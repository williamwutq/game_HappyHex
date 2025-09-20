/**
 * This package contains configurable implementations of various achievement types.
 * <p>
 * Each achievement template class implements the {@link achievements.GameAchievementTemplate} interface and provides
 * specific criteria for achieving the achievement. These implementations can be easily configured at loading with Json
 * and extended to create new types of achievements.
 * <p>
 * <h2>Registration and Deserialization</h2>
 * Achievements must be loaded at game loading time through Json, so classes much implement {@link io.JsonConvertible}
 * and provide a static method to deserialize from a {@link javax.json.JsonObject JsonObject}. The static method must be
 * named {@code fromJsonObject} and have the following signature:
 * <pre>{@code
 * public static <GameAchievementTemplate> fromJsonObject(JsonObject json) throws DataSerializationException
 * }</pre>
 * The method must throw a {@link achievements.DataSerializationException} if the JsonObject is invalid or missing required fields.
 * <p>
 * In addition, the class must register itself with the {@link achievements.AchievementJsonSerializer}
 * through a static method call in a static block or static method. The registration must be done as follows:
 * <pre>{@code
 * AchievementJsonSerializer.registerAchievementClass("ClassName", json -> {
 *     try {
 *         return fromJsonObject(json);
 *     } catch (DataSerializationException e) {
 *         throw new RuntimeException("Failed to deserialize ClassName.", e);
 *     }
 * });
 * }</pre>
 * where "SerialName" is the name of the class being registered. This allows the achievement to be deserialized
 * from Json using the {@link achievements.AchievementJsonSerializer}. When registered, the serial name of the class
 * will be "SerialName", which should match the "type" field in Json objects.
 * <p>
 * The achievement can require additional fields in the Json object to configure the achievement criteria. Checking
 * of the type and presence of required fields must be done in the {@code fromJsonObject} method implemented by the class.
 *
 * <h2>Threading and Game State</h2>
 * The checking of whether an achievement has been achieved is done on the
 * {@link achievements.GameAchievement#invokeLater AUT} to ensure thread safety periodically with a globally obtained
 * {@link hex.GameState}. It is required that the implementation of the achievement is thread-safe and does not
 * modify any game state or rely on mutable state that may change between invocations. Although it is possible to store
 * additional information in the achievement class, storing references to game objects is discouraged. Instead, if game
 * objects are needed to be stored, clone them to avoid unintended side effects.
 * <p>
 * The implementations may depend on other classes or mechanics in the game to determine if the achievement has been
 * achieved, using the {@link hex.GameState} interface or directly obtaining information from game classes. In the later
 * case, the implementation will still be run on the {@link achievements.GameAchievement#invokeLater AUT}, but more careful
 * consideration is needed to ensure that the implementation works correctly with the game mechanics.
 *
 * <h2>Immutability</h2>
 * All templates are stored and managed by the {@link achievements.GameAchievement#getTemplates() GameAchievement class}.
 * Thus, all implementations of {@link achievements.GameAchievementTemplate} in this package are immutable, meaning that
 * once created, their state cannot be changed. This ensures that the achievement criteria remain consistent throughout
 * its lifecycle. Any configuration or parameters needed for the achievement must be provided at construction time
 * and cannot be modified later. If difference in criteria is needed, a new instance of the achievement must be created.
 * If additional instances of the achievement are needed, they must be deserialized from Json.
 *
 * @see achievements.GameAchievementTemplate
 * @see achievements.GameAchievement
 * @see io.JsonConvertible
 * @see achievements.AchievementJsonSerializer
 * @see hex.GameState
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */

package achievements.impl;