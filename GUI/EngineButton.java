package GUI;

import Hex.*;

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
        // Delay for 100 and eliminate
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {}
        GameEssentials.engine().eliminate();
        // Repaint
        GameEssentials.window().repaint();
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
