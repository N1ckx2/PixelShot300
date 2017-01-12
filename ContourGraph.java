import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Nicholas Vadivelu on 2017-01-11.
 */
public class ContourGraph extends ImageCanvas {
    Color[] rainbow;
    public ContourGraph(int w, int h, int xDim, int yDim, GUI g) {
        super(w, h, xDim, yDim, g);
        rainbow = getFullRainBowScale();
        Color[] temp = getFullRainBowScale();
        for (int i = 0 ; i < temp.length ; i++) {
            rainbow[i] = temp[temp.length-i-1];
        }
    }

    @Override
    public void paintComponent (Graphics g) {
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

    public void updateValue(double sensorValue, int xPos, int yPos) { //updates one value
        int b = (int)(sensorValue*(360-70)*multiplier);
        if (b >= 360-70) b = 360-70-1;
        canvas[xPos][yPos] = rainbow[b];
        curX = xPos;
        curY = yPos;
        repaint();
    }

    public void updateAllValues(double[] sensorValues) {
        for (int i = 0 ; i < xPixels; i++) {
            for (int j = 0 ; j < yPixels; j++) {
                int b = (int) ((360-70)*sensorValues[i + j*xPixels]*multiplier); //CHECK THISS
                if (b >= 360-70) b = 360-70-1;
                canvas[i][j] = rainbow[b];
            }
        }
    }

    private static Color[] getFullRainBowScale()
    {
        // minimum of about 200 to not have perceptible steps in color scale
        // whether or not perceptible color gradients show depend upon the
        // legend size and the monitor settings
        int ncolor = 360;
        Color [] rainbow = new Color[ncolor-70];
        // divide the color wheel up into more than ncolor pieces
        // but don't go all of the way around the wheel, or the first color
        // will repeat.  The 60 value is about a minimum of 40, or the
        // red color will repeat.  Too large a value, and there will be no magenta.
        float x = (float) (1./(ncolor + 60.));
        for (int i=0; i < rainbow.length; i++) {
            rainbow[i] = new Color( Color.HSBtoRGB((i)*x,1.0F,1.0F));
        }
        return rainbow;
    }

    @Override
    public void mouseMoved (MouseEvent e) {

    }
    public void mouseDragged(MouseEvent e) {

    }
}
