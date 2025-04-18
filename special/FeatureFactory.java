package special;

public class FeatureFactory {
    private static final boolean randomChance1 = Math.random() >= 0.94;
    private static final boolean randomChance2 = Math.random() >= 0.94;
    public static SpecialFeature createFeature(String className, String hint){
        if(className.equals("Color") || className.equals("java.awt.Color")) {
            // Special Dates
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 9 && date.getDayOfMonth() == 11) || randomChance1) {
                return new special.Styles.Grayscale();
            } else if (hint.equals("Dark") || hint.equals("4")) {
                return new special.Styles.DarkTheme();
            }
        } else if (className.equals("Piece") || className.equals("Hex.Piece")) {
            if (hint.equals("God")){
                return new special.Logic.GodMode();
            } else if (hint.equals("Hard")){
                return new special.Logic.HardMode();
            }
        } else if (className.equals("Animation") || className.equals("GUI.animation.Animation")){
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 2 && date.getDayOfMonth() == 14)) {
                return new special.Valentine.FilledWithLove();
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
