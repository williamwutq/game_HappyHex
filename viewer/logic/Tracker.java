package viewer.logic;

import hex.Hex;
import hex.HexEngine;
import hex.Piece;
import hexio.HexLogger;

import java.io.IOException;

public class Tracker {
    HexEngine[] engines;
    Hex[] origins;
    Piece[][] queues;
    int[] scores;
    int length;
    public Tracker(HexLogger logger){
        this.length = logger.getTurn() + 1;
        this.engines = new HexEngine[length];;
        this.queues = new Piece[length][logger.getQueue().length];
        this.scores = new int[length];
        // Populate
        scores[0] = 0;
        engines[0] = new HexEngine(logger.getEngine().getRadius());
        queues = logger.getMoveQueues();
        origins = logger.getMoveOrigins();
        for (int turn = 1; turn < length; turn ++){
            HexEngine duplicatedEngine = engines[turn-1].clone();
            try{
                Piece piece = logger.getMovePieces()[turn-1];
                duplicatedEngine.add(origins[turn-1], piece);
                scores[turn] += piece.length();
                scores[turn] += 5 * duplicatedEngine.eliminate().length;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tracker cannot be created because error occurred reading game data, ", e.getCause());
            }
            engines[turn] = duplicatedEngine;
        }
    }
    public static void main(String[] args) throws IOException {
        HexLogger logger = HexLogger.generateBinaryLoggers().getFirst();
        logger.readBinary();
        Tracker tracker = new Tracker(logger);
    }
}
