/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package special;

public final class Special {
    private static boolean enable = true;
    private static boolean valid = Special.validate();
    public static final int SUPPORT_VERSION_MAJOR_SAFE = 1;
    public static final int SUPPORT_VERSION_MAJOR_LOW = 1;
    public static final int SUPPORT_VERSION_MINOR = 0;
    private static int CURRENT_VERSION_MAJOR = -1;
    private static int CURRENT_VERSION_MINOR = -1;

    public static boolean validate(){
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
            System.err.println(io.GameTime.generateSimpleTime() + " Special Feature: Validation failed, please relaunch program.");
        }
        return CURRENT_VERSION_MAJOR;
    }
    public static int getCurrentVersionMinor(){
        if(!validate()){
            System.err.println(io.GameTime.generateSimpleTime() + " Special Feature: Validation failed, please relaunch program.");
        }
        return CURRENT_VERSION_MINOR;
    }
}
