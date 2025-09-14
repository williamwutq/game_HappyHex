package achievements.abstractimpl;

import achievements.GameAchievementTemplate;

/**
 * A convenient marker interface for phantom achievements, which are special types of achievements
 * that have their names prefixed with "_PHANTOM_". This prefix indicates that the achievement
 * is a phantom achievement, which will not be shown anywhere by design. In the achievement system,
 * the statues of a phantom achievement is always checked, regardless of whether the player has achieved it or not.
 * <p>
 * Implementing classes must provide a method to retrieve the real name of the achievement,
 * which does not include the "_PHANTOM_" prefix. This real name should be unique among all achievements.
 * <p>
 * The description of a phantom achievement is automatically generated to include the phrase
 * "Phantom Achievement" followed by the simple class name of the implementing class.
 * <p>
 * Phantom achievements templates do not need to be included in Json serialization or deserialization processes,
 * as they are not intended to be displayed or tracked in the same way as regular achievements. Therefore,
 * they do not need to implement any serialization interfaces.
 * <p>
 * Example usage:
 * <pre>{@code
 *     public class MyPhantomAchievement implements PhantomAchievementTemplate {
 *         @Override
 *         public String realName() {
 *             return "MyUniquePhantomAchievement";
 *         }
 *         @Override
 *         public boolean test(GameState state) {
 *             // Achievement logic here
 *         }
 *     }
 * }</pre>
 * <p>
 * Note: The test logic must be implemented by the concrete class, as this interface does not provide
 * a default implementation for it. When creating an actual phantom achievement, all serialization
 * considerations should be implemented accordingly.
 * <p>
 * A few utility static methods are provided to check if an achievement is a phantom and to manipulate names.
 *
 * @see GameAchievementTemplate
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public interface PhantomAchievementTemplate extends GameAchievementTemplate {
    /**
     * The real name of the achievement that does not contain "_PHANTOM_".
     * This should be unique among all achievements.
     * @return the real name of the achievement
     */
    String realName();
    /**
     * {@inheritDoc}
     *
     * @return the name of the achievement prefixed with "_PHANTOM_"
     */
    @Override
    default String name() {
        return "_PHANTOM_" + realName();
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement, combined from "Phantom Achievement " and the simple class name
     */
    @Override
    default String description() {
        return "Phantom Achievement " + getClass().getSimpleName();
    }
    /**
     * A utility method to generate the phantom name from a given real name.
     * This method is useful for ensuring consistency in naming conventions.
     * @param realName the real name of the achievement
     * @return the phantom name prefixed with "_PHANTOM_"
     */
    static String phantomName(String realName) {
        return "_PHANTOM_" + realName;
    }
    /**
     * A utility method to extract the real name from a given phantom name.
     * This method checks if the provided name starts with the "_PHANTOM_" prefix
     * and removes it to return the real name. If the name does not start with the prefix,
     * it is returned as is.
     * @param phantomName the phantom name of the achievement
     * @return the real name without the "_PHANTOM_" prefix
     */
    static String revealName(String phantomName) {
        if (!phantomName.startsWith("_PHANTOM_")) {
            return phantomName;
        }
        return phantomName.substring(9); // Length of "_PHANTOM_" is 9
    }
    /**
     * A utility method to check if a given achievement name is a phantom name.
     * This method checks if the provided name starts with the "_PHANTOM_" prefix.
     * @param name the name of the achievement
     * @return true if the name starts with "_PHANTOM_", false otherwise
     */
    static boolean isPhantom(String name) {
        return name.startsWith("_PHANTOM_");
    }
    /**
     * A utility method to check if a given achievement template is a phantom achievement.
     * This method checks if the provided template is an instance of {@code PhantomAchievementTemplate}.
     * @param template the achievement template to check
     * @return true if the template is a phantom achievement, false otherwise
     */
    static boolean isPhantom(GameAchievementTemplate template) {
        return template instanceof PhantomAchievementTemplate;
    }
}
