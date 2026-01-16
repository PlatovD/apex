package com.apex.tool.rasterization;

import com.apex.buffer.RasterizationBuffer;
import com.apex.buffer.CustomIntArrayBasedRasterizationBuffer;
import com.apex.core.Constants;
import com.apex.math.Vector3f;
import com.apex.model.scene.ZBuffer;
import com.apex.model.texture.Texture;
import com.apex.tool.colorization.ColorData;
import com.apex.tool.colorization.ColorProvider;
import com.apex.tool.light.LightProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apex.math.MathUtil.findBarycentricCords;
import static java.lang.Math.*;

public class Rasterization {
    public static void drawTriangle(
            RasterizationBuffer rb, ZBuffer zBuffer,
            Vector3f light, LightProvider lightProvider, // about light
            ColorData colorData, ColorProvider cp, Texture texture, // about color
            VertexAttribute v0A, VertexAttribute v1A, VertexAttribute v2A,
            double[] barycentric
    ) {
        if (v0A.y > v1A.y) {
            v0A.swapWith(v1A);
        }
        if (v1A.y > v2A.y) {
            v1A.swapWith(v2A);
        }
        if (v0A.y > v1A.y) {
            v0A.swapWith(v1A);
        }

        int x0 = v0A.x;
        int y0 = v0A.y;
        double z0 = v0A.z;
        float u0 = v0A.u;
        float v0 = v0A.v;
        float n_x0 = v0A.n_x;
        float n_y0 = v0A.n_y;
        float n_z0 = v0A.n_z;

        int x1 = v1A.x;
        int y1 = v1A.y;
        double z1 = v1A.z;
        float u1 = v1A.u;
        float v1 = v1A.v;
        float n_x1 = v1A.n_x;
        float n_y1 = v1A.n_y;
        float n_z1 = v1A.n_z;

        int x2 = v2A.x;
        int y2 = v2A.y;
        double z2 = v2A.z;
        float u2 = v2A.u;
        float v2 = v2A.v;
        float n_x2 = v2A.n_x;
        float n_y2 = v2A.n_y;
        float n_z2 = v2A.n_z;

        double invW0 = v0A.invW;
        double invW1 = v1A.invW;
        double invW2 = v2A.invW;

        double uOverW0 = v0A.uOverW;
        double uOverW1 = v1A.uOverW;
        double uOverW2 = v2A.uOverW;

        double vOverW0 = v0A.vOwerW;
        double vOverW1 = v1A.vOwerW;
        double vOverW2 = v2A.vOwerW;

        colorData.uOverW0 = uOverW0;
        colorData.uOverW1 = uOverW1;
        colorData.uOverW2 = uOverW2;
        colorData.vOverW0 = vOverW0;
        colorData.vOverW1 = vOverW1;
        colorData.vOverW2 = vOverW2;
        colorData.invW0 = invW0;
        colorData.invW1 = invW1;
        colorData.invW2 = invW2;


        int minX = min(x0, min(x1, x2));
        int maxX = max(x0, max(x1, x2));
        if (y0 == y1 && y1 == y2) {
            for (int x = minX; x <= max(x0, max(x1, x2)); x++) {
                findBarycentricCords(barycentric, x, y0, x0, y0, x1, y1, x2, y2);
                if (barycentric[0] > -0.0001f && barycentric[1] > -0.0001f && barycentric[2] > -0.0001f) {
                    double pixelZ = findZFromBarycentric(barycentric, z0, invW0, z1, invW1, z2, invW2);
                    double lightFactor = lightProvider.calcLightFactor(n_x0, n_x1, n_x2, n_y0, n_y1, n_y2, n_z0, n_z1, n_z2, light, barycentric);
                    if (zBuffer.setPixel(x, y0, pixelZ)) {
                        colorData.lightFactor = Math.max(lightFactor, Constants.MIN_LIGHT_FACTOR);
                        colorData.barycentric = barycentric;
                        rb.setPixel(x, y0, cp.getColor(colorData, texture));
                    }
                }
            }
        }

        double dxEvenlyDistributedLong = y2 - y0 != 0 ? (double) (x2 - x0) / (y2 - y0) : 0;
        double dxEvenlyDistributedShort1 = y1 - y0 != 0 ? (double) (x1 - x0) / (y1 - y0) : 0;
        double dxEvenlyDistributedShort2 = y2 - y1 != 0 ? (double) (x2 - x1) / (y2 - y1) : 0;
        double longSide = x0;
        double shortSide = x0;
        double nextLongSide = longSide;
        double nextShortSide = shortSide;
        for (int y = y0; y <= y1; y++) {
            nextLongSide = longSide + dxEvenlyDistributedLong;
            nextShortSide = shortSide + dxEvenlyDistributedShort1;

            int xStart = (int) Math.floor(min(shortSide, longSide));
            int xEnd = (int) Math.ceil(max(shortSide, longSide));
            if (y != y1) {
                if (longSide > shortSide) {
                    if (longSide + 1 < nextLongSide && longSide + 1 < nextShortSide) {
                        xEnd = (int) Math.floor(min(nextLongSide, nextShortSide));
                    } else if (shortSide - 1 > nextLongSide && shortSide - 1 > nextShortSide) {
                        xStart = (int) Math.ceil(max(nextLongSide, nextShortSide));
                    }
                } else {
                    if (shortSide + 1 < nextLongSide && shortSide + 1 < nextShortSide) {
                        xEnd = (int) Math.floor(min(nextLongSide, nextShortSide));
                    } else if (longSide - 1 > nextLongSide && longSide - 1 > nextShortSide) {
                        xStart = (int) Math.ceil(max(nextLongSide, nextShortSide));
                    }
                }
            }
            xStart = max(xStart, minX);
            xEnd = min(xEnd, maxX);
            for (int x = xStart; x <= xEnd; x++) {
                findBarycentricCords(barycentric, x, y, x0, y0, x1, y1, x2, y2);
                if (barycentric[0] > -0.0001f && barycentric[1] > -0.0001f && barycentric[2] > -0.0001f) {
                    double pixelZ = findZFromBarycentric(barycentric, z0, invW0, z1, invW1, z2, invW2);
                    double lightFactor = lightProvider.calcLightFactor(n_x0, n_x1, n_x2, n_y0, n_y1, n_y2, n_z0, n_z1, n_z2, light, barycentric);
                    if (zBuffer.setPixel(x, y, pixelZ)) {
                        colorData.lightFactor = Math.max(lightFactor, Constants.MIN_LIGHT_FACTOR);
                        colorData.barycentric = barycentric;
                        rb.setPixel(x, y, cp.getColor(colorData, texture));
                    }
                }
            }

            longSide = nextLongSide;
            shortSide = nextShortSide;
        }
        shortSide = x1;
        longSide -= dxEvenlyDistributedLong;
        for (int y = y1; y <= y2; y++) {
            nextLongSide = longSide + dxEvenlyDistributedLong;
            nextShortSide = shortSide + dxEvenlyDistributedShort2;

            int xStart = (int) Math.floor(min(shortSide, longSide));
            int xEnd = (int) Math.ceil(max(shortSide, longSide));

            if (y != y2) {
                if (longSide > shortSide) {
                    if (longSide + 1 < nextLongSide && longSide + 1 < nextShortSide) {
                        xEnd = (int) Math.floor(min(nextLongSide, nextShortSide));
                    } else if (shortSide - 1 > nextLongSide && shortSide - 1 > nextShortSide) {
                        xStart = (int) Math.ceil(max(nextLongSide, nextShortSide));
                    }
                } else {
                    if (shortSide + 1 < nextLongSide && shortSide + 1 < nextShortSide) {
                        xEnd = (int) Math.floor(min(nextLongSide, nextShortSide));
                    } else if (longSide - 1 > nextLongSide && longSide - 1 > nextShortSide) {
                        xStart = (int) Math.ceil(max(nextLongSide, nextShortSide));
                    }
                }
            }
            xStart = max(xStart, minX);
            xEnd = min(xEnd, maxX);
            for (int x = xStart; x <= xEnd; x++) {
                findBarycentricCords(barycentric, x, y, x0, y0, x1, y1, x2, y2);
                if (barycentric[0] > -0.0001f && barycentric[1] > -0.0001f && barycentric[2] > -0.0001f) {
                    double pixelZ = findZFromBarycentric(barycentric, z0, invW0, z1, invW1, z2, invW2);
                    double lightFactor = lightProvider.calcLightFactor(n_x0, n_x1, n_x2, n_y0, n_y1, n_y2, n_z0, n_z1, n_z2, light, barycentric);
                    if (zBuffer.setPixel(x, y, pixelZ)) {
                        colorData.lightFactor = Math.max(lightFactor, Constants.MIN_LIGHT_FACTOR);
                        colorData.barycentric = barycentric;
                        rb.setPixel(x, y, cp.getColor(colorData, texture));
                    }
                }

            }

            longSide = nextLongSide;
            shortSide = nextShortSide;
        }
    }

