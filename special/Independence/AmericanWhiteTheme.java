package special.Independence;

import special.SpecialFeature;

import java.awt.*;

public class AmericanWhiteTheme implements SpecialFeature {
    private String[] whiteList = new String[]{
            "Player",
            "Author",
            "WW",
    };
    private boolean enable;
    private boolean valid;
    public AmericanWhiteTheme(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 5;
    }
    public int getGroupID() {
        return 999; // EventSpecial
    }
    public String getFeatureName() {
        return "AmericanWhiteTheme";
    }
    public String getGroupName() {
        return "EventSpecial";
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
                        new Color(14, 20, 105),
                        new Color(29, 40, 126),
                        new Color(31, 73, 192),
                        new Color(99, 29, 27),
                        new Color(131, 26, 27),
                        new Color(177, 66, 74),
                        new Color(14, 20, 105),
                        new Color(29, 40, 126),
                        new Color(31, 73, 192),
                        new Color(99, 29, 27),
                        new Color(131, 26, 27),
                        new Color(177, 66, 74),
                };
            } else if(objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if(hint.contains("SlidingButtonOn") || hint.contains("SlidingButtonOff")){
                    objects[0] = Color.BLACK;
                } else if (hint.contains("GameQuitFont") || hint.contains("GameDisplayFont")) {
                    objects[0] = Color.DARK_GRAY;
                } else if (hint.contains("PieceSelected")){
                    objects[0] = Color.LIGHT_GRAY;
                } else if (hint.contains("Button") && !hint.contains("SlidingButtonEmpty")) {
                    objects[0] = Color.DARK_GRAY;
                } else if (hint.contains("Background") && !inWhiteList(hint)) {
                    objects[0] = Color.WHITE;
                } else if (!inWhiteList(hint)){
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
