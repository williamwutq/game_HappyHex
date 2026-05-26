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

/*
  This theme is a special date-dependent default theme of the HappyHex Game.
  It is not intend to be disabled by the user, and shall not be used outside
  its typical applications in any mods of this game. Original author of HappyHex
  William Wu reserve all rights.
*/

package special.WinterSuki;

import special.SpecialFeature;

import java.awt.*;

public class WinterSuki implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public WinterSuki(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 7;
    }
    public int getGroupID() {
        return 1; // default
    }
    public String getFeatureName() {
        return "WinterSuki";
    }
    public String getGroupName() {
        return "ColorModifier";
    }
    public String getFeatureDescription() {
        return "This feature is dedicated to my friend Suki, who loves ice skating and really enjoys all kinds of food. She came up with the theme 'Snow', so here is the theme for her! " +
               "This theme causes ice cream and snowflake animations to appear when blocks are cleared, and changes the color scheme of the game to a winter theme. ";
    }
    public String getFeatureTarget() {
        return "Animation, GUI, LaunchGUI, LauncherEssentials, GameEssentials";
    }
    public int getSupportVersionMajor() {
        return 1;
    }
    public int getSupportVersionMinor() {
        return 2;
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
    public boolean validate() {
        if(special.Special.getCurrentVersionMajor() > getSupportVersionMajor()){
            valid = true;
        } else if (special.Special.getCurrentVersionMajor() == getSupportVersionMajor()){
            valid = special.Special.getCurrentVersionMinor() >= getSupportVersionMinor();
        } else valid = false;
        return valid;
    }
    public Object[] process(Object[] objects) {
        if (isActive()){
            if (objects == null || objects.length == 0) return null;
            if (objects[0] instanceof GUI.animation.DisappearEffect && objects[1] instanceof hex.Block) {
                return new Object[]{new SnowFlakeAnimation((hex.Block) objects[1])};
            } else if (objects[0] instanceof GUI.animation.CenteringEffect && objects[1] instanceof hex.Block) {
                hex.Block b = (hex.Block) objects[1];
                if (GUI.GameEssentials.generateColor(b.getColor()).equals(GUI.GameEssentials.gameBlockDefaultColor)) {
                    return new Object[]{new GUI.animation.NullEffect()};
                }
                return new Object[]{new IceCreamAnimation(b)};
            }
            boolean isColorBaseArray = objects.length == 6 || objects.length == 12;
            int k = 0;
            while (k < objects.length && isColorBaseArray) {
                if (!(objects[k] instanceof Color)) {
                    isColorBaseArray = false;
                } else k++;
            }
            if (isColorBaseArray){
                return new Color[]{
                        new Color(108, 180, 218), // ice blue
                        new Color( 68, 128, 178), // glacier cerulean
                        new Color( 42,  82, 145), // deep navy
                        new Color( 88, 148, 188), // steel blue
                        new Color(132, 108, 192), // twilight violet
                        new Color(168, 138, 198), // dusty lavender
                        new Color( 52,  98,  72), // winter pine
                        new Color( 38,  78,  58), // deep spruce
                        new Color(112,  70,  46), // chestnut brown
                        new Color(148, 100,  64), // warm walnut
                        new Color(208, 190, 164), // frost beige
                        new Color(154, 152, 112), // sage khaki
                };
            } else if(objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if(hint.contains("SlidingButtonOn")){
                    objects[0] = new Color(108, 180, 218); // ice blue
                } else if (hint.contains("SlidingButtonOff")){
                    objects[0] = new Color(112,  70,  46); // chestnut brown
                } else if (hint.contains("GameQuitFont") || hint.contains("GameDisplayFont")) {
                    objects[0] = new Color( 42,  82, 145); // deep navy
                } else if (hint.contains("PieceSelected")){
                    objects[0] = new Color( 88, 148, 188); // steel blue
                } else if (hint.contains("Button") && !hint.contains("SlidingButtonEmpty")) {
                    objects[0] = new Color( 42,  82, 145); // deep navy
                } else if (hint.contains("Background")) {
                    objects[0] = new Color(228, 222, 212); // pale winter frost
                }
            }
        } return objects;
    }
}
