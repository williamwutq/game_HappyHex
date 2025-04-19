package special.Valentine;

import special.SpecialFeature;

import java.awt.*;

public class FilledWithLove implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public FilledWithLove(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 143;
    }
    public int getGroupID() {
        return 999; // EventSpecial
    }
    public String getFeatureName() {
        return "FilledWithLove";
    }
    public String getGroupName() {
        return "EventSpecial";
    }
    public String getFeatureDescription() {
        return "Valentine's Day Special! This feature is filled with love!";
    }
    public String getFeatureTarget() {
        return "Animation, LaunchGUI, LauncherEssentials, GameEssentials";
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
            if (objects[0] instanceof GUI.animation.DisappearEffect && objects[1] instanceof Hex.Block) {
                return new Object[]{new HeartAnimation((Hex.Block) objects[1])};
            } else if(!isColorBaseArray && objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if(hint.contains("GameBlockDefaultColor")){
                    objects[0] = new Color(151, 255, 255);
                } else if (hint.contains("GameQuitFont") || hint.contains("New") || hint.contains("GameDisplayFont")) {
                    objects[0] = new Color(221, 84, 166);
                } else if (hint.contains("PiecePanel")) {
                    objects[0] = new Color(237, 131, 189);
                } else if (hint.contains("PieceSelected")){
                    objects[0] = new Color(44, 193, 193);
                } else if (hint.contains("SlidingButtonEmpty")) {
                    objects[0] = new Color(233, 200, 255);
                } else if (hint.contains("SlidingButton")){
                    objects[0] = new Color(44, 0, 92);
                } else if (hint.contains("Button")) {
                    objects[0] = new Color(209, 50, 145);
                } else if (hint.contains("Author") || hint.contains("Player")) {
                    objects[0] = new Color(28, 0, 58);
                } else if (hint.contains("TitlePanel")) {
                    objects[0] = new Color(241, 152, 205);
                } else if (hint.contains("Background")) {
                    objects[0] = new Color(255, 200, 230);
                } else {
                    objects[0] = new Color(44, 0, 92);
                }
            } else {
                Color[] arr = new Color[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    Color color = (Color) objects[i];
                    color = new Color(color.getRed(), (color.getGreen() * 3 + 255) / 4, (color.getBlue() + 255) / 2);
                    arr[i] = color;
                }
                return arr;
            }
        }
        return objects;
    }
}
