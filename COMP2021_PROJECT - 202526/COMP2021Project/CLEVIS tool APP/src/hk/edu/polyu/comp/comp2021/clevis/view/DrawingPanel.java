package hk.edu.polyu.comp.comp2021.clevis.view;

import hk.edu.polyu.comp.comp2021.clevis.model.ShapeManager;
import hk.edu.polyu.comp.comp2021.clevis.model.Shape;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

public class DrawingPanel extends JPanel {
    private ShapeManager shapeManager;
    private double zoomFactor = 1.0;
    private double minZoom = 0.1;
    private double maxZoom = 5.0;
    private double zoomStep = 0.2;
    private int panX = 0;
    private int panY = 0;
    
    private String boundingBoxShapeName = null;
    private boolean showBoundingBox = false;
    
    
    public DrawingPanel(ShapeManager shapeManager) {
        this.shapeManager = shapeManager;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
    }
    
    /**
     * Zoom in by increasing the zoom factor
     */
    public void zoomIn() {
        double newZoom = Math.min(maxZoom, zoomFactor + zoomStep);
        if (newZoom != zoomFactor) {
            zoomFactor = newZoom;
            repaint();
        }
    }
    
    /**
     * Zoom out by decreasing the zoom factor
     */
    public void zoomOut() {
        double newZoom = Math.max(minZoom, zoomFactor - zoomStep);
        if (newZoom != zoomFactor) {
            zoomFactor = newZoom;
            repaint();
        }
    }
    
    /**
     * Reset zoom to 100%
     */
    public void resetZoom() {
        if (zoomFactor != 1.0) {
            zoomFactor = 1.0;
            panX = 0;
            panY = 0;
            repaint();
        }
    }
    
    /**
     * Fit all shapes to screen
     */
    public void fitToScreen() {
        List<Shape> shapes = shapeManager.getAllShapes();
        if (shapes.isEmpty()) {
            resetZoom();
            return;
        }
        
        // Calculate the bounding box of all shapes
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        
        for (Shape shape : shapes) {
            try {
                String bbox = shape.getBoundingBox();
                String[] coords = bbox.split("\\s+");
                if (coords.length >= 4) {
                    double x = Double.parseDouble(coords[0]);
                    double y = Double.parseDouble(coords[1]);
                    double w = Double.parseDouble(coords[2]);
                    double h = Double.parseDouble(coords[3]);
                    
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x + w);
                    maxY = Math.max(maxY, y + h);
                }
            } catch (Exception e) {
                // Skip shapes with invalid bounding boxes
            }
        }
        
        if (minX == Double.MAX_VALUE) {
            resetZoom();
            return;
        }
        
        // Calculate required zoom to fit all shapes
        double shapeWidth = maxX - minX;
        double shapeHeight = maxY - minY;
        double panelWidth = getWidth() - 40; // Leave some margin
        double panelHeight = getHeight() - 40;
        
        double zoomX = panelWidth / shapeWidth;
        double zoomY = panelHeight / shapeHeight;
        
        zoomFactor = Math.min(zoomX, zoomY);
        zoomFactor = Math.max(minZoom, Math.min(maxZoom, zoomFactor));
        
        // Center the view
        panX = (int) ((getWidth() - shapeWidth * zoomFactor) / 2 - minX * zoomFactor);
        panY = (int) ((getHeight() - shapeHeight * zoomFactor) / 2 - minY * zoomFactor);
        
        repaint();
    }
    
    /**
     * Get current zoom factor
     */
    public double getZoomFactor() {
        return zoomFactor;
    }
        /**
     * Show bounding box for a specific shape
     */
    public void showBoundingBoxForShape(String shapeName) {
        this.boundingBoxShapeName = shapeName;
        this.showBoundingBox = true;
        repaint();
    }
    
    /**
     * Hide bounding box visualization
     */
    public void clearBoundingBox() {
        this.showBoundingBox = false;
        this.boundingBoxShapeName = null;
        repaint();
    }
    
    /**
     * Get the bounding box coordinates for a shape
     */
    private double[] getBoundingBoxCoordinates(Shape shape) {
        try {
            String bbox = shape.getBoundingBox();
            String[] coords = bbox.split("\\s+");
            if (coords.length >= 4) {
                return new double[]{
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]),
                    Double.parseDouble(coords[2]),
                    Double.parseDouble(coords[3])
                };
            }
        } catch (Exception e) {
            System.err.println("Error getting bounding box for " + shape.getName() + ": " + e.getMessage());
        }
        return null;
    }
     @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Apply zoom and pan transformations
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate(panX, panY);
        g2d.scale(zoomFactor, zoomFactor);
        
        // Draw all shapes
        List<Shape> shapes = shapeManager.getAllShapes();
        for (Shape shape : shapes) {
            drawShape(g2d, shape);
        }
        
        // ADD THIS: Draw bounding box if enabled
        if (showBoundingBox && boundingBoxShapeName != null) {
            drawBoundingBox(g2d);
        }
        
        // Restore original transform
        g2d.setTransform(originalTransform);
        
        // Draw zoom information
        drawZoomInfo(g2d);
        
        // If no shapes, show message
        if (shapes.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.drawString("No shapes created. Use commands or buttons to create shapes.", 50, 300);
        }
    }
/**
     * Draw highlighted bounding box for the selected shape
     */
    private void drawBoundingBox(Graphics2D g2d) {
        Shape targetShape = shapeManager.getShape(boundingBoxShapeName);
        if (targetShape == null) {
            return;
        }
        
        double[] bbox = getBoundingBoxCoordinates(targetShape);
        if (bbox == null) {
            return;
        }
        
        double x = bbox[0];
        double y = bbox[1];
        double w = bbox[2];
        double h = bbox[3];
        
        // Draw bounding box with highlighted style
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3)); // Thicker line
        
        // Draw rectangle
        g2d.drawRect((int)x, (int)y, (int)w, (int)h);
        
        // Draw corner markers
        int markerSize = 6;
        g2d.fillRect((int)x - markerSize/2, (int)y - markerSize/2, markerSize, markerSize); // Top-left
        g2d.fillRect((int)(x + w) - markerSize/2, (int)y - markerSize/2, markerSize, markerSize); // Top-right
        g2d.fillRect((int)x - markerSize/2, (int)(y + h) - markerSize/2, markerSize, markerSize); // Bottom-left
        g2d.fillRect((int)(x + w) - markerSize/2, (int)(y + h) - markerSize/2, markerSize, markerSize); // Bottom-right
        
        // Draw bounding box info
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String bboxInfo = String.format("BBox: (%.1f, %.1f) w=%.1f h=%.1f", x, y, w, h);
        g2d.drawString(bboxInfo, (int)x, (int)y - 10);
        
        // Reset stroke to normal
        g2d.setStroke(new BasicStroke(2));
    }
    
    private void drawZoomInfo(Graphics2D g2d) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String zoomText = String.format("Zoom: %.0f%%", zoomFactor * 100);
        g2d.drawString(zoomText, 10, 20);
        
        // Draw instructions
        if (zoomFactor != 1.0) {
            g2d.drawString("Press 'Reset Zoom' to return to 100%", 10, 40);
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
    
    // ... keep all your existing drawShape methods (drawRectangle, drawCircle, etc.) unchanged ...
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