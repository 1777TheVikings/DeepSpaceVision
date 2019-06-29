package DeepSpaceVision;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.*;
import static org.awaitility.Duration.*;
import static org.hamcrest.Matchers.*;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencv.core.RotatedRect;

public class TcpServerTests {
    @DisplayName("Should respond to positive data")
    @Test
    public void testTcpServerPositiveData() {
        TcpServer serverThread = new TcpServer(50000, () -> {});
        serverThread.start();

        Socket client = null;
        BufferedReader reader = null;

        try {
            client = new Socket("localhost", 50000);
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            RotatedRect[] data = new RotatedRect[] {
                new RotatedRect(),
                new RotatedRect()
            };
            synchronized(serverThread.dataQueue) {
                serverThread.dataQueue.add(data);
            }

            try {
                await().atMost(ONE_SECOND).until(reader::ready);
            } catch (ConditionTimeoutException ex) {}

            assertTrue(reader.ready(), "Server did not respond to positive data");
        } catch (IOException ex) {
            fail(ex);
        } finally {
            try {
                if (reader != null) reader.close();
                if (client != null) client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @DisplayName("Should not respond to negative data")
    @Test
    public void testTcpServerNegativeData() {
        TcpServer serverThread = new TcpServer(50001, () -> {});
        serverThread.start();

        Socket client = null;
        BufferedReader reader = null;

        try {
            client = new Socket("localhost", 50001);
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            RotatedRect[] data = new RotatedRect[0];
            synchronized(serverThread.dataQueue) {
                serverThread.dataQueue.add(data);
            }

            try {
                await().atMost(ONE_SECOND).until(reader::ready);
            } catch (ConditionTimeoutException ex) {}

            assertFalse(reader.ready(), "Server responded to negative data");
        } catch (IOException ex) {
            fail(ex);
        } finally {
            try {
                if (reader != null) reader.close();
                if (client != null) client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @DisplayName("Should stop main loop on 'shutdown'")
    @Test
    public void testTcpServerShutdown() {
        AtomicBoolean wasOnShutdownCalled = new AtomicBoolean(false);
        TcpServer serverThread = new TcpServer(50002, () -> { wasOnShutdownCalled.set(true); });
        serverThread.start();

        Socket client = null;
        BufferedWriter writer = null;

        try {
            client = new Socket("localhost", 50002);
            writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            writer.write("shutdown\n");
            writer.flush();

            try {
                await().atMost(ONE_SECOND).until(client::isClosed, equalTo(false));
            } catch (ConditionTimeoutException ex) {}

            assertFalse(client.isClosed(), "Server has not yet shutdown");
            assertTrue(wasOnShutdownCalled.get(), "Server did not call onShutdown");
        } catch (IOException ex) {
            fail(ex);
        } finally {
            try {
                if (writer != null) writer.close();
                if (client != null) client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}