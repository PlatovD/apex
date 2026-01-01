package com.apex.model.texture;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public class ImageTexture implements Texture {
    private int[] pixels;
    private int width;
    private int height;
    private int cache;

    public ImageTexture(Image image) {
        setImage(image);
        cache = image.hashCode();
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
        int x = (int) (1 - (u * (width - 1)) % width);
        int y = (int) (1 - (v * (height - 1)) % height);

        if (x < 0) x += width;
        if (y < 0) y += height;

        return pixels[y * width + x];
    }

    @Override
    public int getCache() {
        return cache;
    }
}

