package util.geom;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CurveGenerator {
    public static void main(String[] args){
        final Color controlColor = new Color(0, 153, 255);
        final Color pointColor = new Color(255, 51, 51);
        final String[] commands = new String[]{
                "add", "set", "sp", "sc", "ins", "mv", "mp", "mc",
                "scl", "sxy", "scb", "ssb",
                "rm", "rml", "rmf", "rma",
                "clear", "print", "json", "info", "undo", "redo",
                "exit", "quit", "help"
        };
        final ArrayList<MutableCurvedShape> pastShapes = new ArrayList<>();
        final AtomicInteger undoIndex = new AtomicInteger(0);
        JFrame f = new JFrame();
        final AtomicReference<MutableCurvedShape> s = new AtomicReference<MutableCurvedShape>(new MutableCurvedShape());
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
                    shape = shape.scaled(scale, -scale).shifted(w / 2.0, h / 2.0);
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
                        g2d.drawString(Integer.toString(i), (int) point[0] - 4, (int) point[1] + 4);
                    }
                    // Write number next to each control point
                    g2d.setColor(controlColor);
                    for (int i = 0; i < array.length; i++) {
                        double[] point = array[i];
                        g2d.drawString(Integer.toString(i), (int) point[2] - 4, (int) point[3] + 4);
                    }
                }
                // Draw border and coordinates of the corners
                g2d.setColor(Color.DARK_GRAY);
                CurvedShape fittedSquare = CurvedShape.SQUARE.scaled(scale, -scale).shifted(w / 2.0, h / 2.0);
                CurvedShape fittedCircle = CurvedShape.CIRCLE.scaled(scale * 0.9, -scale * 0.9).shifted(w / 2.0, h / 2.0);
                CurvedShape smallerSquare = CurvedShape.SQUARE.scaled(scale * 0.9, scale * -0.9).shifted(w / 2.0, h / 2.0);
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
                System.out.print(">>> ");
                String line = scanner.nextLine().trim();
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
                    System.out.println("  mp index dx dy - Moves the point at index by (dx, dy)");
                    System.out.println("  mc index dx dy - Moves the control point at index by (dx, dy)");
                    System.out.println("  scl factor - Scales the entire shape by the given factor");
                    System.out.println("  sxy fx fy - Scales the entire shape by the given x and y factor (fx, fy)");
                    System.out.println("  scb factor - Scales the viewing box by the given factor");
                    System.out.println("  ssb scale - Set the scale of the viewing box to the given scale");
                    System.out.println("  rm index - Removes the indexed point");
                    System.out.println("  rml - Removes the last point");
                    System.out.println("  rmf - Removes the first point");
                    System.out.println("  rma - Clears all points");
                    System.out.println("  clear - Clear the console");
                    System.out.println("  print - Prints the list of points and control points");
                    System.out.println("  json - Outputs the shape as a JSON array");
                    System.out.println("  json [json] - Loads the shape from a JSON array");
                    System.out.println("  info - Shows information about the current shape");
                    System.out.println("  undo - Undoes the last action");
                    System.out.println("  redo - Redoes the last undone action");
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
                            case "mp"   -> "mp index dx dy - Moves the point at index by (dx, dy)";
                            case "mc"   -> "mc index dx dy - Moves the control point at index by (dx, dy)";
                            case "scl"  -> "scl factor - Scales the entire shape by the given factor";
                            case "sxy"  -> "sxy fx fy - Scales the entire shape by the given x and y factor (fx, fy)";
                            case "scb"  -> "scb factor - Scales the viewing box by the given factor";
                            case "ssb"  -> "ssb scale - Set the scale of the viewing box to the given scale";
                            case "rm"   -> "rm index - Removes the indexed point";
                            case "rml"  -> "rml - Removes the last point";
                            case "rmf"  -> "rmf - Removes the first point";
                            case "rma"  -> "rma - Clears all points";
                            case "clear"-> "clear - Clear the console";
                            case "print"-> "print - Prints the list of points and control points";
                            case "json" -> "json - Outputs the shape as a JSON array\njson [json] - Loads the shape from a JSON array";
                            case "info" -> "info - Shows information about the current shape";
                            case "undo" -> "undo - Undoes the last action";
                            case "redo" -> "redo - Redoes the last undone action";
                            case "exit" -> "exit - Exits the program";
                            case "quit" -> "quit - Quits the program";
                            case "help" -> "help - Shows this help message\nhelp command - Shows help message for specific command";
                            default -> "Unknown command";
                        });
                    } else {
                        System.out.println("Invalid number of arguments. Usage: help command");
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
                } else if (line.equals("print")) {
                    CurvedShape shape = s.get().toCurvedShape();
                    if (shape == null) {
                        System.out.println("No points in the shape.");
                    } else {
                        for (double[] point : shape.toArray()) {
                            System.out.printf("Point: (%.2f, %.2f), Control: (%.2f, %.2f)%n", point[0], point[1], point[2], point[3]);
                        }
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
