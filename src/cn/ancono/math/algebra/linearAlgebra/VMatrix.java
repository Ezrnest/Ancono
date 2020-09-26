/**
 *
 */
package cn.ancono.math.algebra.linearAlgebra;

import cn.ancono.math.MathCalculator;
import cn.ancono.utilities.ArraySup;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Function;

/**
 * A matrix compoased of vectors.
 * @author lyc
 *
 */
final class VMatrix<T> extends Matrix<T> {
    private final Vector<T>[] vs;
    /**
     * Whether the vectors are row vector
     */
    private final boolean isRow;

    /**
     * @param row
     * @param column
     * @param isRow determines whether the vectors are row vector
     * @param mc
     */
    public VMatrix(Vector<T>[] vs, int row, int column, MathCalculator<T> mc, boolean isRow) {
        super(row, column, mc);
        this.vs = vs;
        this.isRow = isRow;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#getNumber(int, int)
     */
    @Override
    public T get(int i, int j) {
        rowRangeCheck(i);
        columnRangeCheck(j);
        if (isRow) {
            return vs[i].get(j);
        } else {
            return vs[j].get(i);
        }
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#getValues()
     */
    @Override
    public Object[][] getValues() {
        Object[][] obj;
        if (isRow) {
            obj = new Object[row][];
            for (int i = 0; i < row; i++) {
                obj[i] = vs[i].toArray();
            }
        } else {
            obj = new Object[row][column];
            for (int j = 0; j < column; j++) {
                for (int i = 0; i < row; i++) {
                    obj[i][j] = vs[j].get(i);
                }
            }
        }
        return obj;
    }

    @NotNull
    private Matrix<T> mapTo0(@SuppressWarnings("rawtypes") Function<Vector<T>, Vector> f) {
        @SuppressWarnings("unchecked")
        Vector<T>[] vn = ArraySup.mapTo(vs, f, Vector.class);
        return new VMatrix<>(vn, row, column, getMc(), isRow);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#negative()
     */
    @NotNull
    @Override
    public Matrix<T> negative() {
        return mapTo0(Vector::negative);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#transportMatrix()
     */
    @Contract(" -> new")
    @NotNull
    @Override
    public Matrix<T> transpose() {
        return new VMatrix<>(vs, column, row, getMc(), !isRow);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#multiplyNumber(long)
     */
    @NotNull
    @Override
    public Matrix<T> multiplyNumber(long n) {
        return mapTo0(v -> v.multiplyNumber(n));
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#multiplyNumber(java.lang.Object)
     */
    @NotNull
    @Override
    public Matrix<T> multiplyNumber(T n) {
        return mapTo0(v -> v.multiplyNumber(n));
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#cofactor(int, int)
     */
    @NotNull
    @Contract("_, _ -> new")
    @Override
    public Matrix<T> cofactor(int r, int c) {
        if (row <= 1 || column <= 1)
            throw new ArithmeticException("No cofactor");
        Object[][] mat = new Object[row - 1][column - 1];
        int x = 0, y = 0;
        for (int i = 0; i < row; i++) {
            if (i == r)
                continue;
            y = 0;
            for (int j = 0; j < column; j++) {
                if (j == c)
                    continue;
                mat[x][y] = get(i, j);
                y++;
            }
            x++;
        }
        return new DMatrix<>(mat, row - 1, column - 1, getMc());
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#replaceColumn(int, cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.AbstractVector)
     */
    @Override
    public Matrix<T> replaceColumn(int column, Vector<T> v) {
        if (isRow) {
            return super.replaceColumn(column, v);
        }
        columnRangeCheck(column);
        Vector<T>[] vn = Arrays.copyOf(vs, vs.length);
        vn[column] = v;
        return new VMatrix<>(vn, this.row, this.column, getMc(), isRow);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#replaceRow(int, cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.AbstractVector)
     */
    @Override
    public Matrix<T> replaceRow(int row, Vector<T> v) {
        if (!isRow) {
            return super.replaceRow(column, v);
        }
        rowRangeCheck(column);
        Vector<T>[] vn = Arrays.copyOf(vs, vs.length);
        vn[row] = v;
        return new VMatrix<>(vn, this.row, this.column, getMc(), isRow);
    }

    @Override
    public Vector<T> getRow(int row) {
        if (!isRow) {
            return super.getRow(row);
        }
        rowRangeCheck(row);
        return vs[row];
    }

    @Override
    public Vector<T> getColumn(int column) {
        if (isRow) {
            return super.getColumn(column);
        }
        columnRangeCheck(column);
        return vs[column];
    }


}
