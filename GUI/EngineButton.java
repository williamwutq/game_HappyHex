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
        if(GameEssentials.engine().checkAdd(position, GameEssentials.queue().getFirst())) {
            return Color.GRAY;
        }
        return super.fetchColor();
    }
    public void clicked(){
        GameEssentials.turn ++;
        GameEssentials.setSelectedPieceIndex(-1);
        // Fetch position
        Hex position = fetchBlock().thisHex();
        // Check this position, if good then add
        if(GameEssentials.engine().checkAdd(position, GameEssentials.queue().getFirst())){
            GameEssentials.score += GameEssentials.queue().getFirst().length();
            GameEssentials.engine().add(position, GameEssentials.queue().next());
        }
        // Paint and eliminate
        GameEssentials.window().repaint();
        if(GameEssentials.engine().checkEliminate()) {
            // Eliminate code is here, see GameTimer class
            new GameTimer().start();
        } else if (GameEssentials.checkEnd()){
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
