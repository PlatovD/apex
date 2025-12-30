package com.apex.model;

import com.apex.reflection.AutoCreation;
import com.apex.core.Constants;
import com.apex.exception.FrameBufferException;

import java.util.Arrays;

@AutoCreation
public class FrameBuffer {
    private int width;
    private int height;
    private int[] pixels;

    public FrameBuffer() {
        this.width = Constants.SCENE_WIDTH;
        this.height = Constants.SCENE_HEIGHT;
        this.pixels = new int[width * height];
    }

    public int[] getRawData() {
        return pixels;
    }

    public void setPixel(int x, int y, int rgbColor) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y * width + x] = rgbColor;
        }
//        int index = x * width + y * height;
//        if (index > width * height) throw new FrameBufferException("Pixel out of bounds");
//        pixels[index] = rgbColor;
    }

    public void clear() {
        Arrays.fill(pixels, 0xFFFFFFFF);
    }
}
