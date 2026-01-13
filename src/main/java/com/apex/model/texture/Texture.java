package com.apex.model.texture;

public interface Texture {
    int getPixelColor(double u, double v);

    String getCache();
}
