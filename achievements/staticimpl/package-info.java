/**
 * This package contains static implementations of various achievement types.
 * <p>
 * Each achievement template class implements the {@link achievements.GameAchievementTemplate} interface and provides
 * specific criteria for achieving the achievement. Unlike configurable implementations, these implementations have
 * fixed criteria that cannot be changed at loading time and are fixed to specific game code.
 * <p>
 * <h2>Registration and Deserialization</h2>
 * Static implementations will be loaded at game loading time through Json, but they should not provide any special
 * serialization and deserialization methods, since they do not maintain any complex state. Additionally, they do not
 * need a serial name, since the default system should assign the serial name as "JavaBuildIn".
 * <p>
 * All static implementations must register themselves with the {@link achievements.AchievementJsonSerializer} in the
 * static portion, in which instances of the template are created once at loading time. No special methods are needed
 * as the constructor will be used.
 * <p>
 * Although some implementations of serialization system always include static build in achievement regardless of whether
 * they are referenced in the Json file, it is still recommended to include them in the Json file to ensure that they
 * are loaded and registered properly.
 *
 * <h2>Threading and Game State</h2>
 * To be a static implementation, the checking of whether an achievement has been achieved may depend on the passed in
 * {@link hex.GameState}. Alternatively, the implementation may directly check the game state or other parameters from
 * game classes to determine if the achievement has been achieved. Since the checking is done on the
 * {@link achievements.GameAchievement#invokeLater AUT} to ensure thread safety, all code in the implementation needs to
 * be thread safe and should not modify any game state. Static achievements should not query other achievements.
 *
 * <h2>Immutability</h2>
 * All templates are stored and managed by the {@link achievements.GameAchievement#getTemplates() GameAchievement class}.
 * Thus, all implementations of {@link achievements.GameAchievementTemplate} in this package are immutable, meaning that
 * once created, their state cannot be changed. This ensures that the achievement criteria remain consistent throughout
 * its lifecycle. Since static implementations have fixed criteria, they do not require any configuration or parameters
 * and only one instance of each implementation is needed. Additional instances of the achievement are not needed.
 * <p>
 * For this reason, static implementations do not provide serialization or deserialization methods, as they do not
 * maintain any complex state. In addition, it is recommended to make implementing classes final to prevent subclassing
 * and ensure immutability.
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

package achievements.staticimpl;