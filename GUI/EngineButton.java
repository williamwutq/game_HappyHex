/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package GUI;

import hex.*;
import java.awt.Color;

public class EngineButton extends HexButton {
    public EngineButton(int index){
        super(index);
        super.resetSize();
    }
    protected int fetchHeightExtension(){
        return GameEssentials.engine().getRadius() - 1;
    }
    protected int fetchRawHeightExtension(){
        return GameEssentials.getGamePanelHeightExtension();
    }
    protected int fetchRawWidthExtension(){
        return GameEssentials.getGamePanelWidthExtension();
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
            if(isPotentialPieceBlock(getIndex())){
                Color color = GameEssentials.queue().get(pieceIndex).getColor().getColor();
                return GameEssentials.interpolate(color, GameEssentials.gameBackgroundColor, 1);
            }
            // Get piece
            Piece piece = GameEssentials.queue().get(pieceIndex);
            // Modify position relative to selected block
            position = position.subtract(piece.getBlock(blockIndex));
            if (GameEssentials.engine().checkAdd(position, piece)) {
                // If addition is possible
                return GameEssentials.interpolate(super.fetchColor(), GameEssentials.gameBackgroundColor, 1);
            }
            if (isPotentialEliminationTarget()){
                return GameEssentials.interpolate(super.fetchColor(), GameEssentials.gameBackgroundColor, 1);
            }
        }
        return super.fetchColor();
    }
    protected void clicked(){
        // Communicate clicked index
        GameEssentials.setClickedOnIndex(getIndex());
        if(GameEssentials.getSelectedBlockIndex() != -1) {
            GameEssentials.incrementTurn();
            // Fetch position
            Hex position = fetchBlock().thisHex();
            // Get index
            int pieceIndex = GameEssentials.getSelectedPieceIndex();
            int blockIndex = GameEssentials.getSelectedBlockIndex();
            // Get piece
            Piece piece = GameEssentials.queue().get(pieceIndex);
            // Modify position relative to selected block
            position = position.subtract(piece.getBlock(blockIndex));
            // Check this position, if good then add
            if (GameEssentials.engine().checkAdd(position, piece)) {
                GameEssentials.incrementScore(piece.length());
                GameEssentials.move(position);
                GameEssentials.engine().add(position, GameEssentials.queue().fetch(pieceIndex));
                // Generate animation
                for (int i = 0; i < piece.length(); i ++){
                    GameEssentials.addAnimation(GameEssentials.createCenterEffect(piece.getBlock(i).add(position)));
                }
            }
            // Reset index
            GameEssentials.setSelectedPieceIndex(-1);
            GameEssentials.setClickedOnIndex(-1);
            // Paint and eliminate
            GameEssentials.window().repaint();
            if (GameEssentials.engine().checkEliminate()) {
                // Eliminate code is here, see GameTimer class
                new GameTimer().start();
            } else GameEssentials.checkEnd();
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
    private boolean isPotentialPieceBlock(int index){
        if (GameEssentials.getHoveredOverIndex() != -1 && GameEssentials.getSelectedBlockIndex() != -1) {
            Hex hoverBlock = GameEssentials.engine().getBlock(GameEssentials.getHoveredOverIndex()).thisHex();
            Piece piece = GameEssentials.queue().get(GameEssentials.getSelectedPieceIndex());
            HexEngine engine = GameEssentials.engine();
            hoverBlock = hoverBlock.subtract(piece.getBlock(GameEssentials.getSelectedBlockIndex()));
            for (int i = 0; i < piece.length(); i++) {
                Hex position = piece.getBlock(i).thisHex();
                if (engine.getBlock(index).thisHex().equals(hoverBlock.add(position))) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isPotentialEliminationTarget(){
        if (GameEssentials.getHoveredOverIndex() != -1 && GameEssentials.getSelectedBlockIndex() != -1) {
            Hex hoverBlock = GameEssentials.engine().getBlock(GameEssentials.getHoveredOverIndex()).thisHex();
            Piece piece = GameEssentials.queue().get(GameEssentials.getSelectedPieceIndex());
            hoverBlock = hoverBlock.subtract(piece.getBlock(GameEssentials.getSelectedBlockIndex()));
            for (int i = 0; i < piece.length(); i++) {
                Hex position = hoverBlock.add(piece.getBlock(i).thisHex());
                Hex current = fetchBlock().thisHex();
                if(simulateEliminateI(position.getLineI()) && current.inLineI(position) ||
                   simulateEliminateJ(position.getLineJ()) && current.inLineJ(position) ||
                   simulateEliminateK(position.getLineK()) && current.inLineK(position)
                ) return true;
            }
        }
        return false;
    }
    private boolean simulateEliminateI(int i){
        HexEngine engine = GameEssentials.engine();
        for(int index = 0; index < engine.length(); index ++){
            if(engine.getBlock(index).getLineI() == i){
                // Found block
                if(!(engine.getBlock(index).getState() || isPotentialPieceBlock(index))){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean simulateEliminateJ(int j){
        HexEngine engine = GameEssentials.engine();
        for(int index = 0; index < engine.length(); index ++){
            if(engine.getBlock(index).getLineJ() == j){
                // Found block
                if(!(engine.getBlock(index).getState() || isPotentialPieceBlock(index))){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean simulateEliminateK(int k){
        HexEngine engine = GameEssentials.engine();
        for(int index = 0; index < engine.length(); index ++){
            if(engine.getBlock(index).getLineK() == k){
                // Found block
                if(!(engine.getBlock(index).getState() || isPotentialPieceBlock(index))){
                    return false;
                }
            }
        }
        return true;
    }
}
