package DeepSpaceVision;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import DeepSpaceVision.outputs.*;
import DeepSpaceVision.sources.*;

public class App {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Source source;
        Output output;
        
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

        // output = new TcpServerOutput(12345);

        // politely ask the JVM to call close() on our source/output
        // covers Ctrl+C shutdowns normally, but does not work on Windows when using "gradle run"
        Runtime.getRuntime().addShutdownHook(source.new SourceShutdownHook());
        Runtime.getRuntime().addShutdownHook(output.new OutputShutdownHook());

        AtomicBoolean isRunning = new AtomicBoolean(true);

        Processor processor = new Processor();
        TcpServer server = new TcpServer(5800, () -> { isRunning.set(true); });
        server.start();

        while (source.HasMoreFrames() && isRunning.get()) {
            Mat frame = source.Read();
            RotatedRect[] outData = processor.Process(frame);
            Mat outFrame = frame;
            if (outData.length > 0)
            {
                outFrame = processor.DrawOutput(frame, outData);
                System.out.println("First center: " + outData[0].center);
                System.out.println("Second center: " + outData[1].center);
            }
            output.Write(outFrame, outData);
            server.dataQueue.add(outData);
        }
        try {
            source.close();
            output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
