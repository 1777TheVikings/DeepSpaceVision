package DeepSpaceVision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import DeepSpaceVision.outputs.*;
import DeepSpaceVision.sources.*;

public class App {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        AtomicBoolean isRunning = new AtomicBoolean(true);

        Source source;
        ArrayList<Output> outputs = new ArrayList<>();
        
        if (args.length < 2) {
            System.err.println("Missing arguments for source and output");
            System.exit(1);
        }

        System.out.println("[MAIN] Setting up source...");
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

        System.out.println("[MAIN] Setting up outputs...");
        String[] split = args[1].split(",");
        for (String arg : split) {
            // only .avi file formats are guaranteed to work with OpenCV, so we can safely assume that
            // it's the only option people will use
            if (arg.substring(arg.length() - 4).equals(".avi"))
                outputs.add(new VideoOutput(arg, source.GetFrameRate(), source.GetFrameSize()));
            else {
                try {
                    // check if it's a port number for TCP server
                    int port = Integer.parseInt(arg);
                    outputs.add(new TcpServerOutput(port, () -> {
                        System.out.println("[MAIN] Shutdown call received");
                        isRunning.set(false);
                    }));
                } catch (NumberFormatException ex) {
                    // probably an image output
                    outputs.add(new ImageOutput(arg));
                }
            }
        }

        // politely ask the JVM to call close() on our source/output
        // covers Ctrl+C shutdowns normally, but does not work on Windows when using "gradle run"
        Runtime.getRuntime().addShutdownHook(source.new SourceShutdownHook());
        for (Output output : outputs)
            Runtime.getRuntime().addShutdownHook(output.new OutputShutdownHook());

        TargetData.Factory targetDataFactory = new TargetData.Factory(60.0, source.GetFrameSize().width);
        Processor processor = new Processor(targetDataFactory);

        System.out.println("[MAIN] Starting main loop...");
        while (source.HasMoreFrames() && isRunning.get()) {
            Mat frame = source.Read();
            TargetData outData = processor.Process(frame);
            Mat outFrame = frame;
            if (outData != null)
            {
                outFrame = processor.DrawOutput(frame, outData);  // TODO: Move this somewhere else
                // System.out.println("First center: " + outData[0].center);
                // System.out.println("Second center: " + outData[1].center);
            }
            for (Output output : outputs)
                output.Write(outFrame, outData);
        }

        System.out.println("[MAIN] Cleaning up...");
        try {
            source.close();
            for (Output output : outputs)
                output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
