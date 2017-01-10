import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nicholas Vadivelu on 2017-01-07.
 */

public class GUI extends JFrame implements ActionListener{
    private int width = 730; //the width of the window
    private int height = 630; //the height of the window
    private int canvasWidth = 600;
    private int canvasHeight = 600;
    private int dimension = 300;
    private Main main;
    private Thread thread;
    private JButton start, savePhoto;
    private JLabel posLabel, lumLabel;
    private JPanel panel, UIPanel, infoPanel;
    private ImageCanvas canvas;

    public GUI () throws Exception {
        //Initialize the components
        panel = new JPanel(new BorderLayout()); //main content pane

        UIPanel = new JPanel();
        UIPanel.setLayout(new BoxLayout(UIPanel, BoxLayout.Y_AXIS));

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Sensor Info"));

        canvas = new ImageCanvas(canvasWidth, canvasHeight, dimension, dimension); //fix this
        start = new JButton("Start");
        savePhoto = new JButton("Export JPEG");
        posLabel = new JLabel("Position: (000, 000)");
        lumLabel = new JLabel("Luminosity: 0.000");

        //Adding adding listener
        start.addActionListener(this);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        savePhoto.addActionListener(this);
        savePhoto.setAlignmentX(Component.CENTER_ALIGNMENT);


        //Add components to panel
        infoPanel.add(posLabel);
        infoPanel.add(lumLabel);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        UIPanel.add(infoPanel);
        UIPanel.add(Box.createVerticalStrut(10)); // Fixed width invisible separator.);
        UIPanel.add(start);
        UIPanel.add(savePhoto);
        panel.add(UIPanel, BorderLayout.EAST);
        panel.add(canvas, BorderLayout.WEST);

        //Set up JFrame
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {}
        pack ();
        setContentPane(panel);
        setResizable(false); //don't want user to resize or else images will not be aligned
        setVisible(true);
        setTitle ("PixelShot 300 Software");
        setSize (width, height);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null); // Center window.

        //Initialize main
        main = new Main ("COM3", dimension, this); //COM3 is the port --> create way to get this from user
    }

    public void updateUI(int i, int j, double temp) {
        posLabel.setText("Position: (" + String.format("%03d", j+1) + "," + String.format("%03d", i+1) + ")");
        lumLabel.setText("Luminosity: " + String.format("%.3" + "f", temp));
        canvas.updateValue(temp, j, i);
    }

    public void doneRunning() {
        canvas.updateAllValues(main.getSensorValues());
        canvas.setRepaintAll(true);
        //posLabel.setText("Position: (" + String.format("%03d", dimension) + "," + String.format("%03d", dimension) + ")");
        main.stop();
        start.setText("Start");
        repaint();
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getActionCommand().equals("Start")) { //if the user presses start, run code.
            try {
                main = new Main("COM3", dimension, this); //COM3 is the port --> create way to get this from user
            }catch (Exception g) {}
            thread = new Thread(main);
            canvas.clear();
            canvas.setRepaintAll(true);
            thread.start();
            start.setText("Stop");
        } else if (e.getActionCommand().equals("Stop")) {
            main.stop();
            start.setText("Start");
        } else if (e.getSource() == savePhoto) {
            canvas.generateJPEG();
        }
    }
}