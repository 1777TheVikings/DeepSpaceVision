package DeepSpaceVision.sources;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import DeepSpaceVision.Source;

public class MultiImageSource extends Source {
    private ArrayList<Mat> images = new ArrayList<>();
    private int index = 0;

    public MultiImageSource(String[] fnames) {
        for (String fname : fnames)
            images.add(Imgcodecs.imread(fname));
    }

    @Override
    public Mat Read() {
        Mat image = images.get(index);
        index++;
        return image;
    }

    @Override
    public Size GetFrameSize() {
        return images.get(index).size();
    }

    @Override
    public double GetFrameRate() {
        return 1.0;
    }

    @Override
    public boolean HasMoreFrames() {
        return index < images.size();
    }

    @Override
    public void close() {
        // no cleanup necessary
    }
}