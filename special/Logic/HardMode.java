package special.Logic;

import game.PieceFactory;
import hex.Block;
import hex.HexEngine;
import hex.Piece;
import special.SpecialFeature;

import java.util.ArrayList;

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
        p.add(-1, -1);
        p.add(-1, 0);
        p.add(0, -1);
        p.add(0, 1);
        p.add(1, 0);
        p.add(1, 1);
        return p;
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
        if(isActive() && objects.length > 2 && objects[1] instanceof Boolean){
            if (!(Boolean) objects[1]) {
                if (objects[0] instanceof Piece && objects[2] instanceof HexEngine) {
                    // Implementation: get the most difficult pne
                    HexEngine engine = (HexEngine) objects[2];
                    ArrayList<Piece> candidates = new ArrayList<Piece>();
                    for (int index = 0; index < PieceFactory.getMaxPieceIndex() - 2; index ++){
                        Piece candidate = PieceFactory.getIndexedPiece(index);
                        int opportunities = calculateAdditionOpportunities(engine, candidate);
                        if(opportunities < 6){
                            candidates.add(candidate);
                        }
                    }
                    if(candidates.size() != 0){
                        return new Piece[]{candidates.get((int) (candidates.size() * Math.random()))};
                    }
                    int rando = (int)(Math.random() * 13);
                    Piece result = (Piece) objects[0];
                    if (rando == 0){
                        if(calculateAdditionOpportunities(engine, hollow()) > 0) {
                            result = hollow();
                        }
                    } if (rando == 1 || rando == 9){
                        if(calculateAdditionOpportunities(engine, PieceFactory.fan4A()) > 0) {
                            result = PieceFactory.fan4A();
                        }
                    } else if (rando == 2|| rando == 10){
                        if(calculateAdditionOpportunities(engine, PieceFactory.fan4B()) > 0) {
                            result = PieceFactory.fan4B();
                        }
                    } else if (rando == 3){
                        if(calculateAdditionOpportunities(engine, PieceFactory.fan4A()) > 0) {
                            result = PieceFactory.corner4Il();
                        }
                    } else if (rando == 4){
                        if(calculateAdditionOpportunities(engine, PieceFactory.corner4Ir()) > 0) {
                            result = PieceFactory.corner4Ir();
                        }
                    } else if (rando == 5){
                        if(calculateAdditionOpportunities(engine, PieceFactory.corner4Jl()) > 0) {
                            result = PieceFactory.corner4Jl();
                        }
                    } else if (rando == 6){
                        if(calculateAdditionOpportunities(engine, PieceFactory.corner4Jr()) > 0) {
                            result = PieceFactory.corner4Jr();
                        }
                    } else if (rando == 7){
                        if(calculateAdditionOpportunities(engine, PieceFactory.corner4Kl()) > 0) {
                            result = PieceFactory.corner4Kl();
                        }
                    } else if (rando == 8){
                        if(calculateAdditionOpportunities(engine, PieceFactory.corner4Kr()) > 0) {
                            result = PieceFactory.corner4Kr();
                        }
                    }
                    return new Object[]{result};
                }
            }
        }
        return new Object[]{objects[0]};
    }
}
