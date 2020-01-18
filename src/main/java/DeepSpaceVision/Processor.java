package DeepSpaceVision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Processor {
    // setup
    private static Scalar HSV_LOWER_BOUND = new Scalar(40, 100, 100); // Scalars are used to store pixel colors?
    private static Scalar HSV_UPPER_BOUND = new Scalar(100, 255, 255);

    private TargetData.Factory dataFactory;

    public Processor(TargetData.Factory dataFactory) {
        this.dataFactory = dataFactory;
    }

    // main function
    @Nullable
    public TargetData Process(Mat input) {
        Mat hsv = input.clone(); // clone image
        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV); // convert from RGB to HSV
        Core.inRange(hsv, HSV_LOWER_BOUND, HSV_UPPER_BOUND, hsv); //filter HSV values to black and white

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(hsv, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE); // finds shapes(contours) in the image
        Mat contoursImg = input.clone();
        Imgproc.drawContours(contoursImg, contours, -1, new Scalar(255, 0, 0)); // draws contours on image?
        
        ArrayList<MatOfPoint> filteredContours = new ArrayList<>(); // make new array for filtered contours?
        for (MatOfPoint contour : contours) { // filters out contours with too small of a surface area?
            if (Imgproc.contourArea(contour) < 20 * 20) continue;
            
            filteredContours.add(contour);
        }

        List<RotatedRect> rects = filteredContours.stream() // changes all of the contours into rated rectangles and stores them in a variable named rects
            .map(this::ContourToRotatedRect) // changes all contours into a rotated rectangle
            .collect(Collectors.toList()); // puts the new rotated rectangle contours into the list
        for (RotatedRect rect : rects) {
            Predicate<? super RotatedRect> predicate;
            if (rect.angle < -45.0)  // tilted towards right
                predicate = r -> r.angle > -45.0;  // we want one tilted left
            else  // tilted towards left
                predicate = r -> r.angle < -45.0;  // we want one tilted right
            List<RotatedRect> matchingRects = rects.stream() // sorts all of the other rectangles by how close they are to the rectangle chosen above
                .filter(r -> !r.center.equals(rect.center)) // makes sure there aren't multiple contours in the same place
                .filter(predicate)
                .sorted((r1, r2) -> (DistanceFormula(r1.center, rect.center) > DistanceFormula(r2.center, rect.center)) ? 1 : -1)  // predicate ? if_true : if_false
                .collect(Collectors.toList());
            if (matchingRects.size() > 0) {  // we have a match!
                RotatedRect[] outputRects = new RotatedRect[] { rect, matchingRects.get(0) };
                return dataFactory.createTargetData(outputRects);
            }
        }

        return null;
    }

    public Mat DrawOutput(Mat input, TargetData data) {
        Mat output = input.clone();

        // draw target strips
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>(
            Arrays.asList(data.getRects()).stream()
                .map(this::RotatedRectToContour)
                .collect(Collectors.toList()));
        Imgproc.drawContours(output, contours, -1, new Scalar(255, 0, 0));
        
        // draw midline
        Imgproc.line(output, new Point(data.getCenterX(), 0.0), new Point(data.getCenterX(), output.size().height), new Scalar(255, 0, 0));

        // draw angle
        Imgproc.putText(output,
            String.valueOf(data.getAngle()),
            new Point(data.getCenterX() + 25, 75),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            3,
            new Scalar(255, 255, 255)
        );

        return output;
    }

    private RotatedRect ContourToRotatedRect(MatOfPoint contour) {
        MatOfPoint2f convertedContour = new MatOfPoint2f();
        contour.convertTo(convertedContour, CvType.CV_32F);
        return Imgproc.minAreaRect(convertedContour);
    }

    private MatOfPoint RotatedRectToContour(RotatedRect rect) {
        Point[] pointsArr = new Point[4];
        rect.points(pointsArr);
        return new MatOfPoint(pointsArr);
    }

    private double DistanceFormula(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }
}