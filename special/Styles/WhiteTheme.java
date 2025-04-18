package special.Styles;

import special.SpecialFeature;

import java.awt.*;

public class WhiteTheme implements SpecialFeature {
    private String[] whiteList = new String[]{
            "Button",
            "Author",
            "WW",
            "GameQuitFontColor",
            "GameDisplayFontColor"
    };
    private boolean enable;
    private boolean valid;
    public WhiteTheme(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 5;
    }
    public int getGroupID() {
        return 6; // Color
    }
    public String getFeatureName() {
        return "WhiteTheme";
    }
    public String getGroupName() {
        return "ColorModifier";
    }
    public String getFeatureDescription() {
        return "Use white theme coloring for all GUI elements";
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
            boolean isColorBaseArray = objects.length == 6 || objects.length == 12;
            int k = 0;
            while (k < objects.length && isColorBaseArray) {
                if (!(objects[k] instanceof Color)) {
                    isColorBaseArray = false;
                } else k++;
            }
            if(objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if(hint.contains("Background") && !inWhiteList(hint)){
                    objects[0] = Color.WHITE;
                } else if (!inWhiteList(hint)){
                    objects[0] = GUI.GameEssentials.whitenColor((Color) objects[0]);
                }
            }
        }
        return objects;
    }
    private boolean inWhiteList(String str){
        for(String ignore : whiteList){
            if(str.contains(ignore)) return true;
        } return false;
    }
}
