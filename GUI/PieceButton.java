package GUI;

import Hex.*;
import java.awt.Color;

public class PieceButton extends HexButton {
    public PieceButton(int index){
        super(index);
    }
    protected Block fetchBlock() {
        return GameEssentials.queue().getFirst().getBlock(getIndex());
    }
    protected Color fetchColor() {
        return GameEssentials.queue().getFirst().getColor();
    }
    protected void clicked() {
        // Do nothing currently
    }
}
