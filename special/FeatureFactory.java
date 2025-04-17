package special;

public class FeatureFactory {
    public static SpecialFeature createFeature(String className, String hint){
        if(className.equals("Color") || className.equals("java.awt.Color")) {
            SpecialFeature grayscale = new special.Styles.Grayscale();
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 9 && date.getDayOfMonth() == 11) || Math.random() >= 0.94 || hint.equals("9/11")) {
                return grayscale;
            }
        } else if (className.equals("Piece") || className.equals("Hex.Piece")){
            if(hint.equals("God")){
                return new special.Logic.GodMode();
            } else if (hint.equals("Hard")){
                return new special.Logic.HardMode();
            }
        }
        return new DefaultFeature(); // Default feature do nothing
    }
    public static SpecialFeature createFeature(String className){
        return createFeature(className, "Default");
    }
    public static SpecialFeature createFeature(){
        return new DefaultFeature(); // Default feature do nothing
    }
}
