package util.geom;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class CurveGenerator {
    public static void main(String[] args){
        final Color controlColor = new Color(0, 153, 255);
        final Color controlColorA = new Color(0, 255, 221);
        final Color colorA = new Color(255, 255, 255, 191);
        final Color pointColor = new Color(255, 51, 51);
        final Color pointColorA = new Color(255, 153, 0);
        final Color pointCacheColor = new Color(204, 125, 188);
        final Color backgroundColor = new Color(255, 255, 0,128);
        final String[] commands = new String[]{
                "add", "set", "sp", "sc", "sr", "ins", "mv", "mx", "my", "mp", "mc", "mr", "sm", "st", "div", "dva",
                "scl", "sxy", "scb", "ssb", "rot", "mrx", "mry", "mrc", "mov", "regp", "moa", "mao", "ms",
                "rd", "rm", "rml", "rmf", "rma", "rmr", "rmra", "srz", "r",
                "circle", "square", "make",
                "clear", "print", "pp", "json", "info", "undo", "redo", "rmhis",
                "s", "oa", "ao", "o", "a",
                "psb", "pb", "rmb", "clb", "ldb", "lsb", "printb", "grep",
                "sysinfo", "exit", "quit", "help",
                "svgp", "svgf", "svgb"
        };
        final ArrayList<MutableCurvedShape> pastShapes = new ArrayList<>();
        final ArrayList<MutableCurvedShape> pastShapesA = new ArrayList<>();
        final double[][] pointCache = new double[][] {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0},
                                                      {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}}; // max 18 points
        pastShapes.add(new MutableCurvedShape());
        pastShapesA.add(new MutableCurvedShape());
        final Stack<CurvedShape> backgroundShapes = new Stack<>();
        final AtomicInteger undoIndex = new AtomicInteger(0);
        final AtomicInteger undoIndexA = new AtomicInteger(0);
        JFrame f = new JFrame();
        final AtomicReference<MutableCurvedShape> s = new AtomicReference<MutableCurvedShape>(new MutableCurvedShape());
        final AtomicReference<MutableCurvedShape> a = new AtomicReference<MutableCurvedShape>(new MutableCurvedShape());
        final AtomicBoolean ARegister = new AtomicBoolean(false); // true to use A register instead of O register
        final AtomicReference<String> previousCommand = new AtomicReference<>("");
        AtomicReference<Double> boardScale = new AtomicReference<>(1.0);
        JPanel p = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.WHITE);
                int w = getWidth();
                int h = getHeight();
                double scale = Math.min(w, h) * 0.5 * boardScale.get();
                CurvedShape shape, shapeA;
                if (ARegister.get()){
                    shapeA = s.get().toCurvedShape();
                    shape = a.get().toCurvedShape();
                } else {
                    shape = s.get().toCurvedShape();
                    shapeA = a.get().toCurvedShape();
                }
                double[][] array, arrayA;
                if (shape != null) {
                    shape = shape.scaled(scale, scale).shifted(w / 2.0, h / 2.0);
                    array = shape.toArray();
                } else array = null;
                if (shapeA != null) {
                    shapeA = shapeA.scaled(scale, scale).shifted(w / 2.0, h / 2.0);
                    arrayA = shapeA.toArray();
                } else arrayA = null;
                if (shape != null) {
                    // Fill the shape
                    g2d.setColor(Color.WHITE);
                    g2d.fill(shape.toShape());
                    // Draw line from point to control point
                    g2d.setColor(Color.GRAY);
                    for (int i = 0; i < array.length; i++) {
                        double[] point = array[i];
                        g2d.drawLine((int) point[0], (int) point[1], (int) point[2], (int) point[3]);
                        if (i == 0) {
                            // Draw line from last point to its control point
                            double[] lastPoint = array[array.length - 1];
                            g2d.drawLine((int) point[0], (int) point[1], (int) lastPoint[2], (int) lastPoint[3]);
                        } else {
                            // Draw line from previous point to its control point
                            double[] prevPoint = array[i - 1];
                            g2d.drawLine((int) point[0], (int) point[1], (int) prevPoint[2], (int) prevPoint[3]);
                        }
                    }
                }
                if (shapeA != null) {
                    // Fill the shape
                    g2d.setColor(colorA);
                    g2d.fill(shapeA.toShape());
                    // Draw line from point to control point
                    g2d.setColor(Color.GRAY);
                    for (int i = 0; i < arrayA.length; i++) {
                        double[] point = arrayA[i];
                        g2d.drawLine((int) point[0], (int) point[1], (int) point[2], (int) point[3]);
                        if (i == 0) {
                            // Draw line from last point to its control point
                            double[] lastPoint = arrayA[arrayA.length - 1];
                            g2d.drawLine((int) point[0], (int) point[1], (int) lastPoint[2], (int) lastPoint[3]);
                        } else {
                            // Draw line from previous point to its control point
                            double[] prevPoint = arrayA[i - 1];
                            g2d.drawLine((int) point[0], (int) point[1], (int) prevPoint[2], (int) prevPoint[3]);
                        }
                    }
                }
                if (pointCache != null && pointCache.length > 0) {
                    double[][] transformedPointCache = new double[pointCache.length][2];
                    // Scale and shift points
                    for (int i = 0; i < pointCache.length; i++) {
                        transformedPointCache[i][0] = pointCache[i][0] * scale + w / 2.0;
                        transformedPointCache[i][1] = pointCache[i][1] * scale + h / 2.0;
                    }
                    double[] zero = new double[]{w / 2.0, h / 2.0};
                    // Draw points
                    g2d.setColor(pointCacheColor);
                    for (double[] point : transformedPointCache) {
                        if (Math.abs(point[0] - zero[0]) < 0.01 && Math.abs(point[1] - zero[1]) < 0.01) continue;
                        g2d.drawOval((int) (point[0] - 8), (int) (point[1] - 8), 16, 16);
                    }
                    // Write number next to each point
                    for (int i = 0; i < transformedPointCache.length; i++) {
                        double[] point = transformedPointCache[i];
                        if (Math.abs(point[0] - zero[0]) < 0.01 && Math.abs(point[1] - zero[1]) < 0.01) continue;
                        String str = Integer.toString(i);
                        g2d.drawString(str, (int) point[0] - 4 * (str.length()), (int) point[1] + 4);
                    }
                }
                if (shape != null) {
                    // Draw points
                    g2d.setColor(pointColor);
                    for (double[] point : array) {
                        g2d.drawOval((int) (point[0] - 8), (int) (point[1] - 8), 16, 16);
                    }
                    // Draw control point
                    g2d.setColor(controlColor);
                    for (double[] point : array) {
                        g2d.drawOval((int) (point[2] - 8), (int) (point[3] - 8), 16, 16);
                    }
                    // Write number next to each point
                    g2d.setColor(pointColor);
                    for (int i = 0; i < array.length; i++) {
                        double[] point = array[i];
                        String str = Integer.toString(i);
                        g2d.drawString(str, (int) point[0] - 4 * (str.length()), (int) point[1] + 4);
                    }
                    // Write number next to each control point
                    g2d.setColor(controlColor);
                    for (int i = 0; i < array.length; i++) {
                        double[] point = array[i];
                        String str = Integer.toString(i);
                        g2d.drawString(str, (int) point[2] - 4 * (str.length()), (int) point[3] + 4);
                    }
                }
                if (shapeA != null) {
                    // Draw points
                    g2d.setColor(pointColorA);
                    for (double[] point : arrayA) {
                        g2d.drawOval((int) (point[0] - 8), (int) (point[1] - 8), 16, 16);
                    }
                    // Draw control point
                    g2d.setColor(controlColorA);
                    for (double[] point : arrayA) {
                        g2d.drawOval((int) (point[2] - 8), (int) (point[3] - 8), 16, 16);
                    }
                    // Write number next to each point
                    g2d.setColor(pointColorA);
                    for (int i = 0; i < arrayA.length; i++) {
                        double[] point = arrayA[i];
                        String str = Integer.toString(i);
                        g2d.drawString(str, (int) point[0] - 4 * (str.length()), (int) point[1] + 4);
                    }
                    // Write number next to each control point
                    g2d.setColor(controlColorA);
                    for (int i = 0; i < arrayA.length; i++) {
                        double[] point = arrayA[i];
                        String str = Integer.toString(i);
                        g2d.drawString(str, (int) point[2] - 4 * (str.length()), (int) point[3] + 4);
                    }
                }
                // Draw background shapes
                g2d.setColor(backgroundColor);
                for (CurvedShape bgShape : backgroundShapes) {
                    CurvedShape transformed = bgShape.scaled(scale, scale).shifted(w / 2.0, h / 2.0);
                    g2d.fill(transformed.toShape());
                }
                // Draw border and coordinates of the corners
                g2d.setColor(Color.DARK_GRAY);
                CurvedShape fittedSquare = CurvedShape.SQUARE.scaled(scale, scale).shifted(w / 2.0, h / 2.0);
                CurvedShape fittedCircle = CurvedShape.CIRCLE.scaled(scale * 0.9, scale * 0.9).shifted(w / 2.0, h / 2.0);
                CurvedShape smallerSquare = CurvedShape.SQUARE.scaled(scale * 0.9, scale * 0.9).shifted(w / 2.0, h / 2.0);
                double[][] corners = CurvedShape.SQUARE.toArray();
                g2d.draw(fittedSquare.toShape());
                g2d.draw(fittedCircle.toShape());
                // Write number next to each point
                double[][] smallArray = smallerSquare.toArray();
                for (int i = 0; i < smallArray.length; i++) {
                    double[] point = smallArray[i];
                    g2d.drawString(intToString((int)corners[i][0]) + ", " + intToString((int)corners[i][1]), (int) point[0] - 20, (int) point[1] + 4);
                }
            }
            private String intToString(int i) {
                if (i < 0) {
                    return i + "";
                } else return "+" + i;
            }
        };
        f.setSize(600, 600);
        p.setBackground(Color.BLACK);
        f.add(p);
        f.setTitle("Curve Generator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBackground(Color.BLACK);
        f.setVisible(true);
        // Listen in standard input
        new Thread(() -> {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            listeningLoop:
            while (true) {
                String prev = previousCommand.get();
                System.out.print(">>> ");
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                } else if (line.equals("r")){
                    line = prev; // We do not update previous command on repeat
                } else {
                    previousCommand.set(line);
                }
                AtomicReference<MutableCurvedShape> shapeRef; MutableCurvedShape shapeObj; AtomicInteger undoRef; ArrayList<MutableCurvedShape> pastRef;
                if (ARegister.get()){
                    shapeRef = a; shapeObj = a.get(); undoRef = undoIndexA; pastRef = pastShapesA;
                } else{
                    shapeRef = s; shapeObj = s.get(); undoRef = undoIndex; pastRef = pastShapes;
                }
                if (line.equals("exit") || line.equals("quit")) {
                    System.exit(0);
                }
                else if (line.equals("s") || line.equals("oa") || line.equals("ao")){
                    // Switch registers
                    if (ARegister.getAndSet(!ARegister.get())){
                        System.out.println("Switch to O register");
                    } else {
                        System.out.println("Switch to A register");
                    }
                    p.repaint();
                } else if (line.equals("o")){
                    if (ARegister.getAndSet(false)) {
                        System.out.println("Switch to O register");
                        p.repaint();
                    }
                } else if (line.equals("a")){
                    if (!ARegister.getAndSet(true)) {
                        System.out.println("Switch to A register");
                        p.repaint();
                    }
                } else if (line.equals("mao")){
                    // Move shape in a to o
                    s.set(a.get().clone());
                    addAndBreakUndoChain(s.get(), undoIndex, pastShapes);
                } else if (line.equals("moa")){
                    // Move shape in o to a
                    a.set(s.get().clone());
                    addAndBreakUndoChain(a.get(), undoIndexA, pastShapesA);
                } else if (line.equals("ms")){
                    // If we are in a, move shape to o, else move shape to a
                    if (ARegister.get()){
                        s.set(a.get().clone());
                        addAndBreakUndoChain(s.get(), undoIndex, pastShapes);
                    } else {
                        a.set(s.get().clone());
                        addAndBreakUndoChain(a.get(), undoIndexA, pastShapesA);
                    }
                } else if (line.equals("reg")){
                    if (ARegister.get()) {
                        System.out.println("In A register: Auxiliary Shape Register");
                    } else {
                        System.out.println("In O register: Ordinary Shape Register");
                    }
                } else if (line.equals("regp")){
                    System.out.println("Point Registers:");
                    for (int i = 0; i < pointCache.length; i++) {
                        System.out.print("R" + i + ": ");
                        if (Math.abs(pointCache[i][0]) < 0.0001 && Math.abs(pointCache[i][1]) < 0.0001) {
                            System.out.println("unused");
                        } else {
                            System.out.println("(" + pointCache[i][0] + ", " + pointCache[i][1] + ")");
                        }
                    }
                } else if (line.startsWith("grep")){
                    try {
                        boolean countOnly = false;
                        boolean includeHistory = false;
                        boolean includeIndices = false;
                        String range = null;
                        String regs = null;
                        String[] parts = splitArgs(line, 4);
                        for (String part : parts) {
                            if (part.equals("-c")) {
                                countOnly = true;
                            } else if (part.equals("-h")) {
                                includeHistory = true;
                            } else if (part.equals("-i")) {
                                includeIndices = true;
                            } else if (range == null) {
                                range = part;
                            } else if (regs == null) {
                                regs = part;
                            } else {
                                System.out.println("Extra argument: " + part);
                                continue listeningLoop;
                            }
                        }
                        if (range == null || regs == null) {
                            System.out.println("Invalid arguments. Usage: grep [flags] range regs");
                            continue listeningLoop;
                        }
                        Predicate<double[]> rangePredicate;
                        try {
                            rangePredicate = createTestBasedOnExpression(range);
                        } catch (Exception e) {
                            if (range.equals("all")) {
                                rangePredicate = point -> true;
                            } else {
                                System.out.println("Invalid range expression: " + e.getMessage());
                                continue listeningLoop;
                            }
                        }
                        // Check register only contains o, a, r
                        for (char c : regs.toCharArray()) {
                            if (c != 'o' && c != 'a' && c != 'r' && c != 'O' && c != 'A' && c != 'R') {
                                System.out.println("Invalid register specifier: " + c);
                                continue listeningLoop;
                            }
                        }
                        boolean includeO = regs.toLowerCase().contains("o");
                        boolean includeA = regs.toLowerCase().contains("a");
                        boolean includeR = regs.toLowerCase().contains("r");
                        HashMap<double[], String> map = new HashMap(); // Map from point to name
                        ArrayList<String> outputStrings = new ArrayList<>();
                        if (includeO) {
                            try {
                                double[][] array = s.get().toCurvedShape().toArray();
                                for (int i = 0; i < array.length; i++) {
                                    double[] pointAndCtrl = array[i];
                                    double[] point = new double[]{pointAndCtrl[0], pointAndCtrl[1]};
                                    double[] control = new double[]{pointAndCtrl[2], pointAndCtrl[3]};
                                    String indexString = (includeIndices ? i + "" : "");
                                    map.put(point, "op" + indexString);
                                    map.put(control, "oc" + indexString);
                                }
                                if (includeHistory) {
                                    // Dig history
                                    for (int h = pastShapes.size() - 2; h >= 0; h--) {
                                        MutableCurvedShape histShape = pastShapes.get(h);
                                        double[][] histArray = histShape.toCurvedShape().toArray();
                                        for (int i = 0; i < histArray.length; i++) {
                                            double[] pointAndCtrl = histArray[i];
                                            double[] point = new double[]{pointAndCtrl[0], pointAndCtrl[1]};
                                            double[] control = new double[]{pointAndCtrl[2], pointAndCtrl[3]};
                                            String indexString = (includeIndices ? i + "" : "");
                                            String historyIndexString = (includeIndices ? "h" + (pastShapes.size() - 1 - h) : "");
                                            map.putIfAbsent(point, historyIndexString + "op" + indexString);
                                            map.putIfAbsent(control, historyIndexString + "oc" + indexString);
                                        }
                                    }
                                }
                            } catch (NullPointerException ignored) {

                            }
                        }
                        if (includeA) {
                            try {
                                double[][] array = a.get().toCurvedShape().toArray();
                                for (int i = 0; i < array.length; i++) {
                                    double[] pointAndCtrl = array[i];
                                    double[] point = new double[]{pointAndCtrl[0], pointAndCtrl[1]};
                                    double[] control = new double[]{pointAndCtrl[2], pointAndCtrl[3]};
                                    String indexString = (includeIndices ? i + "" : "");
                                    map.put(point, "ap" + indexString);
                                    map.put(control, "ac" + indexString);
                                }
                                if (includeHistory) {
                                    // Dig history
                                    for (int h = pastShapesA.size() - 2; h >= 0; h--) {
                                        MutableCurvedShape histShape = pastShapesA.get(h);
                                        double[][] histArray = histShape.toCurvedShape().toArray();
                                        for (int i = 0; i < histArray.length; i++) {
                                            double[] pointAndCtrl = histArray[i];
                                            double[] point = new double[]{pointAndCtrl[0], pointAndCtrl[1]};
                                            double[] control = new double[]{pointAndCtrl[2], pointAndCtrl[3]};
                                            String indexString = (includeIndices ? i + "" : "");
                                            String historyIndexString = (includeIndices ? "h" + (pastShapesA.size() - 1 - h) : "");
                                            map.putIfAbsent(point, historyIndexString + "ap" + indexString);
                                            map.putIfAbsent(control, historyIndexString + "ac" + indexString);
                                        }
                                    }
                                }
                            } catch (NullPointerException ignored) {

                            }
                        }
                        if (includeR) {
                            for (int i = 0; i < pointCache.length; i++) {
                                double[] point = pointCache[i];
                                if (Math.abs(point[0]) < 0.0001 && Math.abs(point[1]) < 0.0001) continue;
                                String indexString = (includeIndices ? i + "" : "");
                                map.put(point, "r" + indexString);
                            }
                        }
                        // Check items in map against predicate
                        for (double[] point : map.keySet()) {
                            if (rangePredicate.test(point)) {
                                outputStrings.add(map.get(point) + ": (" + point[0] + ", " + point[1] + ")");
                            }
                        }
                        if (countOnly) {
                            System.out.println("Found " + outputStrings.size() + " matching points.");
                        } else {
                            for (String outputString : outputStrings) {
                                System.out.println(outputString);
                            }
                        }
                    } catch (Exception e){
                        System.out.println("Error during grep: " + e.getMessage());
                    }
                }
                else if (line.equals("help")) {
                    System.out.println("Commands:");
                    System.out.println("  add x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy)");
                    System.out.println("  add x y - Adds a point with coordinates (x, y)");
                    System.out.println("  set index x y cx cy - Sets the point at index to (x, y) with control point (cx, cy)");
                    System.out.println("  sp index dx dy - Sets the point at index by (dx, dy)");
                    System.out.println("  sc index dx dy - Sets the control point at index by (dx, dy)");
                    System.out.println("  sr index dx dy - Sets the point register at index by (dx, dy)");
                    System.out.println("  srz index - Sets the point register at index to (0, 0), effectively clearing it");
                    System.out.println("  ins index x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy) to index index");
                    System.out.println("  mv dx dy - Moves all points by (dx, dy)");
                    System.out.println("  mx dx - Moves all points by (dx, 0)");
                    System.out.println("  my dy - Moves all points by (0, dy)");
                    System.out.println("  mp index dx dy - Moves the point at index by (dx, dy)");
                    System.out.println("  mc index dx dy - Moves the control point at index by (dx, dy)");
                    System.out.println("  mr index dx dy - Moves the point register at index by (dx, dy)");
                    System.out.println("  mov org dest - Set the point at dest to the coordinates of the point at org, including control point. To refer to a point, use (a/o/)(p/c) index for a point or control point in the A or O register, or r index for a point in the point registers");
                    System.out.println("  sm index - Smoothens the point at index with default position 0.5");
                    System.out.println("  sm index pos - Smoothens the point at index with position pos (0 = straight, 1 = full curve)");
                    System.out.println("  sma - Smoothens all points with default position 0.5");
                    System.out.println("  st index - Straightens the point at index with default factor 0.5");
                    System.out.println("  st index factor - Straightens the point at index with factor (0 = no change, 1 = fully straight)");
                    System.out.println("  sta - Straightens all points with default factor 0.5");
                    System.out.println("  div index - Divides the segment starting at index by adding a point in the middle");
                    System.out.println("  div index pos | seg - Divides the segment starting at index at position pos (0 to 1) or into seg segments (>1)");
                    System.out.println("  dva seg - Divides all segments into seg segments (>1)");
                    System.out.println("  mg index - Merges the point at index with the next point, removing the point at index");
                    System.out.println("  scl factor - Scales the entire shape by the given factor");
                    System.out.println("  sxy fx fy - Scales the entire shape by the given x and y factor (fx, fy)");
                    System.out.println("  scb factor - Scales the viewing box by the given factor");
                    System.out.println("  ssb scale - Set the scale of the viewing box to the given scale");
                    System.out.println("  rot angle - Rotates the entire shape by the given angle in degrees");
                    System.out.println("  mrx - Mirrors the shape across the x axis");
                    System.out.println("  mry - Mirrors the shape across the y axis");
                    System.out.println("  mrc - Mirrors the shape across the line y = x, equivalent to swapping x and y coordinates");
                    System.out.println("  rd - Round all points and control points to the nearest two decimal places");
                    System.out.println("  rd n - Round all points and control points to the nearest n decimal places");
                    System.out.println("  rmr index - Removes the point register at index");
                    System.out.println("  rmra - Removes all point registers");
                    System.out.println("  rm index - Removes the indexed point");
                    System.out.println("  rml - Removes the last point");
                    System.out.println("  rmf - Removes the first point");
                    System.out.println("  rma - Clears all points");
                    System.out.println("  make - Creates a shape with the given number of points evenly distributed in a circle");
                    System.out.println("  circle - Sets the shape to a circle");
                    System.out.println("  square - Sets the shape to a square");
                    System.out.println("  clear - Clear the console");
                    System.out.println("  print - Prints the list of points and control points");
                    System.out.println("  pp index - Prints the point and control point at index");
                    System.out.println("  json - Outputs the shape as a JSON array");
                    System.out.println("  json [json] - Loads the shape from a JSON array");
                    System.out.println("  svgp - Outputs the shape as an SVG path");
                    System.out.println("  svgf - Outputs the shape as a complete SVG file string");
                    System.out.println("  svgb - Outputs all background shapes together as a complete SVG file string");
                    System.out.println("  info - Shows information about the current shape");
                    System.out.println("  undo - Undoes the last action");
                    System.out.println("  redo - Redoes the last undone action");
                    System.out.println("  rmhis - Clears the undo/redo history of the current register");
                    System.out.println("  r - Repeat the last command, whether valid or not");
                    System.out.println("  psb - Pushes the current shape to the background shapes");
                    System.out.println("  pb index - Pulls the background shape at index to the first layer");
                    System.out.println("  pb i l - Pulls the background shape at index i to the layer l");
                    System.out.println("  rmb - Removes the most recent background shape");
                    System.out.println("  rmb index - Removes the background shape at index");
                    System.out.println("  clb - Clears all background shapes");
                    System.out.println("  lsb - List the background shapes");
                    System.out.println("  printb - Prints the list of points and control points of background shapes");
                    System.out.println("  grep [flags] range regs - Search for points in shapes matching in the specified range and contain the specific registers, case insensitive");
                    System.out.println("  ldb - Load the most recent background shape");
                    System.out.println("  ldb index - Load the indexed background shape");
                    System.out.println("  s | oa | ao - Switch between O (ordinary) and A (auxiliary) shape registers");
                    System.out.println("  o - Switch to O register");
                    System.out.println("  a - Switch to A register");
                    System.out.println("  mao - Move (copy) shape in A register to O register");
                    System.out.println("  moa - Move (copy) shape in O register to A register");
                    System.out.println("  ms - Move (copy) shape in current register to the other register");
                    System.out.println("  reg - Show which register is currently active");
                    System.out.println("  regp - Show the point registers");
                    System.out.println("  exit - Exits the program");
                    System.out.println("  quit - Quits the program");
                    System.out.println("  sysinfo - Display system information");
                    System.out.println("  help - Shows this help message");
                    System.out.println("  help command - Shows help message for specific command");
                } else if (line.startsWith("help")) {
                    String[] parts = splitArgs(line, 4);
                    if (parts.length == 1) {
                        String command = parts[0];
                        System.out.println(switch (command) {
                            case "add"  -> "add x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy)\nadd x y - Adds a point with coordinates (x, y)";
                            case "set"  -> "set index x y cx cy - Sets the point at index to (x, y) with control point (cx, cy)";
                            case "sp"   -> "sp index dx dy - Sets the point at index by (dx, dy)";
                            case "sc"   -> "sc index dx dy - Sets the control point at index by (dx, dy)";
                            case "sr"   -> "sr index dx dy - Sets the point register at index by (dx, dy)";
                            case "srz"  -> "srz index - Sets the point register at index to (0, 0), effectively clearing it";
                            case "ins"  -> "ins index x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy) to index index";
                            case "mv"   -> "mv dx dy - Moves all points by (dx, dy)";
                            case "mx"   -> "mx dx - Moves all points by (dx, 0)";
                            case "my"   -> "my dy - Moves all points by (0, dy)";
                            case "mp"   -> "mp index dx dy - Moves the point at index by (dx, dy)";
                            case "mc"   -> "mc index dx dy - Moves the control point at index by (dx, dy)";
                            case "mr"   -> "mr index dx dy - Moves the point register at index by (dx, dy)";
                            case "mov"  -> "mov org dest - Set the point at dest to the coordinates of the point at org, including control point. To refer to a point, use (a/o/)(p/c) index for a point or control point in the A or O register, or r index for a point in the point registers";
                            case "sm"   -> "sm index - Smoothens the point at index with default position 0.5\nsm index pos - Smoothens the point at index with position pos (0 = straight, 1 = full curve)";
                            case "sma"  -> "sma - Smoothens all points with default position 0.5";
                            case "st"   -> "st index - Straightens the point at index with default factor 0.5\nst index factor - Straightens the point at index with factor (0 = no change, 1 = fully straight)";
                            case "sta"  -> "sta - Straightens all points with default factor 0.5";
                            case "div"  -> "div index - Divides the segment starting at index by adding a point in the middle\ndiv index pos | seg - Divides the segment starting at index at position pos (0 to 1) or into seg segments (>1)";
                            case "dva"  -> "dva seg - Divides all segments into seg segments (>1)";
                            case "mg"   -> "mg index - Merges the point at index with the next point, removing the point at index";
                            case "scl"  -> "scl factor - Scales the entire shape by the given factor";
                            case "sxy"  -> "sxy fx fy - Scales the entire shape by the given x and y factor (fx, fy)";
                            case "scb"  -> "scb factor - Scales the viewing box by the given factor";
                            case "ssb"  -> "ssb scale - Set the scale of the viewing box to the given scale";
                            case "rot"  -> "rot angle - Rotates the entire shape by the given angle in degrees";
                            case "mrx"  -> "mrx - Mirrors the shape across the x axis";
                            case "mry"  -> "mry - Mirrors the shape across the y axis";
                            case "mrc"  -> "mrc - Mirrors the shape across the line y = x, equivalent to swapping x and y coordinates";
                            case "rd"   -> "rd - Round all points and control points to the nearest two decimal places\nrd n - Round all points and control points to the nearest n decimal places";
                            case "rmr"  -> "rmr index - Removes the point register at index";
                            case "rmra" -> "rmra - Removes all point registers";
                            case "rm"   -> "rm index - Removes the indexed point";
                            case "rml"  -> "rml - Removes the last point";
                            case "rmf"  -> "rmf - Removes the first point";
                            case "rma"  -> "rma - Clears all points";
                            case "make" -> "make count - Creates a shape with the given number of points evenly distributed in a circle";
                            case "circle"-> "circle - Sets the shape to a circle";
                            case "square"-> "square - Sets the shape to a square";
                            case "clear"-> "clear - Clear the console";
                            case "print"-> "print - Prints the list of points and control points";
                            case "pp"   -> "pp index - Prints the point and control point at index";
                            case "json" -> "json - Outputs the shape as a JSON array\njson [json] - Loads the shape from a JSON array";
                            case "svgp" -> "svgp - Outputs the shape as an SVG path";
                            case "svgf" -> "svgf - Outputs the shape as a complete SVG file string";
                            case "info" -> "info - Shows information about the current shape";
                            case "undo" -> "undo - Undoes the last action";
                            case "redo" -> "redo - Redoes the last undone action";
                            case "rmhis"-> "rmhis - Clears the undo/redo history of the current register";
                            case "r"    -> "r - Repeat the last command, whether valid or not";
                            case "psb"  -> "psb - Pushes the current shape to the background shapes";
                            case "pb"   -> "pb index - Pulls the background shape at index to the first layer\npb i l - Pulls the background shape at index i to the layer l";
                            case "rmb"  -> "rmb - Removes the most recent background shape\nrmb index - Removes the background shape at index";
                            case "clb"  -> "clb - Clears all background shapes";
                            case "lsb"  -> "lsb - List the background shapes";
                            case "printb"-> "printb - Prints the list of points and control points of background shapes";
                            case "grep" -> {yield "grep [flags] range regs - Search for points in shapes matching in the specified range and contain the specific registers, case insensitive\n" +
                                    "Flags:\n" +
                                    "    -c (count only)\n" +
                                    "    -h (include history)\n" +
                                    "    -i (include history index and index in shape)\n" +
                                    "Range Modifiers:\n" +
                                    "    x (X coordinate modifier)\n" +
                                    "    y (Y coordinate modifier)\n" +
                                    "    c (X and Y coordinate modifier), empty input for no modifier\n" +
                                    "    Note: They can be chained with & and | operators, in linear precedence order. () are not allowed.\n" +
                                    "Range Restricts:\n" +
                                    "    <(value) (less than)\n" +
                                    "    >(value) (greater than)\n" +
                                    "    =(value) (equal to)\n" +
                                    "    <=(value) (less than or equal to)\n" +
                                    "    >=(value) (greater than or equal to)\n" +
                                    "    [(value1),(value2) (between value1 and value2 exclusive)\n" +
                                    "    [=(value1),(value2) (between value1 and value2 inclusive)\n" +
                                    "    [>(value1),(value2) (between value1, inclusive, and value2, exclusive)\n" +
                                    "    [<(value1),(value2) (between value1, exclusive, and value2, inclusive)\n" +
                                    "    !(value) (not equal to)\n" +
                                    "    !=(value) (not equal to)\n" +
                                    "    ![(value1),(value2) (not between value1 and value2 inclusive)\n" +
                                    "    ![=(value1),(value2) (not between value1 and value2 exclusive)\n" +
                                    "    Note: The symbols can be used in any order, e.g. [=! is the same as ![=\n" +
                                    "Example Ranges:\n" +
                                    "    x[=1,-1&y>-0.5|c<0.2 (X coordinate between 1 and -1 exclusive, and Y coordinate greater than -0.5, or both coordinates less than 0.2)\n" +
                                    "    Note: You may not use space in any range expression\n" +
                                    "Registers:" +
                                    "    o (O register)," +
                                    "    a (A register)," +
                                    "    r (point registers) in any combination";}
                            case "ldb"  -> "ldb - Load the most recent background shape\nldb index - Load the indexed background shape";
                            case "s"    -> "s | oa | ao - Switch between O (ordinary) and A (auxiliary) shape registers";
                            case "oa"   -> "s | oa | ao - Switch between O (ordinary) and A (auxiliary) shape registers";
                            case "ao"   -> "s | oa | ao - Switch between O (ordinary) and A (auxiliary) shape registers";
                            case "o"    -> "o - Switch to O register";
                            case "a"    -> "a - Switch to A register";
                            case "mao"  -> "mao - Move (copy) shape in A register to O register";
                            case "moa"  -> "moa - Move (copy) shape in O register to A register";
                            case "ms"   -> "ms - Move (copy) shape in current register to the other register";
                            case "reg"  -> "reg - Show which register is currently active";
                            case "regp" -> "regp - Show the point registers";
                            case "exit" -> "exit - Exits the program";
                            case "quit" -> "quit - Quits the program";
                            case "sysinfo" -> "sysinfo - Display system information";
                            case "help" -> "help - Shows this help message\nhelp command - Shows help message for specific command";
                            case "section" ->
                                    "Sections: move, modify, shape, transform, refine, background, flow, output, system\n" +
                                    "Use 'help section' to see commands in each section";
                            case "sec" ->
                                    "Use 'help section' to see commands in each section\n" +
                                    "Sections: move, modify, shape, transform, refine, background, flow, output, system, register";
                            // Sectional help
                            case "move" -> "move commands: mv, mx, my, mp, mc, mr, mov";
                            case "modify" -> "modify commands: set, add, sp, sc, sr, srz, ins, rm, rml, rmf, rma, rmr, rmra, mov";
                            case "shape" -> "shape commands: add, ins, rm, make, circle, square, rma";
                            case "transform" -> "transform commands: scl, sxy, scb, ssb, rot, mrx, mry, mrc, rd";
                            case "refine" -> "refine commands: sm, sma, st, sta, div, dva, mg, rd";
                            case "background" -> "background commands: psb, pb, rmb, clb, lsb, printb, ldb, scb, ssb";
                            case "flow" -> "undo/redo commands: undo, redo, rmhis; repeat: r";
                            case "output" -> "output commands: sysinfo, print, pp, json, info, printb, lsb, svgp, svgf, svgb, grep";
                            case "system" -> "system commands: sysinfo, exit, quit, help, clear, grep";
                            case "register" -> "register commands: reg, regp, o, a, oa, ao, s, moa, mao, ms, mov, mr, sr, srz, rmr, rmra";
                            default -> "Unknown command";
                        });
                    } else {
                        System.out.println("Invalid number of arguments. Usage: help command");
                    }
                } else if (line.equals("sysinfo")){
                    int rc = 0;
                    for (double[] pt : pointCache){
                        if (Math.abs(pt[0]) > 0.001 || Math.abs(pt[1]) > 0.001) rc++;
                    }
                    System.out.println("System Information:");
                    System.out.println("  Software:     \u001B[1mCurve Generator\u001B[0m (Standalone)");
                    System.out.println("  Package:      \u001B[1mHappyHex\u001B[0m (since v2.0.0) - util.geom - CurveGenerator");
                    System.out.println("  Authors:      William Wu, Github Copilot");
                    System.out.println("  OS:           " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
                    System.out.println("  Java Version: " + System.getProperty("java.version"));
                    System.out.println("  Dependencies: JavaX Swing, AWT, JSON");
                    System.out.println("  Window Size:  \u001B[4m" + f.getWidth() + "x" + f.getHeight() + "\u001B[0m");
                    System.out.println("  Window Scale: \u001B[4m" + String.format("%.2f", boardScale.get()) + "x\u001B[0m");
                    System.out.println("  Processing:   Single Shape-Processing Thread, Event Dispatch Thread for UI");
                    System.out.println("  Terminal:     Standard Input/Output \u001B[1mEnabled\u001B[0m");
                    System.out.println("  Registers:    \u001B[4m2\u001B[0m/2 Shape Registers (\u001B[1m\u001B[4mO\u001B[0mrdinary and \u001B[1m\u001B[4mA\u001B[0muxiliary) | \u001B[4m" + rc + "\u001B[0m/" + pointCache.length + " Cached Points");
                    System.out.println("                \u001B[1mShape Registers\u001B[0m: (use \u001B[1ms | oa | ao\u001B[0m to switch, \u001B[1mreg\u001B[0m to view info)");
                    System.out.println("                  These are independent shape registers that can be switched between with commands.");
                    System.out.println("                  Each have their individual undo/redo history, and can handle read and modification operations.");
                    System.out.println("                  O register: \u001B[4m" + s.get().size() + "\u001B[0m points \u001B[4m" + (pastShapes.size() - undoIndex.get() - 1) + "\u001B[0m history \u001B[4m" + undoIndex.get() + "\u001B[0m undone");
                    System.out.println("                  A register: \u001B[4m" + a.get().size() + "\u001B[0m points \u001B[4m" + (pastShapesA.size() - undoIndexA.get()-1) + "\u001B[0m history \u001B[4m" + undoIndexA.get()+ "\u001B[0m undone");
                    System.out.println("                \u001B[1mPoint Registers\u001B[0m: (use \u001B[1mregp\u001B[0m to view info)");
                    System.out.println("                  These are a set of cached points that can be used to temporarily store points for use in commands.");
                    System.out.println("                  There is only one set of point registers, shared between both shape registers.");
                    System.out.println("                  There is no differentiation between point and control point, and there is no history tracking.");
                    System.out.println("                  Points in the point registers can be used in commands by referring to them with 'r index'");
                    System.out.println("  Background:   \u001B[4m" + backgroundShapes.size() + "\u001B[0m/Unlimited (Stack, readonly when in stack)");
                    System.out.println("                This is a stack of background shapes that can be pushed to and pulled from.");
                    System.out.println("                These shapes are drawn behind the current shape, and do not affect the current shape.");
                    System.out.println("                The background stack is shared between both the shape registers.");
                    System.out.println("  Commands:     \u001B[4m" + commands.length + "\u001B[0m commands available. Use \u001B[1mhelp\u001B[0m to see the list of commands.");
                } else if (line.equals("circle")){
                    shapeRef.set(new MutableCurvedShape(CurvedShape.CIRCLE));
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.equals("square")){
                    shapeRef.set(new MutableCurvedShape(CurvedShape.SQUARE));
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.startsWith("make")) {
                    String[] parts = splitArgs(line, 4);
                    if (parts.length == 1) {
                        try {
                            int count = Integer.parseInt(parts[0]);
                            shapeObj.clear();
                            for (int i = 0; i < count; i++) {
                                shapeObj.addPoint(0, 1, 0, 0);
                                shapeObj.rotate(360.0 / count);
                            }
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: rot angle");
                    }
                } else if (line.startsWith("add")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 4) {
                        try {
                            double x = Double.parseDouble(parts[0]);
                            double y = Double.parseDouble(parts[1]);
                            double cx = Double.parseDouble(parts[2]);
                            double cy = Double.parseDouble(parts[3]);
                            shapeObj.addPoint(x, y, cx, cy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else if (parts.length == 2){
                        try {
                            double x = Double.parseDouble(parts[0]);
                            double y = Double.parseDouble(parts[1]);
                            shapeObj.addPoint(x, y, x, y);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: add x y cx cy");
                    }
                } else if (line.equals("clear")) {
                    // Clear the console (works in most terminals)
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                } else if (line.equals("rmb")) {
                    // This does not affect current shape
                    if (!backgroundShapes.isEmpty()) {
                        backgroundShapes.pop();
                        p.repaint();
                    }
                } else if (line.equals("rmhis")) {
                    // Clear undo/redo history
                    pastRef.clear();
                    pastRef.add(shapeObj.clone());
                    undoRef.set(0);
                    System.out.println("Cleared undo/redo history.");
                    p.repaint();
                } else if (line.startsWith("rmb")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 1) {
                        // parse index
                        try {
                            int index = Integer.parseInt(parts[0]);
                            backgroundShapes.remove(index);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    }
                } else if (line.equals("rmra")) {
                    for (int i = 0; i < pointCache.length; i++) {
                        pointCache[i] = new double[]{0, 0};
                    }
                    p.repaint();
                } else if (line.startsWith("rmr") || line.startsWith("srz")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            pointCache[index] = new double[]{0, 0};
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: sr index");
                    }
                } else if (line.equals("rma")) {
                    shapeObj.clear();
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.equals("rml")) {
                    shapeObj.removeLast();
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.equals("rmf")) {
                    shapeObj.removeFirst();
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.startsWith("rm")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            shapeObj.removePoint(index);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: rm index");
                    }
                } else if (line.startsWith("mx")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            double dx = Double.parseDouble(parts[0]);
                            shapeObj.move(dx, 0);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mx dx");
                    }
                } else if (line.startsWith("my")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            double dy = Double.parseDouble(parts[0]);
                            shapeObj.move(0, dy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: my dy");
                    }
                } else if (line.startsWith("mv")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 2) {
                        try {
                            double dx = Double.parseDouble(parts[0]);
                            double dy = Double.parseDouble(parts[1]);
                            shapeObj.move(dx, dy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mv dx dy");
                    }
                } else if (line.startsWith("mp")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double dx = Double.parseDouble(parts[1]);
                            double dy = Double.parseDouble(parts[2]);
                            shapeObj.movePoint(index, dx, dy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mp index dx dy");
                    }
                } else if (line.startsWith("mc")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double dx = Double.parseDouble(parts[1]);
                            double dy = Double.parseDouble(parts[2]);
                            shapeObj.moveControl(index, dx, dy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mc index dx dy");
                    }
                } else if (line.equals("sma")){
                    shapeObj.smoothenAll();
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.startsWith("sm")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            shapeObj.smoothen(index, 0.5);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else if (parts.length == 2) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double pos = Double.parseDouble(parts[1]);
                            shapeObj.smoothen(index, pos);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: sm index or sm idx pos");
                    }
                } else if (line.equals("sta")){
                    shapeObj.straightenAll();
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.startsWith("st")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            shapeObj.straighten(index);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else if (parts.length == 2) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double factor = Double.parseDouble(parts[1]);
                            shapeObj.straighten(index, factor);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Factor must be between 0 and 1.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: st index or st idx factor");
                    }
                } else if (line.startsWith("dva")){
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 1) {
                        try {
                            int pts = Integer.parseInt(parts[0]);
                            shapeObj.subdivideAll(pts);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Parts must be greater than 1.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: dva seg");
                    }
                } else if (line.startsWith("mg")){
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            shapeObj.merge(index);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mg index");
                    }
                } else if (line.startsWith("div")){
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            shapeObj.subdivide(index);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else if (parts.length == 2) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double pos = Double.parseDouble(parts[1]);
                            if (pos > 1){
                                // Suspect that this is number of parts
                                int pts = Integer.parseInt(parts[1]);
                                shapeObj.subdivide(index, pts);
                            } else {
                                shapeObj.subdivide(index, pos);
                            }
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Position must be between 0 and 1 as a decimal, or greater than 1 as number of segments.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: div index or div idx pos | seg");
                    }
                } else if (line.startsWith("sp")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double[][] arr = shapeObj.toArray();
                            shapeObj.setPoint(index, x, y, arr[index][2], arr[index][3]);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: sp index dx dy");
                    }
                } else if (line.startsWith("scl")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 1) {
                        try {
                            double factor = Double.parseDouble(parts[0]);
                            if (factor == 0) {
                                System.out.println("Scale factor cannot be zero.");
                                continue;
                            }
                            shapeObj.scale(factor);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: scl factor");
                    }
                } else if (line.startsWith("scb")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 1) {
                        try {
                            double factor = Double.parseDouble(parts[0]);
                            if (factor == 0) {
                                System.out.println("Scale factor cannot be zero.");
                                continue;
                            }
                            boardScale.updateAndGet(v -> v * factor);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: scb factor");
                    }
                } else if (line.startsWith("sc")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double cx = Double.parseDouble(parts[1]);
                            double cy = Double.parseDouble(parts[2]);
                            double[][] arr = shapeObj.toArray();
                            shapeObj.setPoint(index, arr[index][0], arr[index][1], cx, cy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: sc index dx dy");
                    }
                } else if (line.startsWith("set")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 5) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double cx = Double.parseDouble(parts[3]);
                            double cy = Double.parseDouble(parts[4]);
                            shapeObj.setPoint(index, x, y, cx, cy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: set index x y cx cy");
                    }
                } else if (line.startsWith("ins")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 5) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double cx = Double.parseDouble(parts[3]);
                            double cy = Double.parseDouble(parts[4]);
                            shapeObj.addPoint(index, x, y, cx, cy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: ins index x y cx cy");
                    }
                } else if (line.startsWith("sxy")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 2) {
                        try {
                            double fx = Double.parseDouble(parts[0]);
                            double fy = Double.parseDouble(parts[1]);
                            if (fx == 0 || fy == 0) {
                                System.out.println("Scale factor cannot be zero.");
                                continue;
                            }
                            shapeObj.scale(fx, fy);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: sxy sx sy");
                    }
                } else if (line.startsWith("ssb")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 1) {
                        try {
                            double scale = Double.parseDouble(parts[0]);
                            if (scale == 0) {
                                System.out.println("Scale cannot be zero.");
                                continue;
                            }
                            boardScale.updateAndGet(v -> scale);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: ssb scale");
                    }
                } else if (line.equals("rd")) {
                    shapeObj.round(2);
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.startsWith("rd")) {
                    String[] input = splitArgs(line, 2);
                    if (input.length == 1) {
                        try {
                            int decimalPlace = Integer.parseInt(input[0]);
                            shapeObj.round(decimalPlace);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: rot angle");
                    }
                } else if (line.startsWith("rot")) {
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 1) {
                        try {
                            double angle = Double.parseDouble(parts[0]);
                            shapeObj.rotate(angle);
                            addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: rot angle");
                    }
                } else if (line.equals("mrx")) {
                    shapeObj.mirrorX();
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.equals("mry")) {
                    shapeObj.mirrorY();
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.equals("mrc")) {
                    shapeObj.mirrorC();
                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                    p.repaint();
                } else if (line.startsWith("mr")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double dx = Double.parseDouble(parts[1]);
                            double dy = Double.parseDouble(parts[2]);
                            pointCache[index] = new double[]{pointCache[index][0] + dx, pointCache[index][1] + dy};
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mr index dx dy");
                    }
                } else if (line.startsWith("sr")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            pointCache[index] = new double[]{x, y};
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: sr index x y");
                    }
                } else if (line.equals("print")) {
                    CurvedShape shape = shapeObj.toCurvedShape();
                    if (shape == null) {
                        System.out.println("No points in the shape.");
                    } else {
                        for (double[] point : shape.toArray()) {
                            System.out.printf("Point: (%.2f, %.2f), Control: (%.2f, %.2f)%n", point[0], point[1], point[2], point[3]);
                        }
                    }
                } else if (line.startsWith("pp")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double[] point = shapeObj.toArray()[index];
                            System.out.printf("Point: (%.2f, %.2f), Control: (%.2f, %.2f)%n", point[0], point[1], point[2], point[3]);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: pp index");
                    }
                } else if (line.startsWith("mov")){
                    // mov org des, origin and destination starts with (o/a)p (point), (o/a)c (control), or r (one of the point registers)
                    String[] parts = splitArgs(line, 3);
                    if (parts.length == 2) {
                        String org = parts[0];
                        String des = parts[1];
                        boolean useA = ARegister.get(); boolean control = false; boolean bypass = false; int idx;
                        double[] orgPoint;
                        if (org.startsWith("op")) {
                            // Get index
                            int index = Integer.parseInt(org.substring(2));
                            if (index < 0 || index >= s.get().size()) {
                                System.out.println("Index out of bounds for origin.");
                                continue;
                            } else {
                                idx = index;
                                useA = false;
                            }
                        } else if (org.startsWith("p")) {
                            // Get index
                            int index = Integer.parseInt(org.substring(1));
                            if (index < 0 || index >= shapeObj.size()) {
                                System.out.println("Index out of bounds for origin.");
                                continue;
                            } else {
                                idx = index;
                            }
                        } else if (org.startsWith("ap")) {
                            // Get index
                            int index = Integer.parseInt(org.substring(2));
                            if (index < 0 || index >= a.get().size()) {
                                System.out.println("Index out of bounds for origin.");
                                continue;
                            } else {
                                idx = index;
                                useA = true;
                            }
                        } else if (org.startsWith("oc")) {
                            // Get index
                            int index = Integer.parseInt(org.substring(2));
                            if (index < 0 || index >= s.get().size()) {
                                System.out.println("Index out of bounds for origin.");
                                continue;
                            } else {
                                idx = index;
                                useA = false;
                                control = true;
                            }
                        } else if (org.startsWith("c")) {
                            // Get index
                            int index = Integer.parseInt(org.substring(1));
                            if (index < 0 || index >= shapeObj.size()) {
                                System.out.println("Index out of bounds for origin.");
                                continue;
                            } else {
                                idx = index;
                                control = true;
                            }
                        } else if (org.startsWith("ac")) {
                            // Get index
                            int index = Integer.parseInt(org.substring(2));
                            if (index < 0 || index >= a.get().size()) {
                                System.out.println("Index out of bounds for origin.");
                                continue;
                            } else {
                                idx = index;
                                useA = true;
                                control = true;
                            }
                        } else if (org.startsWith("r")) {
                            // Get index
                            int index = Integer.parseInt(org.substring(1));
                            if (index < 0 || index >= pointCache.length) {
                                System.out.println("Index out of bounds for origin.");
                                continue;
                            } else {
                                idx = index;
                                bypass = true;
                            }
                        } else {
                            System.out.println("Invalid origin format. Must start with (o/a/)p, (o/a/)c.");
                            continue;
                        }
                        if (!bypass) {
                            try {
                                if (useA) {
                                    orgPoint = a.get().toArray()[idx];
                                } else {
                                    orgPoint = s.get().toArray()[idx];
                                }
                            } catch (Exception e) {
                                System.out.println("Error retrieving origin point: " + e.getMessage());
                                continue;
                            }
                            if (control) {
                                orgPoint = new double[]{orgPoint[2], orgPoint[3]};
                            } else {
                                orgPoint = new double[]{orgPoint[0], orgPoint[1]};
                            }
                        } else {
                            orgPoint = pointCache[idx];
                        }
                        try {
                            // Reset
                            if (des.startsWith("op")) {
                                // Get index
                                int index = Integer.parseInt(des.substring(2));
                                if (index < 0 || index >= s.get().size()) {
                                    System.out.println("Index out of bounds for destination.");
                                } else {
                                    // Set point
                                    double[][] arr = s.get().toArray();
                                    s.get().setPoint(index, orgPoint[0], orgPoint[1], arr[index][2], arr[index][3]);
                                    addAndBreakUndoChain(s.get(), undoIndex, pastShapes);
                                    p.repaint();
                                }
                            } else if (des.startsWith("p")) {
                                // Get index
                                int index = Integer.parseInt(des.substring(1));
                                if (index < 0 || index >= shapeObj.size()) {
                                    System.out.println("Index out of bounds for destination.");
                                } else {
                                    // Set point
                                    double[][] arr = shapeObj.toArray();
                                    shapeObj.setPoint(index, orgPoint[0], orgPoint[1], arr[index][2], arr[index][3]);
                                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                                    p.repaint();
                                }
                            } else if (des.startsWith("ap")) {
                                // Get index
                                int index = Integer.parseInt(des.substring(2));
                                if (index < 0 || index >= a.get().size()) {
                                    System.out.println("Index out of bounds for destination.");
                                } else {
                                    // Set point
                                    double[][] arr = a.get().toArray();
                                    a.get().setPoint(index, orgPoint[0], orgPoint[1], arr[index][2], arr[index][3]);
                                    addAndBreakUndoChain(a.get(), undoIndexA, pastShapesA);
                                    p.repaint();
                                }
                            } else if (des.startsWith("oc")) {
                                // Get index
                                int index = Integer.parseInt(des.substring(2));
                                if (index < 0 || index >= s.get().size()) {
                                    System.out.println("Index out of bounds for destination.");
                                } else {
                                    // Set control
                                    double[][] arr = s.get().toArray();
                                    s.get().setPoint(index, arr[index][0], arr[index][1], orgPoint[0], orgPoint[1]);
                                    addAndBreakUndoChain(s.get(), undoIndex, pastShapes);
                                    p.repaint();
                                }
                            } else if (des.startsWith("c")) {
                                // Get index
                                int index = Integer.parseInt(des.substring(1));
                                if (index < 0 || index >= shapeObj.size()) {
                                    System.out.println("Index out of bounds for destination.");
                                } else {
                                    // Set control
                                    double[][] arr = shapeObj.toArray();
                                    shapeObj.setPoint(index, arr[index][0], arr[index][1], orgPoint[0], orgPoint[1]);
                                    addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                                    p.repaint();
                                }
                            } else if (des.startsWith("ac")) {
                                // Get index
                                int index = Integer.parseInt(des.substring(2));
                                if (index < 0 || index >= a.get().size()) {
                                    System.out.println("Index out of bounds for destination.");
                                } else {
                                    // Set control
                                    double[][] arr = a.get().toArray();
                                    a.get().setPoint(index, arr[index][0], arr[index][1], orgPoint[0], orgPoint[1]);
                                    addAndBreakUndoChain(a.get(), undoIndexA, pastShapesA);
                                    p.repaint();
                                }
                            } else if (des.startsWith("r")) {
                                // Get index
                                int index = Integer.parseInt(des.substring(1));
                                if (index < 0 || index >= pointCache.length) {
                                    System.out.println("Index out of bounds for destination.");
                                } else {
                                    // Set point in cache
                                    pointCache[index] = new double[]{orgPoint[0], orgPoint[1]};
                                    p.repaint();
                                }
                            } else {
                                System.out.println("Invalid destination format. Must start with (o/a/)p, (o/a/)c.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mov origin destination");
                    }
                } else if (line.equals("json")) {
                    CurvedShape shape = shapeObj.toCurvedShape();
                    if (shape == null) {
                        System.out.println("No points in the shape.");
                    } else {
                        System.out.println(shape.toJsonArrayBuilder().build().toString());
                    }
                } else if (line.startsWith("json")) {
                    String json = line.substring(4).trim();
                    try {
                        javax.json.JsonReader reader = javax.json.Json.createReader(new java.io.StringReader(json));
                        javax.json.JsonArray ja = reader.readArray();
                        reader.close();
                        CurvedShape shape = CurvedShape.fromJsonArray(ja);
                        shapeObj.clear();
                        for (double[] point : shape.toArray()) {
                            shapeObj.addPoint(point[0], point[1], point[2], point[3]);
                        }
                        // Break the undo chain
                        addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                        p.repaint();
                    } catch (Exception e) {
                        System.out.println("Invalid JSON format.");
                    }
                } else if (line.equals("info")) {
                    System.out.println("Number of points: " + shapeObj.size());
                    CurvedShape shape = shapeObj.toCurvedShape();
                    if (shape != null) {
                        Rectangle rect = shape.scaled(10000).toShape().getBounds();
                        System.out.printf("Bounding box: [%.2f, %.2f] to [%.2f, %.2f]%n",
                                rect.getMinX()*0.0001, rect.getMinY()*0.0001, rect.getMaxX()*0.0001, rect.getMaxY()*0.0001);
                    } else {
                        System.out.println("Bounding box: N/A");
                    }
                } else if (line.equals("undo")) {
                    if (undoRef.get() < pastRef.size() - 1) {
                        shapeRef.set(pastRef.get(pastRef.size() - 1 - undoRef.incrementAndGet()).clone());
                        p.repaint();
                    } else {
                        System.out.println("No more actions to undo.");
                    }
                } else if (line.equals("redo")) {
                    if (undoRef.get() > 0) {
                        shapeRef.set(pastRef.get(pastRef.size() - undoRef.get()).clone());
                        undoRef.getAndDecrement();
                        p.repaint();
                    } else {
                        System.out.println("No more actions to redo.");
                    }
                } else if (line.startsWith("pb")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            CurvedShape shape = backgroundShapes.remove(index);
                            backgroundShapes.add(shape);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else if (parts.length == 2) {
                        try {
                            int i = Integer.parseInt(parts[0]);
                            int j = Integer.parseInt(parts[1]);
                            if (i == j) {
                                // No change
                                continue;
                            }
                            CurvedShape shape = backgroundShapes.remove(i);
                            backgroundShapes.add(j, shape);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: pb index or pb i l");
                    }
                } else if (line.equals("psb")) {
                    // This does not affect current shape
                    CurvedShape shape = shapeObj.toCurvedShape();
                    if (shape != null) {
                        backgroundShapes.push(shape);
                        p.repaint();
                    }
                } else if (line.equals("clb")) {
                    backgroundShapes.clear();
                    p.repaint();
                } else if (line.equals("lsb")) {
                    if (backgroundShapes.isEmpty()) {
                        System.out.println("No background shapes.");
                    } else {
                        for (int i = 0; i < backgroundShapes.size(); i++) {
                            CurvedShape shape = backgroundShapes.get(i);
                            System.out.printf("Shape %d: %d points%n", i, shape.toArray().length);
                        }
                    }
                } else if (line.equals("ldb")) {
                    if (backgroundShapes.isEmpty()) {
                        System.out.println("No background shapes.");
                    } else {
                        CurvedShape shape = backgroundShapes.peek();
                        shapeRef.set(new MutableCurvedShape(shape));
                        addAndBreakUndoChain(shapeObj, undoRef, pastRef);
                        p.repaint();
                    }
                } else if (line.equals("printb")) {
                    if (backgroundShapes.isEmpty()) {
                        System.out.println("No background shapes.");
                    } else {
                        for (int i = 0; i < backgroundShapes.size(); i++) {
                            CurvedShape shape = backgroundShapes.get(i);
                            System.out.println("Shape " + i + ":");
                            for (double[] point : shape.toArray()) {
                                System.out.printf("Point: (%.2f, %.2f), Control: (%.2f, %.2f)%n", point[0], point[1], point[2], point[3]);
                            }
                        }
                    }
                } else if (line.equals("svgp")) {
                    CurvedShape shape = shapeObj.toCurvedShape();
                    if (shape == null) {
                        System.out.println("No points in the shape.");
                    } else {
                        System.out.println(shape.toSvgPath());
                    }
                } else if (line.equals("svgf")) {
                    CurvedShape shape = shapeObj.toCurvedShape();
                    if (shape == null) {
                        System.out.println("No points in the shape.");
                    } else {
                        CurvedShape shapeC = shape.scaled(200);
                        System.out.println("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100\" height=\"100\" viewBox=\"-200 -200 400 400\">" +
                                "<path d=\"" + shapeC.toSvgPath() + "\" fill=\"black\" stroke=\"none\"/></svg>");
                    }
                } else if (line.equals("svgb")) {
                    // Get background shapes
                    if (backgroundShapes.isEmpty()) {
                        System.out.println("No background shapes.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100\" height=\"100\" viewBox=\"-200 -200 400 400\">");
                        for (CurvedShape shape : backgroundShapes) {
                            CurvedShape shapeC = shape.scaled(200);
                            sb.append("<path d=\"").append(shapeC.toSvgPath()).append("\" fill=\"\"rgba(0, 0, 0, 0.5)\"\" stroke=\"none\"/>");
                        }
                        sb.append("</svg>");
                        System.out.println(sb.toString());
                    }
                } else if (line.isEmpty()) {
                    // Do nothing for empty input
                } else {
                    // Attempt to find similar command
                    String similar = findMostSimilar(line.split("\\s+")[0], commands);
                    System.out.println("Unknown command. Did you mean: " + similar + "?");
                }
            }
        }).start();
    }
    private static String[] splitArgs(String input, int offset) {
        return input.substring(offset).trim().split("\\s+");
    }
    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j; // insert all b's characters
                } else if (j == 0) {
                    dp[i][j] = i; // remove all a's characters
                } else {
                    int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1,      // deletion
                                    dp[i][j - 1] + 1),      // insertion
                            dp[i - 1][j - 1] + cost         // substitution
                    );
                }
            }
        }
        return dp[a.length()][b.length()];
    }
    public static String findMostSimilar(String string, String[] knownStrings) {
        if (knownStrings == null || knownStrings.length == 0) {
            return null;
        }
        String mostSimilar = knownStrings[0];
        int minDistance = levenshteinDistance(string, mostSimilar);
        for (int i = 1; i < knownStrings.length; i++) {
            int distance = levenshteinDistance(string, knownStrings[i]);
            if (distance < minDistance) {
                minDistance = distance;
                mostSimilar = knownStrings[i];
            }
        }
        return mostSimilar;
    }
    public static void addAndBreakUndoChain(MutableCurvedShape shape, AtomicInteger undoIndex, java.util.List<MutableCurvedShape> pastShapes) {
        if (undoIndex.get() > 0) {
            pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
        }
        pastShapes.add(shape.clone());
    }
    public static Predicate<double[]> createTestBasedOnExpression(String expression) {
        expression += "  ";
        // Grep helper function. Follow the syntax of range modifiers and restrictions.
        int mode = 0; StringBuffer buffer;
        java.util.Stack<Object> objectStack = new java.util.Stack<>();
        // 0 = start of expression: express identifier (x, y, or c)
        // 2 = expect range restriction (<, <=, =, >=, >, etc.)
        // 3 = expect number
        // 4 = end of expression. expect termination or boolean operator (& or |)
        loop:
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            switch (mode) {
                case 0: // Start of expression
                    buffer = new StringBuffer();
                    while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                        buffer.append(expression.charAt(i));
                        i++;
                    }
                    String identifier = buffer.toString();
                    if (identifier.equals("x") || identifier.equals("y") || identifier.equals("c")) {
                        mode = 2;
                        objectStack.push(identifier.charAt(0));
                    } else {
                        throw new IllegalArgumentException("Invalid identifier: " + identifier);
                    }
                    i--; // Step back to reprocess current character
                    break;
                case 2: // Expect range restriction
                    // Read until not [=<>!
                    buffer = new StringBuffer();
                    while (i < expression.length() && ("[=<>!".indexOf(expression.charAt(i)) != -1)) {
                        buffer.append(expression.charAt(i));
                        i++;
                    }
                    String operator = buffer.toString();
                    mode = 3; // Expect number
                    objectStack.push(operator);
                    i--; // Step back to reprocess current character
                    break;
                case 3: // Expect number
                    // Read until non-number character
                    buffer = new StringBuffer();
                    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) ||
                        expression.charAt(i) == '.' || expression.charAt(i) == '-')) {
                        buffer.append(expression.charAt(i));
                        i++;
                    }
                    String numberStr = buffer.toString();
                    double number;
                    try {
                        number = Double.parseDouble(numberStr);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid number format: " + numberStr);
                    }
                    objectStack.push(number);
                    // Read next character, check is , or other
                    if (expression.charAt(i) == ',') {
                        mode = 3; // Expect another number
                    } else mode = 4; // End of expression
                    break;
                default: // Process stack after identifier
                    // At this point, we should have identifier (char), operator (String), number(s) (Double)
                    java.util.List<Double> numbers = new java.util.ArrayList<>();
                    while (!objectStack.isEmpty() && objectStack.peek() instanceof Double) {
                        numbers.add(0, (Double)objectStack.pop());
                    }
                    if (objectStack.isEmpty() || !(objectStack.peek() instanceof String)) {
                        throw new IllegalArgumentException("Expected operator after identifier.");
                    }
                    String op = (String)objectStack.pop();
                    if (objectStack.isEmpty() || !(objectStack.peek() instanceof Character)) {
                        throw new IllegalArgumentException("Expected identifier before operator.");
                    }
                    char id = (Character)objectStack.pop();
                    // Create predicate
                    double[] numsArray = new double[numbers.size()];
                    for (int j = 0; j < numbers.size(); j++) {
                        numsArray[j] = numbers.get(j);
                    }
                    Predicate<double[]> predicate = makePointPredicate(id, op, numsArray);
                    if (objectStack.isEmpty()) {
                        // Nothing to combine with, just push
                        objectStack.push(predicate);
                    } else {
                        // Get boolean operator
                        if (!(objectStack.peek() instanceof Boolean)) {
                            // There is no previous boolean operator, just push
                            objectStack.push(predicate);
                        } else {
                            boolean isAnd = (Boolean)objectStack.pop();
                            if (objectStack.peek() instanceof Predicate) {
                                throw new IllegalArgumentException("Expected boolean operator before predicate.");
                            } else {
                                Predicate<double[]> left = (Predicate<double[]>)objectStack.pop();
                                Predicate<double[]> combined = joinPointPredicates(isAnd, left, predicate);
                                objectStack.push(combined);
                            }
                        }
                    }
                    // Put predicate back on stack
                    if (c == '&') {
                        mode = 0;
                        objectStack.push(Boolean.TRUE); // AND
                    } else if (c == '|') {
                        mode = 0;
                        objectStack.push(Boolean.FALSE); // Or
                    } else {
                        break loop;
                    }
                    break;
            }
        }
        if (objectStack.size() != 1 || !(objectStack.peek() instanceof Predicate) || mode != 4) {
            System.out.println("Remaining objects: ");
            for (Object obj : objectStack) {
                System.out.println(" - " + obj);
            }
            throw new IllegalArgumentException("Invalid expression format.");
        } else {
            return (Predicate<double[]>)objectStack.pop();
        }
    }
    public static Predicate<double[]> makePointPredicate (char target, String operator, double... numbers) {
        boolean hasEx = operator.contains("!");
        boolean hasEq = operator.contains("=");
        boolean hasGt = operator.contains(">");
        boolean hasLt = operator.contains("<");
        boolean hasIn = operator.contains("[");
        if (!hasEx && !hasEq && !hasGt && !hasLt && !hasIn) {
            return point -> true; // All points match
        } else if (hasGt && hasLt) {
            throw new IllegalArgumentException("Illegal Combination of operators");
        } else if (hasIn) {
            if (numbers.length != 2) {
                throw new IllegalArgumentException("Range operator requires exactly two numbers.");
            }
            double lower = Math.min(numbers[0], numbers[1]);
            double upper = Math.max(numbers[0], numbers[1]);
            if (hasGt && hasEq || hasLt && hasEq) {
                throw new IllegalArgumentException("Illegal Combination of operators");
            }
            if (hasGt) {
                if (target == 'x'){
                    return point -> {
                        try {
                            double x = point[0];
                            return hasEx ^ (lower <= x && x < upper);
                        } catch (Exception e) {
                            return false;
                        }
                    };
                } else if (target == 'y'){
                    return point -> {
                        try {
                            double y = point[1];
                            return hasEx ^ (lower <= y && y < upper);
                        } catch (Exception e) {
                            return false;
                        }
                    };
                } else return point -> {
                    try {
                        double x = point[0];
                        double y = point[1];
                        return hasEx ^ (lower <= x && x < upper && lower <= y && y < upper);
                    } catch (Exception e) {
                        return false;
                    }
                };
            } else if (hasLt) {
                if (target == 'x'){
                    return point -> {
                        try {
                            double x = point[0];
                            return hasEx ^ (lower < x && x <= upper);
                        } catch (Exception e) {
                            return false;
                        }
                    };
                } else if (target == 'y'){
                    return point -> {
                        try {
                            double y = point[1];
                            return hasEx ^ (lower < y && y <= upper);
                        } catch (Exception e) {
                            return false;
                        }
                    };
                } else return point -> {
                    try {
                        double x = point[0];
                        double y = point[1];
                        return hasEx ^ (lower < x && x <= upper && lower < y && y <= upper);
                    } catch (Exception e) {
                        return false;
                    }
                };
            } else if (hasEq) {
                if (target == 'x'){
                    return point -> {
                        try {
                            double x = point[0];
                            return hasEx ^ (lower <= x && x <= upper);
                        } catch (Exception e) {
                            return false;
                        }
                    };
                } else if (target == 'y'){
                    return point -> {
                        try {
                            double y = point[1];
                            return hasEx ^ (lower <= y && y <= upper);
                        } catch (Exception e) {
                            return false;
                        }
                    };
                } else return point -> {
                    try {
                        double x = point[0];
                        double y = point[1];
                        return hasEx ^ (lower <= x && x <= upper && lower <= y && y <= upper);
                    } catch (Exception e) {
                        return false;
                    }
                };
            } else // [ or ![
                if (target == 'x'){
                return point -> {
                    try {
                        double x = point[0];
                        return hasEx ^ (lower < x && x < upper);
                    } catch (Exception e) {
                        return false;
                    }
                };
            } else if (target == 'y'){
                return point -> {
                    try {
                        double y = point[1];
                        return hasEx ^ (lower < y && y < upper);
                    } catch (Exception e) {
                        return false;
                    }
                };
            } else {
                return point -> {
                    try {
                        double x = point[0];
                        double y = point[1];
                        return hasEx ^ (lower < x && x < upper && lower < y && y < upper);
                    } catch (Exception e) {
                        return false;
                    }
                };
            }
        } else {
            if (numbers.length != 1) {
                throw new IllegalArgumentException("Operator requires exactly one number.");
            }
            double value = numbers[0];
            final boolean hasEqn = (!hasGt && !hasLt && !hasIn) || hasEq; // turn ! into !=
            if (target == 'x'){
                return point -> {
                    try {
                        double x = point[0];
                        boolean v = false;
                        if (hasGt && x > value)  v = true;
                        if (hasLt && x < value)  v = true;
                        if (hasEqn && x == value)v = true;
                        return v ^ hasEx;
                    } catch (Exception e) {
                        return false;
                    }
                };
            } else if (target == 'y'){
                return point -> {
                    try {
                        double y = point[1];
                        boolean v = false;
                        if (hasGt && y > value)  v = true;
                        if (hasLt && y < value)  v = true;
                        if (hasEqn && y == value)v = true;
                        return v ^ hasEx;
                    } catch (Exception e) {
                        return false;
                    }
                };
            } else {
                return point -> {
                    try {
                        double x = point[0];
                        double y = point[1];
                        boolean v = false;
                        if (hasGt && x > value)  v = true;
                        if (hasLt && x < value)  v = true;
                        if (hasEqn && x == value)v = true;
                        if (hasGt && y > value)  v = true;
                        if (hasLt && y < value)  v = true;
                        if (hasEqn && y == value)v = true;
                        return v ^ hasEx;
                    } catch (Exception e) {
                        return false;
                    }
                };
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static Predicate<double[]> joinPointPredicates (boolean isAnd, Predicate<double[]>... predicates) {
        return point -> {
            for (Predicate<double[]> predicate : predicates) {
                boolean result = predicate.test(point);
                if (isAnd && !result) {
                    return false;
                } else if (!isAnd && result) {
                    return true;
                }
            }
            return isAnd;
        };
    }
}
