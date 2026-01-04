package com.apex.math;

import com.apex.math.exceptions.MathException;

public class Vector3f {
    private double x;
    private double y;
    private double z;

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f other) {
        this(other.x, other.y, other.z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3f add(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3f subtract(Vector3f other) {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3f multiply(double scalar) {
        return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3f divide(double scalar) {
        if (Math.abs(scalar) < MathUtils.EPSILON) {
            throw new MathException("Division by zero");
        }
        return new Vector3f(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    public Vector3f addLocal(Vector3f other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vector3f subtractLocal(Vector3f other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vector3f multiplyLocal(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    public Vector3f divideLocal(double scalar) {
        if (Math.abs(scalar) < MathUtils.EPSILON) {
            throw new MathException("Division by zero");
        }
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        return this;
    }

    public Vector3f normalizeLocal() {
        double len = length();
        if (len < MathUtils.EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divideLocal(len);
    }

    public Vector3f set(Vector3f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3f set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3f normalize() {
        double len = length();
        if (len < MathUtils.EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divide(len);
    }

    public double dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3f cross(Vector3f other) {
        return new Vector3f(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    public Vector3f crossLocal(Vector3f other) {
        double nx = this.y * other.z - this.z * other.y;
        double ny = this.z * other.x - this.x * other.z;
        double nz = this.x * other.y - this.y * other.x;

        this.x = nx;
        this.y = ny;
        this.z = nz;
        return this;
    }

    public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3f other = (Vector3f) obj;
        return MathUtils.equals(this.x, other.x) &&
                MathUtils.equals(this.y, other.y) &&
                MathUtils.equals(this.z, other.z);
    }

    @Override
    public String toString() {
        return String.format("Vector3(%.4f, %.4f, %.4f)", x, y, z);
    }
}

