package GUI;

import Hex.*;
import java.awt.Color;

public class EngineButton extends HexButton {
    public EngineButton(int index){
        super(index);
        super.resetSize();
    }
    protected int fetchHeightExtension(){
        return GameEssentials.engine().getRadius() - 1;
    }
    protected Block fetchBlock(){
        return GameEssentials.engine().getBlock(getIndex());
    }
    protected Color fetchColor() {
        Hex position = fetchBlock().thisHex();
        // Get indexes
        int pieceIndex = GameEssentials.getSelectedPieceIndex();
        int blockIndex = GameEssentials.getSelectedBlockIndex();
        // If selected
        if(blockIndex != -1) {
            // If is the range of potential blocks for adding
            if(isPotentialPieceBlock()){
                Color color = GameEssentials.queue().get(pieceIndex).getColor();
                return new Color((color.getRed() + 255)/2, (color.getGreen() + 255)/2, (color.getBlue() + 255)/2);
            }
            // Get piece
            Piece piece = GameEssentials.queue().get(pieceIndex);
            // Modify position relative to selected block
            position = position.subtract(piece.getBlock(blockIndex));
            if (GameEssentials.engine().checkAdd(position, piece)) {
                // If addition is possible
                return Color.GRAY;
            }
        }
        return super.fetchColor();
    }
    protected void clicked(){
        if(GameEssentials.getSelectedBlockIndex() != -1) {
            GameEssentials.turn++;
            // Fetch position
            Hex position = fetchBlock().thisHex();
            // Get index and reset index
            int pieceIndex = GameEssentials.getSelectedPieceIndex();
            int blockIndex = GameEssentials.getSelectedBlockIndex();
            GameEssentials.setSelectedPieceIndex(-1);
            // Get piece
            Piece piece = GameEssentials.queue().get(pieceIndex);
            // Modify position relative to selected block
            position = position.subtract(piece.getBlock(blockIndex));
            // Check this position, if good then add
            if (GameEssentials.engine().checkAdd(position, piece)) {
                GameEssentials.score += piece.length();
                GameEssentials.engine().add(position, GameEssentials.queue().fetch(pieceIndex));
            }
            // Paint and eliminate
            GameEssentials.window().repaint();
            if (GameEssentials.engine().checkEliminate()) {
                // Eliminate code is here, see GameTimer class
                new GameTimer().start();
            } else if (GameEssentials.checkEnd()) {
                System.out.println("---------- Game Over ----------");
                System.out.println("This game lasted for " + GameEssentials.turn + " turns.");
                System.out.println("The total score is " + GameEssentials.score + " points.");
                // Reset
                GameEssentials.score = 0;
                GameEssentials.turn = 0;
                GameEssentials.engine().reset();
                GameEssentials.window().repaint();
            }
        }
    }
    protected void hovered(){
        // Get indexes
        int pieceIndex = GameEssentials.getSelectedPieceIndex();
        int blockIndex = GameEssentials.getSelectedBlockIndex();
        if(!fetchBlock().getState() && blockIndex != -1) {
            // Get piece
            Piece piece = GameEssentials.queue().get(pieceIndex);
            // Modify position relative to selected block
            Hex position = fetchBlock().thisHex().subtract(piece.getBlock(blockIndex));
            if (GameEssentials.engine().checkAdd(position, piece)) {
                // If addition is possible
                GameEssentials.setHoveredOverIndex(getIndex());
            }
        }
    }
    protected void removed(){
        GameEssentials.setHoveredOverIndex(-1);
    }
    private boolean isPotentialPieceBlock(){
        if (GameEssentials.getHoveredOverIndex() != -1 && GameEssentials.getSelectedBlockIndex() != -1) {
            Hex hoverBlock = GameEssentials.engine().getBlock(GameEssentials.getHoveredOverIndex()).thisHex();
            Piece piece = GameEssentials.queue().get(GameEssentials.getSelectedPieceIndex());
            hoverBlock = hoverBlock.subtract(piece.getBlock(GameEssentials.getSelectedBlockIndex()));
            for (int i = 0; i < piece.length(); i++) {
                Hex position = piece.getBlock(i).thisHex();
                if (fetchBlock().thisHex().equals(hoverBlock.add(position))) {
                    return true;
                }
            }
        }
        return false;
    }
}
