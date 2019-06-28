package DeepSpaceVision;

import java.io.Closeable;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

public abstract class Output implements Closeable {
    public class OutputShutdownHook extends Thread {
        public void run() {
            try
            {
                Output.this.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public abstract void Write(Mat frame, RotatedRect[] data);
    public abstract void close() throws IOException;
}