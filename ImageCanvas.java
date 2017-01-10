import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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

    public void clear() { //updates the colour array based on sensor values
        for (int i = 0; i < xPixels; i++) {
            for (int j = 0; j < yPixels; j++ ){
                canvas[i][j] = new Color(0, 0, 0);
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

    public void updateAllValues(double[] sensorValues) {
        for (int i = 0 ; i < xPixels; i++) {
            for (int j = 0 ; j < yPixels; j++) {
                int b = (int) (255*sensorValues[i + j*xPixels]); //CHECK THISS
                canvas[i][j] = new Color(b, b, b);
            }
        }
    }

    public void setRepaintAll(boolean rep) { repaintAll = rep; }

    public void generateJPEG(){
        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = new BufferedImage(canvas.length, canvas[0].length,
                BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < canvas.length; x++) {
            for (int y = 0; y < canvas[x].length; y++) {
                bufferedImage.setRGB(x, y, canvas[x][y].getRGB());
            }
        }

        File outputfile = new File("C:\\Users\\nick\\OneDrive\\Documents\\Grade 12\\SPH4U0\\Summative\\images\\testImage_" + new java.text.SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new java.util.Date()) + ".jpg");
        try{ImageIO.write(bufferedImage, "jpg", outputfile);} catch (Exception e) {}
    }
}
