import javax.swing.*;
import java.awt.*;

/**
 * Created by Nicholas Vadivelu on 2017-01-07.
 */
public class ImageCanvas extends JPanel { //this panel will be updated live to show progress
    int xPixels, yPixels; //width and height of the image
    int width, height; //width and height of the canva s
    int pixelSize;
    Color[][] canvas; //colour values of all the pixels
    int curX, curY;
    boolean repaintAll;

    public ImageCanvas(int w, int h, int xDim, int yDim) { //set up Image Canvas
        width = w;
        height = h;
        this.setPreferredSize (new Dimension(width, height));
        xPixels = xDim;
        yPixels = yDim;
        curX = 0;
        curY = 0;

        repaintAll = false;

        pixelSize = width/xPixels;

        //Initialize the colour 2D array
        canvas = new Color[xPixels][yPixels];
        for (int i = 0 ; i < xPixels ; i++) {
            for (int j = 0 ; j < yPixels ; j++ ) {
                canvas[i][j] = new Color(0, 0, 0);
            }
        }
    }

    public void paintComponent (Graphics g) { //every repaint
        super.paintComponent(g);
        if (repaintAll) {
            for (int i = 0; i < xPixels; i++) {
                for (int j = 0; j < yPixels; j++) {
                    g.setColor(canvas[i][j]);
                    g.fillRect(i * pixelSize, j * pixelSize, pixelSize, pixelSize); //creates rectangles to fill up
                }
            }
        } else {
            g.setColor(canvas[curX][curY]);
            g.fillRect(curX * pixelSize, curY * pixelSize, pixelSize, pixelSize); //creates rectangles to fill up
        }
    }

    public void updateValues(double[] sensorValues) { //updates the colour array based on sensor values
        for (int i = 0; i < xPixels; i++) {
            for (int j = 0; j < yPixels; j++ ){
                int b = (int)(sensorValues[i*xPixels + j]*255);
                canvas[i][j] = new Color(b, b, b);
            }
        }
        repaint();
    }

    public void updateValue(double sensorValue, int xPos, int yPos) { //updates one value
        int b = (int)(sensorValue*255);
        canvas[xPos][yPos] = new Color(b, b, b);
        curX = xPos;
        curY = yPos;
        this.repaint();
    }

    public void setRepaintAll(boolean rep) { repaintAll = rep; }
}
