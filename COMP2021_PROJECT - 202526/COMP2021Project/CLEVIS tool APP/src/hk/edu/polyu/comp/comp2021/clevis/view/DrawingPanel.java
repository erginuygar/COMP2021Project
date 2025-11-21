package hk.edu.polyu.comp.comp2021.clevis.view;

import hk.edu.polyu.comp.comp2021.clevis.model.ShapeManager;
import hk.edu.polyu.comp.comp2021.clevis.model.Shape;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DrawingPanel extends JPanel {
    private ShapeManager shapeManager;
    
    public DrawingPanel(ShapeManager shapeManager) {
        this.shapeManager = shapeManager;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Get shapes using the correct type
        List<Shape> shapes = shapeManager.getAllShapes();
        for (Shape shape : shapes) {
            drawShape(g2d, shape);
        }
        
        // If no shapes, show message
        if (shapes.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.drawString("No shapes created. Use commands or buttons to create shapes.", 50, 300);
        }
    }
    
    private void drawShape(Graphics2D g2d, Shape shape) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        try {
            // Parse bounding box coordinates
            String bbox = shape.getBoundingBox();
            String[] coords = bbox.split("\\s+");
            if (coords.length < 4) {
                drawGenericShape(g2d, shape);
                return;
            }
            
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            double w = Double.parseDouble(coords[2]);
            double h = Double.parseDouble(coords[3]);
            
            // Determine shape type and draw accordingly
            String className = shape.getClass().getSimpleName();
            
            switch (className) {
                case "Rectangle":
                    drawRectangle(g2d, shape, x, y, w, h);
                    break;
                case "Circle":
                    drawCircle(g2d, shape, x, y, w, h);
                    break;
                case "Line":
                    drawLine(g2d, shape);
                    break;
                case "Square":
                    drawSquare(g2d, shape, x, y, w, h);
                    break;
                case "Group":
                    drawGroup(g2d, shape, x, y, w, h);
                    break;
                default:
                    drawGenericShape(g2d, shape, x, y, w, h);
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("Error drawing shape " + shape.getName() + ": " + e.getMessage());
            drawGenericShape(g2d, shape);
        }
    }
    
    private void drawRectangle(Graphics2D g2d, Shape shape, double x, double y, double w, double h) {
        g2d.drawRect((int)x, (int)y, (int)w, (int)h);
        
        // Draw shape name
        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(shape.getName(), (int)x, (int)y - 5);
    }
    
    private void drawCircle(Graphics2D g2d, Shape shape, double x, double y, double w, double h) {
        // For circle, the bounding box is the enclosing square
        g2d.drawOval((int)x, (int)y, (int)w, (int)h);
        
        // Draw shape name near center
        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(shape.getName(), (int)(x + w/2 - 10), (int)(y + h/2));
    }
    
    private void drawLine(Graphics2D g2d, Shape shape) {
        try {
            // For Line, we need to parse the actual endpoints from getInfo()
            String info = shape.getInfo();
            
            // Parse coordinates from info string - handle different formats
            if (info.contains("(") && info.contains(")")) {
                String[] parts = info.split("[\\(\\)\\,]");
                double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                int coordCount = 0;
                
                for (String part : parts) {
                    part = part.trim();
                    if (!part.isEmpty() && Character.isDigit(part.charAt(0))) {
                        try {
                            double coord = Double.parseDouble(part);
                            switch (coordCount) {
                                case 0: x1 = coord; break;
                                case 1: y1 = coord; break;
                                case 2: x2 = coord; break;
                                case 3: y2 = coord; break;
                            }
                            coordCount++;
                        } catch (NumberFormatException e) {
                            // Skip non-numeric parts
                        }
                    }
                }
                
                if (coordCount >= 4) {
                    g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
                    
                    // Draw shape name at midpoint
                    g2d.setColor(Color.BLUE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    double midX = (x1 + x2) / 2;
                    double midY = (y1 + y2) / 2;
                    g2d.drawString(shape.getName(), (int)midX, (int)midY);
                    return;
                }
            }
            
            // Fallback: use bounding box as diagonal line
            String bbox = shape.getBoundingBox();
            String[] coords = bbox.split("\\s+");
            double x1 = Double.parseDouble(coords[0]);
            double y1 = Double.parseDouble(coords[1]);
            double x2 = x1 + Double.parseDouble(coords[2]);
            double y2 = y1 + Double.parseDouble(coords[3]);
            
            g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
            g2d.setColor(Color.BLUE);
            g2d.drawString(shape.getName(), (int)((x1 + x2)/2), (int)((y1 + y2)/2));
            
        } catch (Exception e) {
            System.err.println("Error drawing line: " + e.getMessage());
            drawGenericShape(g2d, shape);
        }
    }
    
    private void drawSquare(Graphics2D g2d, Shape shape, double x, double y, double w, double h) {
        g2d.drawRect((int)x, (int)y, (int)w, (int)h);
        
        // Draw shape name
        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(shape.getName(), (int)x, (int)y - 5);
    }
    
    private void drawGroup(Graphics2D g2d, Shape shape, double x, double y, double w, double h) {
        // Draw group boundary with dashed line
        g2d.setColor(Color.RED);
        float[] dashPattern = {5, 5};
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, 
                                    BasicStroke.JOIN_MITER, 10, dashPattern, 0));
        g2d.drawRect((int)x, (int)y, (int)w, (int)h);
        
        // Draw group name
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Group: " + shape.getName(), (int)x, (int)y - 5);
        
        // Reset stroke for normal drawing
        g2d.setStroke(new BasicStroke(2));
    }
    
    private void drawGenericShape(Graphics2D g2d, Shape shape, double x, double y, double w, double h) {
        // Draw bounding box for unknown shape types
        g2d.setColor(Color.GRAY);
        g2d.drawRect((int)x, (int)y, (int)w, (int)h);
        
        // Draw shape info
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString(shape.getName() + " (" + shape.getClass().getSimpleName() + ")", 
                      (int)x, (int)y - 5);
    }
    
    private void drawGenericShape(Graphics2D g2d, Shape shape) {
        // Ultimate fallback - just show the name
        g2d.setColor(Color.GRAY);
        g2d.drawString(shape.getName() + " - " + shape.getClass().getSimpleName(), 10, 20);
    }
}