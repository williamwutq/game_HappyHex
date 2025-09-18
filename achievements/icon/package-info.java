
/**
 * This package contains classes and resources related to achievement icons.
 * <p>
 * It includes implementations for different types of achievement icons, such as empty icons,
 * text-based icons, and shaped icons. Each icon type implements the {@link achievements.icon.AchievementIcon}
 * interface, which defines the contract for rendering and managing achievement icons.
 * <p>
 * The package also provides a {@link achievements.icon.AchievementIconSerialHelper utility class} for
 * serializing and deserializing achievement icons to and from JSON format, facilitating easy storage
 * and retrieval of icon data.
 * <p>
 * This package is dependent on the following libraries and packages:
 * <ul>
 *     <li>Java AWT (Abstract Window Toolkit) for graphical representation and manipulation of shapes and colors.</li>
 *     <li>Java JSON Processing (javax.json) for handling JSON serialization and deserialization.</li>
 *     <li>The {@code util.tuple.Pair} class providing basic tuple functionality,</li>
 *     <li>The {@code util.geom.CurvedShape} class for representing complex shapes made out of quadratic Bézier curves.</li>
 * </ul>
 * @author William Wu
 * @version 2.0
 * @see achievements.icon.AchievementIcon
 * @see achievements.icon.AchievementIconSerialHelper
 */
package achievements.icon;