    private static double findZFromBarycentric(double[] barycentric, double z0, double invW0, double z1, double invW1, double z2, double invW2) {
        double invW = barycentric[0] * invW0 + barycentric[1] * invW1 + barycentric[2] * invW2;
        double zOverW = barycentric[0] * z0 * invW0 + barycentric[1] * z1 * invW1 + barycentric[2] * z2 * invW2;
        return zOverW / invW;
    }

    public static Map<Integer, List<Integer>> myBresenhamOneY(int x0, int y0, int x1, int y1) {
        Map<Integer, List<Integer>> points = new HashMap<>();
        int dx = abs(x0 - x1);
        int dy = abs(y0 - y1);

        int x = x0, y = y0;
        int error = 0;
        int stepX = 1;
        int stepY = 1;
        if (x1 - x0 < 0) {
            stepX *= -1;
        }
        if (y1 - y0 < 0) {
            stepY *= -1;
        }
        if (dx > dy) {
            // тогда я меняю на каждой итерации x, а для y коплю ошибки
            // тогда я могу равномерно разделить подъем, когда иду по y. То есть на каждом шаге по x надо на какое то нецелое
            // число, которое меньше нуля (dx > dy) менять y. Но это не целое. Поэтому я умножу все на dx,а потом из за
            // того, что 0.5 - ошибка при которой я должен закрасить клетку сверху - я буду умножать еще на два
            for (int i = 0; i <= dx; i++) {
                if (points.containsKey(y)) {
                    points.get(y).add(x);
                } else {
                    points.put(y, new ArrayList<>());
                    points.get(y).add(x);
                }
                error += 2 * dy;
                if (error > dx) {
                    y += stepY;
                    error = -(2 * dx - error);
                }
                x += stepX;
            }
        } else {
            // тогда я меняю на каждой итерации y, а для x коплю ошибки
            for (int i = 0; i <= dy; i++) {
                points.put(y, new ArrayList<>());
                points.get(y).add(x);
                error += 2 * dx;
                if (error > dy) {
                    x += stepX;
                    error = -(2 * dy - error);
                }
                y += stepY;
            }
        }
        return points;
    }

