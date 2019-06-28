package DeepSpaceVision.outputs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import DeepSpaceVision.Output;

public class TcpServerOutput extends Output {
    public class TcpServer extends Thread {
        private LinkedList<RotatedRect[]> dataQueue = new LinkedList<>();
        private ServerSocket serverSocket;
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;

        public TcpServer(int port) {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException ex) {
                System.err.println("Could not open server socket on port " + port);
                ex.printStackTrace();
                System.exit(1);
                return;
            }
        }

        @Override
        public void run() {
            boolean running = true;
            while (running) {
                if (Thread.interrupted()) {
                    running = false;
                    continue;
                }

                try {
                    socket = serverSocket.accept();
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    
                    boolean connected = socket.isConnected();
                    while (connected) {
                        if (Thread.interrupted()) {
                            running = false;
                            connected = false;
                            continue;
                        }

                        connected = socket.isConnected();
                        if (reader.ready())
                        {
                            String in = reader.readLine();
                            if (in.equals("shutdown")) {
                                connected = false;
                                // do something here
                                continue;
                            }
                        }
                        
                        RotatedRect[] dataToSend = dataQueue.poll();
                        if (dataToSend == null || dataToSend.length == 0)
                            continue;
                        String out = String.valueOf((dataToSend[0].center.x + dataToSend[1].center.x) / 2.0);
                        writer.write(out);
                    }
    
                    reader.close();
                    writer.close();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
        }

        public void addToQueue(RotatedRect[] data) {
            if (dataQueue.size() < 3)  // prevents data buildup
                dataQueue.add(data);
        }

        public void cleanup() {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private TcpServer server;

    public TcpServerOutput(int port) {
        server = new TcpServer(port);
        server.start();
    }

    @Override
    public void Write(Mat frame, RotatedRect[] data) {
        if (server.isAlive())
            server.addToQueue(data);
    }

    @Override
    public void close() throws IOException {
        server.interrupt();
        server.cleanup();
    }
    
}