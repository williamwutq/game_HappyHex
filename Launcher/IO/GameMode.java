package Launcher.IO;

/**
 * Represents the difficulty and size presets available for the game.
 * <p>
 * Each {@code GameMode} corresponds to a combination of size (Small, Medium, Large) and difficulty (Normal or Easy).
 * The {@link #Unspecified} value is reserved for legacy support and unspecified configurations.
 * <p>
 * This enum also provide methods to {@link #determineGameMode} from size and difficulty, and determine {@link #getChar size}
 * and {@link #isEasy difficulty} from an enum constant.
 * @author William Wu
 * @version 1.1
 */
public enum GameMode {
    /**
     * Legacy fallback mode used for compatibility with game version 0.3 and downwards.
     * <p>
     * This value indicates that no valid {@code GameMode} could be determined or that the mode is unsupported in
     * the current context. It should not be used in new implementations and is preserved only for legacy support.
     *
     * @deprecated This mode is retained solely to support legacy save files or configurations from version 0.3 and downwards.
     *             Avoid using in new logic unless required for backwards compatibility.
     */
    @Deprecated
    Unspecified, // Still need to support v0.3, do NOT delete
    /** Small map with normal difficulty */
    Small,
    /** Medium map with normal difficulty */
    Medium,
    /** Large map with normal difficulty */
    Large,
    /** Small map with easy difficulty */
    SmallEasy,
    /** Medium map with easy difficulty */
    MediumEasy,
    /** Large map with easy difficulty */
    LargeEasy;

    /**
     * Determines the appropriate {@code GameMode} based on the given difficulty and preset character.
     * @param easyMode {@code true} if easy mode is enabled, {@code false} for normal mode.
     * @param preset   A single character string representing the map size. Expected values are:
     *                 <ul>
     *                   <li>{@code "S"} for {@link #Small}</li>
     *                   <li>{@code "M"} for {@link #Medium}</li>
     *                   <li>{@code "L"} for {@link #Large}</li>
     *                   <li>{@code " "} for {@link #Unspecified}</li>
     *                 </ul>
     * @return The matching {@code GameMode}, or {@link #Unspecified} if the input does not match a known preset.
     * @see #isEasy(GameMode)
     * @see #getChar(GameMode) 
     */
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
    /**
     * Checks if the provided {@code GameMode} is in easy mode.
     * {@link #Unspecified} is not easy mode by default.
     * @param mode The {@code GameMode} to check.
     * @return {@code true} if the mode is an easy variant, {@code false} otherwise.
     * @see #determineGameMode(boolean, String)
     */
    public static boolean isEasy(GameMode mode){
        return mode == SmallEasy || mode == MediumEasy || mode == LargeEasy;
    }
    /**
     * Returns the character representation of the given {@code GameMode}'s size.
     * @param mode The {@code GameMode} to evaluate.
     * @return 'S' for Small, 'M' for Medium, 'L' for Large, or ' ' if the mode is {@link #Unspecified}.
     * @see #determineGameMode(boolean, String)
     */
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