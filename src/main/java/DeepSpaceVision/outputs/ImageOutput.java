package DeepSpaceVision.outputs;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import DeepSpaceVision.Output;
import DeepSpaceVision.TargetData;

public class ImageOutput extends Output {
    private Mat output;
    private String fname;

    public ImageOutput(String fname) {
        this.fname = fname;
    }

    @Override
    public void Write(Mat frame, TargetData data) {
        output = frame;
    }

    @Override
    public void close() {
        Imgcodecs.imwrite(fname, output);
    }
}