package com.apex.math;

import com.apex.tool.rasterization.VertexAttributeExtended;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MathUtil {
    public static final double EPSILON = 1e-10;

    public static double[] solveByKramer(double a, double b, double c, double d, double v1, double v2) {
        double deltaMain = calcDetermination(
                a, b,
                c, d
        );
        if (deltaMain == 0) return new double[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
        double delta1 = calcDetermination(
                v1, b,
                v2, d);
        double delta2 = calcDetermination(
                a, v1,
                c, v2
        );
        return new double[]{delta1 / deltaMain, delta2 / deltaMain};
    }

    private static double calcDetermination(double a11, double a12, double a21, double a22) {
        return a11 * a22 - a12 * a21;
    }

    public static double calcSquareByGeroneByVertices(double x0, double y0, double x1, double y1, double x2, double y2) {
        double AB = sqrt(pow(x0 - x1, 2) + pow(y0 - y1, 2));
        double AC = sqrt(pow(x0 - x2, 2) + pow(y0 - y2, 2));
        double BC = sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2));

        double semiPerimeter = (AB + AC + BC) / 2;
        return sqrt(semiPerimeter * (semiPerimeter - AB) * (semiPerimeter - AC) * (semiPerimeter - BC));
    }

    public static double findThirdOrderDeterminant(
            double a00, double a01, double a02,
            double a10, double a11, double a12,
            double a20, double a21, double a22
    ) {
        return ((a00 * a11 * a22) + (a10 * a21 * a02) + (a01 * a12 * a20)) - ((a02 * a11 * a20) + (a01 * a10 * a22) + (a12 * a21 * a00));
    }

    public static void findBarycentricCords(double[] barycentric, float xCur, float yCur, float x0, float y0, float x1, float y1, float x2, float y2) {
        double mainDet = findThirdOrderDeterminant(
                x0, x1, x2,
                y0, y1, y2,
                1, 1, 1
        );
        if (mainDet == 0) {
            barycentric[0] = 0;
            barycentric[1] = 0;
            barycentric[2] = 0;
            return;
        }

        double detForAlpha = findThirdOrderDeterminant(
                xCur, x1, x2,
                yCur, y1, y2,
                1, 1, 1
        );
        double detForBeta = findThirdOrderDeterminant(
                x0, xCur, x2,
                y0, yCur, y2,
                1, 1, 1
        );
        double detForLambda = findThirdOrderDeterminant(
                x0, x1, xCur,
                y0, y1, yCur,
                1, 1, 1
        );
        barycentric[0] = detForAlpha / mainDet;
        barycentric[1] = detForBeta / mainDet;
        barycentric[2] = detForLambda / mainDet;
    }

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double findZFromBarycentric(double[] barycentric, double z0, double invW0, double z1, double invW1,
                                              double z2, double invW2) {
        double invW = barycentric[0] * invW0 + barycentric[1] * invW1 + barycentric[2] * invW2;
        double zOverW = barycentric[0] * z0 * invW0 + barycentric[1] * z1 * invW1 + barycentric[2] * z2 * invW2;
        return zOverW / invW;
    }

    public static int getClosestVertexIndexByBarycentric(double[] barycentric, VertexAttributeExtended v0AE, VertexAttributeExtended v1AE, VertexAttributeExtended v2AE) {
        if (barycentric[0] >= barycentric[1] && barycentric[0] >= barycentric[2]) return v0AE.vertexIndex;
        if (barycentric[1] >= barycentric[2]) return v1AE.vertexIndex;
        return v2AE.vertexIndex;
    }
}
