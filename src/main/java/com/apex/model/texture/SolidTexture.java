package com.apex.model.texture;

public class SolidTexture implements Texture {
    private int color = 0xFF000000;

    public SolidTexture() {
    }

    public SolidTexture(int color) {
        this.color = color;
    }

    @Override
    public int getPixelColor(float u, float v) {
        return color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String getCache() {
        return String.valueOf(color);
    }
}
