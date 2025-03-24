package GUI;

import Hex.*;

public class EngineButton extends HexButton {
    public EngineButton(int index){
        super(index);
    }
    protected Block fetchBlock(){
        return GameEssentials.engine().getBlock(getIndex());
    }
    public void clicked(){
        // Fetch position
        Hex position = fetchBlock().thisHex();
        // Check this position, if good then add
        if(GameEssentials.engine().checkAdd(position, GameEssentials.queue().getFirst())){
            GameEssentials.engine().add(position, GameEssentials.queue().next());
        }
        // Delay for 100 and eliminate
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {}
        GameEssentials.engine().eliminate();
        // Repaint
        GameEssentials.window().repaint();
    }
}
