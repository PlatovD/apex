package com.apex.math;

import com.apex.exception.MathException;

import static com.apex.core.Constants.DEFAULT_UP;
import static com.apex.math.MathUtil.EPSILON;

public class Matrix4x4 {
    private final float[][] data;

    public Matrix4x4() {
        data = new float[4][4];
    }

    public Matrix4x4(float[][] values) {
        if (values.length != 4 || values[0].length != 4) {
            throw new MathException("Matrix must be 4x4");
        }
        data = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(values[i], 0, data[i], 0, 4);
        }
    }

    public static Matrix4x4 identity() {
        Matrix4x4 m = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            m.data[i][i] = 1.0f;
        }
        return m;
    }

    public static Matrix4x4 zero() {
        return new Matrix4x4();
    }

    public float get(int row, int col) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new MathException("Index out of bounds");
        }
        return data[row][col];
    }

    public void set(int row, int col, float value) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new MathException("Index out of bounds");
        }
        data[row][col] = value;
    }

    public Matrix4x4 add(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.data[i][j] = this.data[i][j] + other.data[i][j];
            }
        }
        return result;
    }

    public Matrix4x4 subtract(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.data[i][j] = this.data[i][j] - other.data[i][j];
            }
        }
        return result;
    }

    public Vector4f multiply(Vector4f vector) {
        float x = data[0][0] * vector.getX() +
                data[0][1] * vector.getY() +
                data[0][2] * vector.getZ() +
                data[0][3] * vector.getW();
        float y = data[1][0] * vector.getX() +
                data[1][1] * vector.getY() +
                data[1][2] * vector.getZ() +
                data[1][3] * vector.getW();
        float z = data[2][0] * vector.getX() +
                data[2][1] * vector.getY() +
                data[2][2] * vector.getZ() +
                data[2][3] * vector.getW();
        float w = data[3][0] * vector.getX() +
                data[3][1] * vector.getY() +
                data[3][2] * vector.getZ() +
                data[3][3] * vector.getW();
        return new Vector4f(x, y, z, w);
    }

    public Matrix4x4 multiply(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    public Matrix4x4 addLocal(Matrix4x4 other) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] += other.data[i][j];
            }
        }
        return this;
    }

    public Matrix4x4 subtractLocal(Matrix4x4 other) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] -= other.data[i][j];
            }
        }
        return this;
    }

    public Matrix4x4 multiplyLocal(Matrix4x4 other) {
        float[][] temp = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                temp[i][j] = sum;
            }
        }
        for (int i = 0; i < 4; i++) {
            System.arraycopy(temp[i], 0, this.data[i], 0, 4);
        }
        return this;
    }

    public Matrix4x4 transposeLocal() {
        float temp;
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                temp = this.data[i][j];
                this.data[i][j] = this.data[j][i];
                this.data[j][i] = temp;
            }
        }
        return this;
    }

    public Matrix4x4 setToIdentity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] = (i == j) ? 1.0f : 0.0f;
            }
        }
        return this;
    }

    public Matrix4x4 setToZero() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] = 0.0f;
            }
        }
        return this;
    }

    public Matrix4x4 set(Matrix4x4 other) {
        for (int i = 0; i < 4; i++) {
            System.arraycopy(other.data[i], 0, this.data[i], 0, 4);
        }
        return this;
    }

    public Matrix4x4 transpose() {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.data[j][i] = this.data[i][j];
            }
        }
        return result;
    }

    public Matrix4x4 copy() {
        Matrix4x4 copy = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            System.arraycopy(this.data[i], 0, copy.data[i], 0, 4);
        }
        return copy;
    }

    public Matrix4x4 multiply(float scalar) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.data[i][j] = this.data[i][j] * scalar;
            }
        }
        return result;
    }

    public Matrix4x4 multiplyLocal(float scalar) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] *= scalar;
            }
        }
        return this;
    }

    public static Matrix4x4 lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        float zx = target.getX() - eye.getX();
        float zy = target.getY() - eye.getY();
        float zz = target.getZ() - eye.getZ();

        float invLenZ = 1.0f / (float) Math.sqrt(zx * zx + zy * zy + zz * zz);
        zx *= invLenZ;
        zy *= invLenZ;
        zz *= invLenZ;

        // Вектор X: up × Z
        float ux = up.getX(), uy = up.getY(), uz = up.getZ();
        float xx = uy * zz - uz * zy;
        float xy = uz * zx - ux * zz;
        float xz = ux * zy - uy * zx;

        float invLenX = 1.0f / (float) Math.sqrt(xx * xx + xy * xy + xz * xz);
        xx *= invLenX;
        xy *= invLenX;
        xz *= invLenX;

        // Вектор Y: Z × X
        float yx = zy * xz - zz * xy;
        float yy = zz * xx - zx * xz;
        float yz = zx * xy - zy * xx;

        // Вычисляем смещение (перенос обратно)
        float tx = -(xx * eye.getX() + xy * eye.getY() + xz * eye.getZ());
        float ty = -(yx * eye.getX() + yy * eye.getY() + yz * eye.getZ());
        float tz = -(zx * eye.getX() + zy * eye.getY() + zz * eye.getZ());

        // Создаём матрицу напрямую
        Matrix4x4 result = new Matrix4x4();
        result.data[0][0] = xx; result.data[0][1] = yx; result.data[0][2] = zx; result.data[0][3] = 0;
        result.data[1][0] = xy; result.data[1][1] = yy; result.data[1][2] = zy; result.data[1][3] = 0;
        result.data[2][0] = xz; result.data[2][1] = yz; result.data[2][2] = zz; result.data[2][3] = 0;
        result.data[3][0] = tx; result.data[3][1] = ty; result.data[3][2] = tz; result.data[3][3] = 1;

        return result;
    }

    public static Matrix4x4 lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, DEFAULT_UP);
    }

    public static Matrix4x4 perspective(float fov, float aspectRatio, float nearPlane, float farPlane) {
        if (nearPlane <= 0.0f || farPlane <= 0.0f || fov <= 0.0f) {
            throw new MathException("Near, far and fov must be positive");
        }
        if (Math.abs(farPlane - nearPlane) < EPSILON) {
            throw new MathException("Near and far planes are too close");
        }

        float f = (float) (1.0 / Math.tan(Math.toRadians(fov * 0.5)));

        Matrix4x4 result = new Matrix4x4();
        result.data[0][0] = f / aspectRatio;
        result.data[1][1] = f;
        result.data[2][2] = -(farPlane + nearPlane) / (farPlane - nearPlane);
        result.data[2][3] = -1.0f;
        result.data[3][2] = -2.0f * farPlane * nearPlane / (farPlane - nearPlane);
        result.data[3][3] = 0.0f;

        return result;
    }

    public Vector3f transformPerspective(Vector3f vertex) {
        float x = vertex.getX() * data[0][0] +
                vertex.getY() * data[1][0] +
                vertex.getZ() * data[2][0] +
                data[3][0];

        float y = vertex.getX() * data[0][1] +
                vertex.getY() * data[1][1] +
                vertex.getZ() * data[2][1] +
                data[3][1];

        float z = vertex.getX() * data[0][2] +
                vertex.getY() * data[1][2] +
                vertex.getZ() * data[2][2] +
                data[3][2];

        float w = vertex.getX() * data[0][3] +
                vertex.getY() * data[1][3] +
                vertex.getZ() * data[2][3] +
                data[3][3];

        if (Math.abs(w) < EPSILON) {
            throw new IllegalArgumentException("Divide by zero in perspective division");
        }

        return new Vector3f(x / w, y / w, z / w);
    }

    public static Matrix4x4 translation(Vector3f t) {
        Matrix4x4 result = identity();
        result.data[0][3] = t.getX();
        result.data[1][3] = t.getY();
        result.data[2][3] = t.getZ();
        return result;
    }

    public static Matrix4x4 rotationX(float angleRad) {
        Matrix4x4 result = identity();
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);
        result.data[1][1] = cos;
        result.data[1][2] = -sin;
        result.data[2][1] = sin;
        result.data[2][2] = cos;
        return result;
    }

    public static Matrix4x4 rotationY(float angleRad) {
        Matrix4x4 result = identity();
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);
        result.data[0][0] = cos;
        result.data[0][2] = sin;
        result.data[2][0] = -sin;
        result.data[2][2] = cos;
        return result;
    }

    public static Matrix4x4 rotationZ(float angleRad) {
        Matrix4x4 result = identity();
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);
        result.data[0][0] = cos;
        result.data[0][1] = -sin;
        result.data[1][0] = sin;
        result.data[1][1] = cos;
        return result;
    }

    public static Matrix4x4 scale(Vector3f s) {
        Matrix4x4 result = identity();
        result.data[0][0] = s.getX();
        result.data[1][1] = s.getY();
        result.data[2][2] = s.getZ();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Matrix4x4 other = (Matrix4x4) obj;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!MathUtil.equals(this.data[i][j], other.data[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Создаёт матрицу трансформации: T * R * S
     * Порядок: сначала масштаб, потом поворот, потом перенос.
     *
     * @param position Позиция (вектор переноса)
     * @param rotation Углы поворота в градусах по осям X, Y, Z
     * @param scale    Вектор масштабирования
     * @return Новая матрица 4x4
     */
    public static Matrix4x4 transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        // Масштабирование
        Matrix4x4 S = scale(scale);

        // Повороты (в порядке Z -> Y -> X, умножение справа)
        Matrix4x4 Rz = rotationZ((float) Math.toRadians(rotation.getZ()));
        Matrix4x4 Ry = rotationY((float) Math.toRadians(rotation.getY()));
        Matrix4x4 Rx = rotationX((float) Math.toRadians(rotation.getX()));
        Matrix4x4 R = Rx.multiply(Ry.multiply(Rz)); // R = Rx * Ry * Rz

        // Перенос
        Matrix4x4 T = translation(position);

        // Итог: T * R * S
        return T.multiply(R.multiply(S));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix4x4:\n");
        for (int i = 0; i < 4; i++) {
            sb.append("[ ");
            for (int j = 0; j < 4; j++) {
                sb.append(String.format("%.4f ", data[i][j]));
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}