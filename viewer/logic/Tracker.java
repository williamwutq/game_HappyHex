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
    int length;
    public Tracker(HexLogger logger){
        this.length = logger.getTurn();
        this.engines = new HexEngine[length];;
        this.queues = new Piece[length][logger.getQueue().length];
        // Populate
        engines[0] = new HexEngine(logger.getEngine().getRadius());
        queues = logger.getMoveQueues();
        for (int turn = 1; turn < length; turn ++){
            HexEngine duplicatedEngine = engines[turn-1].clone();
            try{
                duplicatedEngine.add(origins[turn], logger.getMovePieces()[turn]);
                duplicatedEngine.eliminate();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tracker cannot be created because error occurred reading game data, ", e.getCause());
            }
        }
    }
    public static void main(String[] args) throws IOException {
        HexLogger logger = HexLogger.generateBinaryLoggers().getFirst();
        logger.readBinary();

    }
}
