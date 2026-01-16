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
        int bufferIndex = y * width + x;
        if (bufferIndex < 0 || bufferIndex >= width * height) {
            return false;
        }

        double normalizedZ = zCord;
        if (normalizedZ < -1.0 || normalizedZ > 1.0) {
            return false;
        }
        normalizedZ = (normalizedZ + 1.0) / 2.0; // [-1,1] → [0,1]

        if (normalizedZ < buffer[bufferIndex]) {
            buffer[bufferIndex] = normalizedZ;
            return true;
        }
        return false;
    }

    public boolean canSetPixel(int x, int y, double zCord) {
        int bufferIndex = y * width + x;
        if (bufferIndex < 0 || bufferIndex >= width * height) {
            return false;
        }

        double normalizedZ = zCord;
        if (normalizedZ < -1.0 || normalizedZ > 1.0) {
            return false;
        }
        normalizedZ = (normalizedZ + 1.0) / 2.0; // [-1,1] → [0,1]

        if (normalizedZ < buffer[bufferIndex]) {
            return true;
        }
        return false;
    }

    public void clear() {
        Arrays.fill(buffer, Double.MAX_VALUE);
    }
}