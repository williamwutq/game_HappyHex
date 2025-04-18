package special.Styles;

import special.SpecialFeature;

import java.awt.*;

public class DarkTheme implements SpecialFeature {
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
            if(isColorBaseArray){
                Color[] newArray = new Color[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    Color color = (Color) objects[i];
                    newArray[i] = new Color(
                            constraint((int)(240-color.getRed() * 0.75)),
                            constraint((int)(240-color.getGreen() * 0.75)),
                            constraint((int)(240-color.getBlue() * 0.75)),
                            color.getAlpha());
                }
                return newArray;
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
        int upper = 248;
        int lower = 22;
        if(number > upper){
            return upper;
        } else if (number < lower){
            return lower;
        } else return number;
    }
}
