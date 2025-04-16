package special.Logic;

import Hex.HexEngine;
import Hex.Piece;
import special.SpecialFeature;

import java.util.ArrayList;

public class GodMode implements SpecialFeature{
    private boolean enable;
    private boolean valid;
    public GodMode(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 5;
    }
    public int getGroupID() {
        return 11; // GameDifficulty
    }
    public String getFeatureName() {
        return "GodMode";
    }
    public String getGroupName() {
        return "GameDifficultyMode";
    }
    public String getFeatureDescription() {
        return "Intervene the piece generation to make sure the game does not end";
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
    private static int calculateAdditionOpportunities(HexEngine engine, Piece piece){
        int count = 0;
        for (int i = 0; i < engine.length(); i ++){
            if(engine.checkAdd(engine.getBlock(i).thisHex(), piece)){
                count++;
            }
        }
        return count;
    }
    public Object[] process(Object[] objects) {
        if(objects.length > 2 && objects[1] instanceof Boolean){
            if (!(Boolean) objects[1]) {
                if (objects[0] instanceof Piece && objects[2] instanceof HexEngine) {
                    // Implementation: get an easy one if the generated does not work
                    HexEngine engine = (HexEngine) objects[2];
                    if(calculateAdditionOpportunities(engine, (Piece)objects[0]) < 2){
                        ArrayList<Piece> candidates = new ArrayList<Piece>();
                        for (int index = 0; index < Piece.getMaxPieceIndex(); index ++){
                            Piece candidate = Piece.getIndexedPiece(index);
                            int opportunities = calculateAdditionOpportunities(engine, candidate);
                            if(opportunities > 3){
                                candidates.add(candidate);
                            }
                        }
                        return new Piece[]{candidates.get((int) (candidates.size() * Math.random()))};
                    }
                }
            }
        }
        return new Object[]{objects[0]};
    }
}
