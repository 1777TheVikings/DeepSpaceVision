package DeepSpaceVision.outputs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import DeepSpaceVision.Output;

public class TcpServerOutput extends Output {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public TcpServerOutput(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void Write(Mat frame, RotatedRect[] data) {
        try {
            clientSocket = serverSocket.accept();
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        if (data.length > 0) {
            String out = String.valueOf((data[0].center.x + data[1].center.x) / 2.0);
            try {
                writer.write(out + "\n");
                writer.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        clientSocket.close();
        serverSocket.close();
    }
}