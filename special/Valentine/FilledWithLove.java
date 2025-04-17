package special.Valentine;

import special.SpecialFeature;

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
        if(objects[0] instanceof GUI.animation.DisappearEffect && objects[1] instanceof Hex.Block){
            return new Object[]{new HeartAnimation((Hex.Block) objects[1])};
        }
        return objects;
    }
}
