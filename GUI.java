import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nicholas Vadivelu on 2017-01-07.
 */
public class GUI extends JFrame implements ActionListener{
    private int width = 800; //the width of the window
    private int height = 600; //the height of the window
    private Main main;
    private JButton start;
    private ImageCanvas canvas;

    public GUI () throws Exception{
        //Initialize main
        main = new Main ("COM3"); //COM3 is the port --> create way to get this from user

        //Initialize the components
        JPanel panel = new JPanel(); //main content pane
        canvas = new ImageCanvas(300, 300, 300, 300); //fix this
        start = new JButton("Start");

        //Add components to panel
        panel.add(start);
        panel.add(canvas);


        //Set up JFrame
        pack ();
        setContentPane(panel);
        setResizable(false); //don't want user to resize or else images will not be aligned
        setVisible(true);
        setTitle ("PixelShot 300 Software");
        setSize (width, height);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null); // Center window.
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == start) { //if the user presses start, run code.
            start();
        }
    }

    public void start() { //captures a full image
        boolean dir = true; //dir  true is CW, false is CCW
        for (int i = 1; i <= 300; i++){ //loops through all the y positions
            for (int j = 1; j <=300; j++) { //loops through all the x positions
                main.step(j, i, dir);
                canvas.updateValues(main.getSensorValues());
                canvas.repaint();
                repaint();
            }
            dir = !dir; //flips direction of the NEMA8 after each horizontal sweep
        }
    }
}
