package com.apex.tool.rasterization;

public class VertexAttribute {
    public int x, y, z;
    public float u = 0, v = 0;
    public float n_x, n_y, n_z;

    public VertexAttribute() {
    }

    public VertexAttribute(int x, int y, int z, float u, float v, float n_x, float n_y, float n_z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
        this.n_x = n_x;
        this.n_y = n_y;
        this.n_z = n_z;
    }
}
