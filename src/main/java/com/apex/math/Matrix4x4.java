package com.apex.math;

import com.apex.math.exceptions.MathException;

public class Matrix4x4 {
    private final double[][] data;

    public Matrix4x4() {
        data = new double[4][4];
    }

    public Matrix4x4(double[][] values) {
        if (values.length != 4 || values[0].length != 4) {
            throw new MathException("Matrix must be 4x4");
        }
        data = new double[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(values[i], 0, data[i], 0, 4);
        }
    }

    public static Matrix4x4 identity() {
        Matrix4x4 m = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            m.data[i][i] = 1.0;
        }
        return m;
    }

    public static Matrix4x4 zero() {
        return new Matrix4x4();
    }

    public double get(int row, int col) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new MathException("Index out of bounds");
        }
        return data[row][col];
    }

    public void set(int row, int col, double value) {
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
        double x = data[0][0] * vector.getX() +
                data[0][1] * vector.getY() +
                data[0][2] * vector.getZ() +
                data[0][3] * vector.getW();
        double y = data[1][0] * vector.getX() +
                data[1][1] * vector.getY() +
                data[1][2] * vector.getZ() +
                data[1][3] * vector.getW();
        double z = data[2][0] * vector.getX() +
                data[2][1] * vector.getY() +
                data[2][2] * vector.getZ() +
                data[2][3] * vector.getW();
        double w = data[3][0] * vector.getX() +
                data[3][1] * vector.getY() +
                data[3][2] * vector.getZ() +
                data[3][3] * vector.getW();
        return new Vector4f(x, y, z, w);
    }

    public Matrix4x4 multiply(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double sum = 0;
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
        double[][] temp = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double sum = 0;
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
        double temp;
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
                this.data[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }
        return this;
    }

    public Matrix4x4 setToZero() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] = 0.0;
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

    public Matrix4x4 multiply(double scalar) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.data[i][j] = this.data[i][j] * scalar;
            }
        }
        return result;
    }

    public Matrix4x4 multiplyLocal(double scalar) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] *= scalar;
            }
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Matrix4x4 other = (Matrix4x4) obj;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!MathUtils.equals(this.data[i][j], other.data[i][j])) {
                    return false;
                }
            }
        }
        return true;
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
