package com.apex.buffer;

import com.apex.core.Constants;
import com.apex.reflection.AutoCreation;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.nio.IntBuffer;
import java.util.Arrays;

import static com.apex.core.Constants.backgroundColor;

public class JavaFXBasedRasterizationBuffer implements RasterizationBuffer {
    private int width;
    private int height;
    private int[] pixels;
    private PixelBuffer<IntBuffer> pixelBufferFX;
    private WritableImage writableImage;

    public JavaFXBasedRasterizationBuffer() {
        width = Constants.SCENE_WIDTH;
        height = Constants.SCENE_HEIGHT;
        IntBuffer buffer = IntBuffer.allocate(width * height);
        pixels = buffer.array();
        pixelBufferFX = new PixelBuffer<>(width, height, buffer, PixelFormat.getIntArgbPreInstance());
        writableImage = new WritableImage(pixelBufferFX);
    }

    @Override
    public int[] getRawData() {
        return new int[0];
    }

    @Override
    public void updateBufferForNewScreenSizes(int newWidth, int newHeight) {
        width = Constants.SCENE_WIDTH;
        height = Constants.SCENE_HEIGHT;
        IntBuffer buffer = IntBuffer.allocate(width * height);
        pixels = buffer.array();
        pixelBufferFX = new PixelBuffer<>(width, height, buffer, PixelFormat.getIntArgbPreInstance());
        writableImage = new WritableImage(pixelBufferFX);
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
        pixelBufferFX.updateBuffer(b -> null);
    }

    public WritableImage getWritableImage() {
        return writableImage;
    }
}