    public static Map<Integer, List<Integer>> myBresenhamOneX(int x0, int y0, int x1, int y1) {
        Map<Integer, List<Integer>> points = new HashMap<>();
        int dx = abs(x0 - x1);
        int dy = abs(y0 - y1);

        int x = x0, y = y0;
        int error = 0;
        int stepX = 1;
        int stepY = 1;
        if (x1 - x0 < 0) {
            stepX *= -1;
        }
        if (y1 - y0 < 0) {
            stepY *= -1;
        }
        if (dx > dy) {
            // тогда я меняю на каждой итерации x, а для y коплю ошибки
            // тогда я могу равномерно разделить подъем, когда иду по y. То есть на каждом шаге по x надо на какое то нецелое
            // число, которое меньше нуля (dx > dy) менять y. Но это не целое. Поэтому я умножу все на dx,а потом из за
            // того, что 0.5 - ошибка при которой я должен закрасить клетку сверху - я буду умножать еще на два
            for (int i = 0; i <= dx; i++) {
//                pw.setColor(x, y, Color.BLACK);
                points.put(x, new ArrayList<>());
                points.get(x).add(y);
                error += 2 * dy;
                if (error > dx) {
                    y += stepY;
                    error = -(2 * dx - error);
                }
                x += stepX;
            }
        } else {
            // тогда я меняю на каждой итерации y, а для x коплю ошибки
            for (int i = 0; i <= dy; i++) {
//                pw.setColor(x, y, Color.BLACK);
                if (points.containsKey(x)) {
                    points.get(x).add(y);
                } else {
                    points.put(x, new ArrayList<>());
                    points.get(x).add(y);
                }
                error += 2 * dx;
                if (error > dy) {
                    x += stepX;
                    error = -(2 * dy - error);
                }
                y += stepY;
            }
        }
        return points;
    }

