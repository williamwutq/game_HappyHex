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

package hex;

import java.util.Objects;

public class SolidColor {
    int index;
    int red;
    int green;
    int blue;
    public SolidColor(java.awt.Color color){
        this.index = -3; // Out of group
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
    }
    public SolidColor(int red, int green, int blue) {
        this.index = -3; // Out of group
        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    private SolidColor(int index, int red, int green, int blue) {
        this.index = index;
        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    public int getRed(){
        return red;
    }
    public int getGreen(){
        return green;
    }
    public int getBlue(){
        return blue;
    }
    public java.awt.Color getColor(){
        return new java.awt.Color(red, green, blue);
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SolidColor that)) return false;
        return red == that.red && green == that.green && blue == that.blue;
    }
    public int hashCode() {
        return Objects.hash(red, green, blue);
    }
    public SolidColor clone(){
        return new SolidColor(red, green, blue);
    }
    public static final SolidColor BLACK = new SolidColor(-2, 0, 0, 0);
    public static final SolidColor RED = new SolidColor(-2, 255, 0, 0);
    public static final SolidColor YELLOW = new SolidColor(-2, 255, 255, 0);
    public static final SolidColor GREEN = new SolidColor(-2, 0, 255, 0);
    public static final SolidColor CYAN = new SolidColor(-2, 0, 255, 255);
    public static final SolidColor BLUE = new SolidColor(-2, 0, 0, 255);
    public static final SolidColor MAGENTA = new SolidColor(-2, 255, 0, 255);
    public static final SolidColor WHITE = new SolidColor(-2, 255, 255, 255);
}
