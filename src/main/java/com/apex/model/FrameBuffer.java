package com.apex.model;

import com.apex.reflection.AutoCreation;
import com.apex.core.Constants;
import com.apex.exception.FrameBufferException;

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

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public int[] getRawData() {
        return pixels;
    }

    public void setPixel(int x, int y, int rgbColor) {
        int index = x * width + y * height;
        if (index > width * height) throw new FrameBufferException("Pixel out of bounds");
        pixels[index] = rgbColor;
    }

    public void clear() {
        java.util.Arrays.fill(pixels, 0xFFFFFFFF);
    }
}
