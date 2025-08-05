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
            if (objects.length == 2 && objects[0] instanceof String && objects[1] instanceof String hint){
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
        }
        return objects;
    }
}
