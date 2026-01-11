package com.apex.math;

public class MathUtils {
    public static final float EPSILON = 1e-5f;

    public static boolean equals(float a, float b) {
        return Math.abs(a - b) < EPSILON;
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}