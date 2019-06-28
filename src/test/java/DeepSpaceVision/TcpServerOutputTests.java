package DeepSpaceVision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static java.time.Duration.ofSeconds;
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

    @DisplayName("Server handles positive data as expected")
    @Test
    public void testServerPositiveData() {
        assertTimeout(ofSeconds(15), () -> {
            try {
                output = new TcpServerOutput(12345);
                socket = new Socket("localhost", 12345);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
                RotatedRect[] data = new RotatedRect[] {
                    new RotatedRect(new Point(200, 300), new Size(50, 50), 0.0),
                    new RotatedRect(new Point(400, 300), new Size(50, 50), 0.0)
                };
                output.Write(null, data);
                Double result = Double.parseDouble(reader.readLine().strip());
                assertEquals(Double.valueOf(300.0), result, "Server did not return the correct value");
    
            } catch (IOException ex) {
                Assertions.fail(ex);
            }
        });
    }

    @AfterEach
    public void tearDownEach() {
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
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}