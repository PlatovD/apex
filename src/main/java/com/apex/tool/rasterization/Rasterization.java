package com.apex.tool.rasterization;

import com.apex.buffer.RasterizationBuffer;
import com.apex.buffer.CustomIntArrayBasedRasterizationBuffer;
import com.apex.core.Constants;
import com.apex.math.Vector3f;
import com.apex.model.scene.ZBuffer;
import com.apex.model.texture.Texture;
import com.apex.tool.colorization.ColorData;
import com.apex.tool.colorization.ColorProvider;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apex.math.MathUtil.findBarycentricCords;
import static java.lang.Math.*;

public class Rasterization {
    public static void drawTriangle(
            RasterizationBuffer rb, ZBuffer zBuffer,
            com.apex.math.Vector3f light, ColorData colorData, ColorProvider cp, Texture texture,
            VertexAttribute v0A, VertexAttribute v1A, VertexAttribute v2A,
            float[] barycentric
    ) {
        int x0 = v0A.x;
        int y0 = v0A.y;
        float z0 = v0A.z;
        float u0 = v0A.u;
        float v0 = v0A.v;
        float n_x0 = v0A.n_x;
        float n_y0 = v0A.n_y;
        float n_z0 = v0A.n_z;

        int x1 = v1A.x;
        int y1 = v1A.y;
        float z1 = v1A.z;
        float u1 = v1A.u;
        float v1 = v1A.v;
        float n_x1 = v1A.n_x;
        float n_y1 = v1A.n_y;
        float n_z1 = v1A.n_z;

        int x2 = v2A.x;
        int y2 = v2A.y;
        float z2 = v2A.z;
        float u2 = v2A.u;
        float v2 = v2A.v;
        float n_x2 = v2A.n_x;
        float n_y2 = v2A.n_y;
        float n_z2 = v2A.n_z;

        int tmp;
        float tmpF;
        if (y0 > y1) {
            // Свап y
            tmp = y1;
            y1 = y0;
            y0 = tmp;

            // Свап x
            tmp = x1;
            x1 = x0;
            x0 = tmp;

            // Свап z
            tmpF = z1;
            z1 = z0;
            z0 = tmpF;

            // Свап u
            tmpF = u1;
            u1 = u0;
            u0 = tmpF;

            // Свап v
            tmpF = v1;
            v1 = v0;
            v0 = tmpF;

            // Свап нормалей
            tmpF = n_x1;
            n_x1 = n_x0;
            n_x0 = tmpF;

            tmpF = n_y1;
            n_y1 = n_y0;
            n_y0 = tmpF;

            tmpF = n_z1;
            n_z1 = n_z0;
            n_z0 = tmpF;
        }
        if (y1 > y2) {
            // Свап y
            tmp = y2;
            y2 = y1;
            y1 = tmp;

            // Свап x
            tmp = x2;
            x2 = x1;
            x1 = tmp;

            // Свап z
            tmpF = z2;
            z2 = z1;
            z1 = tmpF;

            // Свап u
            tmpF = u2;
            u2 = u1;
            u1 = tmpF;

            // Свап v
            tmpF = v2;
            v2 = v1;
            v1 = tmpF;

            // Свап нормалей
            tmpF = n_x2;
            n_x2 = n_x1;
            n_x1 = tmpF;

            tmpF = n_y2;
            n_y2 = n_y1;
            n_y1 = tmpF;

            tmpF = n_z2;
            n_z2 = n_z1;
            n_z1 = tmpF;
        }
        if (y0 > y1) {
            // Свап y
            tmp = y1;
            y1 = y0;
            y0 = tmp;

            // Свап x
            tmp = x1;
            x1 = x0;
            x0 = tmp;

            // Свап z
            tmpF = z1;
            z1 = z0;
            z0 = tmpF;

            // Свап u
            tmpF = u1;
            u1 = u0;
            u0 = tmpF;

            // Свап v
            tmpF = v1;
            v1 = v0;
            v0 = tmpF;

            // Свап нормалей
            tmpF = n_x1;
            n_x1 = n_x0;
            n_x0 = tmpF;

            tmpF = n_y1;
            n_y1 = n_y0;
            n_y0 = tmpF;

            tmpF = n_z1;
            n_z1 = n_z0;
            n_z0 = tmpF;
        }

//        float invZ1 = 1 / z0;
//        float invZ2 = 1 / z1;
//        float invZ3 = 1 / z2;

        colorData.u0 = u0;
        colorData.u1 = u1;
        colorData.u2 = u2;
        colorData.v0 = v0;
        colorData.v1 = v1;
        colorData.v2 = v2;

        int minX = min(x0, min(x1, x2));
        int maxX = max(x0, max(x1, x2));
        if (y0 == y1 && y1 == y2) {
            for (int x = minX; x <= max(x0, max(x1, x2)); x++) {
                findBarycentricCords(barycentric, x + 0.5f, y0 + 0.5f, x0 + 0.5f, y0 + 0.5f, x1 + 0.5f, y1 + 0.5f, x2 + 0.5f, y2 + 0.5f);
                if (barycentric[0] > -0.0001f && barycentric[1] > -0.0001f && barycentric[2] > -0.0001f) {
                    float pixelZ = findZFromBarycentric(barycentric, z0, z1, z2);
                    float lightFactor = findLightFactorForPixel(n_x0, n_x1, n_x2, n_y0, n_y1, n_y2, n_z0, n_z1, n_z2, light, barycentric);
                    if (lightFactor > Constants.EPS && zBuffer.setPixel(x, y0, pixelZ)) {
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
                findBarycentricCords(barycentric, x + 0.5f, y + 0.5f, x0 + 0.5f, y0 + 0.5f, x1 + 0.5f, y1 + 0.5f, x2 + 0.5f, y2 + 0.5f);
                if (barycentric[0] > -0.0001f && barycentric[1] > -0.0001f && barycentric[2] > -0.0001f) {
                    float pixelZ = findZFromBarycentric(barycentric, z0, z1, z2);
                    float lightFactor = findLightFactorForPixel(n_x0, n_x1, n_x2, n_y0, n_y1, n_y2, n_z0, n_z1, n_z2, light, barycentric);
                    if (lightFactor > Constants.EPS && zBuffer.setPixel(x, y, pixelZ)) {
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
                findBarycentricCords(barycentric, x + 0.5f, y + 0.5f, x0 + 0.5f, y0 + 0.5f, x1 + 0.5f, y1 + 0.5f, x2 + 0.5f, y2 + 0.5f);
                if (barycentric[0] > -0.0001f && barycentric[1] > -0.0001f && barycentric[2] > -0.0001f) {
                    float pixelZ = findZFromBarycentric(barycentric, z0, z1, z2);
                    float lightFactor = findLightFactorForPixel(n_x0, n_x1, n_x2, n_y0, n_y1, n_y2, n_z0, n_z1, n_z2, light, barycentric);
                    if (lightFactor > Constants.EPS && zBuffer.setPixel(x, y, pixelZ)) {
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

    private static float findLightFactorForPixel(float n_x0, float n_x1, float n_x2,
                                                 float n_y0, float n_y1, float n_y2,
                                                 float n_z0, float n_z1, float n_z2,
                                                 Vector3f light,
                                                 float[] barycentric) {
        return -(n_x0 * barycentric[0] + n_x1 * barycentric[1] + n_x2 * barycentric[2]) * light.getX()
                - (n_y0 * barycentric[0] + n_y1 * barycentric[1] + n_y2 * barycentric[2]) * light.getY()
                - (n_z0 * barycentric[0] + n_z1 * barycentric[1] + n_z2 * barycentric[2]) * light.getZ();
    }

    private static float findZFromBarycentric(float[] barycentric, float z0, float z1, float z2) {
        return barycentric[0] * z0 + barycentric[1] * z1 + barycentric[2] * z2;
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
     * Метод рисования линии. Позволяет рисовать ее из точки 1 в точку 2. Использует округление и
     * линейную интерполяцию для того, чтобы получить промежуточные значения и построить путь из 1 в 2.
     *
     * @param pixelWriter объект для рисования
     * @param x1          точка начала
     * @param y1          точка начала
     * @param x2          точка конца
     * @param y2          точка конца
     */
    public static void drawLine(PixelWriter pixelWriter, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (x1 > x2) {
                double tmp = x2;
                x2 = x1;
                x1 = tmp;

                tmp = y2;
                y2 = y1;
                y1 = tmp;
            }
            int[] points = interpolate(y1, x1, y2, x2);
            for (double x = x1; x < x2; x++) {
                pixelWriter.setColor((int) x, points[(int) (x - x1)], Color.RED);
            }
        } else {
            if (y1 > y2) {
                double tmp = x2;
                x2 = x1;
                x1 = tmp;

                tmp = y2;
                y2 = y1;
                y1 = tmp;
            }
            int[] points = interpolate(x1, y1, x2, y2);
            for (double y = y1; y < y2; y++) {
                pixelWriter.setColor(points[(int) (y - y1)], (int) y, Color.RED);
            }
        }
    }

    /**
     * Вычисляет значения функции d = f(i) от i=i0 до i=i1
     * Использует числа с плавающей точкой и их округление.
     *
     * @param d0 значение функции в начальной координате
     * @param i0 аргумент функции в начальной координате
     * @param d1 значение функции в конечной координате
     * @param i1 аргумент функции в конечной координате
     */
    private static int[] interpolate(double d0, double i0, double d1, double i1) {
        double tmp;
        int[] values = new int[(int) ((i1 - i0) + 1)];
        if (i0 > i1) {
            tmp = i1;
            i1 = i0;
            i0 = tmp;
        }

        double a = (d1 - d0) / (i1 - i0);
        double value = d0;
        for (double i = i0; i <= i1; i++) {
            values[(int) (i - i0)] = (int) Math.round(value);
            value += a;
        }
        return values;
    }

    /**
     * С помощью линий рисует треугольник, однако не заполняет его, а ставит пиксели только на стороны.
     */
    public static void drawWireFrameTriangle(PixelWriter pixelWriter, double x0, double y0, double x1, double y1, double x2, double y2) {
        drawLine(pixelWriter, x0, y0, x1, y1);
        drawLine(pixelWriter, x0, y0, x2, y2);
        drawLine(pixelWriter, x2, y2, x1, y1);
    }
}
