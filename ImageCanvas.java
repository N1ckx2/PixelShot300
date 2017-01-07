import javax.swing.*;
import java.awt.*;

/**
 * Created by Nicholas Vadivelu on 2017-01-07.
 */
public class ImageCanvas extends JPanel { //this panel will be updated live to show progress
    int xPixels, yPixels; //width and height of the image
    int width, height; //width and height of the canva s
    Color[][] canvas; //colour values of all the pixels

    public ImageCanvas(int w, int h, int xDim, int yDim) { //set up Image Canvas
        width = w;
        height = h;
        this.setPreferredSize (new Dimension(width, height));
        xPixels = xDim;
        yPixels = yDim;

        //Initialize the colour 2D array
        canvas = new Color[xPixels][yPixels];
        for (int i = 0 ; i < xPixels ; i++) {
            for (int j = 0 ; j < yPixels ; j++ ) {
                canvas[i][j] = new Color(0, 0, 0);
            }
        }
    }

    public void paintComponent (Graphics g) { //every repaint
        int pixelSize = width/xPixels;

        for (int i = 0 ; i < xPixels; i++) {
            for (int j = 0 ; j < yPixels; j++) {
                g.drawRect(i*pixelSize, j*pixelSize, pixelSize, pixelSize); //creates rectangles to fill up
            }
        }
    }

    public void updateValues(double[] sensorValues) { //updates the colour array based on sensor values
        for (int i = 0; i < xPixels; i++) {
            for (int j = 0; j < yPixels; j++ ){
                int b = (int)sensorValues[i*xPixels + j]*255;
                canvas[i][j] = new Color(b, b, b);
        }
        }
    }

}
