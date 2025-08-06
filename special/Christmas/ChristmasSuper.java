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

package special.Christmas;

import special.SpecialFeature;

import java.awt.Color;

public class ChristmasSuper implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public ChristmasSuper(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 1225;
    }
    public int getGroupID() {
        return 999; // EventSpecial
    }
    public String getFeatureName() {
        return "ChristmasSuper";
    }
    public String getGroupName() {
        return "EventSpecial";
    }
    public String getFeatureDescription() {
        return "Christmas Super Special";
    }
    public String getFeatureTarget() {
        return "Animation, LaunchGUI, LauncherEssentials, GameEssentials";
    }
    public int getSupportVersionMajor() {
        return 1;
    }
    public int getSupportVersionMinor() {
        return 2;
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
            if (objects[0] instanceof GUI.animation.DisappearEffect && objects[1] instanceof hex.Block) {
                return new Object[]{new TreeAnimation((hex.Block) objects[1])};
            } else if (objects.length == 2 && objects[0] instanceof String && objects[1] instanceof String hint){
                if(hint.contains("Title")){
                    return new String[]{"Comic Sans MS"};
                } else if(hint.contains("VersionFont")){
                    return new String[]{"Courier"};
                } else if(hint.contains("GameDisplayFont")){
                    return new String[]{"Skia"};
                } else if(hint.contains("SlidingButtonFont")){
                    return new String[]{"Herculanum"};
                } else if(hint.contains("ButtonFont")){
                    return new String[]{"Comic Sans MS"};
                } else if(hint.contains("MonoFont")){
                    return new String[]{"Andale Mono"};
                }
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
                        new Color(220, 151, 18),
                        new Color(207, 140, 86),
                        new Color(63, 189, 139),
                        new Color(209, 104, 127),
                        new Color(162, 118, 195),
                        new Color(50, 154, 189),
                        new Color(188, 185, 38),
                        new Color(168, 107, 61),
                        new Color(163, 84, 120),
                        new Color(113, 168, 41),
                        new Color(225, 80, 59),
                        new Color(184, 184, 184),
                };
            } else if(objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String hint) {
                if (hint.contains("TitlePanel") || hint.contains("OverBackground")){
                    objects[0] = new Color(216, 194, 121);
                } else if (hint.contains("PiecePanel")){
                    objects[0] = new Color(168, 27, 49);
                } else if (hint.contains("Hint")){
                    objects[0] = new Color(159, 147, 145);
                } else if (hint.contains("Version")){
                    objects[0] = new Color(156, 120, 6);
                } else if (hint.contains("SlidingButtonOn")){
                    objects[0] = new Color(255, 191, 0);
                } else if (hint.contains("SlidingButtonOff")){
                    objects[0] = new Color(116, 190, 214);
                } else if (hint.contains("SlidingButtonEmpty")){
                    objects[0] = new Color(54, 103, 14, 128);
                } else if (hint.contains("DynamicStart")){
                    objects[0] = new Color(133, 57, 189);
                } else if (hint.contains("DynamicEnd")){
                    objects[0] = new Color(25, 177, 119);
                } else if (hint.contains("StartButton")){
                    objects[0] = new Color(58, 67, 41);
                } else if (hint.contains("QuitButton")){
                    objects[0] = new Color(149, 0, 0);
                } else if (hint.contains("ConfirmButton")){
                    objects[0] = new Color(34, 139, 34);
                } else if (hint.contains("LoginField")){
                    objects[0] = new Color(119, 0, 0);
                } else if (hint.contains("PlayerName")){
                    objects[0] = new Color(234, 234, 0);
                } else if (hint.contains("PlayerPrompt")){
                    objects[0] = new Color(0, 234, 0);
                } else if (hint.contains("PlayerError")){
                    objects[0] = new Color(255, 29, 29);
                } else if (hint.contains("PlayerSpecial")){
                    objects[0] = new Color(0, 225, 225);
                } else if (hint.contains("BlockDefault")){
                    objects[0] = new Color(0, 13, 50);
                } else if (hint.contains("PieceSelected")){
                    objects[0] = new Color(227, 139, 99);
                } else if (hint.contains("GameDisplay")){
                    objects[0] = new Color(5, 34, 24);
                } else if (hint.contains("GameQuit")){
                    objects[0] = new Color(195, 140, 25);
                } else if (hint.contains("Background")){
                    objects[0] = new Color(216, 212, 185);
                }
            }
        }
        return objects;
    }
}
