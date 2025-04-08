package special;

public interface SpecialFeature {
    int getFeatureID();
    int getGroupID();
    String getFeatureName();
    String getGroupName();
    String getFeatureDescription();
    String getFeatureTarget();
    int getSupportVersionMajor();
    int getSupportVersionMinor();
    void enable();
    void disable();
    boolean isActive();
    Object[] process(Object[] objects);
    default Object process(Object process){
        Object[] result = process(new Object[]{process});
        if(result == null || result.length == 0){
            return null;
        } return result[0];
    }
    default String description(){
        return "Feature " + getGroupID() + "-" + getFeatureID() + " " +
               getFeatureName() + " in the " + getGroupName() + " group targets " +
               getFeatureTarget() + ".\n" + getFeatureDescription() + "This feature requires" +
               " version " + getSupportVersionMajor() + "." + getSupportVersionMinor() +
               " or higher and can be disabled.\n" + "It is now " + (isActive() ? "" : "in") + "active";
    }
}
