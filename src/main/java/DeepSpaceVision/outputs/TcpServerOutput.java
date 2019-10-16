package DeepSpaceVision.outputs;

import java.io.IOException;

import org.opencv.core.Mat;

import DeepSpaceVision.Output;
import DeepSpaceVision.TargetData;
import DeepSpaceVision.TcpServer;

public class TcpServerOutput extends Output {
    private TcpServer serverThread;

    public TcpServerOutput(int port, Runnable onShutdown) {
        serverThread = new TcpServer(port, onShutdown);
        serverThread.start();
    }

    @Override
    public void Write(Mat frame, TargetData data) {
        synchronized(serverThread.dataQueue) {
            serverThread.dataQueue.add(data);
        }
    }

    @Override
    public void close() throws IOException {
        if (serverThread.isAlive()) {
            serverThread.interrupt();
            try {
                serverThread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}