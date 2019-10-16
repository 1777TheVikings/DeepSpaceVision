package DeepSpaceVision;

import org.opencv.core.RotatedRect;

public class TargetData {
    private RotatedRect[] rects;
    private double centerX;
    private double angle;

    public TargetData(RotatedRect[] rects, double fov, double frameWidth) {
        this.rects = rects;

        this.centerX = (rects[0].center.x + rects[1].center.x) / 2.0;
        
        double degreesPerPixel = fov / frameWidth;
        this.angle = (centerX - (frameWidth / 2.0)) * degreesPerPixel;
    }

    public RotatedRect[] getRects() {
        return this.rects;
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getAngle() {
        return this.angle;
    }

    public static class Factory {
        private double fov;
        private double frameWidth;

        public Factory(double fov, double frameWidth) {
            this.fov = fov;
            this.frameWidth = frameWidth;
        }

        public TargetData createTargetData(RotatedRect[] rects) {
            return new TargetData(rects, this.fov, this.frameWidth);
        }
    }
}