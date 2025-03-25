package GUI;

import Hex.*;
import java.awt.Color;

public class PieceButton extends HexButton {
    private final int pieceIndex;
    public PieceButton(int pieceIndex, int blockIndex){
        super(blockIndex);
        this.pieceIndex = pieceIndex;
        super.resetSize();
    }

    protected int fetchHeightExtension() {
        return 1;
    }
    protected int fetchWidthExtension() {
        return 2 + 6 * pieceIndex;
    }

    protected Block fetchBlock() {
        Piece piece = GameEssentials.queue().get(pieceIndex);
        try{
            return piece.getBlock(getIndex());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    protected Color fetchColor() {
        if(isTarget()){
            return Color.WHITE;
        } else return GameEssentials.queue().get(pieceIndex).getColor();
    }
    protected void clicked() {
        if(isTarget()){
            // Unselect
            GameEssentials.setSelectedPieceIndex(-1);
        } else {
            // Select
            GameEssentials.setSelectedPieceIndex(pieceIndex);
            GameEssentials.setSelectedBlockIndex(super.getIndex());
        }
    }
    private boolean isTarget(){
        // Whether the selected block is this
        return GameEssentials.getSelectedPieceIndex() == pieceIndex && GameEssentials.getSelectedBlockIndex() == super.getIndex();
    }
}
