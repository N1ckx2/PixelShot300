import gnu.io.*;
import java.io.*;

/**
 * Created by Nicholas Vadivelu on 2017-01-04.
 */

//This class will be used to test the functionality of the RxT
//Download here: http://rxtx.qbang.org/wiki/index.php/Download

public class Testing implements SerialPortEventListener {
    private SerialPort serialPort;

    /** Streams */
    private InputStream serialIn;
    private OutputStream serialOut;
    private BufferedReader serialReader;

    public Testing() throws Exception{
        //Open port
        CommPortIdentifier port = CommPortIdentifier.getPortIdentifier("COM4");
        CommPort commPort = port.open(this.getClass().getName(),2000);

        serialPort = (SerialPort) commPort; //cast communication port to serial port

        //set up serial point using same speed as Arduino
        serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        //set up input, output, and reader streams
        serialIn=serialPort.getInputStream();
        serialOut=serialPort.getOutputStream();
        serialReader = new BufferedReader( new InputStreamReader(serialIn) );

        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);

        //turn on LED 1:
        while (true) {
            try {
                serialOut.write("1".getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Thread.sleep(1000);

            try {
                serialOut.write("0".getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void main (String[] args) throws Exception {
        new Testing();
    }

    //info sent by Arduino is taken here
    public void serialEvent(SerialPortEvent e) {
        try {
            String line = serialReader.readLine(); //read string from serial port
            System.out.println(line); //print out this line
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
