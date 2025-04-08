package special;

public class DefaultFeature implements SpecialFeature{
    private boolean enable;
    public DefaultFeature(){
        this.enable = true;
    }
    public int getFeatureID() {
        return 0;
    }
    public int getGroupID() {
        return 1; // default
    }
    public String getFeatureName() {
        return "DefaultFeature";
    }
    public String getGroupName() {
        return "Default";
    }
    public String getFeatureDescription() {
        return "This feature does nothing";
    }
    public String getFeatureTarget() {
        return "Everywhere";
    }
    public int getSupportVersionMajor() {
        return 0;
    }
    public int getSupportVersionMinor() {
        return 0;
    }
    public boolean validate() {
        return true; // Always valid
    }
    public void enable() {
        enable = true;
    }
    public void disable() {
        enable = false;
    }
    public boolean isActive() {
        return enable;
    }
    public Object[] process(Object[] objects) {
        return objects;
    }
}
