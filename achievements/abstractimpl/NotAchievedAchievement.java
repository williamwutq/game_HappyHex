package achievements.abstractimpl;

import achievements.GameAchievementTemplate;
import hex.GameState;

/**
 * A wrapper for {@link GameAchievementTemplate} that marks an achievement as not achieved.
 * Not achieved achievements have their test logic inverted to indicate their status.
 * This class delegates all functionality to the wrapped template, except for the test method,
 * which is modified to return true when the original template's test method returns false.
 * <p>
 * Note: The name and description remain unchanged and are directly delegated to the
 * wrapped template.
 * <p>
 * This is marked phantom to prevent display in achievement lists and to enable constant checking regardless
 * of whether the achievement has been achieved.
 *
 * @see GameAchievementTemplate
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class NotAchievedAchievement implements PhantomAchievementTemplate {
    private final GameAchievementTemplate template;
    /**
     * Constructs a new {@code NotAchievedAchievement} that wraps the given template.
     * <p>
     * The name of the achievement will be prefixed with "_PHANTOM_" to indicate its phantom status.
     * @param template the achievement template to wrap
     */
    public NotAchievedAchievement(GameAchievementTemplate template) {
        this.template = template;
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement prefixed with "_PHANTOM_"
     */
    @Override
    public String realName() {
        return template.name();
    }
    /**
     * {@inheritDoc}
     * @return true if the user has not achieved this achievement, false otherwise
     */
    @Override
    public boolean test(GameState state) {
        return !template.test(state);
    }
}
