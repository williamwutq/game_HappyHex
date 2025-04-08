package special.Styles;

import special.SpecialFeature;

import java.awt.*;

public class Grayscale implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public Grayscale(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 3;
    }
    public int getGroupID() {
        return 6; // Color
    }
    public String getFeatureName() {
        return "Grayscale";
    }
    public String getGroupName() {
        return "ColorModifier";
    }
    public String getFeatureDescription() {
        return "Convert colored display into grayscale for all GUI elements";
    }
    public String getFeatureTarget() {
        return "GUI, LaunchGUI, LauncherEssentials, GameEssentials";
    }
    public int getSupportVersionMajor() {
        return 1;
    }
    public int getSupportVersionMinor() {
        return 0;
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
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = (Object) new Color(160, 160, 160);
                }
            } else for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof Color) {
                    Color color = (Color) objects[i];
                    int gray = (int) Math.round((color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114)*1.5) - 64;
                    if (gray > 255) {
                        gray = 255;
                    } else if (gray < 0) {
                        gray = 0;
                    }
                    color = new Color(gray, gray, gray, color.getAlpha());
                    objects[i] = (Object) color;
                }
            }
        }
        return objects;
    }
}
