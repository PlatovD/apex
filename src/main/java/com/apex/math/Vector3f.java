package com.apex.math;

import com.apex.core.Constants;

public class Vector3f {
    private float x;
    private float y;
    private float z;

    public Vector3f() {
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Vector3f other) {
        return Math.abs(x - other.x) < Constants.EPS && Math.abs(y - other.y) < Constants.EPS && Math.abs(z - other.z) < Constants.EPS;
    }

    public static Vector3f cross(Vector3f vector1, Vector3f vector2) {
        float x = vector1.y * vector2.z - vector1.z * vector2.y;
        float y = vector1.z * vector2.x - vector1.x * vector2.z;
        float z = vector1.x * vector2.y - vector1.y * vector2.x;

        return new Vector3f(x, y, z);
    }

    public static float dot(Vector3f vector1, Vector3f vector2) {
        return vector1.x * vector2.x +
                vector1.y * vector2.y +
                vector1.z * vector2.z;
    }

    public static Vector3f subtract(Vector3f vector1, Vector3f vector2) {
        float x = vector1.x - vector2.x;
        float y = vector1.y - vector2.y;
        float z = vector1.z - vector2.z;
        return new Vector3f(x, y, z);
    }

    public void sum(Vector3f vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
    }

    public void divide(double scalar) {
        if (Math.abs(scalar) < Constants.EPS) {
            throw new ArithmeticException("Деление на 0. Скаляр близок к нулю");
        }

        float invScalar = (float) (1 / scalar);
        x *= invScalar;
        y *= invScalar;
        z *= invScalar;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public void normalize() {
        double length = this.length();

        if (Math.abs(length) < Constants.EPS) {
            throw new ArithmeticException("Ошибка деления на 0. Передан нулевой вектор");
        }

        this.divide(length);
    }

    public void multiply(int scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}