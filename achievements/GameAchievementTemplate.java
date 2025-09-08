package achievements;

import hex.GameState;

import java.util.function.Predicate;

/**
 * The {@code GameAchievementTemplate} interface defines the contract for creating game achievements.
 * It extends the {@link Predicate} interface to allow checking if an achievement has been achieved
 * based on the current {@link GameState}.
 * <p>
 * Implementing classes should provide a unique name and description for the achievement, as well as
 * the logic to determine if the achievement has been achieved. All templates should be immutable.
 * <p>
 * This interface is intended to be used in conjunction with game state management systems to track
 * and reward player accomplishments.
 * <p>
 * The achievement logic should be stateless, relying solely on the provided {@link GameState} to determine
 * if the achievement criteria have been met.
 * <p>
 * The template itself is not guaranteed to be serializable, but implementations may choose to implement
 * serialization and deserialization as needed.
 *
 * @see GameState
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public interface GameAchievementTemplate extends Predicate<GameState> {
    /**
     * The name of the achievement. This should be unique among all achievements.
     * @return the name of the achievement
     */
    String name();
    /**
     * The description of the achievement. This should provide a brief summary of what the achievement is about.
     * @return the description of the achievement
     */
    String description();
    /**
     * Tests whether the achievement has been achieved based on the provided game state.
     * @param state the current game state
     * @return {@code true} if the achievement has been achieved, {@code false} otherwise
     */
    boolean test(GameState state);
}
