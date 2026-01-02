package com.apex.buffer;

import com.apex.reflection.AutoCreation;
import com.apex.core.Constants;
import com.apex.reflection.AutoInject;
import com.apex.util.PixelWriterWrapper;
import javafx.scene.image.PixelFormat;

import java.util.Arrays;

import static com.apex.core.Constants.backgroundColor;

//@AutoCreation
public class CustomIntArrayBasedRasterizationBuffer implements RasterizationBuffer {
    //    @AutoInject
    private PixelWriterWrapper pixelWriterWrapper;

    private int width;
    private int height;
    private int[] pixels;

    public CustomIntArrayBasedRasterizationBuffer() {
        this.width = Constants.SCENE_WIDTH;
        this.height = Constants.SCENE_HEIGHT;
        this.pixels = new int[width * height];
    }

    @Override
    public int[] getRawData() {
        return pixels;
    }

    @Override
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
        int width = Constants.SCENE_WIDTH;
        int height = Constants.SCENE_HEIGHT;

        pixelWriterWrapper.getPixelWriter().setPixels(
                0, 0,
                width, height,
                PixelFormat.getIntArgbInstance(),
                pixels,
                0,
                pixels.length / height
        );
    }
}
