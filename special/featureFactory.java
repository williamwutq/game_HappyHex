package special;

public class featureFactory {
    public static SpecialFeature createFeature(String className){
        if(className.equals("Color")) {
            return new special.Styles.Grayscale();
        } else return new DefaultFeature();
    }
}
