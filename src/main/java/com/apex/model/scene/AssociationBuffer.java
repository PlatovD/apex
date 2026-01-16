package com.apex.model.scene;

import com.apex.reflection.AutoCreation;

import java.util.Arrays;

@AutoCreation
public class AssociationBuffer {
    private int width;
    private int height;
    private PixelVertexInfo[] buffer;

    public record PixelVertexInfo(
            int polygonIndex,
            int vertexIndex,
            String modelFilename
    ) {
    }

    public AssociationBuffer() {
    }

    public void update(int width, int height) {
        if (this.width != width || this.height == height) {
            this.width = width;
            this.height = height;
            buffer = new PixelVertexInfo[width * height];
        }
        Arrays.fill(buffer, null);
    }

    public void setPixel(int x, int y, int closestVertexIndex, int closestPolygonIndex, String modelFilename) {
        buffer[y * width + x] = new PixelVertexInfo(closestPolygonIndex, closestVertexIndex, modelFilename);
    }

    public int getPolygonIndex(int x, int y) {
        return buffer[y * width + x].polygonIndex();
    }

    public int getVertexIndex(int x, int y) {
        return buffer[y * width + x].vertexIndex();
    }

    public String getModelFilename(int x, int y) {
        return buffer[y * width + x].modelFilename();
    }
}
