package hk.edu.polyu.comp.comp2021.clevis.view;

import hk.edu.polyu.comp.comp2021.clevis.controller.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.ShapeManager;
import hk.edu.polyu.comp.comp2021.clevis.model.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ClevisGUI {
    private JFrame mainFrame;
    private DrawingPanel drawingPanel;
    private JTextArea commandHistory;
    private JTextField commandInput;
    private JList<String> shapeList;
    private Clevis.CommandParser parser;
    private ShapeManager shapeManager;
    private JLabel zoomLabel;
    private JButton undoBtn;
    private JButton redoBtn;
    
    public ClevisGUI(ShapeManager shapeManager, Clevis.CommandParser parser) {
        this.shapeManager = shapeManager;
        this.parser = parser;
        initializeGUI();
    }
    
    private void initializeGUI() {
        mainFrame = new JFrame("Clevis Drawing Tool - GUI");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        
        createToolbar();
        createDrawingPanel();
        createSidePanel();
        createCommandPanel();
        
        mainFrame.pack();
        mainFrame.setSize(1200, 800);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        
        // Initial status message
        commandHistory.append("Clevis GUI Started\n");
        commandHistory.append("Use buttons or type commands to create shapes\n\n");
    }
    
        private void createToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Shape creation buttons
        JButton rectBtn = new JButton("Rectangle");
        rectBtn.addActionListener((ActionEvent e) -> {
            String command = "rectangle rect_" + System.currentTimeMillis() + " 50 50 100 60";
            executeCommand(command);
        });
        
        JButton circleBtn = new JButton("Circle");
        circleBtn.addActionListener((ActionEvent e) -> {
            String command = "circle circ_" + System.currentTimeMillis() + " 150 150 50";
            executeCommand(command);
        });
        
        JButton lineBtn = new JButton("Line");
        lineBtn.addActionListener((ActionEvent e) -> {
            String command = "line line_" + System.currentTimeMillis() + " 10 10 200 200";
            executeCommand(command);
        });
        
        JButton squareBtn = new JButton("Square");
        squareBtn.addActionListener((ActionEvent e) -> {
            String command = "square sq_" + System.currentTimeMillis() + " 100 100 80";
            executeCommand(command);
        });
        
        // ADD UNDO/REDO BUTTONS HERE
        JButton undoBtn = new JButton("Undo");
        undoBtn.setToolTipText("Undo the last command");
        undoBtn.addActionListener(e -> {
            executeCommand("undo");
            updateUndoRedoButtons(); // Update button states
        });
        
        JButton redoBtn = new JButton("Redo");
        redoBtn.setToolTipText("Redo the last undone command");
        redoBtn.addActionListener(e -> {
            executeCommand("redo");
            updateUndoRedoButtons(); // Update button states
        });
        
        // Zoom buttons
        JButton zoomInBtn = new JButton("Zoom In");
        zoomInBtn.addActionListener((ActionEvent e) -> {
            drawingPanel.zoomIn();
            updateZoomLabel();
        });
        
        JButton zoomOutBtn = new JButton("Zoom Out");
        zoomOutBtn.addActionListener((ActionEvent e) -> {
            drawingPanel.zoomOut();
            updateZoomLabel();
        });
        
        JButton resetZoomBtn = new JButton("Reset Zoom");
        resetZoomBtn.addActionListener((ActionEvent e) -> {
            drawingPanel.resetZoom();
            updateZoomLabel();
        });
        
        // Zoom label
        zoomLabel = new JLabel("100%");
        zoomLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        // Action buttons
        JButton listAllBtn = new JButton("List All");
        listAllBtn.addActionListener(e -> executeCommand("listAll"));
        
        JButton helpBtn = new JButton("Help");
        helpBtn.addActionListener(e -> executeCommand("help"));
        
        // Add components to toolbar
        toolBar.add(rectBtn);
        toolBar.add(circleBtn);
        toolBar.add(lineBtn);
        toolBar.add(squareBtn);
        toolBar.addSeparator();
        toolBar.add(undoBtn);    // Add undo button
        toolBar.add(redoBtn);    // Add redo button
        toolBar.addSeparator();
        toolBar.add(zoomInBtn);
        toolBar.add(zoomOutBtn);
        toolBar.add(resetZoomBtn);
        toolBar.add(zoomLabel);
        toolBar.addSeparator();
        toolBar.add(listAllBtn);
        toolBar.add(helpBtn);
        
        mainFrame.add(toolBar, BorderLayout.NORTH);
        
        // Store button references for updating states
        this.undoBtn = undoBtn;
        this.redoBtn = redoBtn;
        
        // Initial button state update
        updateUndoRedoButtons();
    }
    
    private void updateZoomLabel() {
        int zoomPercentage = (int) (drawingPanel.getZoomFactor() * 100);
        zoomLabel.setText(zoomPercentage + "%");
    }
    
    private void createDrawingPanel() {
        drawingPanel = new DrawingPanel(shapeManager);
        JScrollPane scrollPane = new JScrollPane(drawingPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        
        // Add mouse wheel listener to scroll pane for zoom
        scrollPane.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    drawingPanel.zoomIn();
                } else {
                    drawingPanel.zoomOut();
                }
                updateZoomLabel();
                e.consume();
            }
        });
        
        mainFrame.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createSidePanel() {
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBorder(BorderFactory.createTitledBorder("Shapes"));
        
        shapeList = new JList<>();
        shapeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateShapeList();
        
        JScrollPane listScroll = new JScrollPane(shapeList);
        
        // Control buttons
        JPanel controlPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        
        JButton refreshBtn = new JButton("Refresh View");
        refreshBtn.addActionListener(e -> {
            drawingPanel.repaint();
            drawingPanel.clearBoundingBox(); // Clear bounding box visualization
            updateShapeList();
        });
        
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            String selected = shapeList.getSelectedValue();
            if (selected != null) {
                String shapeName = selected.split(" ")[0]; // Extract name before space
                executeCommand("delete " + shapeName);
            }
        });
        
        JButton boundingBoxBtn = new JButton("Bounding Box");
        boundingBoxBtn.addActionListener(e -> {
            String selected = shapeList.getSelectedValue();
            if (selected != null) {
                String shapeName = selected.split(" ")[0];
                executeCommand("boundingbox " + shapeName);
                // Show bounding box visualization
                drawingPanel.showBoundingBoxForShape(shapeName);
            }
        });
        
        JButton fitToScreenBtn = new JButton("Fit to Screen");
        fitToScreenBtn.addActionListener(e -> {
            drawingPanel.fitToScreen();
            updateZoomLabel();
        });
        
        controlPanel.add(refreshBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(boundingBoxBtn);
        controlPanel.add(fitToScreenBtn);
        
        sidePanel.add(listScroll, BorderLayout.CENTER);
        sidePanel.add(controlPanel, BorderLayout.SOUTH);
        sidePanel.setPreferredSize(new Dimension(250, 0));
        
        mainFrame.add(sidePanel, BorderLayout.EAST);
    }
    
    private void createCommandPanel() {
        JPanel commandPanel = new JPanel(new BorderLayout());
        commandPanel.setBorder(BorderFactory.createTitledBorder("Command Console"));
        
        commandHistory = new JTextArea(10, 60);
        commandHistory.setEditable(false);
        commandHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane historyScroll = new JScrollPane(commandHistory);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        commandInput = new JTextField();
        commandInput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        commandInput.addActionListener((ActionEvent e) -> executeUserCommand());
        
        JButton executeBtn = new JButton("Execute");
        executeBtn.addActionListener((ActionEvent e) -> executeUserCommand());
        
        inputPanel.add(new JLabel("Command: "), BorderLayout.WEST);
        inputPanel.add(commandInput, BorderLayout.CENTER);
        inputPanel.add(executeBtn, BorderLayout.EAST);
        
        commandPanel.add(historyScroll, BorderLayout.CENTER);
        commandPanel.add(inputPanel, BorderLayout.SOUTH);
        commandPanel.setPreferredSize(new Dimension(0, 250));
        
        mainFrame.add(commandPanel, BorderLayout.SOUTH);
    }
    
    private void executeUserCommand() {
        String command = commandInput.getText().trim();
        if (!command.isEmpty()) {
            executeCommand(command);
            commandInput.setText("");
        }
    }
    
    private void executeCommand(String command) {
    // Show the command that was executed
    commandHistory.append("> " + command + "\n");
    
    // Execute the command - outputs will now appear via the callback
    parser.execute(command);
    
    // Handle bounding box visualization
    if (command.trim().toLowerCase().startsWith("boundingbox ")) {
        String[] tokens = command.trim().split("\\s+");
        if (tokens.length >= 2) {
            String shapeName = tokens[1];
            drawingPanel.showBoundingBoxForShape(shapeName);
        }
    }
    
    drawingPanel.repaint();
    updateShapeList();
    commandHistory.setCaretPosition(commandHistory.getDocument().getLength());
    
    // UPDATE UNDO/REDO BUTTON STATES AFTER EVERY COMMAND
    updateUndoRedoButtons();
}

    private void updateUndoRedoButtons() {
    if (undoBtn != null && redoBtn != null) {
        String status = parser.getUndoRedoStatus();
        
        // Parse the status string to determine button states
        boolean canUndo = status.contains("Undo: Yes");
        boolean canRedo = status.contains("Redo: Yes");
        
        undoBtn.setEnabled(canUndo);
        redoBtn.setEnabled(canRedo);
        
        // Update tooltips with more information
        undoBtn.setToolTipText(canUndo ? "Undo the last command" : "Nothing to undo");
        redoBtn.setToolTipText(canRedo ? "Redo the last undone command" : "Nothing to redo");
    }
}
    
    private void updateShapeList() {
        try {
            List<Shape> shapes = shapeManager.getAllShapes();
            String[] shapeNames = new String[shapes.size()];
            for (int i = 0; i < shapes.size(); i++) {
                Shape shape = shapes.get(i);
                shapeNames[i] = shape.getName() + " - " + shape.getClass().getSimpleName();
            }
            shapeList.setListData(shapeNames);
        } catch (Exception e) {
            shapeList.setListData(new String[]{"No shapes available"});
        }
    }
}