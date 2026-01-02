package com.apex.buffer;

import com.apex.core.Constants;
import com.apex.reflection.AutoCreation;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.nio.IntBuffer;
import java.util.Arrays;

import static com.apex.core.Constants.backgroundColor;

@AutoCreation
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

    public void setPixel(int x, int y, int rgbColor) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y * width + x] = rgbColor;
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
