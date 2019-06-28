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
        if (args.length == 1) {
            source = new ImageSource(args[0]);
        } else if (args.length >= 2) {
            source = new MultiImageSource(args);
        } else {
            source = new ImageSource("");
        }
        Processor processor = new Processor();
        IOutput output = new ImageOutput("output.jpg");

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

        try {
            source.close();
            output.close();
        } catch (IOException ex) {
            System.out.println("Error when closing ISource");
            ex.printStackTrace();
        }
    }
}
