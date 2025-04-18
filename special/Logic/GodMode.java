package special.Logic;

import GUI.GameEssentials;
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
        if(isActive() && objects.length > 2 && objects[0] instanceof Piece && objects[1] instanceof Boolean && objects[2] instanceof HexEngine){
            // Implementation: get an easy one if the generated does not work
            HexEngine engine = (HexEngine) objects[2];
            if (objects.length > 3 && objects[3] instanceof Piece) try {
                engine = (HexEngine) engine.clone();
                if(GameEssentials.getClickedOnIndex() != -1 && GameEssentials.getSelectedBlockIndex() != -1){
                    Hex.Hex position = GameEssentials.queue().get(GameEssentials.getSelectedPieceIndex()).getBlock(GameEssentials.getSelectedBlockIndex());
                    engine.add(engine.getBlock(GameEssentials.getClickedOnIndex()).subtract(position), (Piece) objects[3]);
                }
            } catch (CloneNotSupportedException e) {}
            if (calculateAdditionOpportunities(engine, (Piece) objects[0]) < 2) {
                ArrayList<Piece> candidates = new ArrayList<Piece>();
                for (int index = 0; index < Piece.getMaxPieceIndex(); index++) {
                    Piece candidate = Piece.getIndexedPiece(index);
                    int opportunities = calculateAdditionOpportunities(engine, candidate);
                    if (opportunities > 0) {
                        candidates.add(candidate);
                    }
                }
                if(candidates.size() == 0) candidates.add(Piece.uno());
                return new Piece[]{candidates.get((int) (candidates.size() * Math.random()))};
            }
        }
        return new Object[]{objects[0]};
    }
}
