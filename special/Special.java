package special;

import java.io.File;
import java.net.URL;

public final class Special {
    private static boolean enable = true;
    private static boolean valid = Special.validate();
    public static final int SUPPORT_VERSION_MAJOR_SAFE = 1;
    public static final int SUPPORT_VERSION_MAJOR_LOW = 1;
    public static final int SUPPORT_VERSION_MINOR = 0;
    private static int CURRENT_VERSION_MAJOR = -1;
    private static int CURRENT_VERSION_MINOR = -1;

    public static boolean validate(){
        // Valid Main
        URL main = Special.class.getResource("/Main.class");
        if (main == null || !(new File(main.getPath()).exists())) return false;
        // Valid Launcher
        URL launchEssentials = Special.class.getResource("/Launcher/LaunchEssentials.class");
        if (launchEssentials == null || !(new File(launchEssentials.getPath()).exists())) return false;
        // Valid GUI
        URL GameEssentials = Special.class.getResource("/GUI/GameEssentials.class");
        if (GameEssentials == null || !(new File(GameEssentials.getPath()).exists())) return false;
        // Fetch version
        try{
            CURRENT_VERSION_MAJOR = Launcher.LaunchEssentials.currentGameVersion.major();
        } catch (Exception e){return false;}
        try{
            CURRENT_VERSION_MINOR = Launcher.LaunchEssentials.currentGameVersion.minor();
        } catch (Exception e){return false;}
        // Check version support
        if (CURRENT_VERSION_MAJOR < SUPPORT_VERSION_MAJOR_LOW){
            return false;
        } else if (CURRENT_VERSION_MINOR < SUPPORT_VERSION_MAJOR_SAFE && CURRENT_VERSION_MINOR < SUPPORT_VERSION_MINOR){
            return false;
        }
        return true;
    }
    public static boolean isActive(){
        return enable && valid;
    }
    public static boolean activate(){
        valid = Special.validate();
        return valid;
    }
    public static void enable(){
        enable = true;
    }
    public static void disable(){
        enable = false;
    }
    public static int getCurrentVersionMajor(){
        if(!validate()){
            System.err.println("Special Features Validation failed, please relaunch program");
        }
        return CURRENT_VERSION_MAJOR;
    }
    public static int getCurrentVersionMinor(){
        if(!validate()){
            System.err.println("Special Features Validation failed, please relaunch program");
        }
        return CURRENT_VERSION_MINOR;
    }
}
