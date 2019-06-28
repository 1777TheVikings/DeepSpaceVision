package DeepSpaceVision;

import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import DeepSpaceVision.outputs.*;
import DeepSpaceVision.sources.*;

public class App {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        ISource source;
        IOutput output;
        
        if (args.length < 2) {
            System.err.println("Missing arguments for source and output");
            System.exit(1);
        }

        try {
            // check for webcam ID
            int cameraNum = Integer.parseInt(args[0]);
            source = new WebcamSource(cameraNum);
        } catch (NumberFormatException ex) {
            // probably a file or list of files instead
            String[] split = args[0].split(",");
            if (split.length > 1)
                source = new MultiImageSource(split);
            else
                source = new ImageSource(args[0]);
        }

        // only .avi file formats are guaranteed to work with OpenCV, so we can safely assume that
        // it's the only option people will use
        if (args[1].substring(args[1].length() - 4).equals(".avi"))
            output = new VideoOutput(args[1], source.GetFrameRate(), source.GetFrameSize());
        else
            output = new ImageOutput(args[1]);
        
        Processor processor = new Processor();
        
        try {
            while (source.HasMoreFrames()) {
                Mat frame = source.Read();
                RotatedRect[] outData = processor.Process(frame);
                Mat outFrame = frame;
                if (outData.length > 0)
                {
                    outFrame = processor.DrawOutput(frame, outData);
                    System.out.println("First center: " + outData[0].center);
                    System.out.println("Second center: " + outData[1].center);
                }
                output.Write(outFrame);
            }
        } finally {
            try {
                source.close();
                output.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
