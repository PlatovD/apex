package com.apex.tool.rasterization;

public class VertexAttribute {
    public int x, y;
    public double z, invW;
    public float u = 0, v = 0;
    public double uOverW;
    public double vOverW;
    public float normalX, normalY, normalZ;

    public VertexAttribute() {
    }

    public VertexAttribute(int x, int y, int z, float invW, float u, float v, float normalX, float normalY, float normalZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
        this.normalX = normalX;
        this.normalY = normalY;
        this.normalZ = normalZ;
    }

    public void swapWith(VertexAttribute other) {
        // int
        int tmpInt = this.x;
        this.x = other.x;
        other.x = tmpInt;

        tmpInt = this.y;
        this.y = other.y;
        other.y = tmpInt;

        // double
        double tmpDouble = this.z;
        this.z = other.z;
        other.z = tmpDouble;

        tmpDouble = this.invW;
        this.invW = other.invW;
        other.invW = tmpDouble;

        tmpDouble = this.uOverW;
        this.uOverW = other.uOverW;
        other.uOverW = tmpDouble;

        tmpDouble = this.vOverW;
        this.vOverW = other.vOverW;
        other.vOverW = tmpDouble;

        // float
        float tmpFloat = this.u;
        this.u = other.u;
        other.u = tmpFloat;

        tmpFloat = this.v;
        this.v = other.v;
        other.v = tmpFloat;

        tmpFloat = this.normalX;
        this.normalX = other.normalX;
        other.normalX = tmpFloat;

        tmpFloat = this.normalY;
        this.normalY = other.normalY;
        other.normalY = tmpFloat;

        tmpFloat = this.normalZ;
        this.normalZ = other.normalZ;
        other.normalZ = tmpFloat;
    }
}
