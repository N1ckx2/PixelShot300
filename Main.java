import gnu.io.*;

import java.io.*;

/**
 * Created by Nicholas Vadivelu on 2017-01-06.
 */
public class Main implements SerialPortEventListener {
    private SerialPort serialPort;

    /** Streams */
    private InputStream serialIn;
    private OutputStream serialOut;
    private BufferedReader serialReader;

    double[] sensorValues = new double[300*300];
    int sensorValIndex = -1;

    public Main() throws Exception {
        //Open port
        CommPortIdentifier port = CommPortIdentifier.getPortIdentifier("COM3"); //COM3 for mega, COM4 for UNO
        CommPort commPort = port.open(this.getClass().getName(), 2000);

        serialPort = (SerialPort) commPort; //cast communication port to serial port

        //set up serial point using same speed as Arduino
        serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        //set up input, output, and reader streams
        serialIn = serialPort.getInputStream();
        serialOut = serialPort.getOutputStream();
        serialReader = new BufferedReader(new InputStreamReader(serialIn));

        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);

        double dir = 0.5; //dir  +0.5 is CW, -0.5 is neg
        for (int i = 1; i <= 300; i++){ //loops through all the y positions
            for (int j = 1; j <=300; j++) { //loops through all the x positions
                String toSend = "";
                toSend = toSend + (j == 1 ? "0" : Integer.toString((int)(1.5 + dir))); //nema8 movement
                toSend = toSend + (j == 1 ? "1" : "0"); //nema17 movement
                toSend = toSend + String.format("%04d", j); //x position
                toSend = toSend + String.format("%04d", i); //y position
                toSend = toSend + String.format("%04d", sensorValues[sensorValIndex]*1000); //brightness value
                try {
                    serialOut.write(toSend.getBytes());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            dir *= -1;
        }

        for (int i = 0 ; i < 300; i++){ //rudimentary display to show all the brightness values in a table format
            for (int j = 0 ; j < 300 ; j ++) {
                System.out.print(sensorValues[i*300+j] + " ");
            }
            System.out.println();
        }
    }
    public static void main (String[] args) throws Exception {
        new Main();
    }

    //info sent by Arduino is taken here
    public void serialEvent(SerialPortEvent e) {
        try {
            String line = serialReader.readLine(); //read string from serial port
            sensorValues[++sensorValIndex] = Integer.parseInt(line)/1023;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
