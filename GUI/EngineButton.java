package GUI;

import Hex.*;
import java.awt.Color;

public class EngineButton extends HexButton {
    public EngineButton(int index){
        super(index);
    }
    protected Block fetchBlock(){
        return GameEssentials.engine().getBlock(getIndex());
    }
    protected Color fetchColor() {
        Hex position = fetchBlock().thisHex();
        for(int i = 0; i < GameEssentials.queue().length(); i ++){
            if(GameEssentials.engine().checkAdd(position, GameEssentials.queue().get(i))) {
                return Color.GRAY;
            }
        }
        return super.fetchColor();
    }

    public void clicked(){
        // Fetch position
        Hex position = fetchBlock().thisHex();
        // Check this position, if good then add
        if(GameEssentials.engine().checkAdd(position, GameEssentials.queue().getFirst())){
            GameEssentials.engine().add(position, GameEssentials.queue().next());
        } else {
            // For now: check queue
            checkQueue();
        }
        // Paint and eliminate
        GameEssentials.window().repaint();
        if(GameEssentials.engine().checkEliminate()) {
            // Eliminate code is here, see GameTimer class
            new GameTimer().start();
        } else if (GamePanel.checkEnd()){
            // Reset
            GameEssentials.engine().reset();
            GameEssentials.window().repaint();
        }
    }
    private void checkQueue(){
        Hex position = fetchBlock().thisHex();
        for(int i = 0; i < GameEssentials.queue().length(); i ++){
            if(GameEssentials.engine().checkAdd(position, GameEssentials.queue().get(i))){
                GameEssentials.engine().add(position, GameEssentials.queue().fetch(i));
                break;
            }
        }
    }
}
