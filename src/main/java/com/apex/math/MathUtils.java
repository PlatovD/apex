package com.apex.math;

public class MathUtils {
    public static final double EPSILON = 1e-10;

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
