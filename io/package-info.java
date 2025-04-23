/**
 * Provides I/O utilities and JSON serialization for game-related data structures.
 * <p>
 * The {@code io} package enables reading, writing, and converting
 * game metadata—including player information, game sessions, and configuration presets—
 * into JSON format for persistent storage or transmission.
 * <p>
 * Core components include:
 * <ul>
 *   <li>{@link io.GameInfo} – Representation and data storage of a game session.</li>
 *   <li>{@link io.PlayerInfo} – Persistent tracking of player statistics.</li>
 *   <li>{@link io.GameTime} – Timestamp of a game session with time zone support.</li>
 *   <li>{@link io.GameVersion} – Semantic game versioning.</li>
 *   <li>{@link io.Username} – Validated, immutable player name.</li>
 *   <li>{@link io.GameMode} – Enum for difficulty and game size presets.</li>
 *   <li>{@link io.JsonConvertible} – Interface for JSON serialization.</li>
 *   <li>{@link io.LaunchLogger} – Static utility for reading and writing logs in JSON.</li>
 * </ul>
 * <p>
 * This package depends on {@link javax.json} for JSON handling, and ensures
 * consistent serialization through the {@link io.JsonConvertible} interface.
 * @since 1.0
 */
package io;