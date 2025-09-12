package hex;

/**
 * The {@code GameState} interface defines the contract for accessing the current state of a game.
 * It provides methods to retrieve the game engine, piece queue, score, and turn number.
 * This interface is essential for components that need to interact with or display the game's state.
 * <p>
 * This is a read-only interface; it does not provide methods to modify the game state, as that many
 * implementations may choose to make the game state immutable or readonly.
 * <p>
 * Implementing classes do not need to guarantee immutability. This means that the state can change over time,
 * however, if immutability is not guaranteed, it is the responsibility of the implementing class to ensure that
 * the state is not modified in a way that violates the intended use of this interface. Legitimate users of this
 * interface should not attempt to modify the state directly. For example, if necessary, clone the returned
 * {@link Piece} array and {@link HexEngine }to avoid unintended side effects.
 *
 * @see HexEngine
 * @see Piece
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public interface GameState {
    /**
     * Returns the current state of the game engine.
     * @return the current {@link HexEngine} representing the game state
     */
    HexEngine getEngine();
    /**
     * Returns the current {@code Piece} queue in the game.
     * @return an array of {@link Piece} objects representing the current piece queue
     */
    Piece[] getQueue();
    /**
     * Returns the current score of the game.
     * @return the current score
     */
    int getScore();
    /**
     * Returns the current turn number in the game.
     * @return the current turn number
     */
    int getTurn();
}
