/**
 * Provides classes and interfaces for managing a hexagonal grid system and colors,
 * including coordinate calculations, game engine operations, and piece queuing.
 * <p>
 * This package includes:
 * <ul>
 *   <li>{@link hex.Hex} – Represents 2D coordinates in a hexagonal grid and provides methods for raw and line-based computations.</li>
 *   <li>{@link hex.SolidColor} – Wrapper of {@code Color} to exclude explicit transparency and provide indexed coloring system.</li>
 *   <li>{@link hex.Block} – Extends {@code hex} to include a color and an occupancy state for blocks in the grid.</li>
 *   <li>{@link hex.HexGrid} – Defines the interface for grids composed of {@code Block} elements.</li>
 *   <li>{@link hex.Piece} – Models a game piece as a collection of {@code Block} objects arranged as a smaller hex grid.</li>
 *   <li>{@link hex.HexEngine} – Implements {@code HexGrid} to manage the overall hexagonal grid for game purposes,
 *       including block lookup, grid reset, and elimination operations.</li>
 * </ul>
 * </p>
 * @since 1.0
 * @author William Wu
 * @version 1.3
 */
package hex;
