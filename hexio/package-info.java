/**
 * The {@code hexio} package provides a collection of utility classes designed for managing
 * hexadecimal-encoded data and game data logging in file systems in binary formats.
 * It is primarily used in conjunction with the {@code hex} package, which provides core hexagonal
 * grid-based game logic and models.
 * <p>
 * This package is centered around the {@link hexio.HexLogger} class to serve as logging tool
 * for the {@link hex} package, with other classes supporting file serialization and conversion needs.
 * <p>
 * The package includes subpackages and utility classes used to persist,
 * convert, and manage game-related data in various formats, including text-based
 * hexadecimal, and binary formats. It has the permission to create, write, read, and destroy files.
 *
 * <h2>Components</h2>
 * <h3>{@code hexio.hexdata} Subpackage</h3>
 * This subpackage provides low-level file and stream tools for writing and reading
 * hexadecimal content. It can be directly by end users, but mainly serves the higher-level
 * binary file logging mechanism of {@link hexio.HexLogger}.
 * <ul>
 *     <li>{@link hexio.hexdata.HexDataWriter} –
 *         Encodes primitive types and strings into hexadecimal representation and
 *         accumulates them in memory for writing to disk in either text or binary mode.
 *         Methods like {@code addInt(int)}, {@code addString(String)}, and {@code addHex(String)}
 *         are used to queue data for export.</li>
 *
 *     <li>{@link hexio.hexdata.HexDataReader} –
 *         Parses hexadecimal data from files and reconstructs original data values
 *         sequentially. Used in workflows involving {@code HexLogger.read()} or
 *         {@code HexLogger.readBinary()} to restore stored game state.</li>
 *
 *     <li>{@link hexio.hexdata.HexDataFactory} –
 *         A central factory class to create and manage {@code HexDataReader} and
 *         {@code HexDataWriter} instances, also handling reading/writing to file, and
 *         managing file paths. Offers simple static methods like {@code write(writer)}
 *         and {@code read(reader)}.</li>
 * </ul>
 *
 * <h3>Converter</h3>
 * <ul>
 *     <li>{@link hexio.HexDataConverter} –
 *         Converts the same set of game components to and from the binary and text-based
 *         hexadecimal format handled by {@code HexDataWriter} and {@code HexDataReader}.
 *         It enables low-overhead serialization for compact storage or obfuscated formats.</li>
 * </ul>
 *
 * <h3>Main Logger</h3>
 * <ul>
 *     <li>{@link hexio.HexLogger} –
 *         The central class of the package. This logger provides high-level API for
 *         logging game sessions into files. It can write binary logs ({@code .hpyhex}),
 *         stored in the {@code /data/} directory.
 *         It stores the game engine, piece queue, move history, and player info.
 *         Methods like {@code setEngine}, {@code setQueue}, {@code addMove}, and
 *         {@code completeGame} allow building the log step-by-step.
 *         It supports both writing and reading, allowing for full playback or
 *         reconstruction of a past game session.
 *     </li>
 * </ul>
 *
 * <h2>Logger Workflow</h2>
 * A typical usage of the logger involves the following steps:
 * <ol>
 *     <li>Create a {@link hexio.HexLogger} instance using a player's name and ID.</li>
 *     <li>Set the game engine and piece queue using {@link hexio.HexLogger#setEngine} and {@link hexio.HexLogger#setQueue}.</li>
 *     <li>Add moves to the log with {@link hexio.HexLogger#addMove} during gameplay.</li>
 *     <li>Once the game ends, call {@link hexio.HexLogger#completeGame} to mark the log final.</li>
 *     <li>Call {@code write()} to save in a compact binary format.</li>
 *     <li>Later, reconstruct the game state with {@code read()} or {@code read("hex.binary")} using a new {@code HexLogger} pointing to the same file name.</li>
 *     <li>Optionally call {@link hexio.HexLogger#deleteFile} to remove the saved game data.</li>
 * </ol>
 *
 * <h2>Versioning</h2>
 * This package supports data format versioning, with version {@code 1.3} currently supported
 * for {@code HexLogger}. See {@link hexio.HexLogger} for supported formats.
 *
 * <h2>Notes</h2>
 * <ul>
 *     <li>New versions of binary logs include user information and colors for game pieces. Old versions of binary logs are lighter and do not include user info.</li>
 *     <li>File names are generated using hash-based obfuscation and are guaranteed to be unique.</li>
 *     <li>Errors during file I/O or parsing throw {@code IOException}; callers must handle such cases.</li>
 *     <li>The package integrates with the {@code hex} game framework and depends on its core components.</li>
 * </ul>
 * @author William Wu
 * @version 1.3.2-binary
 * @since 1.3
 */
package hexio;
