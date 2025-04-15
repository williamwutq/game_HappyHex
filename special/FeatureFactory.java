package special;

public class FeatureFactory {
    public static SpecialFeature createFeature(String className){
        if(className.equals("Color") || className.equals("java.awt.Color")) {
            SpecialFeature grayscale = new special.Styles.Grayscale();
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 9 && date.getDayOfMonth() == 11) || Math.random() >= 0.94) {
                return grayscale;
            }
        }
        return new DefaultFeature(); // Default feature do nothing
    }
    public static SpecialFeature createFeature(){
        return new DefaultFeature(); // Default feature do nothing
    }
}
