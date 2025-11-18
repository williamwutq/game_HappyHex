package util.geom;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Represents a closed shape defined by a series of points and control points for quadratic Bézier curves.
 * Each point is defined by its (x, y) coordinates and a control point (controlX, controlY) that influences the curve
 * between this point and the next point.
 * The shape is closed by connecting the last point back to the first point using its control point.
 */
public class CurvedShape {
    private final double[] xPoints;
    private final double[] yPoints;
    private final double[] xControls;
    private final double[] yControls;
    private final int numPoints;
    /**
     * A predefined CurvedShape representing a circle.
     * The circle is approximated using four quadratic Bézier curves.
     */
    public static final CurvedShape CIRCLE = new CurvedShape(
            new double[][]{
                    {1, 0, 1, 1}, {0, 1, -1, 1},
                    {-1, 0, -1, -1}, {0, -1, 1, -1}
            }
    );
    /**
     * A predefined CurvedShape representing a square.
     * The square is defined by its four corners and control points that create straight lines between them.
     */
    public static final CurvedShape SQUARE = new CurvedShape(
            new double[][]{
                    {1, 1, 1, 0}, {1, -1, 0, -1},
                    {-1, -1, -1, 0}, {-1, 1, 0, 1}
            }
    );

    /**
     * Constructs a CurvedShape from an array of points. Each point is represented as a double array of four elements:
     * [x, y, controlX, controlY].
     * @param points an array of points defining the curved shape
     * @throws IllegalArgumentException if less than two points are provided or if any point does not have exactly four elements
     */
    public CurvedShape(double[][] points){
        if (points.length < 2) {
            throw new IllegalArgumentException("At least two points are required");
        }
        this.numPoints = points.length;
        this.xPoints = new double[numPoints];
        this.yPoints = new double[numPoints];
        this.xControls = new double[numPoints];
        this.yControls = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            if (points[i].length != 4) {
                throw new IllegalArgumentException("Each point must have exactly four elements: [x, y, controlX, controlY]");
            }
            xPoints[i] = points[i][0];
            yPoints[i] = points[i][1];
            xControls[i] = points[i][2];
            yControls[i] = points[i][3];
        }
    }
    /**
     * Constructs a CurvedShape from separate arrays of x and y coordinates for points and control points.
     * @param xPoints array of x coordinates for the points
     * @param yPoints array of y coordinates for the points
     * @param xControls array of x coordinates for the control points
     * @param yControls array of y coordinates for the control points
     * @throws IllegalArgumentException if the lengths of the input arrays do not match
     */
    public CurvedShape(double[] xPoints, double[] yPoints, double[] xControls, double[] yControls) {
        if (xPoints.length != yPoints.length || xPoints.length != xControls.length || xPoints.length != yControls.length) {
            throw new IllegalArgumentException("All input arrays must have the same length.");
        }
        this.xControls = xControls.clone();
        this.yControls = yControls.clone();
        this.xPoints = xPoints.clone();
        this.yPoints = yPoints.clone();
        this.numPoints = xPoints.length;
    }
    /**
     * Returns a new CurvedShape that is a scaled version of this shape.
     * @param scaleX the scaling factor in the x direction
     * @param scaleY the scaling factor in the y direction
     * @return a new CurvedShape scaled by the specified factors
     */
    public CurvedShape scaled(double scaleX, double scaleY) {
        double[] newXPoints = new double[numPoints];
        double[] newYPoints = new double[numPoints];
        double[] newXControls = new double[numPoints];
        double[] newYControls = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            newXPoints[i] = xPoints[i] * scaleX;
            newYPoints[i] = yPoints[i] * scaleY;
            newXControls[i] = xControls[i] * scaleX;
            newYControls[i] = yControls[i] * scaleY;
        }
        return new CurvedShape(newXPoints, newYPoints, newXControls, newYControls);
    }
    /**
     * Returns a new CurvedShape that is a scaled version of this shape.
     * @param scale the scaling factor
     * @return a new CurvedShape scaled by the specified factor
     */
    public CurvedShape scaled(double scale) {
        double[] newXPoints = new double[numPoints];
        double[] newYPoints = new double[numPoints];
        double[] newXControls = new double[numPoints];
        double[] newYControls = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            newXPoints[i] = xPoints[i] * scale;
            newYPoints[i] = yPoints[i] * scale;
            newXControls[i] = xControls[i] * scale;
            newYControls[i] = yControls[i] * scale;
        }
        return new CurvedShape(newXPoints, newYPoints, newXControls, newYControls);
    }
    /**
     * Returns a new CurvedShape that is a shifted version of this shape.
     * @param deltaX the amount to shift in the x direction
     * @param deltaY the amount to shift in the y direction
     * @return a new CurvedShape shifted by the specified amounts
     */
    public CurvedShape shifted(double deltaX, double deltaY) {
        double[] newXPoints = new double[numPoints];
        double[] newYPoints = new double[numPoints];
        double[] newXControls = new double[numPoints];
        double[] newYControls = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            newXPoints[i] = xPoints[i] + deltaX;
            newYPoints[i] = yPoints[i] + deltaY;
            newXControls[i] = xControls[i] + deltaX;
            newYControls[i] = yControls[i] + deltaY;
        }
        return new CurvedShape(newXPoints, newYPoints, newXControls, newYControls);
    }
    /**
     * Converts the CurvedShape to a java.awt.Shape object.
     * @return a Shape representing the curved shape
     */
    public Shape toShape() {
        GeneralPath path = new GeneralPath();
        if (numPoints > 0) {
            path.moveTo(xPoints[0], yPoints[0]);
            for (int i = 1; i < numPoints; i++) {
                path.quadTo(
                        xControls[i - 1], yControls[i - 1],
                        xPoints[i], yPoints[i]
                );
            }
            path.quadTo(
                    xControls[numPoints - 1], yControls[numPoints - 1],
                    xPoints[0], yPoints[0]
            );
            path.closePath();
        }
        return path;
    }
    /**
     * Converts the CurvedShape to an array of doubles in the format [[x1, y1, controlX1, controlY1], [x2, y2, controlX2, controlY2], ...].
     * @return an array of doubles representing the curved shape
     */
    public double[][] toArray() {
        double[][] points = new double[numPoints][4];
        for (int i = 0; i < numPoints; i++) {
            points[i][0] = xPoints[i];
            points[i][1] = yPoints[i];
            points[i][2] = xControls[i];
            points[i][3] = yControls[i];
        }
        return points;
    }
    /**
     * Converts the CurvedShape to a JsonArrayBuilder in the format [{"x": x1, "y": y1, "cx": controlX1, "cy": controlY1}, ...].
     * @return a JsonArrayBuilder representing the curved shape
     */
    public JsonArrayBuilder toJsonArrayBuilder() {
        JsonArrayBuilder jab = javax.json.Json.createArrayBuilder();
        for (int i = 0; i < numPoints; i++) {
            JsonObjectBuilder pointBuilder = javax.json.Json.createObjectBuilder();
            pointBuilder.add("x", xPoints[i]);
            pointBuilder.add("y", yPoints[i]);
            pointBuilder.add("cx", xControls[i]);
            pointBuilder.add("cy", yControls[i]);
            jab.add(pointBuilder);
        }
        return jab;
    }
    /**
     * Constructs a CurvedShape from a JsonArray in the format [{"x": x1, "y": y1, "cx": controlX1, "cy": controlY1}, ...].
     * @param ja a JsonArray representing the curved shape
     * @return a CurvedShape constructed from the JsonArray
     * @throws IllegalArgumentException if the JsonArray is not in the expected format
     */
    public static CurvedShape fromJsonArray(javax.json.JsonArray ja) {
        int size = ja.size();
        double[][] points = new double[size][4];
        for (int i = 0; i < size; i++) {
            try {
                javax.json.JsonObject pointObj = ja.getJsonObject(i);
                points[i][0] = pointObj.getJsonNumber("x").doubleValue();
                points[i][1] = pointObj.getJsonNumber("y").doubleValue();
                points[i][2] = pointObj.getJsonNumber("cx").doubleValue();
                points[i][3] = pointObj.getJsonNumber("cy").doubleValue();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON format for CurvedShape", e);
            }
        }
        return new CurvedShape(points);
    }
    /**
     * Converts the CurvedShape to an SVG path string.
     * @return a String representing the SVG path of the curved shape
     */
    public String toSvgPath() {
        StringBuilder sb = new StringBuilder();
        if (numPoints > 0) {
            sb.append(String.format("M %.3f %.3f ", xPoints[0], yPoints[0]));
            for (int i = 1; i < numPoints; i++) {
                sb.append(String.format("Q %.3f %.3f, %.3f %.3f ", xControls[i - 1], yControls[i - 1], xPoints[i], yPoints[i]));
            }
            sb.append(String.format("Q %.3f %.3f, %.3f %.3f Z", xControls[numPoints - 1], yControls[numPoints - 1], xPoints[0], yPoints[0]));
        }
        return sb.toString().trim();
    }
}
