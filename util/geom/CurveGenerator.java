package util.geom;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class CurveGenerator {
    public static void main(String[] args){
        JFrame f = new JFrame();
        MutableCurvedShape s = new MutableCurvedShape();
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
                CurvedShape shape = s.toCurvedShape();
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
                    g2d.setColor(Color.RED);
                    for (double[] point : array) {
                        g2d.drawOval((int) (point[0] - 8), (int) (point[1] - 8), 16, 16);
                    }
                    // Draw control point
                    g2d.setColor(Color.BLUE);
                    for (double[] point : array) {
                        g2d.drawOval((int) (point[2] - 8), (int) (point[3] - 8), 16, 16);
                    }
                    // Write number next to each point
                    g2d.setColor(Color.RED);
                    for (int i = 0; i < array.length; i++) {
                        double[] point = array[i];
                        g2d.drawString(Integer.toString(i), (int) point[0] - 4, (int) point[1] + 4);
                    }
                    // Write number next to each control point
                    g2d.setColor(Color.BLUE);
                    for (int i = 0; i < array.length; i++) {
                        double[] point = array[i];
                        g2d.drawString(Integer.toString(i), (int) point[2] - 4, (int) point[3] + 4);
                    }
                }
                // Draw border and coordinates of the corners
                g2d.setColor(Color.DARK_GRAY);
                CurvedShape fittedSquare = CurvedShape.SQUARE.scaled(scale, -scale).shifted(w / 2.0, h / 2.0);
                CurvedShape smallerSquare = CurvedShape.SQUARE.scaled(scale * 0.9, scale * -0.9).shifted(w / 2.0, h / 2.0);
                double[][] corners = CurvedShape.SQUARE.toArray();
                g2d.draw(fittedSquare.toShape());
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
                System.out.println("Enter command: ");
                String line = scanner.nextLine().trim();
                if (line.equals("exit")){
                    System.exit(0);
                } else if (line.equals("help")) {
                    System.out.println("Commands:");
                    System.out.println("  add x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy)");
                    System.out.println("  set index x y cx cy - Sets the point at index to (x, y) with control point (cx, cy)");
                    System.out.println("  sp index dx dy - Sets the point at index by (dx, dy)");
                    System.out.println("  sc index dx dy - Sets the control point at index by (dx, dy)");
                    System.out.println("  ins index x y cx cy - Adds a point with coordinates (x, y) and control point (cx, cy) to index index");
                    System.out.println("  mp index dx dy - Moves the point at index by (dx, dy)");
                    System.out.println("  mc index dx dy - Moves the control point at index by (dx, dy)");
                    System.out.println("  scl factor - Scales the entire shape by the given factor");
                    System.out.println("  scb factor - Scales the viewing box by the given factor");
                    System.out.println("  ssb scale - Set the scale of the viewing box to the given scale");
                    System.out.println("  rm index - Removes the indexed point");
                    System.out.println("  rml - Removes the last point");
                    System.out.println("  rmf - Removes the first point");
                    System.out.println("  rma - Clears all points");
                    System.out.println("  clear - Clear the console");
                    System.out.println("  print - Prints the list of points and control points");
                    System.out.println("  json - Outputs the shape as a JSON array");
                    System.out.println("  exit - Exits the program");
                    System.out.println("  help - Shows this help message");
                } else if (line.startsWith("add ")) {
                    String[] parts = line.substring(4).split(" ");
                    if (parts.length == 4) {
                        try {
                            double x = Double.parseDouble(parts[0]);
                            double y = Double.parseDouble(parts[1]);
                            double cx = Double.parseDouble(parts[2]);
                            double cy = Double.parseDouble(parts[3]);
                            s.addPoint(x, y, cx, cy);
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
                    s.clear();
                    p.repaint();
                } else if (line.equals("rml")) {
                    s.removeLast();
                    p.repaint();
                } else if (line.equals("rmf")) {
                    s.removeFirst();
                    p.repaint();
                } else if (line.startsWith("rm ")) {
                    String[] parts = line.substring(3).split(" ");
                    if (parts.length == 1) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            s.removePoint(index);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: rm index");
                    }
                } else if (line.startsWith("mp ")) {
                    String[] parts = line.substring(3).split(" ");
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double dx = Double.parseDouble(parts[1]);
                            double dy = Double.parseDouble(parts[2]);
                            s.movePoint(index, dx, dy);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mp index dx dy");
                    }
                } else if (line.startsWith("mc ")) {
                    String[] parts = line.substring(3).split(" ");
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double dx = Double.parseDouble(parts[1]);
                            double dy = Double.parseDouble(parts[2]);
                            s.moveControl(index, dx, dy);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: mc index dx dy");
                    }
                } else if (line.startsWith("sp ")) {
                    String[] parts = line.substring(3).split(" ");
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            s.setPoint(index, x, y, s.toCurvedShape().toArray()[index][2], s.toCurvedShape().toArray()[index][3]);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: sp index dx dy");
                    }
                } else if (line.startsWith("sc ")) {
                    String[] parts = line.substring(3).split(" ");
                    if (parts.length == 3) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double cx = Double.parseDouble(parts[1]);
                            double cy = Double.parseDouble(parts[2]);
                            s.setPoint(index, s.toCurvedShape().toArray()[index][0], s.toCurvedShape().toArray()[index][1], cx, cy);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: sc index dx dy");
                    }
                } else if (line.startsWith("set ")) {
                    String[] parts = line.substring(4).split(" ");
                    if (parts.length == 5) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double cx = Double.parseDouble(parts[3]);
                            double cy = Double.parseDouble(parts[4]);
                            s.setPoint(index, x, y, cx, cy);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: set index x y cx cy");
                    }
                } else if (line.startsWith("ins ")) {
                    String[] parts = line.substring(4).split(" ");
                    if (parts.length == 5) {
                        try {
                            int index = Integer.parseInt(parts[0]);
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double cx = Double.parseDouble(parts[3]);
                            double cy = Double.parseDouble(parts[4]);
                            s.addPoint(index, x, y, cx, cy);
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: ins index x y cx cy");
                    }
                } else if (line.startsWith("scl ")) {
                    String[] parts = line.substring(4).split(" ");
                    if (parts.length == 1) {
                        try {
                            double factor = Double.parseDouble(parts[0]);
                            if (factor == 0) {
                                System.out.println("Scale factor cannot be zero.");
                                continue;
                            }
                            CurvedShape shape = s.toCurvedShape();
                            shape = shape.scaled(factor);
                            s.clear();
                            for (double[] point : shape.toArray()) {
                                s.addPoint(point[0], point[1], point[2], point[3]);
                            }
                            p.repaint();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format.");
                        }
                    } else {
                        System.out.println("Invalid number of arguments. Usage: scl factor");
                    }
                } else if (line.startsWith("scb ")) {
                    String[] parts = line.substring(4).split(" ");
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
                } else if (line.startsWith("ssb ")) {
                    String[] parts = line.substring(4).split(" ");
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
                    CurvedShape shape = s.toCurvedShape();
                    if (shape == null) {
                        System.out.println("No points in the shape.");
                    } else {
                        for (double[] point : shape.toArray()) {
                            System.out.printf("Point: (%.2f, %.2f), Control: (%.2f, %.2f)%n", point[0], point[1], point[2], point[3]);
                        }
                    }
                } else if (line.equals("json")) {
                    CurvedShape shape = s.toCurvedShape();
                    if (shape == null) {
                        System.out.println("No points in the shape.");
                    } else {
                        System.out.println(shape.toJsonArrayBuilder().build().toString());
                    }
                } else if (line.isEmpty()) {
                    // Do nothing for empty input
                } else {
                    System.out.println("Unknown command.");
                }
            }
        }).start();
    }
}
