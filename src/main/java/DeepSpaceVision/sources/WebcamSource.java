package DeepSpaceVision.sources;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;

import DeepSpaceVision.ISource;

public class WebcamSource implements ISource {
    private VideoCapture cam;
    private boolean rval;

    public WebcamSource(int camNumber) {
        cam = new VideoCapture(camNumber);
        rval = cam.read(new Mat());
    }

    public WebcamSource(int camNumber, Size frameSize, int fps) {
        cam = new VideoCapture(camNumber);
        cam.set(3, frameSize.width);
        cam.set(4, frameSize.height);
        cam.set(5, fps);
        rval = cam.read(new Mat());
    }

    @Override
    public Mat Read() {
        Mat output = new Mat();
        rval = cam.read(output);
        return output;
    }

    @Override
    public Size GetFrameSize() {
        return new Size(cam.get(3), cam.get(4));
    }

    @Override
    public boolean HasMoreFrames() {
        return rval;
    }

    @Override
    public void close() {
        cam.release();
    }
}