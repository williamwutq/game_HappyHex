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

/* Authors of mods should put their license and below. */

/* Authors of mods should put their license and above. */

package special;

public class FeatureFactory {
    private static final boolean randomChance1 = Math.random() >= 0.94;
    private static final boolean randomChance2 = Math.random() >= 0.94;
    public static SpecialFeature createFeature(String className, String hint){
        if(className.equals("Color") || className.equals("java.awt.Color")) {
            // Special Dates
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 2 && date.getDayOfMonth() == 14)) {
                return new special.Valentine.FilledWithLove();
            } else if ((date.getMonthValue() == 10 && date.getDayOfMonth() == 31)) {
                return new special.Halloween.SpookyTheme();
            } else if (special.Thanksgiving.ThanksgivingDate.isThanksgiving()){
                return new special.Thanksgiving.GratefulHarvest();
            } else if ((date.getMonthValue() == 6 && date.getDayOfMonth() == 16)) {
                return new special.Snowy.Snowy();
            } else if ((date.getMonthValue() == 12 && date.getDayOfMonth() == 25)) {
                return new special.Christmas.ChristmasSuper();
            } else if ((date.getMonthValue() == 7 && date.getDayOfMonth() == 4)) {
                if (hint.equals("Dark") || hint.equals("4")) {
                    return new special.Independence.AmericanBlackTheme();
                } else {
                    return new special.Independence.AmericanWhiteTheme();
                }
            } else if ((date.getMonthValue() == 9 && date.getDayOfMonth() == 11) || randomChance1) {
                return new special.Styles.Grayscale();
            } else if (hint.equals("Dark") || hint.equals("4")) {
                return new special.Styles.DarkTheme();
            } else if (hint.equals("White") || hint.equals("5")) {
                return new special.Styles.WhiteTheme();
            }
        } else if (className.equals("Piece") || className.equals("hex.Piece")) {
            if (hint.equals("God")){
                return new special.Logic.GodMode();
            } else if (hint.equals("Hard")){
                return new special.Logic.HardMode();
            }
        } else if (className.equals("Font") || className.equals("java.awt.Font")) {
            // Special Dates
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 10 && date.getDayOfMonth() == 31)) {
                return new special.Halloween.SpookyTheme();
            } else if (special.Thanksgiving.ThanksgivingDate.isThanksgiving()){
                return new special.Thanksgiving.GratefulHarvest();
            }
        } else if (className.equals("Animation") || className.equals("GUI.animation.Animation")){
            java.time.LocalDate date = java.time.LocalDate.now();
            if ((date.getMonthValue() == 2 && date.getDayOfMonth() == 14)) {
                return new special.Valentine.FilledWithLove();
            } else if ((date.getMonthValue() == 12 && date.getDayOfMonth() == 25)) {
                return new special.Christmas.ChristmasSuper();
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
