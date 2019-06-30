package DeepSpaceVision.outputs;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;
import org.opencv.imgcodecs.Imgcodecs;

import DeepSpaceVision.Output;

public class ImageOutput extends Output {
    private Mat output;
    private String fname;

    public ImageOutput(String fname) {
        this.fname = fname;
    }

    @Override
    public void Write(Mat frame, RotatedRect[] data) {
        output = frame;
    }

    @Override
    public void close() {
        Imgcodecs.imwrite(fname, output);
    }
}