/**
 * Provides the core game logic and mechanics related to piece generation and management.
 * <p>
 * The {@code game} package focuses on managing gameplay elements, particularly the generation,
 * queuing, and retrieval of {@link hex.Piece} objects. It includes utility and state management
 * classes that support different gameplay difficulty levels and player interactions.
 *
 * <h2>Classes</h2>
 * <ul>
 *   <li>
 *       {@link game.PieceFactory} — A utility class responsible for generating new {@code Piece} instances
 *       in accordance with the selected difficulty mode. It is stateless except for a global easy/normal
 *       mode toggle and is not intended to be instantiated. This class depends on {@link GUI.GameEssentials}
 *       for random color generation.
 *   </li>
 *   <li>
 *       {@link game.Queue} — A fixed-length circular queue of {@code Piece} references that supports
 *       indexed access and automatic regeneration of elements as they are consumed. It does not support
 *       insertion of custom elements and is intended for predictable gameplay pacing. It require
 *       {@link special.SpecialFeature} to alter piece generation, {@link GUI.GameEssentials} and
 *       {@link Launcher.LaunchEssentials} for fetching essential settings.
 *   </li>
 * </ul>
 *
 * <h2>Design Notes</h2>
 * <p>
 * The package ensures stateless and deterministic behavior by isolating static generation logic inside
 * {@link game.PieceFactory} and controlling piece flow using the {@link game.Queue}. Difficulty modes
 * influence the types of pieces created, enabling basic or advanced gameplay without altering the
 * core mechanics.
 * <p>
 * All game pieces are referenced statically, meaning the system works with shared {@code Piece} instances
 * rather than unique copies, which simplifies memory management and consistency across gameplay sessions.
 * There is no cloning for {@link hex.Piece}.
 *
 * @since 0.6
 * @version 1.2
 */
package game;
