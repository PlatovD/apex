package com.apex.math;

import com.apex.exception.MathException;

import static com.apex.math.MathUtil.EPSILON;

public class Vector3f {
    private float x;
    private float y;
    private float z;

    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f other) {
        this(other.x, other.y, other.z);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Vector3f add(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3f subtract(Vector3f other) {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3f multiply(float scalar) {
        return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3f divide(float scalar) {
        if (Math.abs(scalar) < EPSILON) {
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

    public Vector3f multiplyLocal(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    public Vector3f divideLocal(float scalar) {
        if (Math.abs(scalar) < EPSILON) {
            throw new MathException("Division by zero");
        }
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        return this;
    }

    public Vector3f normalizeLocal() {
        float len = length();
        if (len < EPSILON) {
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

    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3f normalize() {
        float len = length();
        if (len < EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divide(len);
    }

    public float dot(Vector3f other) {
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
        float nx = this.y * other.z - this.z * other.y;
        float ny = this.z * other.x - this.x * other.z;
        float nz = this.x * other.y - this.y * other.x;

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
        return MathUtil.equals(this.x, other.x) &&
                MathUtil.equals(this.y, other.y) &&
                MathUtil.equals(this.z, other.z);
    }

    @Override
    public String toString() {
        return String.format("Vector3(%.4f, %.4f, %.4f)", x, y, z);
    }
}