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

package special.Thanksgiving;

import special.SpecialFeature;

import java.awt.Color;

public class GratefulHarvest implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public GratefulHarvest(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 67;
    }
    public int getGroupID() {
        return 999; // EventSpecial
    }
    public String getFeatureName() {
        return "GratefulHarvest";
    }
    public String getGroupName() {
        return "EventSpecial";
    }
    public String getFeatureDescription() {
        return "Thanksgiving special theme";
    }
    public String getFeatureTarget() {
        return "GUI, LaunchGUI, LauncherEssentials, GameEssentials";
    }
    public int getSupportVersionMajor() {
        return 1;
    }
    public int getSupportVersionMinor() {
        return 3;
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
            if (objects.length == 2 && objects[0] instanceof String && objects[1] instanceof String hint){
                if(hint.contains("Title")){
                    return new String[]{"Canela Text"};
                } else if(hint.contains("VersionFont")){
                    return new String[]{"Noteworthy"};
                } else if(hint.contains("GameDisplayFont")){
                    return new String[]{"Skia"};
                } else if(hint.contains("SlidingButtonFont")){
                    return new String[]{"Herculanum"};
                } else if(hint.contains("ButtonFont")){
                    return new String[]{"Times New Roman"};
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
                        new Color(237, 58, 21),
                        new Color(236, 180, 38),
                        new Color(236, 136, 45),
                        new Color(150, 16, 92),
                        new Color(126, 100, 120),
                        new Color(151, 101, 51),
                        new Color(92, 47, 6),
                        new Color(176, 171, 77),
                        new Color(124, 145, 58),
                        new Color(163, 211, 68),
                        new Color(214, 1, 63),
                        new Color(33, 143, 80),
                };
            } else if(objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if (hint.contains("SlidingButtonOn")){
                    objects[0] = new Color(11, 189, 141);
                } else if (hint.contains("SlidingButtonOff")){
                    objects[0] = new Color(234, 55, 55);
                } else if (hint.contains("SlidingButtonEmpty")){
                    objects[0] = new Color(237, 211, 186);
                } else if (hint.contains("GamePiecePanel") || hint.contains("GameOverBackground")){
                    objects[0] = new Color(220, 185, 153);
                } else if (hint.contains("Selected")){
                    objects[0] = new Color(165, 113, 66);
                } else if (hint.contains("Author") || hint.contains("DisplayFont")){
                    objects[0] = new Color(69, 85, 15);
                } else if (hint.contains("Button")){
                    objects[0] = new Color(244, 102, 46);
                } else if (hint.contains("Background")){
                    objects[0] = new Color(237, 211, 186);
                } else if (objects[0].equals(Color.BLACK)){
                    objects[0] = new Color(36,36,36);
                }
            }
        }
        return objects;
    }
}
