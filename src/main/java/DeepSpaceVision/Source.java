package DeepSpaceVision;

import java.io.Closeable;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.Size;

public abstract class Source implements Closeable {
    public class SourceShutdownHook extends Thread {
        public void run() {
            try
            {
                Source.this.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public abstract Mat Read();
    public abstract Size GetFrameSize();
    public abstract double GetFrameRate();
    public abstract boolean HasMoreFrames();
    public abstract void close() throws IOException;
}