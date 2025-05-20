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

package special.Logic;

import GUI.GameEssentials;
import hex.HexEngine;
import hex.Piece;
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
            if (objects.length > 3 && objects[3] instanceof Piece) {
                engine = engine.clone();
                if (GameEssentials.getClickedOnIndex() != -1 && GameEssentials.getSelectedBlockIndex() != -1) {
                    hex.Hex position = GameEssentials.queue().get(GameEssentials.getSelectedPieceIndex()).getBlock(GameEssentials.getSelectedBlockIndex());
                    engine.add(engine.getBlock(GameEssentials.getClickedOnIndex()).subtract(position), (Piece) objects[3]);
                }
            }
            if (calculateAdditionOpportunities(engine, (Piece) objects[0]) < 2) {
                ArrayList<Piece> candidates = new ArrayList<Piece>();
                for (int index = 0; index < game.PieceFactory.getMaxPieceIndex(); index++) {
                    Piece candidate = game.PieceFactory.getIndexedPiece(index);
                    int opportunities = calculateAdditionOpportunities(engine, candidate);
                    if (opportunities > 0) {
                        candidates.add(candidate);
                    }
                }
                if(candidates.size() == 0) candidates.add(game.PieceFactory.uno());
                return new Piece[]{candidates.get((int) (candidates.size() * Math.random()))};
            }
        }
        return new Object[]{objects[0]};
    }
}
