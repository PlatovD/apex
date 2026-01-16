package com.apex.buffer;

import com.apex.core.Constants;
import com.apex.core.RuntimeStates;
import com.apex.reflection.AutoInject;
import com.apex.util.PixelWriterWrapper;
import javafx.scene.image.PixelFormat;

import java.util.Arrays;

import static com.apex.core.Constants.backgroundColor;

public class CustomIntArrayBasedRasterizationBuffer implements RasterizationBuffer {
    @AutoInject
    private PixelWriterWrapper pixelWriterWrapper;
    @AutoInject
    private RuntimeStates runtimeStates;

    private int width;
    private int height;
    private int[] pixels;

    public CustomIntArrayBasedRasterizationBuffer() {
        this.width = runtimeStates.SCENE_WIDTH;
        this.height = runtimeStates.SCENE_HEIGHT;
        this.pixels = new int[width * height];
    }

    @Override
    public int[] getRawData() {
        return pixels;
    }

    @Override
    public void updateBufferForNewScreenSizes() {
        width = runtimeStates.SCENE_WIDTH;
        height = runtimeStates.SCENE_HEIGHT;
        pixels = new int[width * height];
    }

    @Override
    public void setPixel(int x, int y, int rgbColor) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y * width + x] = rgbColor;
        }
    }

    @Override
    public void drawPoint(int x, int y, int size, int color) {
        int half = size / 2;
        for (int i = -half; i <= half; i++) {
            for (int j = -half; j <= half; j++) {
                setPixel(x + i, y + j, color);
            }
        }
    }

    @Override
    public void clear() {
        Arrays.fill(pixels, backgroundColor);
    }

    @Override
    public void rasterize() {
        int width = runtimeStates.SCENE_WIDTH;
        int height = runtimeStates.SCENE_HEIGHT;

        pixelWriterWrapper.getPixelWriter().setPixels(
                0, 0,
                width, height,
                PixelFormat.getIntArgbInstance(),
                pixels,
                0,
                pixels.length / height);
    }
}
