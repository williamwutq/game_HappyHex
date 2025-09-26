package util.geom;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CurveGenerator {
    public static void main(String[] args){
        final Color controlColor = new Color(0, 153, 255);
        final Color pointColor = new Color(255, 51, 51);
        final Color backgroundColor = new Color(255, 255, 0,128);
        final String[] commands = new String[]{
                "add", "set", "sp", "sc", "ins", "mv", "mx", "my", "mp", "mc", "sm", "st", "div", "dva",
                "scl", "sxy", "scb", "ssb", "rot", "mrx", "mry", "mrc",
                "rd", "rm", "rml", "rmf", "rma", "r",
                "circle", "square", "make",
                "clear", "print", "pp", "json", "info", "undo", "redo",
                "psb", "pb", "rmb", "clb", "ldb", "lsb", "printb",
                "exit", "quit", "help"
        };
        final ArrayList<MutableCurvedShape> pastShapes = new ArrayList<>();
        final Stack<CurvedShape> backgroundShapes = new Stack<>();
        final AtomicInteger undoIndex = new AtomicInteger(0);
        JFrame f = new JFrame();
        final AtomicReference<MutableCurvedShape> s = new AtomicReference<MutableCurvedShape>(new MutableCurvedShape());
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
                CurvedShape shape = s.get().toCurvedShape();
                if (shape != null) {
                    shape = shape.scaled(scale, scale).shifted(w / 2.0, h / 2.0);
                    // Fill the shape
                    g2d.setColor(Color.WHITE);
                    g2d.fill(shape.toShape());
                    // Draw line from point to control point
                    g2d.setColor(Color.GRAY);
                    double[][] array = shape.toArray();
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
            while (true) {
                assert s != null;
                assert s.get() != null;
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
                if (line.equals("exit") || line.equals("quit")) {
                    System.exit(0);
                } else if (line.equals("help")) {
                    System.out.println("Commands:");
                    System.out.println("  add x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy)");
                    System.out.println("  add x y - Adds a point with coordinates (x, y)");
                    System.out.println("  set index x y cx cy - Sets the point at index to (x, y) with control point (cx, cy)");
                    System.out.println("  sp index dx dy - Sets the point at index by (dx, dy)");
                    System.out.println("  sc index dx dy - Sets the control point at index by (dx, dy)");
                    System.out.println("  ins index x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy) to index index");
                    System.out.println("  mv dx dy - Moves all points by (dx, dy)");
                    System.out.println("  mx dx - Moves all points by (dx, 0)");
                    System.out.println("  my dy - Moves all points by (0, dy)");
                    System.out.println("  mp index dx dy - Moves the point at index by (dx, dy)");
                    System.out.println("  mc index dx dy - Moves the control point at index by (dx, dy)");
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
                    System.out.println("  info - Shows information about the current shape");
                    System.out.println("  undo - Undoes the last action");
                    System.out.println("  redo - Redoes the last undone action");
                    System.out.println("  r - Repeat the last command, whether valid or not");
                    System.out.println("  psb - Pushes the current shape to the background shapes");
                    System.out.println("  pb index - Pulls the background shape at index to the first layer");
                    System.out.println("  pb i l - Pulls the background shape at index i to the layer l");
                    System.out.println("  rmb - Removes the most recent background shape");
                    System.out.println("  rmb index - Removes the background shape at index");
                    System.out.println("  clb - Clears all background shapes");
                    System.out.println("  lsb - List the background shapes");
                    System.out.println("  printb - Prints the list of points and control points of background shapes");
                    System.out.println("  ldb - Load the most recent background shape");
                    System.out.println("  ldb index - Load the indexed background shape");
                    System.out.println("  exit - Exits the program");
                    System.out.println("  quit - Quits the program");
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
                            case "ins"  -> "ins index x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy) to index index";
                            case "mv"   -> "mv dx dy - Moves all points by (dx, dy)";
                            case "mx"   -> "mx dx - Moves all points by (dx, 0)";
                            case "my"   -> "my dy - Moves all points by (0, dy)";
                            case "mp"   -> "mp index dx dy - Moves the point at index by (dx, dy)";
                            case "mc"   -> "mc index dx dy - Moves the control point at index by (dx, dy)";
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
                            case "info" -> "info - Shows information about the current shape";
                            case "undo" -> "undo - Undoes the last action";
                            case "redo" -> "redo - Redoes the last undone action";
                            case "r"    -> "r - Repeat the last command, whether valid or not";
                            case "psb"  -> "psb - Pushes the current shape to the background shapes";
                            case "pb"   -> "pb index - Pulls the background shape at index to the first layer\npb i l - Pulls the background shape at index i to the layer l";
                            case "rmb"  -> "rmb - Removes the most recent background shape\nrmb index - Removes the background shape at index";
                            case "clb"  -> "clb - Clears all background shapes";
                            case "lsb"  -> "lsb - List the background shapes";
                            case "printb"-> "printb - Prints the list of points and control points of background shapes";
                            case "ldb"  -> "ldb - Load the most recent background shape\nldb index - Load the indexed background shape";
                            case "exit" -> "exit - Exits the program";
                            case "quit" -> "quit - Quits the program";
                            case "help" -> "help - Shows this help message\nhelp command - Shows help message for specific command";
                            case "section" ->
                                    "Sections: move, modify, shape, transform, refine, background, flow, output, system\n" +
                                    "Use 'help section' to see commands in each section";
                            case "sec" ->
                                    "Use 'help section' to see commands in each section\n" +
                                    "Sections: move, modify, shape, transform, refine, background, flow, output, system";
                            // Sectional help
                            case "move" -> "move commands: mv, mx, my, mp, mc";
                            case "modify" -> "modify commands: set, add, sp, sc, ins, rm, rml, rmf, rma";
                            case "shape" -> "shape commands: add, make, circle, square";
                            case "transform" -> "transform commands: scl, sxy, scb, ssb, rot, mrx, mry, mrc";
                            case "refine" -> "refine commands: sm, sma, st, sta, div, dva, mg";
                            case "background" -> "background commands: psb, pb, rmb, clb, lsb, printb, ldb";
                            case "flow" -> "undo/redo commands: undo, redo, repeat: r";
                            case "output" -> "output commands: print, pp, json, info";
                            case "system" -> "system commands: exit, quit, help, clear";
                            default -> "Unknown command";
                        });
                    } else {
                        System.out.println("Invalid number of arguments. Usage: help command");
                    }
                } else if (line.equals("circle")){
                    s.set(new MutableCurvedShape(CurvedShape.CIRCLE));
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.equals("square")){
                    s.set(new MutableCurvedShape(CurvedShape.SQUARE));
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.startsWith("make")) {
                    String[] parts = splitArgs(line, 4);
                    if (parts.length == 1) {
                        try {
                            int count = Integer.parseInt(parts[0]);
                            s.get().clear();
                            for (int i = 0; i < count; i++) {
                                s.get().addPoint(0, 1, 0, 0);
                                s.get().rotate(360.0 / count);
                            }
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().addPoint(x, y, cx, cy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else if (parts.length == 2){
                        try {
                            double x = Double.parseDouble(parts[0]);
                            double y = Double.parseDouble(parts[1]);
                            s.get().addPoint(x, y, x, y);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.get(), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                } else if (line.equals("rma")) {
                    s.get().clear();
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.equals("rml")) {
                    s.get().removeLast();
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.equals("rmf")) {
                    s.get().removeFirst();
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.startsWith("rm")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            s.get().removePoint(index);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().move(dx, 0);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().move(0, dy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().move(dx, dy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().movePoint(index, dx, dy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().moveControl(index, dx, dy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                    s.get().smoothenAll();
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.startsWith("sm")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            s.get().smoothen(index, 0.5);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().smoothen(index, pos);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                    s.get().straightenAll();
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.startsWith("st")) {
                    String[] parts = splitArgs(line, 2);
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            s.get().straighten(index);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().straighten(index, factor);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().subdivideAll(pts);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().merge(index);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().subdivide(index);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                                s.get().subdivide(index, pts);
                            } else {
                                s.get().subdivide(index, pos);
                            }
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().setPoint(index, x, y, s.get().toArray()[index][2], s.get().toArray()[index][3]);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().scale(factor);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().setPoint(index, s.get().toArray()[index][0], s.get().toArray()[index][1], cx, cy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().setPoint(index, x, y, cx, cy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().addPoint(index, x, y, cx, cy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().scale(fx, fy);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                    s.get().round(2);
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.startsWith("rd")) {
                    String[] input = splitArgs(line, 2);
                    if (input.length == 1) {
                        try {
                            int decimalPlace = Integer.parseInt(input[0]);
                            s.get().round(decimalPlace);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
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
                            s.get().rotate(angle);
                            // Break the undo chain
                            if (undoIndex.get() > 0) {
                                pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                            }
                            pastShapes.add(s.get().clone());
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: rot angle");
                    }
                } else if (line.equals("mrx")) {
                    s.get().mirrorX();
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.equals("mry")) {
                    s.get().mirrorY();
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.equals("mrc")) {
                    s.get().mirrorC();
                    // Break the undo chain
                    if (undoIndex.get() > 0) {
                        pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                    }
                    pastShapes.add(s.get().clone());
                    p.repaint();
                } else if (line.equals("print")) {
                    CurvedShape shape = s.get().toCurvedShape();
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
                            double[] point = s.get().toArray()[index];
                            System.out.printf("Point: (%.2f, %.2f), Control: (%.2f, %.2f)%n", point[0], point[1], point[2], point[3]);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: pp index");
                    }
                } else if (line.equals("json")) {
                    CurvedShape shape = s.get().toCurvedShape();
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
                        s.get().clear();
                        for (double[] point : shape.toArray()) {
                            s.get().addPoint(point[0], point[1], point[2], point[3]);
                        }
                        // Break the undo chain
                        if (undoIndex.get() > 0) {
                            pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                        }
                        pastShapes.add(s.get().clone());
                        p.repaint();
                    } catch (Exception e) {
                        System.out.println("Invalid JSON format.");
                    }
                } else if (line.equals("info")) {
                    System.out.println("Number of points: " + s.get().size());
                    CurvedShape shape = s.get().toCurvedShape();
                    if (shape != null) {
                        Rectangle rect = shape.scaled(10000).toShape().getBounds();
                        System.out.printf("Bounding box: [%.2f, %.2f] to [%.2f, %.2f]%n",
                                rect.getMinX()*0.0001, rect.getMinY()*0.0001, rect.getMaxX()*0.0001, rect.getMaxY()*0.0001);
                    } else {
                        System.out.println("Bounding box: N/A");
                    }
                } else if (line.equals("undo")) {
                    if (undoIndex.get() < pastShapes.size() - 1) {
                        s.set(pastShapes.get(pastShapes.size() - 1 - undoIndex.incrementAndGet()).clone());
                        p.repaint();
                    } else {
                        System.out.println("No more actions to undo.");
                    }
                } else if (line.equals("redo")) {
                    if (undoIndex.get() > 0) {
                        s.set(pastShapes.get(pastShapes.size() - undoIndex.get()).clone());
                        undoIndex.getAndDecrement();
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
                    CurvedShape shape = s.get().toCurvedShape();
                    if (shape != null) {
                        backgroundShapes.push(shape);
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
                        s.set(new MutableCurvedShape(shape));
                        if (undoIndex.get() > 0) {
                            pastShapes.subList(pastShapes.size() - undoIndex.getAndSet(0), pastShapes.size()).clear();
                        }
                        pastShapes.add(s.get().clone());
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
}
