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

package special.Snowy;

import special.SpecialFeature;

import java.awt.*;

public class Snowy implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public Snowy(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 0;
    }
    public int getGroupID() {
        return 1; // default
    }
    public String getFeatureName() {
        return "Snowy";
    }
    public String getGroupName() {
        return "ColorModifier";
    }
    public String getFeatureDescription() {
        return "This feature is to celebrate my friend who is the first person to reach 40 turns in Devil mode. To celebrate, I promise him that I will add this new theme." +
               "This theme cause game hexagons to be RGB, background to be white, unfilled tiles to be blue, pieces and settings buttons to be RGBW, and home screen buttons to be yellow.";
    }
    public String getFeatureTarget() {
        return "GUI, LaunchGUI, LauncherEssentials, GameEssentials";
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
            boolean isColorBaseArray = objects.length == 6 || objects.length == 12;
            int k = 0;
            while (k < objects.length && isColorBaseArray) {
                if (!(objects[k] instanceof Color)) {
                    isColorBaseArray = false;
                } else k++;
            }
            if (isColorBaseArray){
                return new Color[]{
                        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
                        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
                        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW
                };
            } else if(objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if(hint.contains("SlidingButtonOn")){
                    objects[0] = Color.BLUE;
                } else if (hint.contains("SlidingButtonOff")){
                    objects[0] = Color.RED;
                } else if (hint.contains("GameQuitFont") || hint.contains("GameDisplayFont")) {
                    objects[0] = Color.DARK_GRAY;
                } else if (hint.contains("PieceSelected")){
                    objects[0] = Color.LIGHT_GRAY;
                } else if (hint.contains("Button") && !hint.contains("SlidingButtonEmpty")) {
                    objects[0] = Color.DARK_GRAY;
                } else if (hint.contains("Background")) {
                    objects[0] = Color.WHITE;
                }
            }
        } return objects;
    }
}
