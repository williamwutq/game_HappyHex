package util.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a mutable curved shape defined by a series of points and control points for quadratic Bézier curves.
 * Each point is defined by its (x, y) coordinates and a control point (controlX, controlY) that influences the curve
 * between this point and the next point.
 * The shape is closed by connecting the last point back to the first point using its control point.
 * This class allows for dynamic modification of the shape by adding, removing, or changing points, and offers method
 * to convert to an immutable {@link CurvedShape}.
 */
public class MutableCurvedShape implements Cloneable {
    private final List<double[]> points;
    /**
     * Constructs an empty MutableCurvedShape. This shape can be modified by adding, removing, or changing points.
     */
    public MutableCurvedShape(){
        this.points = new ArrayList<>();
    }
    /**
     * Constructs a MutableCurvedShape from an existing CurvedShape.
     * The new shape will have the same points and control points as the provided shape.
     * @param shape the CurvedShape to copy points from
     */
    public MutableCurvedShape(CurvedShape shape){
        this.points = new ArrayList<>();
        double[][] array = shape.toArray();
        for(double[] point : array){
            points.add(new double[]{point[0], point[1], point[2], point[3]});
        }
    }
    /**
     * Adds a point to the MutableCurvedShape.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param controlX the x coordinate of the control point
     * @param controlY the y coordinate of the control point
     */
    public void addPoint(double x, double y, double controlX, double controlY) {
        points.add(new double[]{x, y, controlX, controlY});
    }
    /**
     * Inserts a point at the specified index in the MutableCurvedShape.
     * @param index the index at which the point should be inserted
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param controlX the x coordinate of the control point
     * @param controlY the y coordinate of the control point
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    public void addPoint(int index, double x, double y, double controlX, double controlY) {
        if (index < 0 || index > points.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + points.size());
        }
        points.add(index, new double[]{x, y, controlX, controlY});
    }
    /**
     * Removes the point at the specified index from the MutableCurvedShape.
     * @param index the index of the point to be removed
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    public void removePoint(int index) {
        if (index < 0 || index >= points.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + points.size());
        }
        points.remove(index);
    }
    /**
     * Removes the last point from the MutableCurvedShape.
     * If the shape is empty, this method does nothing.
     */
    public void removeLast() {
        if (!points.isEmpty()) {
            points.removeLast();
        }
    }
    /**
     * Removes the first point from the MutableCurvedShape.
     * If the shape is empty, this method does nothing.
     */
    public void removeFirst() {
        if (!points.isEmpty()) {
            points.removeFirst();
        }
    }
    /**
     * Clears all points from the MutableCurvedShape.
     */
    public void clear() {
        points.clear();
    }
    /**
     * Sets the coordinates and control point of the specified point.
     * @param index the index of the point to be set
     * @param x the new x coordinate of the point
     * @param y the new y coordinate of the point
     * @param controlX the new x coordinate of the control point
     * @param controlY the new y coordinate of the control point
     */
    public void setPoint(int index, double x, double y, double controlX, double controlY) {
        if (index < 0 || index >= points.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + points.size());
        }
        points.set(index, new double[]{x, y, controlX, controlY});
    }
    /**
     * Moves the specified point by the given delta values.
     * @param index the index of the point to be moved
     * @param deltaX the amount to move the point in the x direction
     * @param deltaY the amount to move the point in the y direction
     */
    public void movePoint(int index, double deltaX, double deltaY) {
        if (index < 0 || index >= points.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + points.size());
        }
        double[] point = points.get(index);
        point[0] += deltaX;
        point[1] += deltaY;
    }
    /**
     * Moves the control point of the specified point by the given delta values.
     * @param index the index of the point whose control point is to be moved
     * @param deltaX the amount to move the control point in the x direction
     * @param deltaY the amount to move the control point in the y direction
     */
    public void moveControl(int index, double deltaX, double deltaY) {
        if (index < 0 || index >= points.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + points.size());
        }
        double[] point = points.get(index);
        point[2] += deltaX;
        point[3] += deltaY;
    }
    /**
     * Scales the entire MutableCurvedShape by the given factors in the x and y directions.
     * @param scaleX the factor to scale in the x direction
     * @param scaleY the factor to scale in the y direction
     */
    public void scale(double scaleX, double scaleY) {
        for (double[] point : points) {
            point[0] *= scaleX;
            point[1] *= scaleY;
            point[2] *= scaleX;
            point[3] *= scaleY;
        }
    }
    /**
     * Scales the entire MutableCurvedShape uniformly by the given factor.
     * @param scale the factor to scale in both the x and y directions
     */
    public void scale(double scale) {
        scale(scale, scale);
    }
    /**
     * Moves the entire MutableCurvedShape by the given delta values in the x and y directions.
     * @param deltaX the amount to move in the x direction
     * @param deltaY the amount to move in the y direction
     */
    public void move(double deltaX, double deltaY) {
        for (double[] point : points) {
            point[0] += deltaX;
            point[1] += deltaY;
            point[2] += deltaX;
            point[3] += deltaY;
        }
    }
    /**
     * Rotates the entire MutableCurvedShape around the origin (0,0) by the given angle in degrees.
     * Positive angles represent counter-clockwise rotation.
     * @param angle the angle in degrees to rotate the shape
     */
    public void rotate(double angle) {
        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        for (double[] point : points) {
            double x = point[0];
            double y = point[1];
            point[0] = x * cos - y * sin;
            point[1] = x * sin + y * cos;
            double controlX = point[2];
            double controlY = point[3];
            point[2] = controlX * cos - controlY * sin;
            point[3] = controlX * sin + controlY * cos;
        }
    }
    /**
     * Mirrors the entire MutableCurvedShape across the Y-axis.
     * This operation negates the x-coordinates of all points and control points.
     */
    public void mirrorY(){
        for(double[] point : points){
            point[1] = -point[1];
            point[3] = -point[3];
        }
    }
    /**
     * Mirrors the entire MutableCurvedShape across the X-axis.
     * This operation negates the y-coordinates of all points and control points.
     */
    public void mirrorX(){
        for(double[] point : points){
            point[0] = -point[0];
            point[2] = -point[2];
        }
    }
    /**
     * Mirrors the entire MutableCurvedShape across the line y = x.
     * This operation swaps the x and y coordinates of all points and control points.
     */
    public void mirrorC(){
        for(double[] point : points){
            double temp = point[0];
            point[0] = point[1];
            point[1] = temp;
            temp = point[2];
            point[2] = point[3];
            point[3] = temp;
        }
    }
    /**
     * Returns the number of points in the MutableCurvedShape.
     * @return the number of points
     */
    public int size() {
        return points.size();
    }
    /**
     * Converts the MutableCurvedShape to an immutable CurvedShape.
     * If the shape has fewer than 3 points, this method returns null.
     * @return a CurvedShape representing the current state of the MutableCurvedShape
     */
    public CurvedShape toCurvedShape() {
        if (points.size() < 3) {
            return null;
        }
        return new CurvedShape(points.toArray(new double[0][0]));
    }
    /**
     * Rounds the coordinates and control points of all points in the MutableCurvedShape to the specified number of decimal places.
     * @param precision the number of decimal places to round to
     */
    public void round(int precision){
        double factor = Math.pow(10, precision);
        for(double[] point : points){
            point[0] = Math.round(point[0] * factor) / factor;
            point[1] = Math.round(point[1] * factor) / factor;
            point[2] = Math.round(point[2] * factor) / factor;
            point[3] = Math.round(point[3] * factor) / factor;
        }
    }
    /**
     * Converts the points of the MutableCurvedShape to a 2D array.
     * Each row in the array represents a point in the format [x, y, controlX, controlY].
     * @return a 2D array of points
     */
    public double [][] toArray(){
        return points.toArray(new double[0][0]);
    }
    /**
     * Creates and returns a deep copy of this MutableCurvedShape.
     * @return a clone of this MutableCurvedShape
     */
    public MutableCurvedShape clone(){
        MutableCurvedShape clone = new MutableCurvedShape();
        for(double[] point : points){
            clone.addPoint(point[0], point[1], point[2], point[3]);
        }
        return clone;
    }
}
