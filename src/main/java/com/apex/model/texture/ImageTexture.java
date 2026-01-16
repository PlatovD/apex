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
    public int getPixelColor(double u, double v) {
        // Standard wrapping logic for UV coords (support for tiling or just clamping)
        // Here we use standard wrapping (fmod 1.0) then scale to width/height
        double u_wrapped = u - Math.floor(u);
        double v_wrapped = v - Math.floor(v);

        int x = (int) (u_wrapped * (width - 1));
        int y = (int) (v_wrapped * (height - 1));

        // Final defensive clamping to prevent OOB due to precision issues
        x = Math.max(0, Math.min(width - 1, x));
        y = Math.max(0, Math.min(height - 1, y));

        return pixels[y * width + x];
    }

    @Override
    public String getCache() {
        return cache;
    }
}
