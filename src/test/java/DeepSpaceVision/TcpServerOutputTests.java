package DeepSpaceVision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.awaitility.Duration;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofSeconds;

import static org.awaitility.Awaitility.*;

import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;

import DeepSpaceVision.outputs.TcpServerOutput;

public class TcpServerOutputTests {
    private TcpServerOutput output;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    @BeforeAll
    public static void setUpAll() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @DisplayName("Server responds to positive data")
    @Test
    public void testServerPositiveData() {
        System.out.println("pos");
        assertTimeoutPreemptively(ofSeconds(15), () -> {
            try {
                output = new TcpServerOutput(50000);
                socket = new Socket("localhost", 50000);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
                RotatedRect[] data = new RotatedRect[] {
                    new RotatedRect(new Point(200, 300), new Size(50, 50), 0.0),
                    new RotatedRect(new Point(400, 300), new Size(50, 50), 0.0)
                };
                output.Write(null, data);
                try {
                    await().atMost(Duration.ONE_SECOND).until(reader::ready);
                } catch (ConditionTimeoutException ex) { }
                assertTrue(reader.ready(), "Server did not respond to positive data");
    
            } catch (IOException ex) {
                Assertions.fail(ex);
            }
        });
    }

    @DisplayName("Server does not respond to negative data")
    @Test
    public void testServerNegativeData() {
        System.out.println("neg");
        assertTimeoutPreemptively(ofSeconds(15), () -> {
            try {
                output = new TcpServerOutput(50000);
                socket = new Socket("localhost", 50000);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
                RotatedRect[] data = new RotatedRect[0];
                output.Write(null, data);
                try {
                    await().atMost(Duration.ONE_SECOND).until(reader::ready);
                } catch (ConditionTimeoutException ex) { }
                assertFalse(reader.ready(), "Server did not respond to positive data");
    
            } catch (IOException ex) {
                Assertions.fail(ex);
            }
        });
    }

    @AfterEach
    public void tearDownEach() {
        System.out.println("tearing down");
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
            if (output != null) {
                output.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}