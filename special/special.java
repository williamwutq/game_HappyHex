package special;

import java.io.File;
import java.net.URL;

public final class special {
    private static boolean enable = true;
    private static boolean valid = special.validate();
    public static final int SUPPORT_VERSION_MAJOR_SAFE = 1;
    public static final int SUPPORT_VERSION_MAJOR_LOW = 1;
    public static final int SUPPORT_VERSION_MINOR = 0;

    public static boolean validate(){
        // Valid Main
        URL main = special.class.getResource("/Main.class");
        if (main == null || !(new File(main.getPath()).exists())) return false;
        // Valid Launcher
        URL launchEssentials = special.class.getResource("/Launcher/LaunchEssentials.class");
        if (launchEssentials == null || !(new File(launchEssentials.getPath()).exists())) return false;
        // Valid GUI
        URL GameEssentials = special.class.getResource("/GUI/GameEssentials.class");
        if (GameEssentials == null || !(new File(GameEssentials.getPath()).exists())) return false;
        // Fetch version
        int major = -1; int minor = -1;
        try{
            major = Launcher.LaunchEssentials.currentGameVersion.major();
        } catch (Exception e){return false;}
        try{
            minor = Launcher.LaunchEssentials.currentGameVersion.minor();
        } catch (Exception e){return false;}
        // Check version support
        if (major < SUPPORT_VERSION_MAJOR_LOW){
            return false;
        } else if (major < SUPPORT_VERSION_MAJOR_SAFE && minor < SUPPORT_VERSION_MINOR){
            return false;
        }
        return true;
    }
    public static boolean isActive(){
        return enable && valid;
    }
    public static boolean activate(){
        valid = special.validate();
        return valid;
    }
    public static void enable(){
        enable = true;
    }
    public static void disable(){
        enable = false;
    }
}
