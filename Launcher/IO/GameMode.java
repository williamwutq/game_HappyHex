package Launcher.IO;

public enum GameMode {
    @Deprecated
    Unspecified,
    Small, Medium, Large, SmallEasy, MediumEasy, LargeEasy;
    public static GameMode determineGameMode(boolean easyMode, String preset) {
        if (easyMode) {
            if (preset.equals("S")) {
                return GameMode.SmallEasy;
            } else if (preset.equals("M")){
                return GameMode.MediumEasy;
            } else if (preset.equals("L")){
                return GameMode.LargeEasy;
            } else return GameMode.Unspecified;
        } else {
            if (preset.equals("S")) {
                return GameMode.Small;
            } else if (preset.equals("M")){
                return GameMode.Medium;
            } else if (preset.equals("L")){
                return GameMode.Large;
            } else return GameMode.Unspecified;
        }
    }
    public static boolean isEasy(GameMode mode){
        return mode == SmallEasy || mode == MediumEasy || mode == LargeEasy;
    }
    public static char getChar(GameMode mode){
        if(mode == SmallEasy || mode == Small){
            return 'S';
        } else if (mode == MediumEasy || mode == Medium){
            return 'M';
        } else if (mode == LargeEasy || mode == Large){
            return 'L';
        } else return ' ';
    }
}