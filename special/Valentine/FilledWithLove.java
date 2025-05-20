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

package special.Valentine;

import special.SpecialFeature;

import java.awt.*;

public class FilledWithLove implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public FilledWithLove(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 143;
    }
    public int getGroupID() {
        return 999; // EventSpecial
    }
    public String getFeatureName() {
        return "FilledWithLove";
    }
    public String getGroupName() {
        return "EventSpecial";
    }
    public String getFeatureDescription() {
        return "Valentine's Day Special! This feature is filled with love!";
    }
    public String getFeatureTarget() {
        return "Animation, LaunchGUI, LauncherEssentials, GameEssentials";
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
    public Object[] process(Object[] objects) {
        if(isActive()) {
            if (objects == null || objects.length == 0) return null;
            boolean isColorBaseArray = objects.length == 6 || objects.length == 12;
            int k = 0;
            while (k < objects.length && isColorBaseArray) {
                if (!(objects[k] instanceof Color)) {
                    isColorBaseArray = false;
                } else k++;
            }
            if (objects[0] instanceof GUI.animation.DisappearEffect && objects[1] instanceof hex.Block) {
                return new Object[]{new HeartAnimation((hex.Block) objects[1])};
            } else if(!isColorBaseArray && objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if(hint.contains("GameBlockDefaultColor")){
                    objects[0] = new Color(151, 255, 255);
                } else if (hint.contains("GameQuitFont") || hint.contains("New") || hint.contains("GameDisplayFont")) {
                    objects[0] = new Color(221, 84, 166);
                } else if (hint.contains("PiecePanel")) {
                    objects[0] = new Color(237, 131, 189);
                } else if (hint.contains("PieceSelected")){
                    objects[0] = new Color(44, 193, 193);
                } else if (hint.contains("SlidingButtonEmpty")) {
                    objects[0] = new Color(233, 200, 255);
                } else if (hint.contains("SlidingButton")){
                    objects[0] = new Color(44, 0, 92);
                } else if (hint.contains("Button")) {
                    objects[0] = new Color(209, 50, 145);
                } else if (hint.contains("Author") || hint.contains("Player")) {
                    objects[0] = new Color(28, 0, 58);
                } else if (hint.contains("TitlePanel")) {
                    objects[0] = new Color(241, 152, 205);
                } else if (hint.contains("Background")) {
                    objects[0] = new Color(255, 200, 230);
                } else {
                    objects[0] = new Color(44, 0, 92);
                }
            } else {
                Color[] arr = new Color[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    Color color = (Color) objects[i];
                    color = new Color(color.getRed(), (color.getGreen() * 3 + 255) / 4, (color.getBlue() + 255) / 2);
                    arr[i] = color;
                }
                return arr;
            }
        }
        return objects;
    }
}
