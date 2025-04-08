package special;

public class FeatureFactory {
    public static SpecialFeature createFeature(String className){
        if(className.equals("Color") || className.equals("java.awt.Color")) {
            return new special.Styles.Grayscale();
        } else return new DefaultFeature();
    }
}