    /**
     * Метод для растеризации треугольника с использованием идеи scanline и нахождения границ через алгоритм
     * Брезенхейма.
     */
    public static void drawTriangleBresenham(CustomIntArrayBasedRasterizationBuffer fb, int x0, int y0, int x1, int y1, int x2, int y2) {
        if (max(y0, max(y1, y2)) - min(y0, max(y1, y2)) > max(x0, max(x1, x2)) - min(x0, max(x1, x2))) {
            int tmp;
            if (y0 > y1) {
                tmp = y1;
                y1 = y0;
                y0 = tmp;

                tmp = x1;
                x1 = x0;
                x0 = tmp;
            }
            if (y1 > y2) {
                tmp = y2;
                y2 = y1;
                y1 = tmp;

                tmp = x2;
                x2 = x1;
                x1 = tmp;
            }
            if (y0 > y1) {
                tmp = y1;
                y1 = y0;
                y0 = tmp;

                tmp = x1;
                x1 = x0;
                x0 = tmp;
            }

            Map<Integer, List<Integer>> lineAC = myBresenhamOneY(x0, y0, x2, y2);
            Map<Integer, List<Integer>> lineAB = myBresenhamOneY(x0, y0, x1, y1);
            Map<Integer, List<Integer>> lineBC = myBresenhamOneY(x1, y1, x2, y2);

            for (int y = y0; y <= y2; y++) {
                int borderFirst = lineAC.get(y).get(lineAC.get(y).size() - 1);
                int borderLast;
                if (lineAB.containsKey(y)) {
                    borderLast = lineAB.get(y).get(lineAB.get(y).size() - 1);
                } else {
                    borderLast = lineBC.get(y).get(lineBC.get(y).size() - 1);
                }
                for (int x = min(borderFirst, borderLast); x <= max(borderFirst, borderLast); x++) {

                    fb.setPixel(x, y, 0xFF000000);
                }
            }
        } else {
            int tmp;
            if (x0 > x1) {
                tmp = x1;
                x1 = x0;
                x0 = tmp;

                tmp = y1;
                y1 = y0;
                y0 = tmp;
            }
            if (x1 > x2) {
                tmp = x2;
                x2 = x1;
                x1 = tmp;

                tmp = y2;
                y2 = y1;
                y1 = tmp;
            }
            if (x0 > x1) {
                tmp = x1;
                x1 = x0;
                x0 = tmp;

                tmp = y1;
                y1 = y0;
                y0 = tmp;
            }

            Map<Integer, List<Integer>> lineAC = myBresenhamOneX(x0, y0, x2, y2);
            Map<Integer, List<Integer>> lineAB = myBresenhamOneX(x0, y0, x1, y1);
            Map<Integer, List<Integer>> lineBC = myBresenhamOneX(x1, y1, x2, y2);

            for (int x = x0; x <= x2; x++) {
                int borderFirst = lineAC.get(x).get(lineAC.get(x).size() - 1);
                int borderLast;
                if (lineAB.containsKey(x)) {
                    borderLast = lineAB.get(x).get(lineAB.get(x).size() - 1);
                } else {
                    borderLast = lineBC.get(x).get(lineBC.get(x).size() - 1);
                }
                for (int y = min(borderFirst, borderLast); y <= max(borderFirst, borderLast); y++) {
                    fb.setPixel(x, y, 0xFF000000);
                }
            }
        }
    }

