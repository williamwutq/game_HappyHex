/**
 * Provides core logic components for the HappyHex game simulation and viewer.
 *
 * <p>
 * The {@code logic} package encapsulates the data-driven backend logic required
 * to load, interpret, track, and replay a game of HappyHex. It defines the primary
 * model-controller structure, which includes a historical game state tracker ({@link viewer.logic.Tracker}),
 * a user-driven controller ({@link viewer.logic.Controller}), and a set of standardized GUI interfaces
 * ({@link viewer.logic.GameGUIInterface}, {@link viewer.logic.InfoGUIInterface},
 * {@link viewer.logic.FileGUIInterface}). These components decouple game logic and rendering, enabling
 * robust replay functionality with thread-safe playback control, scalable GUI design, and high-fidelity
 * state reconstruction.
 *
 * <h2>Package Overview</h2>
 * The package is designed with the Model-View-Controller (MVC) paradigm in mind:
 * <ul>
 *     <li><b>Model:</b> {@link viewer.logic.Tracker}, which stores immutable state history and metadata.</li>
 *     <li><b>Controller:</b> {@link viewer.logic.Controller}, which acts as the central coordinator, responding to user input and GUI events.</li>
 *     <li><b>View:</b> GUI implementations based on {@link viewer.logic.GameGUIInterface},
 *     {@link viewer.logic.InfoGUIInterface}, and {@link viewer.logic.FileGUIInterface}.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Step 1: Set up custom GUI components implementing the required interfaces
 * GameGUIInterface gameView = new GameGUI();
 * InfoGUIInterface infoView = new ScoreGUI();
 * FileGUIInterface fileView = new FileGUI();
 *
 * // Step 2: Create controller to wire everything together
 * Controller controller = new Controller();
 *
 * // Step 3: Bind GUIs
 * controller.bindGameGUI(gameView);
 * controller.bindInfoGUI(infoView);
 * controller.bindFileGUI(fileView);
 *
 * // Step 4: Execute actions
 * controller.setSpeed(50);
 * controller.run();
 * }</pre>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *     <li><b>Tracker Immutability:</b>
 *         {@link viewer.logic.Tracker} generates and caches full, immutable state snapshots for each turn,
 *         unlike {@link hexio.HexLogger}, which stores deltas. This makes Tracker optimal for random
 *         access and reliable replay without side effects.</li>
 *
 *     <li><b>Thread-Safe Playback:</b>
 *         The {@link viewer.logic.Controller} manages playback in a separate thread using safe interruption
 *         logic, ensuring the GUI remains responsive even during long replays.</li>
 *
 *     <li><b>Separation of Concerns:</b>
 *         Each GUI interface is designed with minimal coupling. GUI classes can evolve independently
 *         as long as they adhere to the respective interface contracts. This promotes testability
 *         and platform flexibility (e.g., Swing, JavaFX, or web frontends).</li>
 *
 *     <li><b>Error Handling:</b>
 *         Construction of {@link viewer.logic.Tracker} validates logger integrity and will throw an
 *         {@link java.lang.IllegalArgumentException} on malformed data. All index-based access
 *         is range-checked and throws {@link java.lang.IndexOutOfBoundsException} if violated.</li>
 * </ul>
 *
 * <h2>Key Classes and Interfaces</h2>
 * <ul>
 *     <li><b>{@link viewer.logic.Tracker}:</b>
 *         Reconstructs and holds the full sequence of game states, including board state, queue, placement
 *         coordinates, and cumulative scores. Provides read-only access with pointer-based navigation.</li>
 *
 *     <li><b>{@link viewer.logic.Controller}:</b>
 *         Controls user interactions and playback flow. Mediates between {@code Tracker} and GUI components.
 *         Provides step-through and automated playback features, and handles file input/output updates.</li>
 *
 *     <li><b>{@link viewer.logic.GameGUIInterface}:</b>
 *         Defines how the game engine view is updated and rendered in a GUI component.</li>
 *     <li><b>{@link viewer.logic.InfoGUIInterface}:</b>
 *         Defines how the current turn and score are displayed and synchronized with game state.</li>
 *     <li><b>{@link viewer.logic.FileGUIInterface}:</b>
 *         Defines how file inputs and display metadata (e.g., file name) are handled by the GUI.</li>
 * </ul>
 *
 * <h2>Threading Model</h2>
 * The {@code Controller} uses cooperative thread interruption to run and stop playback via a {@code Thread}.
 * All GUI-related callbacks must be synchronized with the UI thread (e.g., Swing EDT) when updating view components.
 *
 * @see viewer.logic.Tracker
 * @see viewer.logic.Controller
 * @see viewer.logic.GameGUIInterface
 * @see viewer.logic.InfoGUIInterface
 * @see viewer.logic.FileGUIInterface
 * @see hex.HexEngine
 * @see hex.Piece
 * @see hex.Hex
 */
package viewer.GameViewer.app.Contents.app.logic;