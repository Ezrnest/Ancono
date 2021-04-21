package cn.ancono.math.algebra.linear;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.function.MathFunction;
import cn.ancono.utilities.Printer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The default implement for Matrix. The internal storage is based on two-dimensional array.
 *
 * @author liyicheng
 */
class DMatrix<T> extends Matrix<T> implements Serializable {
    final Object[][] data;

    /**
     * This method won't check mat's size and all the changed to the mat will be
     * reflected to this Matrix.
     */
    DMatrix(Object[][] mat, int row, int column, MathCalculator<T> mc) {
        super(row, column, mc);
        data = mat;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DMatrix) {
            DMatrix<?> m2 = (DMatrix<?>) obj;
            // if(m2.row==this.row&&m2.column==this.column){
            return Arrays.deepEquals(data, m2.data);
            // }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(data);
    }

    @NotNull
    @Override
    public String toString() {
        return Arrays.deepToString(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Matrix<T> negate() {
        Object[][] ne = new Object[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                ne[i][j] = getMc().negate((T) data[i][j]);
            }
        }
        return new DMatrix<>(ne, row, column, getMc());
    }

    /**
     * Return a MatrixN = {@code n*this}
     *
     * @return {@code n*this}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Matrix<T> multiplyNumber(long n) {
        Object[][] re = new Object[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                re[i][j] = getMc().multiplyLong((T) data[i][j], n);
            }
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int i, int j) {
        return (T) data[i][j];
    }

    @Override
    public Matrix<T> subMatrix(int i1, int j1, int i2, int j2) {
        subMatrixRangeCheck(i1, j1, i2, j2);
        // range check
        return new SubMatrix<>(data, i1, j1, i2 - i1 + 1, j2 - j1 + 1, getMc());
    }

    @Override
    public T[][] getValues() {
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) new Object[row][column];
        for (int i = 0; i < row; i++) {
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(data[i], 0, re[i], 0, column);
        }
        return re;
    }

    @Override
    public Matrix<T> transpose() {
        if (row != column) {
            throw new ArithmeticException("Row != Column");
        }
        Object[][] re = new Object[column][row];
        for (int l = 0; l < row; ++l) {
            for (int c = 0; c < column; ++c) {
                re[c][l] = data[l][c];
            }
        }
        return new DMatrix<>(re, column, row, getMc());
    }

    @Override
    public Matrix<T> exchangeRow(int r1, int r2) {
        rowRangeCheck(r1, r2);
        if (r1 == r2)
            return this;
        // override to reduce memory cost
        Object[][] re = new Object[row][];
        System.arraycopy(data, 0, re, 0, row);
        re[r1] = data[r2];
        re[r2] = data[r1];
        return new DMatrix<>(re, row, column, getMc());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Matrix<T> multiplyNumberRow(T n, int l) {
        rowRangeCheck(l);
        Object[][] re = new Object[row][];
        System.arraycopy(data, 0, re, 0, row);

        re[l] = new Object[column];
        for (int i = 0; i < column; ++i) {
            re[l][i] = getMc().multiply((T) data[l][i], n);
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Matrix<T> multiplyNumberRow(long n, int l) {
        rowRangeCheck(l);
        Object[][] re = new Object[row][];
        System.arraycopy(data, 0, re, 0, row);

        re[l] = new Object[column];
        for (int i = 0; i < column; ++i) {
            re[l][i] = getMc().multiplyLong((T) data[l][i], n);
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Matrix<T> multiplyAndAddRow(long k, int r1, int r2) {
        rowRangeCheck(r1, r2);
        if (r1 == r2) {
            throw new IllegalArgumentException("The identity row:" + r1);
        }
        Object[][] re = new Object[row][];
        System.arraycopy(data, 0, re, 0, row);

        re[r2] = new Object[column];
        // just copy this row

        for (int i = 0; i < column; ++i) {

            re[r2][i] = getMc().add((T) data[r2][i], getMc().multiplyLong((T) data[r1][i], k));
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Matrix<T> multiplyAndAddRow(T k, int r1, int r2) {
        rowRangeCheck(r1, r2);
        if (r1 == r2) {
            throw new IllegalArgumentException("The identity row:" + r1);
        }
        Object[][] re = new Object[row][];
        System.arraycopy(data, 0, re, 0, row);

        re[r2] = new Object[column];
        // just copy this row

        for (int i = 0; i < column; ++i) {
            re[r2][i] = getMc().add((T) data[r2][i], getMc().multiply((T) data[r1][i], k));
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Matrix<T> multiplyNumber(T n) {
        Object[][] re = new Object[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                re[i][j] = getMc().multiply((T) data[i][j], n);
            }
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Print the matrix using the Printer
     */
    @Override
    public void printMatrix() {
        Printer.printMatrix(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Matrix<T> applyFunction(MathFunction<T, T> f) {
        T[][] mat = (T[][]) new Object[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                mat[i][j] = f.apply((T) data[i][j]);
            }
        }
        return new DMatrix<>(mat, row, column, getMc());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Matrix<T> cofactor(int r, int c) {

        if (row == 1 || column == 1)
            throw new ArithmeticException("Too small for cofactor");

        // check for edge situation which can use sub-matrix instead
        //noinspection Duplicates
        if (r == 0) {
            if (c == 0) {
                return subMatrix(1, 1, row - 1, column - 1);
            } else if (c == column - 1) {
                return subMatrix(1, 0, row - 1, column - 2);
            }
        } else if (r == row - 1) {
            if (c == 0) {
                return subMatrix(0, 1, row - 2, column - 1);
            } else if (c == column - 1) {
                return subMatrix(0, 0, row - 2, column - 2);
            }
        }

        // create a new Matrix
        rowRangeCheck(r);
        columnRangeCheck(c);
        Object[][] ma = new Object[row - 1][column - 1];

        copyCofactor(row, column, r, c, data, ma);
        // copy ends
        return new DMatrix<>(ma, row - 1, column - 1, getMc());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Vector<T> getRow(int row) {
        // special simplification for row vectors
        rowRangeCheck(row);
        return new DVector<>((T[]) data[row], true, getMathCalculator());
    }


    private transient T det;
    private transient int rank = -1;
    private transient Matrix<T> inverse;

    @Override
    public T calDet() {
        if (det == null) {
            det = super.calDet();
        }
        return det;
    }

    @Override
    public int calRank() {
        if (rank < 0) {
            rank = super.calRank();
        }
        return rank;
    }

    @Override
    public Matrix<T> inverse() {
        if (inverse == null) {
            inverse = super.inverse();
        }
        return inverse;
    }
}
