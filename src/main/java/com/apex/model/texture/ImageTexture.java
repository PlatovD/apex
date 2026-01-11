package com.apex.model.texture;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public class ImageTexture implements Texture {
    private int[] pixels;
    private int width;
    private int height;
    private String cache;

    public ImageTexture(String filename, Image image) {
        cache = filename;
        setImage(image);
    }

    public void setImage(Image image) {
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
        this.pixels = new int[width * height];

        PixelReader pr = image.getPixelReader();
        pr.getPixels(0, 0, width, height,
                WritablePixelFormat.getIntArgbInstance(),
                pixels, 0, width);
    }

    @Override
    public int getPixelColor(float u, float v) {
        // Standard UV wrapping: wrap coordinates to [0, 1] range
        float wrappedU = u - (float) Math.floor(u);
        float wrappedV = v - (float) Math.floor(v);

        // Map to pixel coordinates [0, width-1] and [0, height-1]
        // Note: conventional texture mapping often flips V axis (1-V)
        int x = (int) (wrappedU * (width - 1));
        int y = (int) ((1 - wrappedV) * (height - 1));

        // Final bounds protection
        x = Math.max(0, Math.min(width - 1, x));
        y = Math.max(0, Math.min(height - 1, y));

        return pixels[y * width + x];
    }

    @Override
    public String getCache() {
        return cache;
    }
}
