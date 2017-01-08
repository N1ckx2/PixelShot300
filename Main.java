import gnu.io.*;

import javax.swing.*;
import java.io.*;

/**
 * Created by Nicholas Vadivelu on 2017-01-06.
 */
public class Main implements Runnable{
    private SerialPort serialPort;
    private volatile boolean stop = false;
    /** Streams */
    private InputStream serialIn;
    private OutputStream serialOut;
    private BufferedReader serialReader;

    double[] sensorValues;
    int sensorValIndex = -1;

    //GUI Elements that need to be updated
    GUI gui;

    int dimension;

    public Main(String com, int dim, GUI gui) throws Exception {

        dimension = dim;
        sensorValues = new double[dimension*dimension];
        /*
        //Open port
        CommPortIdentifier port = CommPortIdentifier.getPortIdentifier(com); //COM3 for mega, COM4 for UNO
        CommPort commPort = port.open(this.getClass().getName(), 2000);

        serialPort = (SerialPort) commPort; //cast communication port to serial port

        //set up serial point using same speed as Arduino
        serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        //set up input, output, and reader streams
        serialIn = serialPort.getInputStream();
        serialOut = serialPort.getOutputStream();
        serialReader = new BufferedReader(new InputStreamReader(serialIn));

        //serialPort.notifyOnDataAvailable(true);
        */

        //fill up sensor values array
        for (int i = 0 ; i < sensorValues.length; i++){
            sensorValues[i] = 0;
        }

        //assign GUI values
        this.gui = gui;

    }

    public void run() {
        while (!stop){
            boolean dir = true; //dir  true is CW, false is CCW
            for (int i = 0; i < dimension && !stop; i++) { //loops through all the y positions
                for (int j = (dir ? 0 : dimension - 1); !stop && (dir ? j < dimension : j >= 0); j += (dir ? 1 : -1)) { //loops through all the x positions
                    double temp = step(j, i, dir);
                    gui.updateUI(i, j, temp);
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }
                }
                dir = !dir; //flips direction of the NEMA8 after each horizontal sweep
            }
            gui.doneRunning();
        }
    }

    public double step(int x, int y, boolean dir) { //true = cw, false = ccw
        if (sensorValIndex == sensorValues.length-1)
            sensorValIndex = -1;
        sensorValues[++sensorValIndex] = Math.random();
        return sensorValues[sensorValIndex];

        /* Will be used for the legit program
        try {
            String line = serialReader.readLine(); //read string from serial port
            sensorValues[++sensorValIndex] = Integer.parseInt(line)/1023;
        } catch (IOException ex) {
            return -1;
        }
        double direction = dir ? 0.5 : -0.5;
        String toSend = "";
        toSend = toSend + (x == 1 ? "0" : Integer.toString((int)(1.5 + direction))); //nema8 movement
        toSend = toSend + (y == 1 ? "1" : "0"); //nema17 movement
        toSend = toSend + String.format("%04d", y); //x position
        toSend = toSend + String.format("%04d", x); //y position
        toSend = toSend + String.format("%04d", sensorValues[sensorValIndex]*1000); //brightness value
        try {
            serialOut.write(toSend.getBytes());
        } catch (IOException e1) {
            return -1;
        }

        return sensorValues[sensorValIndex];*/
    }
    public static void main (String[] args) throws Exception {
        new GUI();
    }

    public double[] getSensorValues() {return sensorValues;} //getter method for the sensor values

    public void stop() {
        stop = true;
    }
}
