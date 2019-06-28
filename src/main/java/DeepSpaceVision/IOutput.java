package DeepSpaceVision;

import java.io.Closeable;

import org.opencv.core.Mat;

public interface IOutput extends Closeable {
    public void Write(Mat frame);
}