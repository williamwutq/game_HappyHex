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

package special.Independence;

import special.SpecialFeature;

import java.awt.*;

public class AmericanBlackTheme implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public AmericanBlackTheme(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 64;
    }
    public int getGroupID() {
        return 999; // EventSpecial
    }
    public String getFeatureName() {
        return "AmericanBlackTheme";
    }
    public String getGroupName() {
        return "EventSpecial";
    }
    public String getFeatureDescription() {
        return "Dark independence theme";
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
            boolean isColorBaseArray = objects.length == 6 || objects.length == 12;
            int k = 0;
            while (k < objects.length && isColorBaseArray) {
                if (!(objects[k] instanceof Color)) {
                    isColorBaseArray = false;
                } else k++;
            }
            if (isColorBaseArray){
                return new Color[]{
                        new Color(31, 73, 192),
                        new Color(63, 104, 205),
                        new Color(105, 126, 184),
                        new Color(202, 135, 133),
                        new Color(189, 92, 93),
                        new Color(177, 66, 74),
                        new Color(31, 73, 192),
                        new Color(63, 104, 205),
                        new Color(105, 126, 184),
                        new Color(202, 135, 133),
                        new Color(189, 92, 93),
                        new Color(177, 66, 74),
                };
            } else if(objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if (hint.contains("SlidingButtonOn")){
                    objects[0] = new Color(88, 121, 186);
                } else if (hint.contains("SlidingButtonOff")){
                    objects[0] = new Color(177, 80, 80);
                } else if (hint.contains("SlidingButtonEmpty")){
                    objects[0] = new Color(1, 33, 51);
                } else if (hint.contains("Selected")){
                    objects[0] = new Color(179, 81, 60);
                } else if (hint.contains("Button") || hint.contains("Author")){
                    objects[0] = new Color(174, 57, 70);
                } else if (hint.contains("TitlePanel")){
                    objects[0] = new Color(3, 53, 81);
                } else if (hint.contains("GamePiecePanel") || hint.contains("GameOverBackground")){
                    objects[0] = new Color(97, 20, 3);
                } else if (hint.contains("GamePanelBackground")){
                    objects[0] = new Color(67, 14, 2);
                } else if (hint.contains("Background")){
                    objects[0] = new Color(1, 33, 51);
                } else if (hint.contains("DisplayFont") || hint.contains("QuitFont")){
                    objects[0] = new Color(125, 124, 207);
                } else if (objects[0].equals(Color.BLACK)){
                    objects[0] = new Color(189, 189, 189);
                }
            }
        }
        return objects;
    }
}
