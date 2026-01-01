package com.apex.model.texture;

public interface Texture {
    int getPixelColor(float u, float v);

    String getCache();
}
