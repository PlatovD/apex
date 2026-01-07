package com.apex.math;

import com.apex.math.exceptions.MathException;

public class Matrix3x3 {
    private final double[][] data;

    public Matrix3x3() {
        data = new double[3][3];
    }

    public Matrix3x3(double[][] values) {
        if (values.length != 3 || values[0].length != 3) {
            throw new MathException("Matrix must be 3x3");
        }
        data = new double[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(values[i], 0, data[i], 0, 3);
        }
    }

    public static Matrix3x3 identity() {
        Matrix3x3 m = new Matrix3x3();
        for (int i = 0; i < 3; i++) {
            m.data[i][i] = 1.0;
        }
        return m;
    }

    public static Matrix3x3 zero() {
        return new Matrix3x3();
    }

    public double get(int row, int col) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new MathException("Index out of bounds");
        }
        return data[row][col];
    }

    public void set(int row, int col, double value) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new MathException("Index out of bounds");
        }
        data[row][col] = value;
    }

    public Matrix3x3 add(Matrix3x3 other) {
        Matrix3x3 result = new Matrix3x3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.data[i][j] = this.data[i][j] + other.data[i][j];
            }
        }
        return result;
    }

    public Matrix3x3 subtract(Matrix3x3 other) {
        Matrix3x3 result = new Matrix3x3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.data[i][j] = this.data[i][j] - other.data[i][j];
            }
        }
        return result;
    }

    public Vector3f multiply(Vector3f vector) {
        double x = data[0][0] * vector.getX() +
                data[0][1] * vector.getY() +
                data[0][2] * vector.getZ();
        double y = data[1][0] * vector.getX() +
                data[1][1] * vector.getY() +
                data[1][2] * vector.getZ();
        double z = data[2][0] * vector.getX() +
                data[2][1] * vector.getY() +
                data[2][2] * vector.getZ();
        return new Vector3f(x, y, z);
    }

    public Matrix3x3 multiply(Matrix3x3 other) {
        Matrix3x3 result = new Matrix3x3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double sum = 0;
                for (int k = 0; k < 3; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    public Matrix3x3 addLocal(Matrix3x3 other) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.data[i][j] += other.data[i][j];
            }
        }
        return this;
    }

    public Matrix3x3 subtractLocal(Matrix3x3 other) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.data[i][j] -= other.data[i][j];
            }
        }
        return this;
    }

    public Matrix3x3 multiplyLocal(Matrix3x3 other) {
        double[][] temp = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double sum = 0;
                for (int k = 0; k < 3; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                temp[i][j] = sum;
            }
        }

        for (int i = 0; i < 3; i++) {
            System.arraycopy(temp[i], 0, this.data[i], 0, 3);
        }
        return this;
    }

    public Matrix3x3 transposeLocal() {
        double temp;
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 3; j++) {
                temp = this.data[i][j];
                this.data[i][j] = this.data[j][i];
                this.data[j][i] = temp;
            }
        }
        return this;
    }

    public Matrix3x3 setToIdentity() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.data[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }
        return this;
    }

    public Matrix3x3 setToZero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.data[i][j] = 0.0;
            }
        }
        return this;
    }

    public Matrix3x3 set(Matrix3x3 other) {
        for (int i = 0; i < 3; i++) {
            System.arraycopy(other.data[i], 0, this.data[i], 0, 3);
        }
        return this;
    }

    public Matrix3x3 transpose() {
        Matrix3x3 result = new Matrix3x3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.data[j][i] = this.data[i][j];
            }
        }
        return result;
    }

    public Matrix3x3 multiply(double scalar) {
        Matrix3x3 result = new Matrix3x3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.data[i][j] = this.data[i][j] * scalar;
            }
        }
        return result;
    }

    public Matrix3x3 multiplyLocal(double scalar) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.data[i][j] *= scalar;
            }
        }
        return this;
    }

    public Matrix3x3 copy() {
        Matrix3x3 copy = new Matrix3x3();
        for (int i = 0; i < 3; i++) {
            System.arraycopy(this.data[i], 0, copy.data[i], 0, 3);
        }
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Matrix3x3 other = (Matrix3x3) obj;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
        sb.append("Matrix3x3:\n");
        for (int i = 0; i < 3; i++) {
            sb.append("[ ");
            for (int j = 0; j < 3; j++) {
                sb.append(String.format("%.4f ", data[i][j]));
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}
