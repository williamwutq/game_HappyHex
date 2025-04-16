/**
 * Provides classes and interfaces for managing a hexagonal grid system,
 * including coordinate calculations, game engine operations, and piece queuing.
 * <p>
 * This package includes:
 * <ul>
 *   <li>{@link Hex.Hex} – Represents 2D coordinates in a hexagonal grid and provides methods for raw and line-based computations.</li>
 *   <li>{@link Hex.Block} – Extends {@code Hex} to include a color and an occupancy state for blocks in the grid.</li>
 *   <li>{@link Hex.HexGrid} – Defines the interface for grids composed of {@code Block} elements.</li>
 *   <li>{@link Hex.Piece} – Models a game piece as a collection of {@code Block} objects arranged as a smaller hex grid.</li>
 *   <li>{@link Hex.HexEngine} – Implements {@code HexGrid} to manage the overall hexagonal grid for game purposes,
 *       including block lookup, grid reset, and elimination operations.</li>
 *   <li>{@link Hex.Queue} – Maintains a fixed-length queue of {@code Piece} objects, automatically generating new pieces upon consumption.</li>
 * </ul>
 * </p>
 *
 * @author William Wu
 * @version 1.1
 */
package Hex;
