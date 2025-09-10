package achievements;

import hex.GameState;
import hex.Hex;
import hex.HexEngine;
import hex.Piece;

/**
 * The {@code EnginePerfectFitAchievement} class represents a game achievement that is achieved when the player
 * has all piece in the queue that perfectly fits an empty slot in the game board. It implements the
 * {@link GameAchievementTemplate} interface and provides functionality to check if the achievement has been
 * achieved based on the current {@link GameState}.
 * <p>
 * This class checks if all pieces in the queue that can fit perfectly into any empty slot on the game board.
 * The achievement is considered achieved if this condition is met.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed. This ensures
 * that the achievement criteria remain consistent throughout its lifecycle.
 * <p>
 * The class does not provide serialization or deserialization methods, as it does not maintain any complex state.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class EngineAllPerfectFitAchievement implements GameAchievementTemplate {
    @Override
    public String name() {
        return "All Perfect Fit";
    }
    @Override
    public String description() {
        return "Have all pieces in queue fitting empty slots in the game board";
    }
    @Override
    public boolean test(GameState state) {
        HexEngine engine = state.getEngine();
        Piece[] queue = state.getQueue();
        for (Piece piece : queue) {
            boolean foundFit = false;
            for (Hex position : engine.blocks()) {
                if (engine.computeDenseIndex(position, piece) == 1) {
                    foundFit = true;
                    break;
                }
            }
            if (!foundFit) {
                return false; // this piece cannot fit in any block
            }
        }
        return true; // every piece had at least one block
    }
}
