import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;

/**
 * Created by Nicholas Vadivelu on 2017-01-07.
 */

public class GUI extends JFrame implements ActionListener, ChangeListener {
    private int width = 1380; //the width of the window
    private int height = 725; //the height of the window
    private int canvasWidth = 600;
    private int canvasHeight = 600;
    private int dimension = 75;
    private Main main;
    private Thread thread;
    private JButton start, savePhoto;
    private JLabel posLabel, lumLabel, mousePosLabel, mouseLumLabel;
    private JPanel panel, UIPanel, infoPanel, controlPanel, mousePanel;
    private ImageCanvas canvas;
    private ContourGraph contour;
    private JPanel graphPanel;
    private JSlider brightness, fisheye;
    private Histogram histogram;
    private double[] values;
    private int numHisto = 100;

    public GUI() throws Exception {
        //Initialize graphs
        values = new double[numHisto];
        for (int i = 0; i < numHisto; i++) {
            values[i] = 0;
        }

        //Initialize the components
        //panel = new JPanel(); //main content pane
        //UIPanel = new JPanel();
        //UIPanel.setLayout(new BoxLayout(UIPanel, BoxLayout.Y_AXIS));
        //graphsPanel = new JPanel();
        //UIPanel.setLayout(new BoxLayout(UIPanel, BoxLayout.Y_AXIS));

        //infoPanel = new JPanel();
        //infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        $$$setupUI$$$();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Sensor Information"));

        //controlPanel = new JPanel();
        //controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Image Controls"));
        mousePanel.setBorder(BorderFactory.createTitledBorder("Mouse Hover Information"));
        //start = new JButton("Start");
        savePhoto = new JButton("Export JPEG");
        //posLabel = new JLabel("Position: (000, 000)");
        //lumLabel = new JLabel("Luminosity: 0.000");

        //Sliders
        Hashtable brightnessLabel = new Hashtable();
        brightnessLabel.put(new Integer(0), new JLabel("0"));
        brightnessLabel.put(new Integer(50), new JLabel("50"));
        brightnessLabel.put(new Integer(100), new JLabel("100"));
        brightness.setLabelTable(brightnessLabel);
        brightness.setPaintLabels(true);

        Hashtable fisheyeLabel = new Hashtable();
        fisheyeLabel.put(new Integer(0), new JLabel("0"));
        fisheyeLabel.put(new Integer(20), new JLabel("2"));
        fisheyeLabel.put(new Integer(40), new JLabel("4"));
        fisheye.setLabelTable(fisheyeLabel);
        fisheye.setPaintLabels(true);

        //Adding adding listener
        start.addActionListener(this);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        savePhoto.addActionListener(this);
        savePhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        brightness.addChangeListener(this);
        fisheye.addChangeListener(this);

        /*
        //Add components to panel
        infoPanel.add(posLabel);
        infoPanel.add(lumLabel);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(new JLabel("Brightness"));
        controlPanel.add(brightness);
        controlPanel.add(new JLabel("Fisheye Correction"));
        controlPanel.add(fisheye);
        canvasPanel.add(canvas);
        UIPanel.add(infoPanel);
        UIPanel.add(controlPanel);
        UIPanel.add(start);
        UIPanel.add(savePhoto);
        panel.add(canvasPanel, BorderLayout.WEST);
        panel.add(histogram);
        panel.add(UIPanel, BorderLayout.EAST);

                        */
        //Set up JFrame
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        pack();
        setContentPane(panel);
        setResizable(true); //don't want user to resize or else images will not be aligned
        setVisible(true);
        setTitle("PixelShot 300 Software");
        setSize(width, height);
        //setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window.

        //Initialize main
        main = new Main("COM3", dimension, this); //COM3 is the port --> create way to get this from user
    }

    public void updateMouse(int x, int y, int lum) {
        mousePosLabel.setText("Mouse Position: (" + String.format("%03d", x + 1) + "," + String.format("%03d", y + 1) + ")");
        mouseLumLabel.setText("Luminosity at mouse: " + String.format("%.3" + "f", lum));
    }

