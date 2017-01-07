import gnu.io.*;

import java.io.*;

/**
 * Created by Nicholas Vadivelu on 2017-01-06.
 */
public class Main{
    private SerialPort serialPort;

    /** Streams */
    private InputStream serialIn;
    private OutputStream serialOut;
    private BufferedReader serialReader;

    double[] sensorValues = new double[300*300];
    int sensorValIndex = -1;

    public Main(String com) throws Exception {
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

        //fill up sensor values array
        for (int i = 0 ; i < sensorValues.length; i++){
            sensorValues[i] = 0;
        }
    }

    public double step(int x, int y, boolean dir) { //true = cw, false = ccw
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

        return sensorValues[sensorValIndex];
    }
    public static void main (String[] args) throws Exception {
        new GUI();
    }

    public double[] getSensorValues() {return sensorValues;} //getter method for the sensor values
}
