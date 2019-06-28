package DeepSpaceVision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;
import org.opencv.imgcodecs.Imgcodecs;

public class ProcessorTests
 {
    private Processor processor;

    @BeforeAll
    public static void setUpAll() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @BeforeEach
    public void setUpEach() {
        processor = new Processor();
    }

    @DisplayName("Should detect two RotatedRects in a valid image")
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {
        "src/test/resources/camtest1.jpg",
        "src/test/resources/camtest2.jpg",
        "src/test/resources/camtest3.jpg"
    })
    public void testPositiveDetection(String fname) {
        Mat frame = Imgcodecs.imread(fname);
        RotatedRect[] outData = processor.Process(frame);
        assertEquals(2, outData.length, "Failed on " + fname);
    }

    @DisplayName("Should not detect any RotatedRects in an invalid image")
    @Test
    public void testNegativeDetection() {
        Mat frame = Imgcodecs.imread("src/test/resources/fail.jpg");
        RotatedRect[] outData = processor.Process(frame);
        assertEquals(0, outData.length);
    }

    @DisplayName("Should detect RotatedRects with +-10px accuracy")
    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource({
        "src/test/resources/camtest1.jpg,239,377,328,382",
        "src/test/resources/camtest2.jpg,322,229,227,231",
        "src/test/resources/camtest3.jpg,406,156,306,159"
    })
    public void testPositionAccuracy(String fname, int x1, int y1, int x2, int y2) {
        Mat frame = Imgcodecs.imread(fname);
        RotatedRect[] outData = processor.Process(frame);

        assertTrue(outData[0].center.x > x1 - 10);
        assertTrue(outData[0].center.x < x1 + 10);
        assertTrue(outData[0].center.y > y1 - 10);
        assertTrue(outData[0].center.y < y1 + 10);

        assertTrue(outData[1].center.x > x2 - 10);
        assertTrue(outData[1].center.x < x2 + 10);
        assertTrue(outData[1].center.y > y2 - 10);
        assertTrue(outData[1].center.y < y2 + 10);
    }
}
