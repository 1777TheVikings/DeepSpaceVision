package DeepSpaceVision.sources;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import DeepSpaceVision.Source;

public class ImageSource extends Source {
    private Mat image;
    private boolean hasRead = false;

    public ImageSource(String fname) {
        image = Imgcodecs.imread(fname);
    }

    @Override
    public Mat Read() {
        hasRead = true;
        return image;
    }

    @Override
    public Size GetFrameSize() {
        return image.size();
    }

    @Override
    public double GetFrameRate() {
        return 1.0;
    }

    @Override
    public boolean HasMoreFrames() {
        return !hasRead;
    }

    @Override
    public void close() {
        // no cleanup necessary
    }
}