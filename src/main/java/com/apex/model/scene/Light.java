package com.apex.model.scene;

import com.apex.math.Vector3f;

public class Light {
    private Vector3f position;
    private int color;

    public Light(Vector3f position, int color) {
        this.position = position.copy();
        this.color = color;
    }

    public Vector3f getPosition() {
        return position.copy();
    }

    public int getColor() {
        return color;
    }

    public void setPosition(Vector3f position) {
        this.position = position.copy();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorR() {
        return color >> 16 & 0xFF;
    }

    public int getColorG() {
        return color >> 8 & 0xFF;
    }

    public int getColorB() {
        return color & 0xFF;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Light light = (Light) o;
        return color == light.color && position.equals(light.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode() * 31 + Integer.hashCode(color);
    }

    @Override
    public String toString() {
        return "Light{position=" + position + ", color=" + color + '}';
    }
}