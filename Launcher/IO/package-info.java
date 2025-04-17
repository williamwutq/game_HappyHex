/**
 * Provides I/O utilities and JSON serialization for game-related data structures.
 * <p>
 * The {@code Launcher.IO} package enables reading, writing, and converting
 * game metadata—including player information, game sessions, and configuration presets—
 * into JSON format for persistent storage or transmission.
 * <p>
 * Core components include:
 * <ul>
 *   <li>{@link Launcher.IO.GameInfo} – Representation and data storage of a game session.</li>
 *   <li>{@link Launcher.IO.PlayerInfo} – Persistent tracking of player statistics.</li>
 *   <li>{@link Launcher.IO.GameTime} – Timestamp of a game session with time zone support.</li>
 *   <li>{@link Launcher.IO.GameVersion} – Semantic game versioning.</li>
 *   <li>{@link Launcher.IO.Username} – Validated, immutable player name.</li>
 *   <li>{@link Launcher.IO.GameMode} – Enum for difficulty and game size presets.</li>
 *   <li>{@link Launcher.IO.JsonConvertible} – Interface for JSON serialization.</li>
 *   <li>{@link Launcher.IO.LaunchLogger} – Static utility for reading and writing logs in JSON.</li>
 * </ul>
 * <p>
 * This package depends on {@link javax.json} for JSON handling, and ensures
 * consistent serialization through the {@link Launcher.IO.JsonConvertible} interface.
 */
package Launcher.IO;