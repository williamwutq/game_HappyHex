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

public class DefaultFeature implements SpecialFeature{
    private boolean enable;
    public DefaultFeature(){
        this.enable = true;
    }
    public int getFeatureID() {
        return 0;
    }
    public int getGroupID() {
        return 1; // default
    }
    public String getFeatureName() {
        return "DefaultFeature";
    }
    public String getGroupName() {
        return "Default";
    }
    public String getFeatureDescription() {
        return "This feature does nothing";
    }
    public String getFeatureTarget() {
        return "Everywhere";
    }
    public int getSupportVersionMajor() {
        return 0;
    }
    public int getSupportVersionMinor() {
        return 0;
    }
    public boolean validate() {
        return true; // Always valid
    }
    public void enable() {
        enable = true;
    }
    public void disable() {
        enable = false;
    }
    public boolean isActive() {
        return enable;
    }
    public Object[] process(Object[] objects) {
        return objects;
    }
}
