package DeepSpaceVision.outputs;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import DeepSpaceVision.IOutput;

/**
 * Sends processed frames to a video file. This is only guaranteed to work with the .avi file extension.
 */
public class VideoOutput implements IOutput {
    private static int FOURCC = VideoWriter.fourcc('M', 'J', 'P', 'G');
    private VideoWriter output;

    public VideoOutput(String fname, double fps, Size size) {
        output = new VideoWriter(fname, FOURCC, fps, size);
    }

    @Override
    public void Write(Mat frame) {
        output.write(frame);
    }

    @Override
    public void close() {
        output.release();
    }
}