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

package special.Halloween;

import special.SpecialFeature;

import java.awt.*;

public class SpookyTheme implements SpecialFeature {
    private String[] whiteList = new String[]{
            "Quit",
            "Confirm",
    };
    private boolean enable;
    private boolean valid;
    public SpookyTheme(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 13;
    }
    public int getGroupID() {
        return 999; // EventSpecial
    }
    public String getFeatureName() {
        return "SpookyTheme";
    }
    public String getGroupName() {
        return "EventSpecial";
    }
    public String getFeatureDescription() {
        return "Halloween Special! Spooky spooky spooky!";
    }
    public String getFeatureTarget() {
        return "GUI, LaunchGUI, LauncherEssentials, GameEssentials";
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
            if (objects.length == 2 && objects[0] instanceof String && objects[1] instanceof String){
                String font = (String) objects[0];
                String hint = (String) objects[1];
                if(hint.contains("Title")){
                    return new String[]{"Luminari"}; // or maybe Brush Script MT
                } else if(hint.contains("VersionFont")){
                    return new String[]{"Papyrus"};
                } else if(hint.contains("GameDisplayFont")){
                    return new String[]{"Courier New"};
                } else if(hint.contains("SlidingButtonFont")){
                    return new String[]{"Herculanum"};
                } else if(hint.contains("ButtonFont")){
                    return new String[]{"Cochin"};
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
            if(!isColorBaseArray && objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                Color color = (Color) objects[0];
                String hint = (String) objects[1];
                if (hint.contains("GameQuit")) {
                    objects[0] = new Color(255, 164, 60);
                } else if (hint.contains("New") || hint.contains("HintFont")) {
                    objects[0] = new Color(189, 56, 56);
                } else if (hint.contains("GamePieceSelected")) {
                    objects[0] = new Color(92, 36, 45);
                } else if (hint.contains("PiecePanel")){
                    objects[0] = new Color(223, 180, 89);
                } else if (hint.contains("Quit") || hint.contains("Confirm")){
                    return objects;
                } else if (hint.contains("SlidingButtonOn")){
                    objects[0] = new Color(173, 216, 18, 255);
                } else if (hint.contains("SlidingButtonOff")){
                    objects[0] = new Color(228, 53, 66);
                } else if (hint.contains("VersionFont")){
                    objects[0] = new Color(221, 200, 130);
                } else if (color.equals(Color.BLACK)) {
                    objects[0] = new Color(204, 204, 204);
                } else if (hint.contains("GameDisplayFont") || hint.contains("LaunchAuthorFont")) {
                    objects[0] = new Color(172, 34, 14);
                } else {
                    objects[0] = new Color(constraint(286-color.getRed()), constraint(270-color.getGreen()), constraint(270-color.getBlue()), color.getAlpha());
                }
            } else for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof Color) {
                    Color color = (Color) objects[i];
                    objects[i] = new Color(constraint(220 + color.getRed() / 4), constraint(64 + color.getGreen() / 4), constraint(46 + color.getBlue() / 8));
                }
            }
        }
        return objects;
    }
    private int constraint(int number){
        int upper = 240;
        int lower = 22;
        if(number > upper){
            return upper;
        } else if (number < lower){
            return lower;
        } else return number;
    }
    private boolean inWhiteList(String str){
        for(String ignore : whiteList){
            if(str.contains(ignore)) return true;
        } return false;
    }
}
