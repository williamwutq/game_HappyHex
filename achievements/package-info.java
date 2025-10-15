/**
 * The Achievement System package provides a framework for defining, tracking,
 * and persisting player achievements in a game. It is designed with
 * <b>thread safety</b>, <b>extensibility</b>, and <b>JSON-based configuration</b>
 * in mind.
 *
 * <h1>Overview</h1>
 * <ul>
 *   <li>Achievements are defined by templates that specify criteria ({@link achievements.GameAchievementTemplate}).</li>
 *   <li>Achievements are tracked per user ({@link achievements.UserAchievements}).</li>
 *   <li>The system is updated asynchronously by a dedicated Achievement Update Thread (AUT).</li>
 *   <li>Achievements can be serialized and deserialized to JSON for persistence.</li>
 * </ul>
 *
 * <h2>Core Concepts</h2>
 * <p>
 * <h3>1. Achievement Templates ({@link achievements.GameAchievementTemplate})</h3>
 * <ul>
 *   <li>Defines name, description, icon, and stateless logic (based on {@code GameState}).</li>
 *   <li>Immutable by design, extending {@code Predicate<GameState>} for achievement checks.</li>
 *   <li>Default icon generation is based on name hash, or custom icons can be provided.</li>
 *   <li>Custom icons are responsible for their own serialization.</li>
 * </ul>
 *
 * <h3>2. Achievement Icons ({@link achievements.icon.AchievementIcon})</h3>
 * <ul>
 *   <li>Visual representation of an achievement, composed of scalable colored shapes.</li>
 *   <li>Supports text-based icons ({@link achievements.icon.AchievementTextIcon}),
 *       shape-based icons ({@link achievements.icon.AchievementShapedIcon}),
 *       and gradient overlays ({@link achievements.icon.AchievementGradientIcon}).</li>
 *   <li>Background colors can be overridden; empty icons are provided by
 *       {@link achievements.icon.AchievementEmptyIcon}.</li>
 *   <li>Default implementations support scaling and rendering automatically.</li>
 * </ul>
 *
 * <h3>3. Game Achievements ({@link achievements.GameAchievement})</h3>
 * <ul>
 *   <li>Concrete achievement instances tied to specific users.</li>
 *   <li>Thread-safe management of active achievements.</li>
 *   <li>Periodic evaluation every 120 ms (default, configurable).</li>
 *   <li>Maintains registered templates and user-specific achievements.</li>
 *   <li>Integrated with {@code Supplier<GameState>} for live updates.</li>
 *   <li>Provides JSON serialization and deserialization.</li>
 *   <li>Uses a dedicated Achievement Update Thread (AUT), with async tasks supported via {@code invokeLater}.</li>
 * </ul>
 *
 * <h3>4. User Achievements ({@link achievements.UserAchievements})</h3>
 * <ul>
 *   <li>Stores all achievements for a given user.</li>
 *   <li>Achievements are user-specific and non-transferable.</li>
 *   <li>History is immutable—achievements cannot be deleted.</li>
 *   <li>Read access is thread-safe, write operations must occur on the AUT thread.</li>
 *   <li>Supports adding, bulk JSON deserialization, and retrieval of all user achievements.</li>
 * </ul>
 *
 * <h3>5. Special Achievement Types</h3>
 *
 * <h4>Hidden Achievements</h4>
 * <ul>
 *   <li>Hidden from the player interface but fully functional internally.</li>
 *   <li>Names prefixed with {@code _HIDDEN_} are automatically wrapped as hidden.</li>
 *   <li>Icons are suppressed with {@link achievements.icon.AchievementEmptyIcon}.</li>
 *   <li>Serialized and deserialized with their hidden status preserved.</li>
 * </ul>
 *
 * <h4>Phantom Achievements</h4>
 * <ul>
 *   <li>Special "ghost" achievements that are never displayed in UI.</li>
 *   <li>Always evaluated regardless of user progress and can be "unachieved."</li>
 *   <li>Implemented by templates extending {@link achievements.abstractimpl.PhantomAchievementTemplate}.</li>
 *   <li>Names are prefixed with {@code _PHANTOM_}, and each provides a {@code realName()}.</li>
 * </ul>
 *
 * <h4>Markable Achievements</h4>
 * <ul>
 *   <li>Can be marked achieved or reset outside of game state logic.</li>
 *   <li>Useful for GUI triggers, network events, or external interactions.</li>
 *   <li>Thread-safe, supporting global or instance-based marking.</li>
 *   <li>Maintained in a static global registry; requires reset when switching users.</li>
 * </ul>
 *
 * <h2>JSON Configuration</h2>
 * <ul>
 *   <li>Achievements are defined in JSON format with fields for name, description, icon, and type.</li>
 *   <li>Custom achievement classes must define unique type identifiers.</li>
 *   <li>Unknown achievements are stored unaltered for compatibility, ensuring data preservation.</li>
 * </ul>
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li><b>Initialization:</b> Load templates from JSON and start the system.</li>
 *   <li><b>Runtime:</b> Set active user and evaluate achievements periodically against {@code GameState}.</li>
 *   <li><b>Shutdown:</b> Call {@code GameAchievement.shutdownAchievementSystem()} to release resources.</li>
 * </ol>
 *
 * <h2>Package Structure</h2>
 * <ul>
 *   <li>Core classes: {@link achievements.GameAchievement}, {@link achievements.GameAchievementTemplate},
 *       {@link achievements.UserAchievements}, {@link achievements.AchievementJsonSerializer}</li>
 *   <li>Icon support: {@link achievements.icon}</li>
 *   <li>Abstract/specialized types: {@link achievements.abstractimpl}</li>
 *   <li>Static implementations: {@link achievements.staticimpl}.</li>
 *   <li>Exception: {@link achievements.DataSerializationException}</li>
 * </ul>
 * @author William Wu
 * @since 2.0
 * @version 2.0
 */
package achievements;
