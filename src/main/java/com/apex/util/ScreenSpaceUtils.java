package com.apex.util;


import com.apex.math.Vector3f;

public class ScreenSpaceUtils {
    public static Point2f toScreenSpace(double x, double y, int width, int height) {
        float screenX = (float) ((x + 1.0) * 0.5 * width);
        float screenY = (float) ((1.0 - (y + 1.0) * 0.5) * height);
        return new Point2f(screenX, screenY);
    }


    public static ScreenPoint toScreenPoint(double x, double y, double z, int width, int height) {
        float screenX = (float) ((x + 1.0) * 0.5 * width);
        float screenY = (float) ((1.0 - (y + 1.0) * 0.5) * height);
        return new ScreenPoint(screenX, screenY, (float) z);
    }

    public static class ScreenSpace {
        public static Point2f project(Vector3f vertex, int width, int height) {
            return new Point2f(
                    vertex.getX() * width + width / 2.0f,
                    -vertex.getY() * height + height / 2.0f
            );
        }
    }


    public static class Point2f {
        public final float x, y;

        public Point2f(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("Point2f(%.2f, %.2f)", x, y);
        }
    }


    public static class ScreenPoint extends Point2f {
        public final float z;

        public ScreenPoint(float x, float y, float z) {
            super(x, y);
            this.z = z;
        }

        @Override
        public String toString() {
            return String.format("ScreenPoint(%.2f, %.2f, %.2f)", x, y, z);
        }
    }
}