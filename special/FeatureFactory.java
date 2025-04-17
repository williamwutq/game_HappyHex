package special;

public class FeatureFactory {
    public static SpecialFeature createFeature(String className){
        if(className.equals("Color") || className.equals("java.awt.Color")) {
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 9 && date.getDayOfMonth() == 11) || Math.random() >= 0.94) {
                return new special.Styles.Grayscale();
            }
        } else if (className.equals("Piece") || className.equals("Hex.Piece")){
            return new special.Logic.HardMode();
        } else if (className.equals("Animation") || className.equals("GUI.animation.Animation")){
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 2 && date.getDayOfMonth() == 14)) {
                return new special.Valentine.FilledWithLove();
            }
        }
        return new DefaultFeature(); // Default feature do nothing
    }
    public static SpecialFeature createFeature(){
        return new DefaultFeature(); // Default feature do nothing
    }
}
