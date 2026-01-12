package com.apex.math;

import com.apex.exception.MathException;

import static com.apex.math.MathUtil.EPSILON;

public class Vector2f {
    private float x;
    private float y;

    public Vector2f() {
        this(0.0f, 0.0f);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f other) {
        this(other.x, other.y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(this.x + other.x, this.y + other.y);
    }

    public Vector2f subtract(Vector2f other) {
        return new Vector2f(this.x - other.x, this.y - other.y);
    }

    public Vector2f multiply(float scalar) {
        return new Vector2f(this.x * scalar, this.y * scalar);
    }

    public Vector2f divide(float scalar) {
        if (Math.abs(scalar) < EPSILON) {
            throw new MathException("Division by zero");
        }
        return new Vector2f(this.x / scalar, this.y / scalar);
    }

    public Vector2f addLocal(Vector2f other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2f subtractLocal(Vector2f other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2f multiplyLocal(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Vector2f divideLocal(float scalar) {
        if (Math.abs(scalar) < EPSILON) {
            throw new MathException("Division by zero");
        }
        this.x /= scalar;
        this.y /= scalar;
        return this;
    }

    public Vector2f normalizeLocal() {
        float len = length();
        if (len < EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divideLocal(len);
    }

    public Vector2f set(Vector2f other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public Vector2f normalize() {
        float len = length();
        if (len < EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divide(len);
    }

    public float dot(Vector2f other) {
        return this.x * other.x + this.y * other.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2f other = (Vector2f) obj;
        return MathUtil.equals(this.x, other.x) &&
                MathUtil.equals(this.y, other.y);
    }

    @Override
    public String toString() {
        return String.format("Vector2(%.4f, %.4f)", x, y);
    }
}