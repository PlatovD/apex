package com.apex.math;

import com.apex.math.exceptions.MathException;

public class Vector2f {
    private double x;
    private double y;

    public Vector2f() {
        this(0, 0);
    }

    public Vector2f(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f other) {
        this(other.x, other.y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(this.x + other.x, this.y + other.y);
    }

    public Vector2f subtract(Vector2f other) {
        return new Vector2f(this.x - other.x, this.y - other.y);
    }

    public Vector2f multiply(double scalar) {
        return new Vector2f(this.x * scalar, this.y * scalar);
    }

    public Vector2f divide(double scalar) {
        if (Math.abs(scalar) < MathUtils.EPSILON) {
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

    public Vector2f multiplyLocal(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Vector2f divideLocal(double scalar) {
        if (Math.abs(scalar) < MathUtils.EPSILON) {
            throw new MathException("Division by zero");
        }
        this.x /= scalar;
        this.y /= scalar;
        return this;
    }

    public Vector2f normalizeLocal() {
        double len = length();
        if (len < MathUtils.EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divideLocal(len);
    }

    public Vector2f set(Vector2f other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vector2f set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public Vector2f copy() {
        return new Vector2f(this.x, this.y);
    }

    public Vector2f normalize() {
        double len = length();
        if (len < MathUtils.EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divide(len);
    }

    public double dot(Vector2f other) {
        return this.x * other.x + this.y * other.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2f other = (Vector2f) obj;
        return MathUtils.equals(this.x, other.x) &&
                MathUtils.equals(this.y, other.y);
    }

    @Override
    public String toString() {
        return String.format("Vector2(%.4f, %.4f)", x, y);
    }
}
