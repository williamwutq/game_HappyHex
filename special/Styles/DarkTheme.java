package special.Styles;

import special.SpecialFeature;

import java.awt.*;

public class DarkTheme implements SpecialFeature {
    private String[] whiteList = new String[]{
            "Quit",
            "Confirm",
            "SlidingButtonOn",
            "SlidingButtonOff",
            "New"
    };
    private boolean enable;
    private boolean valid;
    public DarkTheme(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 4;
    }
    public int getGroupID() {
        return 6; // Color
    }
    public String getFeatureName() {
        return "DarkTheme";
    }
    public String getGroupName() {
        return "ColorModifier";
    }
    public String getFeatureDescription() {
        return "Use dark theme coloring for all GUI elements";
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
            if(!isColorBaseArray && objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                Color color = (Color) objects[0];
                String hint = (String) objects[1];
                if (hint.contains("GameQuitFont")) {
                    objects[0] = new Color(255, 144, 110);
                } else if (hint.contains("GamePieceSelected")) {
                    objects[0] = new Color(36, 33, 101);
                } else if(inWhiteList(hint)){
                    return objects;
                } else if (color.equals(Color.BLACK)) {
                    objects[0] = new Color(204, 204, 204);
                } else if (hint.contains("GamePiecePanelBackground") || hint.contains("GameOverBackground")) {
                    objects[0] = new Color(63, 61, 112);
                } else if (hint.contains("GamePanelBackground")) {
                    objects[0] = new Color(23, 23, 42);
                } else if (hint.contains("GameDisplayFont") || hint.contains("LaunchAuthorFont")) {
                    objects[0] = new Color(158, 157, 232);
                } else {
                    objects[0] = new Color(constraint(255-color.getRed()), constraint(255-color.getGreen()), constraint(255-color.getBlue()), color.getAlpha());
                }
            } else for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof Color) {
                    Color color = (Color) objects[i];
                    color = new Color(constraint(255-color.getRed()), constraint(255-color.getGreen()), constraint(255-color.getBlue()), color.getAlpha());
                    objects[i] = color;
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
