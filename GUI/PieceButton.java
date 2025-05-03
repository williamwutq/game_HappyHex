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
    protected int fetchRawWidthExtension(){
        return GameEssentials.getPiecePanelWidthExtension();
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
            return GameEssentials.gamePieceSelectedColor;
        } else if(GameEssentials.getSelectedPieceIndex() == pieceIndex){
            // One in the piece is selected
            return GameEssentials.interpolate(GameEssentials.queue().get(pieceIndex).getColor(), GameEssentials.gameBackgroundColor, 1);
        } else return GameEssentials.queue().get(pieceIndex).getColor();
    }
    protected void clicked() {
        if(isTarget()){
            // Unselect
            GameEssentials.setSelectedPieceIndex(-1);
            GameEssentials.setClickedOnIndex(-1);
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
