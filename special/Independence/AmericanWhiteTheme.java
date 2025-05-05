package special.Independence;

import special.SpecialFeature;

import java.awt.*;

public class AmericanWhiteTheme implements SpecialFeature {
    private boolean enable;
    private boolean valid;
    public AmericanWhiteTheme(){
        this.enable = true;
        this.valid = false;
        validate();
    }
    public int getFeatureID() {
        return 65;
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
        return "Bright independence theme";
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
                        new Color(29, 40, 126),
                        new Color(14, 20, 105),
                        new Color(99, 29, 27),
                        new Color(131, 26, 27),
                        new Color(177, 66, 74),
                        new Color(31, 73, 192),
                        new Color(29, 40, 126),
                        new Color(14, 20, 105),
                        new Color(99, 29, 27),
                        new Color(131, 26, 27),
                        new Color(177, 66, 74),
                };
            } else if(objects.length == 2 && objects[0] instanceof Color && objects[1] instanceof String){
                String hint = (String) objects[1];
                if (hint.contains("SlidingButtonOn")){
                    objects[0] = new Color(2, 41, 116);
                } else if (hint.contains("SlidingButtonOff")){
                    objects[0] = new Color(106, 4, 4);
                } else if (hint.contains("SlidingButtonEmpty")){
                    objects[0] = new Color(228, 222, 221);
                } else if (hint.contains("Selected")){
                    objects[0] = new Color(76, 148, 174);
                } else if (hint.contains("Button") || hint.contains("Author")){
                    objects[0] = new Color(6, 51, 136);
                } else if (hint.contains("TitlePanel")){
                    objects[0] = new Color(217, 206, 193);
                } else if (hint.contains("GamePiecePanel") || hint.contains("GameOverBackground")){
                    objects[0] = new Color(133, 187, 205);
                } else if (hint.contains("GamePanelBackground")){
                    objects[0] = new Color(180, 210, 220);
                } else if (hint.contains("Background")){
                    objects[0] = new Color(228, 222, 221);
                } else if (hint.contains("DisplayFont") || hint.contains("QuitFont")){
                    objects[0] = new Color(85, 38, 38);
                } else if (objects[0].equals(Color.BLACK)){
                    objects[0] = new Color(36,36,36);
                }
            }
        }
        return objects;
    }
}
