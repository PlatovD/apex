package com.apex.tool.rasterization;

import com.apex.core.Constants;

/**
 * Класс с умными сеттерами. При установке сразу интерполирует
 */
public class PixelAttribute {
    public double worldX, worldY, worldZ;
    public double normalX, normalY, normalZ;
    public double u, v;

    public void setFromBarycentric(VertexAttributeExtended v0E, VertexAttributeExtended v1E, VertexAttributeExtended v2E, double[] barycentric) {
        if (barycentric.length != 3) throw new IllegalArgumentException("Wrong size of barycentric");

        this.worldX = v0E.worldX * barycentric[0] + v1E.worldX * barycentric[1] + v2E.worldX * barycentric[2];
        this.worldY = v0E.worldY * barycentric[0] + v1E.worldY * barycentric[1] + v2E.worldY * barycentric[2];
        this.worldZ = v0E.worldZ * barycentric[0] + v1E.worldZ * barycentric[1] + v2E.worldZ * barycentric[2];

        this.normalX = v0E.normalX * barycentric[0] + v1E.normalX * barycentric[1] + v2E.normalX * barycentric[2];
        this.normalY = v0E.normalY * barycentric[0] + v1E.normalY * barycentric[1] + v2E.normalY * barycentric[2];
        this.normalZ = v0E.normalZ * barycentric[0] + v1E.normalZ * barycentric[1] + v2E.normalZ * barycentric[2];
        normalizeNormal();

        double invW = v0E.invW * barycentric[0] + v1E.invW * barycentric[1] + v2E.invW * barycentric[2];
        double uOverW = v0E.uOverW * barycentric[0] + v1E.uOverW * barycentric[1] + v2E.uOverW * barycentric[2];
        double vOverW = v0E.vOverW * barycentric[0] + v1E.vOverW * barycentric[1] + v2E.vOverW * barycentric[2];
        u = uOverW / invW;
        v = vOverW / invW;
    }

    private void normalizeNormal() {
        double len = Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        if (len > Constants.EPS) {
            normalX /= len;
            normalY /= len;
            normalZ /= len;
        }
    }
}
