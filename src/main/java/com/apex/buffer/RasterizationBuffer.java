package com.apex.buffer;

public interface RasterizationBuffer {
    int[] getRawData();

    void updateBufferForNewScreenSizes(int newWidth, int newHeight);

    void setPixel(int x, int y, int rgbColor);

    void clear();

    void drawPoint(int x, int y, int size, int color);

    void rasterize();
}
