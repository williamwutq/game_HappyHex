package special.Logic;

import Hex.Block;
import Hex.HexEngine;
import Hex.Piece;
import special.SpecialFeature;

public class HardMode implements SpecialFeature{
    private boolean enable;
    private boolean valid;
    public HardMode(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 2;
    }
    public int getGroupID() {
        return 11; // GameDifficulty
    }
    public String getFeatureName() {
        return "HardMode";
    }
    public String getGroupName() {
        return "GameDifficultyMode";
    }
    public String getFeatureDescription() {
        return "Make the normal version of the game harder";
    }
    public String getFeatureTarget() {
        return "Piece";
    }
    public int getSupportVersionMajor() {
        return 1;
    }
    public int getSupportVersionMinor() {
        return 1;
    }
    public boolean validate() {
        if(special.Special.getCurrentVersionMajor() > getSupportVersionMajor()){
            valid = true;
        } else if (special.Special.getCurrentVersionMajor() == getSupportVersionMajor()){
            valid = special.Special.getCurrentVersionMinor() >= getSupportVersionMinor();
        } else valid = false;
        return valid;
    }
    public void enable() {
        enable = true;
    }
    public void disable() {
        enable = false;
    }
    public boolean isActive() {
        return enable && valid;
    }
    private static Piece hollow(){
        Piece p = new Piece(6, GUI.GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public Object[] process(Object[] objects) {
        if(objects.length > 2 && objects[1] instanceof Boolean){
            if (!(Boolean) objects[1]) {
                if (objects[0] instanceof Piece && objects[2] instanceof HexEngine) {
                    // Implementation: get the most difficult pne
                    HexEngine engine = (HexEngine) objects[2];
                    for (int index = 0; index < Piece.getMaxPieceIndex(); index ++){
                        Piece candidate = Piece.getIndexedPiece(index);
                        boolean addable = false;
                        for (int i = 0; i < engine.length(); i ++){
                            if(engine.checkAdd(engine.getBlock(i).thisHex(), candidate)){
                                addable = true; break;
                            }
                        }
                        if(!addable){
                            return new Object[]{candidate};
                        }
                    }
                    int rando = (int)(Math.random() * 9);
                    Piece result = hollow();
                    if (rando == 1){
                        result = Piece.fan4A();
                    } else if (rando == 2){
                        result = Piece.fan4A();
                    } else if (rando == 3){
                        result = Piece.corner4Il();
                    } else if (rando == 4){
                        result = Piece.corner4Ir();
                    } else if (rando == 5){
                        result = Piece.corner4Jl();
                    } else if (rando == 6){
                        result = Piece.corner4Jr();
                    } else if (rando == 7){
                        result = Piece.corner4Kl();
                    } else if (rando == 8){
                        result = Piece.corner4Kr();
                    }
                    return new Object[]{result};
                }
            }
        }
        return new Object[]{objects[0]};
    }
}
