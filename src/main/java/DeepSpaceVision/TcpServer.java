package DeepSpaceVision;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TcpServer extends Thread {
    private Thread thread;
    private Runnable onShutdown;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    
    public LinkedList<TargetData> dataQueue = new LinkedList<>();

    public TcpServer(int port, Runnable onShutdown) {
        this.onShutdown = onShutdown;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

	public void start() {
        if (thread == null) {
            thread = new Thread(this, "TcpServer");
            thread.start();
        }
    }

    public void run() {
        try {
            clientSocket = serverSocket.accept();
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            while (true) {
                if (!clientSocket.isConnected()) break;
                if (Thread.interrupted()) break;

                if (reader.ready()) {
                    String in = reader.readLine();
                    if (in.startsWith("shutdown")) {
                        System.out.println("[SERVER] Received shutdown request from client");
                        onShutdown.run();
                        break;
                    }
                }

                synchronized (dataQueue) {
                    TargetData out = dataQueue.poll();
                    if (out != null) {
                        writer.write(String.valueOf(out.getAngle()) + "\n");
                        writer.flush();
                    }
                }
            }

            reader.close();
            writer.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}