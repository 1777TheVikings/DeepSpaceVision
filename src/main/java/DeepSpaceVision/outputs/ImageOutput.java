package DeepSpaceVision.outputs;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import DeepSpaceVision.IOutput;

public class ImageOutput implements IOutput {
    private Mat output;
    private String fname;

    public ImageOutput(String fname) {
        this.fname = fname;
    }

    @Override
    public void Write(Mat frame) {
        output = frame;
    }

    @Override
    public void close() {
        Imgcodecs.imwrite(fname, output);
    }
}