    /**
     * Метод рисования линии с учётом Z-буфера.
     */
    public static void drawLine(
            RasterizationBuffer rb,
            ZBuffer zBuffer,
            int x1, int y1, double z1,
            int x2, int y2, double z2,
            int color) {

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        if (Math.abs(dx) > Math.abs(dy)) {
            // Горизонтальная линия
            if (x1 > x2) {
                // Свап
                int tmpX = x2;
                x2 = x1;
                x1 = tmpX;
                int tmpY = y2;
                y2 = y1;
                y1 = tmpY;
                double tmpZ = z2;
                z2 = z1;
                z1 = tmpZ;
            }

            double slope = dy / dx;
            double zSlope = dz / dx;

            double y = y1;
            double z = z1;
            for (int x = x1; x <= x2; x++) {
                if (zBuffer.setPixel(x, (int) Math.round(y), z + Constants.WIREFRAME_GAP)) {
                    rb.setPixel(x, (int) Math.round(y), color);
                }
                y += slope;
                z += zSlope;
            }
        } else {
            // Вертикальная линия
            if (y1 > y2) {
                int tmpX = x2;
                x2 = x1;
                x1 = tmpX;
                int tmpY = y2;
                y2 = y1;
                y1 = tmpY;
                double tmpZ = z2;
                z2 = z1;
                z1 = tmpZ;
            }

            double slope = dx / dy;
            double zSlope = dz / dy;

            double x = x1;
            double z = z1;
            for (int y = y1; y <= y2; y++) {
                if (zBuffer.setPixel((int) Math.round(x), y, z + Constants.WIREFRAME_GAP)) {
                    rb.setPixel((int) Math.round(x), y, color);
                }
                x += slope;
                z += zSlope;
            }
        }
    }

    /**
     * Рисует контур треугольника (wireframe) с учётом Z-буфера.
     */
    public static void drawWireFrameTriangle(
            RasterizationBuffer rb,
            ZBuffer zBuffer,
            VertexAttribute v0, VertexAttribute v1, VertexAttribute v2,
            int color) {

        // Сортируем вершины по Y
        if (v0.y > v1.y) v0.swapWith(v1);
        if (v1.y > v2.y) v1.swapWith(v2);
        if (v0.y > v1.y) v0.swapWith(v1);

        // Рисуем 3 ребра
        drawLine(rb, zBuffer, v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, color);
        drawLine(rb, zBuffer, v0.x, v0.y, v0.z, v2.x, v2.y, v2.z, color);
        drawLine(rb, zBuffer, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, color);
    }

    /**
     * Упрощённая версия для 2D (без Z-буфера, для отладки).
     */
    public static void drawWireFrameTriangle2D(
            RasterizationBuffer rb,
            int x0, int y0, int x1, int y1, int x2, int y2,
            int color) {

        drawLine2D(rb, x0, y0, x1, y1, color);
        drawLine2D(rb, x0, y0, x2, y2, color);
        drawLine2D(rb, x1, y1, x2, y2, color);
    }

    private static void drawLine2D(RasterizationBuffer rb, int x1, int y1, int x2, int y2, int color) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (x1 > x2) {
                int tmp = x2;
                x2 = x1;
                x1 = tmp;
                tmp = y2;
                y2 = y1;
                y1 = tmp;
            }
            double slope = dy / dx;
            double y = y1;
            for (int x = x1; x <= x2; x++) {
                rb.setPixel(x, (int) Math.round(y), color);
                y += slope;
            }
        } else {
            if (y1 > y2) {
                int tmp = x2;
                x2 = x1;
                x1 = tmp;
                tmp = y2;
                y2 = y1;
                y1 = tmp;
            }
            double slope = dx / dy;
            double x = x1;
            for (int y = y1; y <= y2; y++) {
                rb.setPixel((int) Math.round(x), y, color);
                x += slope;
            }
        }
    }
}