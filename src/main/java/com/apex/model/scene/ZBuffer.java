package com.apex.model.scene;

import com.apex.core.Constants;
import com.apex.reflection.AutoCreation;

import java.util.Arrays;

@AutoCreation
public class ZBuffer {
    private int width;
    private int height;
    private double[] buffer;

    public ZBuffer() {
        this.width = Constants.SCENE_WIDTH;
        this.height = Constants.SCENE_HEIGHT;
        buffer = new double[width * height];
        clear();
    }

    public boolean setPixel(int x, int y, double zCord) {
        if (zCord < 0)
            return false;
        int bufferIndex = y * width + x;
        if (bufferIndex < 0 || bufferIndex >= width * height)
            return false;
        if (buffer[bufferIndex] < zCord)
            return false;
        buffer[bufferIndex] = zCord;
        return true;
    }

    public void clear() {
        Arrays.fill(buffer, Float.MAX_VALUE);
    }
}
