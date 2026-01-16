package com.apex.tool.rasterization;

public class VertexAttributeExtended extends VertexAttribute {
    public Integer polygonIndex;
    public Integer vertexIndex;
    public String modelFilename;

    public void swapWith(VertexAttributeExtended other) {
        // int
        int tmpInt = this.x;
        this.x = other.x;
        other.x = tmpInt;

        tmpInt = this.y;
        this.y = other.y;
        other.y = tmpInt;

        tmpInt = vertexIndex;
        this.vertexIndex = other.vertexIndex;
        other.vertexIndex = tmpInt;

        tmpInt = polygonIndex;
        this.polygonIndex = other.polygonIndex;
        other.polygonIndex = tmpInt;

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

        tmpFloat = this.n_x;
        this.n_x = other.n_x;
        other.n_x = tmpFloat;

        tmpFloat = this.n_y;
        this.n_y = other.n_y;
        other.n_y = tmpFloat;

        tmpFloat = this.n_z;
        this.n_z = other.n_z;
        other.n_z = tmpFloat;

        String tmpString = this.modelFilename;
        this.modelFilename = other.modelFilename;
        other.modelFilename = tmpString;
    }
}
