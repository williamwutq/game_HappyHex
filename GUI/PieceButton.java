package GUI;

import Hex.*;
import java.awt.Color;

public class PieceButton extends HexButton {
    private int pieceIndex;
    public PieceButton(int pieceIndex, int blockIndex){
        super(blockIndex);
        this.pieceIndex = pieceIndex;
    }

    protected int fetchHeightExtension() {
        // Depend on block, searching for min distance to J
        Piece piece = GameEssentials.queue().get(pieceIndex);
        int minPosition = 0;
        for(int i = 0; i < piece.length(); i ++){
            int currentPosition = piece.getBlock(i).getLineJ();
            if(currentPosition < minPosition){
                minPosition = currentPosition;
            }
        }
        return -minPosition;
    }
    protected int fetchWidthExtension() {
        // Depend on block, searching for min J value
        Piece piece = GameEssentials.queue().get(pieceIndex);
        int minPosition = 0;
        for(int i = 0; i < piece.length(); i ++){
            int currentPosition = piece.getBlock(i).J();
            if(currentPosition < minPosition){
                minPosition = currentPosition;
            }
        }
        return -minPosition;
    }

    protected Block fetchBlock() {
        return GameEssentials.queue().get(pieceIndex).getBlock(getIndex());
    }
    protected Color fetchColor() {
        return GameEssentials.queue().get(pieceIndex).getColor();
    }
    protected void clicked() {
        // Do nothing currently
    }
}