    public void updateUI(int i, int j, double temp) {
        posLabel.setText("Position: (" + String.format("%03d", j + 1) + "," + String.format("%03d", i + 1) + ")");
        lumLabel.setText("Luminosity: " + String.format("%.3" + "f", temp));
        histogram.update(main.rnSensorValues(canvas.getMultiplier()));
        canvas.updateValue(temp, j, i);
        contour.updateValue(temp, j, i);
    }

    public void doneRunning() {
        canvas.updateAllValues(main.getSensorValues());
        contour.updateAllValues(main.getSensorValues());
        canvas.setRepaintAll(true);
        contour.setRepaintAll(true);
        //posLabel.setText("Position: (" + String.format("%03d", dimension) + "," + String.format("%03d", dimension) + ")");
        main.stop();
        start.setText("Start");
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Start")) { //if the user presses start, run code.
            try {
                main = new Main("COM3", dimension, this); //COM3 is the port --> create way to get this from user
            } catch (Exception g) {
            }
            thread = new Thread(main);
            canvas.clear();
            contour.clear();
            canvas.setRepaintAll(true);
            contour.setRepaintAll(true);
            thread.start();
            start.setText("Stop");
        } else if (e.getActionCommand().equals("Stop")) {
            main.stop();
            start.setText("Start");
        } else if (e.getSource() == savePhoto) {
            canvas.generateJPEG();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == brightness) {
            canvas.updateMultiplier(brightness.getValue() / 50.0);
            contour.updateMultiplier(brightness.getValue() / 50.0);
        } else if (e.getSource() == fisheye) {
            main.correctFisheye(fisheye.getValue() / 10.0, 1);
        }
        canvas.updateAllValues(main.getSensorValues());
        contour.updateAllValues(main.getSensorValues());
        histogram.update(main.rnSensorValues(canvas.getMultiplier()));
        repaint();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void createUIComponents() {
        canvas = new ImageCanvas(canvasWidth, canvasHeight, dimension, dimension, this); //fix this
        histogram = new Histogram(values, numHisto);
        contour = new ContourGraph(300, 300, dimension, dimension, this); //fix this
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 7, new Insets(0, 0, 0, 0), -1, -1));
        UIPanel = new JPanel();
        UIPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(UIPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 5, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        infoPanel = new JPanel();
        infoPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        UIPanel.add(infoPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, false));
        posLabel = new JLabel();
        posLabel.setText("Position: (000, 000)");
        infoPanel.add(posLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lumLabel = new JLabel();
        lumLabel.setText("Luminance: 0.000");
        infoPanel.add(lumLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        controlPanel = new JPanel();
        controlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        UIPanel.add(controlPanel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        start = new JButton();
        start.setText("Start");
        controlPanel.add(start, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Brightness");
        controlPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        brightness = new JSlider();
        controlPanel.add(brightness, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Fisheye");
        controlPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fisheye = new JSlider();
        fisheye.setMaximum(40);
        fisheye.setPaintLabels(false);
        fisheye.setPaintTicks(false);
        fisheye.setPaintTrack(true);
        fisheye.setValue(10);
        controlPanel.add(fisheye, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mousePanel = new JPanel();
        mousePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        UIPanel.add(mousePanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mousePosLabel = new JLabel();
        mousePosLabel.setText("Mouse Position: (000, 000)");
        mousePanel.add(mousePosLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mouseLumLabel = new JLabel();
        mouseLumLabel.setText("Luminance at Mouse: 0.000");
        mousePanel.add(mouseLumLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(canvas, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 5, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        graphPanel = new JPanel();
        graphPanel.setLayout(new BorderLayout(0, 0));
        panel.add(graphPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 5, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        graphPanel.add(histogram, BorderLayout.NORTH);
        graphPanel.add(contour, BorderLayout.EAST);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}