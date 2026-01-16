package com.apex.model.scene;

import com.apex.core.RuntimeStates;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;

import java.util.Arrays;

@AutoCreation
public class ZBuffer {
    @AutoInject
    private RuntimeStates runtimeStates;

    private int width;
    private int height;
    private double[] buffer;

    public ZBuffer() {
    }

    public void initZBuffer(){
        this.width = runtimeStates.SCENE_WIDTH;
        this.height = runtimeStates.SCENE_HEIGHT;
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
        if (runtimeStates.SCENE_WIDTH != width && runtimeStates.SCENE_HEIGHT != height) {
            buffer = new double[runtimeStates.SCENE_WIDTH * runtimeStates.SCENE_HEIGHT];
            width = runtimeStates.SCENE_WIDTH;
            height = runtimeStates.SCENE_HEIGHT;
        }
        Arrays.fill(buffer, Double.MAX_VALUE);
    }
}