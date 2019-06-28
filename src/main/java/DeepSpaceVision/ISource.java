package DeepSpaceVision;

import java.io.Closeable;

import org.opencv.core.Mat;
import org.opencv.core.Size;

public interface ISource extends Closeable {
    public Mat Read();
    public Size GetFrameSize();
    public boolean HasMoreFrames();
}