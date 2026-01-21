package com.apex.tool.rasterization;

import com.apex.buffer.RasterizationBuffer;
import com.apex.model.scene.AssociationBuffer;
import com.apex.model.scene.ZBuffer;
import com.apex.reflection.AutoCreation;

import static com.apex.math.MathUtil.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

@AutoCreation
public class Rasterizator {
    public void drawTriangle(
            RasterizationBuffer rb, ZBuffer zBuffer, AssociationBuffer assBuffer,
            SceneAttribute sceneAttribute,
            DrawableModelAttribute modelAttribute,
            VertexAttributeExtended v0AE, VertexAttributeExtended v1AE, VertexAttributeExtended v2AE,
            double[] barycentric
    ) {
        if (v0AE.y > v1AE.y) {
            v0AE.swapWith(v1AE);
        }
        if (v1AE.y > v2AE.y) {
            v1AE.swapWith(v2AE);
        }
        if (v0AE.y > v1AE.y) {
            v0AE.swapWith(v1AE);
        }

        int x0 = v0AE.x;
        int y0 = v0AE.y;

        int x1 = v1AE.x;
        int y1 = v1AE.y;

        int x2 = v2AE.x;
        int y2 = v2AE.y;

        int minX = min(x0, min(x1, x2));
        int maxX = max(x0, max(x1, x2));
        if (y0 == y1 && y1 == y2) {
            for (int x = minX; x <= max(x0, max(x1, x2)); x++) {
                findBarycentricCords(barycentric, x, y0, x0, y0, x1, y1, x2, y2);
                // todo
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
                // todo
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
                // todo
            }

            longSide = nextLongSide;
            shortSide = nextShortSide;
        }
    }

    private static boolean updateBuffers(
            int x, int y,
            RasterizationBuffer rb, ZBuffer zBuffer, AssociationBuffer assBuffer,
            SceneAttribute sceneAttribute,
            DrawableModelAttribute modelAttribute,
            VertexAttributeExtended v0AE, VertexAttributeExtended v1AE, VertexAttributeExtended v2AE,
            double[] barycentric) {
        // проверка принадлежности треугольнику
        if (!(barycentric[0] > -0.0001f && barycentric[1] > -0.0001f && barycentric[2] > -0.0001f)) return false;

        // тут проверяю через z-buffer
        double pixelZ = findZFromBarycentric(barycentric, v0AE.z, v0AE.invW, v1AE.z, v1AE.invW, v2AE.z, v2AE.invW);
        if (!zBuffer.setPixel(x, y, pixelZ)) return false;

        // здесь уже должен быть расчет цвета пикселя через шейдер
        int color = modelAttribute.shader.calcPixel();

        // обновляю буферы
        assBuffer.setPixel(x, y, getClosestVertexIndexByBarycentric(barycentric, v0AE, v1AE, v2AE), v0AE.polygonIndex, v0AE.modelFilename);
        rb.setPixel(x, y, color);
        return true;
    }
}
