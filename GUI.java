import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nicholas Vadivelu on 2017-01-07.
 */

public class GUI extends JFrame implements ActionListener{
    private int width = 800; //the width of the window
    private int height = 600; //the height of the window
    private int canvasWidth = 500;
    private int canvasHeight = 500;
    private int dimension = 100;
    private Main main;
    private Thread thread;
    private JButton start, stop;
    private JLabel posLabel, lumLabel;
    private JPanel panel, UIPanel;
    private ImageCanvas canvas;

    public GUI () throws Exception{

        //Initialize the components
        panel = new JPanel(new BorderLayout()); //main content pane
        UIPanel = new JPanel(new FlowLayout());
        canvas = new ImageCanvas(canvasWidth, canvasHeight, dimension, dimension); //fix this
        start = new JButton("Start");
        stop = new JButton("Stop");
        posLabel = new JLabel("Position: (000, 000)");
        lumLabel = new JLabel("Luminosity: 0.000");

        //Adding adding listener
        start.addActionListener(this);
        stop.addActionListener(this);

        //Add components to panel
        UIPanel.add(start);
        UIPanel.add(posLabel);
        UIPanel.add(lumLabel);
        panel.add(UIPanel, BorderLayout.SOUTH);
        panel.add(canvas, BorderLayout.NORTH);

        //Set up JFrame
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
        thread = new Thread(main);
    }

    public void updateUI(int i, int j, double temp) {
        posLabel.setText("Position: (" + String.format("%03d", j+1) + "," + String.format("%03d", i+1) + ")");
        lumLabel.setText("Luminosity: " + String.format("%.3" + "f", temp));
        /*
        posLabel.paintImmediately(posLabel.getVisibleRect());
        lumLabel.paintImmediately(lumLabel.getVisibleRect());
        */
        canvas.updateValue(temp, j, i);
    }

    public void doneRunning() {
        canvas.setRepaintAll(true);
        posLabel.setText("Position: (" + String.format("%03d", dimension) + "," + String.format("%03d", dimension) + ")");
        repaint();
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getActionCommand().equals("Start")) { //if the user presses start, run code.
            canvas.setRepaintAll(true);
            thread.start();
            start.setText("Stop");
        } else if (e.getActionCommand().equals("Stop")) {
            main.stop();
            start.setText("Start");
        }
    }
}