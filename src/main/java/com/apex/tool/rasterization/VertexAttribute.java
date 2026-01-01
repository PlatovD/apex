package com.apex.tool.rasterization;

public class VertexAttribute {
    public int x, y, z;
    public float u = 0, v = 0;

    public VertexAttribute() {
    }

    public VertexAttribute(int x, int y, int z, float u, float v) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
    }
}
