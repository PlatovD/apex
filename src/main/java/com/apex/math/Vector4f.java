package com.apex.math;

import com.apex.exception.MathException;

public class Vector4f {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4f() {
        this(0, 0, 0, 0);
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector4f other) {
        this(other.x, other.y, other.z, other.w);
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

    public float getW() {
        return w;
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

    public void setW(float w) {
        this.w = w;
    }

    public Vector4f add(Vector4f other) {
        return new Vector4f(
                this.x + other.x,
                this.y + other.y,
                this.z + other.z,
                this.w + other.w
        );
    }

    public Vector4f subtract(Vector4f other) {
        return new Vector4f(
                this.x - other.x,
                this.y - other.y,
                this.z - other.z,
                this.w - other.w
        );
    }

    public Vector4f multiply(float scalar) {
        return new Vector4f(
                this.x * scalar,
                this.y * scalar,
                this.z * scalar,
                this.w * scalar
        );
    }

    public Vector4f divide(float scalar) {
        if (Math.abs(scalar) < MathUtils.EPSILON) {
            throw new MathException("Division by zero");
        }
        return new Vector4f(
                this.x / scalar,
                this.y / scalar,
                this.z / scalar,
                this.w / scalar
        );
    }

    public Vector4f addLocal(Vector4f other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;
        return this;
    }

    public Vector4f subtractLocal(Vector4f other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        this.w -= other.w;
        return this;
    }

    public Vector4f multiplyLocal(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }

    public Vector4f divideLocal(float scalar) {
        if (Math.abs(scalar) < MathUtils.EPSILON) {
            throw new MathException("Division by zero");
        }
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        this.w /= scalar;
        return this;
    }

    public Vector4f normalizeLocal() {
        float len = length();
        if (len < MathUtils.EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divideLocal(len);
    }

    public Vector4f set(Vector4f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
        return this;
    }

    public Vector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public static Vector4f fromVector3(Vector3f v3, float w) {
        if (v3 == null) {
            throw new IllegalArgumentException("Vector3 cannot be null");
        }
        return new Vector4f(v3.getX(), v3.getY(), v3.getZ(), w);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public Vector3f toVector3() {
        return new Vector3f(x, y, z);
    }

    public Vector3f toVector3Projected() {
        if (Math.abs(w) < MathUtils.EPSILON) {
            throw new IllegalArgumentException("Cannot project Vector4 with w = 0 (division by zero)");
        }
        return new Vector3f(x / w, y / w, z / w);
    }

    public Vector4f divideByW() {
        if (Math.abs(w) < MathUtils.EPSILON) {
            throw new IllegalArgumentException("Cannot divide by w: w = 0");
        }
        return new Vector4f(x / w, y / w, z / w, 1.0f);
    }

    public Vector4f divideByWLocal() {
        if (Math.abs(w) < MathUtils.EPSILON) {
            throw new IllegalArgumentException("Cannot divide by w: w = 0");
        }
        this.x /= this.w;
        this.y /= this.w;
        this.z /= this.w;
        this.w = 1.0f;
        return this;
    }

    public Vector4f normalize() {
        float len = length();
        if (len < MathUtils.EPSILON) {
            throw new MathException("Cannot normalize zero-length vector");
        }
        return divide(len);
    }

    public float dot(Vector4f other) {
        return this.x * other.x + this.y * other.y +
                this.z * other.z + this.w * other.w;
    }

    public Vector4f copy() {
        return new Vector4f(this.x, this.y, this.z, this.w);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector4f other = (Vector4f) obj;
        return MathUtils.equals(this.x, other.x) &&
                MathUtils.equals(this.y, other.y) &&
                MathUtils.equals(this.z, other.z) &&
                MathUtils.equals(this.w, other.w);
    }

    @Override
    public String toString() {
        return String.format("Vector4(%.4f, %.4f, %.4f, %.4f)", x, y, z, w);
    }
}