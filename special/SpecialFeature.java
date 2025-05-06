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

/**
 * Represents an optional special feature in the game that can be identified,
 * described, and toggled on or off. This feature may modify objects in the game.
 * Provides methods to retrieve metadata about the feature and process input objects.
 */
public interface SpecialFeature {
    /**
     * Returns the unique identifier of the feature within its group.
     * This identifier must be consistent across all versions.
     * @return the feature ID
     */
    int getFeatureID();
    /**
     * Returns the identifier of the group to which the feature belongs.
     * This identifier must be consistent across all versions.
     * @return the group ID
     */
    int getGroupID();
    /**
     * Returns the human-readable name of the feature.
     * @return the feature name
     */
    String getFeatureName();
    /**
     * Returns the human-readable name of the group this feature belongs to.
     * @return the group name
     */
    String getGroupName();
    /**
     * Returns a description of the feature's behavior or intention.
     * This paragraph of description must end in {@code \n}.
     * @return the feature description
     */
    String getFeatureDescription();
    /**
     * Returns the name of the target this feature is intended to affect.
     * This may be specific to a class or feature in the game, or a graphics element.
     * @return the feature target
     */
    String getFeatureTarget();
    /**
     * Returns the major version of the system that this feature support.
     * @return the major support version number
     */
    int getSupportVersionMajor();
    /**
     * Returns the minor version of the system that this feature support.
     * @return the minor support version number
     */
    int getSupportVersionMinor();
    /** Validate this feature. It is recommended to only call this check once. */
    boolean validate();
    /** Enables the feature. This does not necessarily activate this feature. */
    void enable();
    /** Disables the feature. This always deactivate this feature. */
    void disable();
    /**
     * Checks whether the feature is currently active.
     * When a feature fails validation checks, it will not be active despite being enabled.
     * @return {@code true} if the feature is active; {@code false} otherwise
     */
    boolean isActive();
    /**
     * Processes the given array of objects according to the feature's logic.
     *
     * @param objects the input objects to process
     * @return an array of processed objects, or {@code null} if the feature does not or fails produce output
     */
    Object[] process(Object[] objects);
    /**
     * Processes a single object according to the feature's logic.
     *
     * @param object the input object to process
     * @return the processed object, or {@code null} if the feature does not or fails to produce output
     */
    default Object process(Object object){
        Object[] result = process(new Object[]{object});
        if(result == null || result.length == 0){
            return null;
        } return result[0];
    }
    /**
     * Processes a single object with a description according to the feature's logic.
     *
     * @param object      the input object to process
     * @param description the description of the object to be processed
     * @return the processed object, or {@code null} if the feature does not or fails to produce output
     */
    default Object process(Object object, String description){
        Object[] result = process(new Object[]{object, description});
        if(result == null || result.length == 0){
            return null;
        } return result[0];
    }
    /**
     * An automatic generated human-readable description string for this feature, including ID, name, group,
     * target, description, required version, and active status.
     *
     * @return the description string of the feature
     */
    default String description(){
        return "Feature " + getGroupID() + "-" + getFeatureID() + " " +
               getFeatureName() + " in the " + getGroupName() + " group targets " +
               getFeatureTarget() + ".\n" + getFeatureDescription() + "This feature requires" +
               " version " + getSupportVersionMajor() + "." + getSupportVersionMinor() +
               " or higher and can be disabled.\n" + "It is now " + (isActive() ? "" : "in") + "active";
    }
}
