package cn.ancono.math.algebra.linearAlgebra;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.MathObjectExtend;
import cn.ancono.math.algebra.abstractAlgebra.calculator.ModuleCalculator;
import cn.ancono.math.equation.EquationSolver;
import cn.ancono.math.equation.SVPEquation;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.MathCalculatorAdapter;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.math.numberModels.api.NumberFormatter;
import cn.ancono.math.numberModels.structure.Polynomial;
import cn.ancono.math.numberTheory.combination.CombUtils;
import cn.ancono.math.property.Invertible;
import cn.ancono.utilities.ArraySup;
import cn.ancono.utilities.ModelPatterns;
import cn.ancono.utilities.Printer;
import cn.ancono.utilities.StringSup;
import cn.ancono.utilities.structure.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A matrix is like a two dimension array,but the number in the matrix
 * is unchangeable. A n*m matrix has n rows and m columns. m,n=1,2,3,4,5...,
 * this matrix also provides some basic matrix operations such as elementary
 * operations of matrix.
 * <p>Numbers in this matrix are flexible,but using Fraction is one of the suggested ways.Using fraction  means
 * this matrix can do calculations in rational numbers without losing precision.But overflow
 * or underflow are not considered.This is because this kind of implement is thought better
 * when considering time performance,number range , memory cost and other issues comprehensively.
 * You can also use primary type such as {@code long} or {@code int} when creating the matrix and
 * convention will be done automatically.
 * <p>
 * In addition to the operations provided by matrix object,
 * the matrix class itself provides several static methods for basic matrix calculation such as {@link
 * #addMatrix(Matrix, Matrix)}
 * and {@link #multiplyMatrix(Matrix, Matrix)}.To do more complex operations or to have better
 * time performance , you can seek {@linkplain MatrixSup} for some useful methods.
 * <p>
 * To create a Matrix , you can either use a two-dimension array as parameter or
 * directly call some prepared methods.No construction function is available for the safety of the
 * immutability.
 *
 * @author lyc
 */
@SuppressWarnings("WeakerAccess")
public abstract class Matrix<T> extends MathObjectExtend<T> implements Invertible<Matrix<T>> {

    /**
     * Row count:
     */
    protected final int row;
    /**
     * Column count:
     */
    protected final int column;

    /**
     * Must set row and column count first.
     */
    protected Matrix(int row, int column, MathCalculator<T> mc) {
        super(mc);
        this.row = row;
        this.column = column;
    }

    /*
     * data:int[row][column],the numbers in this matrix
     */

    /**
     * Return the row count of this matrix.
     *
     * @return row count of this matrix
     */
    public int getRowCount() {
        return row;
    }

    /**
     * Return the row column of this matrix.
     *
     * @return row column of this matrix
     */
    public int getColumnCount() {
        return column;
    }

    /**
     * Determines whether the matrix is a square matrix.
     */
    public boolean isSquare() {
        return row == column;
    }

    protected void requireSquare() {
        if (!isSquare()) {
            throw new ArithmeticException("The matrix is not square!");
        }
    }

    public <S> boolean sizeEquals(Matrix<S> m) {
        return column == m.column && row == m.row;
    }


    /**
     * Get the number of the matrix in position i,j.The index is start from 0.
     *
     * @param i the row index
     * @param j the column index
     * @return this[i][j]
     */
    public abstract T getNumber(int i, int j);

    /**
     * Return a copy of the values in this matrix. The returned array is a copy
     * of the value so the change of it won't change the original values in this
     * Matrix. The returned array assures that {@code arr[i][j] == getNumber(i,j)}
     *
     * @return a two-dimension array containing the values in this matrix
     */
    public abstract Object[][] getValues();

    /**
     * Returns a copy of the values in this matrix. If the size of the array is not enough,
     * this method will try to creates a new array.
     *
     * @param arr the array to receive the data
     * @param <N> must be a super type of T.
     */
    @SuppressWarnings("unchecked")
    public <N> N[][] getValues(N[][] arr) {
        if (arr.length < row) {
            arr = Arrays.copyOf(arr, row);
        }
        for (int i = 0; i < row; i++) {
            N[] r = arr[i];
            if (r.length < column)
                r = Arrays.copyOf(r, column);
            for (int j = 0; j < column; j++) {
                r[j] = (N) getNumber(i, j);
            }
            arr[i] = r;
        }
        return arr;
    }

    public Vector<T> getColumn(int column) {
        columnRangeCheck(column);
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[this.row];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = getNumber(i, column);
        }
        return new DVector<>(arr, false, getMathCalculator());
    }

    public Vector<T> getRow(int row) {
        rowRangeCheck(row);
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[this.column];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = getNumber(row, i);
        }
        return new DVector<>(arr, true, getMathCalculator());
    }

    public List<Vector<T>> rowVectors() {
        List<Vector<T>> list = new ArrayList<>(row);
        for (int i = 0; i < row; i++) {
            list.add(getRow(i));
        }
        return list;
    }

    public List<Vector<T>> columnVectors() {
        List<Vector<T>> list = new ArrayList<>(column);
        for (int i = 0; i < column; i++) {
            list.add(getColumn(i));
        }
        return list;
    }


    /**
     * Apply the function to this matrix and returns the result.
     */
    public Matrix<T> applyFunction(MathFunction<T, T> f) {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                mat[i][j] = f.apply(getNumber(i, j));
            }
        }
        return new DMatrix<>(mat, row, column, getMc());
    }

    /**
     * Check if the numbers in {@code this} and {@code obj} are the identity.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Matrix) {
            Matrix<?> mat = (Matrix<?>) obj;
            if (!getMc().equals(mat.getMc()))
                return false;
            return Arrays.deepEquals(getValues(), mat.getValues());
        }
        return false;
//		return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = getMc().hashCode();
        hash = hash * 37 + column;
        hash = hash * 37 + row;
        hash = hash * 31 + getNumber(0, 0).hashCode();
        return hash;
    }

    /**
     * Return a MatrixN = {@code -this}
     *
     * @return {@code -this}
     */
    public abstract Matrix<T> negative();

    /**
     * Return a MatrixN = this<sup>T</sup>.The new Matrix's row count = this.column
     * , new Matrix's column count = this.row.
     *
     * @return <tt>this<sup>T</sup></tt>
     */
    public Matrix<T> transportMatrix() {
        Object[][] re = new Object[column][row];
        for (int l = 0; l < row; ++l) {
            for (int c = 0; c < column; ++c) {
                re[c][l] = getNumber(l, c);
            }
        }
        return new DMatrix<>(re, column, row, getMc());
    }

    public Matrix<T> adjugateMatrix() {
        Object[][] re = new Object[column][row];
        var mc = getMc();
        for (int l = 0; l < row; ++l) {
            for (int c = 0; c < column; ++c) {
                var t = cofactor(c, l).calDet();
                if ((l + c) % 2 != 0) {
                    t = mc.negate(t);
                }
                re[l][c] = t;
            }
        }
        return new DMatrix<>(re, column, row, mc);
    }

    /**
     * Return a MatrixN = {@code n*this} .
     * <p>
     * This is one of the elementary transformations.
     *
     * @return {@code n*this}
     */
    public abstract Matrix<T> multiplyNumber(long n);

    /**
     * Return a MatrixN = {@code n*this} .
     * <p>
     * This is one of the elementary transformations.
     *
     * @return {@code n*this}
     */
    public abstract Matrix<T> multiplyNumber(T n);

    /**
     * Multiply the specific column with the given T.
     * <p>
     * This is one of the elementary transformations.
     *
     * @param n a number except 0
     * @param c the index of the column to multiply
     * @return a MatrixN as the change result.
     */
    public Matrix<T> multiplyNumberColumn(T n, int c) {
        columnRangeCheck(c);
        if (getMc().isEqual(n, getMc().getZero())) {
            throw new IllegalArgumentException("Multiply by 0");
        }
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        for (int i = 0; i < row; i++) {
            re[i][c] = getMc().multiply(re[i][c], n);
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Multiply the specific column with the given T.
     * <p>
     * This is one of the elementary transformations.
     *
     * @param n a number except 0
     * @param c the index of the column to multiply
     * @return a MatrixN as the change result.
     */
    @SuppressWarnings("unused")
    public Matrix<T> multiplyNumberColumn(long n, int c) {
        columnRangeCheck(c);
        if (n == 0) {
            throw new IllegalArgumentException("Multiply by 0");
        }
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        for (int i = 0; i < row; i++) {
            re[i][c] = getMc().multiplyLong(re[i][c], n);
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Multiply the specific column with the given T.
     * <p>
     * This is one of the elementary transformations.
     *
     * @param n a number except 0
     * @param r the index of the row to multiply
     * @return a MatrixN as the change result.
     */
    public Matrix<T> multiplyNumberRow(T n, int r) {
        rowRangeCheck(r);
        if (getMc().isEqual(n, getMc().getZero())) {
            throw new IllegalArgumentException("Multiply by 0");
        }
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        for (int i = 0; i < column; i++) {
            re[r][i] = getMc().multiply(re[r][i], n);
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Multiply the specific column with the given T.
     * <p>
     * This is one of the elementary transformations.
     *
     * @param n a number except 0
     * @param r the index of the row to multiply
     * @return a MatrixN as the change result.
     */
    @SuppressWarnings("unused")
    public Matrix<T> multiplyNumberRow(long n, int r) {
        rowRangeCheck(r);
        if (n == 0) {
            throw new IllegalArgumentException("Multiply by 0");
        }
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        for (int i = 0; i < column; i++) {
            re[r][i] = getMc().multiplyLong(re[r][i], n);
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Exchange two row in the matrix and return the new Matrix.
     * <p>
     * This is one of the elementary transformations.
     *
     * @param r1 a row
     * @param r2 another row
     * @return an exchanged Matrix
     */
    public Matrix<T> exchangeRow(int r1, int r2) {
        rowRangeCheck(r1, r2);
        if (r1 == r2) {
            return this;
        }
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        for (int c = 0; c < column; ++c) {
            T t = re[r1][c];
            re[r1][c] = re[r2][c];
            re[r2][c] = t;
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Exchange two column in the matrix and return the new Matrix.
     * <p>
     * This is one of the elementary transformations.
     *
     * @param c1 a column
     * @param c2 another column
     * @return an exchanged Matrix
     */
    public Matrix<T> exchangeColumn(int c1, int c2) {
        columnRangeCheck(c1, c2);
        if (c1 == c2) {
            return this;
        }
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        for (int l = 0; l < column; ++l) {
            T t = re[l][c1];
            re[l][c1] = re[l][c2];
            re[l][c2] = t;
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Multiply one row in this matrix with k and add the result to another
     * row.
     * <p>
     * This is one of the elementary transformations.
     *
     * @param k  multiplier
     * @param r1 the row to multiply
     * @param r2 the row to add to
     * @return the result Matrix
     */
    @SuppressWarnings("unused")
    public Matrix<T> multiplyAndAddRow(long k, int r1, int r2) {
        rowRangeCheck(r1, r2);
        if (r1 == r2) {
            throw new IllegalArgumentException("The identity row:" + r1);
        }
        var mc = getMc();
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        for (int i = 0; i < column; i++) {
            re[r2][i] = mc.add(re[r2][i], getMc().multiplyLong(re[r1][i], k));
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Multiply one row in this matrix with k and add the result to another
     * row.
     * <p>
     * This is one of the elementary transformations.
     *
     * @param k  multiplier
     * @param r1 the row to multiply
     * @param r2 the row to add to
     * @return the result Matrix
     */
    public Matrix<T> multiplyAndAddRow(T k, int r1, int r2) {
        rowRangeCheck(r1, r2);
        if (r1 == r2) {
            throw new IllegalArgumentException("The identity row:" + r1);
        }
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        var mc = getMc();
        for (int i = 0; i < column; i++) {
            re[r2][i] = mc.add(re[r2][i], getMc().multiply(re[r1][i], k));
        }
        return new DMatrix<>(re, row, column, getMc());
    }

    /**
     * Multiply one column in this matrix with k and add the result to another column.
     *
     * @param k  multiplier
     * @param c1 the column to multiply
     * @param c2 the column to add to
     * @return the result Matrix
     */
    @SuppressWarnings("unused")
    public Matrix<T> multiplyAndAddColumn(long k, int c1, int c2) {
        columnRangeCheck(c1, c1);
        if (c1 == c2) {
            throw new IllegalArgumentException("The identity column:" + c1);
        }
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        var mc = getMc();
        for (int i = 0; i < row; i++) {
            re[i][c2] = mc.add(re[i][c2], mc.multiplyLong(re[i][c1], k));
        }
        return new DMatrix<>(re, row, column, mc);
    }

    /**
     * Multiply one column in this matrix with k and add the result to another column.
     *
     * @param k  multiplier
     * @param c1 the column to multiply
     * @param c2 the column to add to
     * @return the result Matrix
     */
    public Matrix<T> multiplyAndAddColumn(T k, int c1, int c2) {
        columnRangeCheck(c1, c1);
        if (c1 == c2) {
            throw new IllegalArgumentException("The identity column:" + c1);
        }
        var mc = getMc();
        @SuppressWarnings("unchecked")
        T[][] re = (T[][]) getValues();
        for (int i = 0; i < row; i++) {
            re[i][c2] = mc.add(re[i][c2], mc.multiply(re[i][c1], k));
        }
        return new DMatrix<>(re, row, column, mc);
    }


    public Matrix<T> applyPolynomial(Polynomial<T> p) {
        requireSquare();
        var mc = getMc();
        Matrix<T> re = Matrix.diag(p.constant(), column, mc);
        for (int i = p.getDegree() - 1; i > -1; i--) {
            re = multiplyMatrix(re, this);
            re = addMatrix(Matrix.diag(p.getCoefficient(i), column, mc), re);
        }
        return re;
    }


    /**
     * Return det(this).
     *
     * @return det(this)
     * @throws ArithmeticException if this Matrix is not a square matrix.
     */
    public T calDet() {
        requireSquare();
        if (row <= 3) {
            return calDetDefault();
        }
        try {
            return MatrixSup.fastDet(this);
        } catch (UnsupportedOperationException ignore) {
        }
        return calDetDefault();
        //calculate by separating first row
//		boolean turn = false;
//		for(int i=0;i<column;i++){
//            T det = mc.multiply(getNumber(0, i), cofactor(0, i).calDet());
//			if(turn){
//                sum = mc.subtract(sum, det);
//			}else{
//                sum = mc.add(sum, det);
//			}
//			turn = !turn;
//		}
//		return sum;
    }

    /**
     * Return det(this), this method computes the determinant of this
     * matrix by the definition. It can only provide a correct result  but  its time performance may
     * not be the best and can vary a lot.
     *
     * @return det(this)
     * @throws ArithmeticException if this Matrix is not a square matrix.
     */
    public T calDetDefault() {
        //just calculate the value by recursion definition.

        var mc = getMc();
        //some fast implement when the order is below 4
        if (row == 3) {
            T sum = mc.multiply(mc.multiply(getNumber(0, 0), getNumber(1, 1)), getNumber(2, 2));
            sum = mc.add(sum, mc.multiply(mc.multiply(getNumber(0, 1), getNumber(1, 2)), getNumber(2, 0)));
            sum = mc.add(sum, mc.multiply(mc.multiply(getNumber(0, 2), getNumber(1, 0)), getNumber(2, 1)));
            sum = mc.subtract(sum, mc.multiply(mc.multiply(getNumber(0, 0), getNumber(1, 2)), getNumber(2, 1)));
            sum = mc.subtract(sum, mc.multiply(mc.multiply(getNumber(0, 1), getNumber(1, 0)), getNumber(2, 2)));
            sum = mc.subtract(sum, mc.multiply(mc.multiply(getNumber(0, 2), getNumber(1, 1)), getNumber(2, 0)));
            return sum;
        } else if (row == 1) {
            return getNumber(0, 0);
        } else if (row == 2) {
            return mc.subtract(mc.multiply(getNumber(0, 0), getNumber(1, 1)), mc.multiply(getNumber(0, 1), getNumber(1, 0)));
        }
        return det0(new int[row], 0, mc.getOne());
    }

    private T det0(int[] selected, int index, T mulTemp) {
        var mc = getMc();
        if (index == row) {
            if (CombUtils.inverseCount(selected) % 2 == 0) {
                return mulTemp;
            } else {
                return mc.negate(mulTemp);
            }
        }
        T sum = mc.getZero();
        for (int i = 0; i < column; i++) {
            if (ArraySup.arrayContains(i, selected, 0, index)) {
                continue;
            }
            selected[index] = i;
            sum = mc.add(sum, det0(selected, index + 1, mc.multiply(mulTemp, getNumber(index, i))));
        }
        return sum;
    }


    /**
     * Returns the factor of the given rows and columns in this matrix.
     *
     * @param rows    the rows of the factor
     * @param columns the columns of the factor
     */
    public Matrix<T> factor(int[] rows, int[] columns) {
        if (rows.length == 0 || columns.length == 0) {
            throw new IllegalArgumentException("rows or columns are empty!");
        }
        rowRangeCheck(rows);
        columnRangeCheck(columns);
        Object[][] ndata = new Object[rows.length][columns.length];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < columns.length; j++) {
                ndata[i][j] = getNumber(rows[i], columns[j]);
            }
        }
        return new DMatrix<>(ndata, rows.length, columns.length, getMc());
    }


    /**
     * Return the cofactor of the element in row {@code r} and column {@code c}.
     *
     * @return the cofactor matrix.
     * @throws IndexOutOfBoundsException if r or c is out of range
     * @throws ArithmeticException       if this Matrix's row count or column count is less than two,
     *                                   which means it doesn't have cofactor matrix
     */
    @SuppressWarnings("Duplicates")
    public Matrix<T> cofactor(int r, int c) {

        if (row == 1 || column == 1)
            throw new ArithmeticException("Too small for cofactor");

        // check for edge situation which can use sub-matrix instead
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
        var data = getValues();
        Object[][] ma = new Object[row - 1][column - 1];

        copyCofactor(row, column, r, c, data, ma);
        // copy ends
        return new DMatrix<>(ma, row - 1, column - 1, getMc());
    }


    static void copyCofactor(int row, int column, int r, int c, Object[][] data, Object[][] ma) {
        for (int i = 0; i < r; i++) {
            // upper-left
            if (c > 0) System.arraycopy(data[i], 0, ma[i], 0, c);
            // upper-right
            if (c < column - 1) System.arraycopy(data[i], c + 1, ma[i], c, column - c - 1);
        }
        for (int i = r + 1; i < row; i++) {
            // downer-left
            if (c > 0) System.arraycopy(data[i], 0, ma[i - 1], 0, c);
            // downer-right
            if (c < column - 1) System.arraycopy(data[i], c + 1, ma[i - 1], c, column - c - 1);
        }
    }

    /**
     * Use elementary operations that don't change the det of this matrix to transform this
     * matrix to an upper triangle matrix.
     *
     * @return a new upper triangle matrix
     */
    public Matrix<T> toUpperTriangle() {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) getValues();
        toUpperTri0(mat, row, column);
        return new DMatrix<>(mat, row, column, getMc());
    }

    /**
     * Return a series of operations that can transform the matrix to an upper-triangle matrix.
     * The method is normally used in calculating this^-1.
     *
     * @return a List of operation,the order is specified.
     * @see MatrixOperation
     */
    public MatResult<T> toUpperTriangleWay() {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) getValues();
        List<MatrixOperation<T>> ops = toUpperTri0(mat, row, column);
        Matrix<T> res = new DMatrix<>(mat, row, column, getMc());
        return new MatResult<>(ops, res);
    }

    /**
     * A basic operation to transform this matrix to upper triangle
     */
    List<MatrixOperation<T>> toUpperTri0(T[][] mat, int row, int column) {
        List<MatrixOperation<T>> ops = new LinkedList<>();
        //use Gaussian elimination
        int target = 0;
        //target row
        for (int i = 0; i < row - 1 && target < column; target++) {
//			Printer.printMatrix(mat);
            //search for a non-zero row
            T f = mat[i][target];
            if (getMc().isEqual(f, getMc().getZero())) {
                //find another one
                boolean found = false;
                for (int a = i + 1; a < row; a++) {
                    if (!getMc().isEqual(mat[a][target], getMc().getZero())) {
                        f = mat[a][target];
                        T[] t = mat[a];
                        mat[a] = mat[i];
                        mat[i] = t;
                        found = true;
                        ops.add(MatrixOperation.exchangeRow(i, a));
                    }
                }
                if (!found) {
                    continue;
                }
            }
            for (int j = i + 1; j < row; j++) {
                if (!getMc().isEqual(mat[j][target], getMc().getZero())) {
                    T mul = getMc().negate(getMc().divide(mat[j][target], f));
                    mat[j][target] = getMc().getZero();
                    for (int c = target + 1; c < column; c++) {
                        mat[j][c] = getMc().add(mat[j][c], getMc().multiply(mat[i][c], mul));
                    }
                    ops.add(MatrixOperation.multiplyAddRow(i, j, mul));
                }
            }
            i++;
        }
        //do exchanged to move 0 lines to below
        Entry[] ens = new Entry[row];
        for (int i = 0; i < row; i++) {
            int v = column;
            for (int c = 0; c < column; c++) {
                if (!getMc().isEqual(mat[i][c], getMc().getZero())) {
                    v = c;
                    break;
                }
            }
            ens[i] = new Entry(i, v);
        }
        //use insert sort to mark operation easier
        for (int i = 0; i < row; i++) {
            for (int j = row - 1; j > i; j--) {
                if (ens[j].value < ens[j - 1].value) {
                    Entry et = ens[j];
                    ens[j] = ens[j - 1];
                    ens[j - 1] = et;
                    ops.add(MatrixOperation.exchangeRow(j, j - 1));
                }
            }
        }
        T[][] re = Arrays.copyOf(mat, row);
        for (int i = 0; i < row; i++) {
            mat[i] = re[ens[i].key];
        }
        return ops;
    }

    /**
     * Transform the matrix to step-matrix.Which is like a step-matrix but implements
     * following features:<p>
     * <ul>
     * <li>1.The first number in a non-zero line is ONE.
     * <li>2.Except the first number in a non-zero line,the other numbers in the identity column are ZERO.
     * </ul>
     *
     * @param row    row count
     * @param column column count
     * @return a list of operations with which the matrix can be transformed to normative step-matrix.
     */
    List<MatrixOperation<T>> toStepMatrix(T[][] mat, int row, int column) {
        List<MatrixOperation<T>> ops = toUpperTri0(mat, row, column);
        var mc = getMc();
//		Printer.print("TRI :: ");
//		Printer.printMatrix(mat);
        //transform to step matrix first.

        /* First we locate the first non-zero element in each line ,
         * then we turn the others in the identity row to 0.
         */
        int lastTar = -1;
        for (int i = 0; i < row; i++) {
            int tar = -1;
            for (int c = lastTar + 1; c < column; c++) {
                if (!mc.isZero(mat[i][c])) {
                    //non-zero
                    tar = c;
                    break;
                }
            }
            if (tar < 0) {
                // zero line
                break;
            }
            //turn to 1

            makeUnit(mat, column, ops, i, tar);

            for (int r = 0; r < i; r++) {
                if (!mc.isZero(mat[r][tar])) {
                    T fra = mc.negate(mat[r][tar]);
                    for (int j = tar + 1; j < column; j++) {
                        mat[r][j] = mc.add(mat[r][j], mc.multiply(mat[i][j], fra));
                    }
                    mat[r][tar] = mc.getZero();
                    ops.add(MatrixOperation.multiplyAddRow(i, r, fra));
//					Printer.print(ops.get(ops.size()-1).toDetail());
//					Printer.printMatrix(mat);
                }

            }
        }

        return ops;
    }

    private void makeUnit(T[][] mat, int column, List<MatrixOperation<T>> ops, int i, int tar) {
        var mc = getMc();
        if (!mc.isEqual(mat[i][tar], mc.getOne())) {
            T f = mc.reciprocal(mat[i][tar]);
            mat[i][tar] = mc.getOne();
            for (int j = tar + 1; j < column; j++) {
                mat[i][j] = mc.multiply(mat[i][j], f);
            }
            ops.add(MatrixOperation.multiplyRow(i, f));
//				Printer.print(ops.get(ops.size()-1).toDetail());
//				Printer.printMatrix(mat);
        }
    }


    /**
     * A basic operation to transform this matrix to like identity
     *
     * @param mat will be changed by this method
     * @throws ArithmeticException if cannot
     */
    List<MatrixOperation<T>> toIdentity(T[][] mat, int row, int column) {
        var mc = getMc();
        List<MatrixOperation<T>> ops = toUpperTri0(mat, row, column);
        int bound = Math.min(column, row);
        //check for ability
        for (int i = 0; i < bound; i++) {
            if (mc.isEqual(mat[i][i], mc.getZero())) {
                throw new ArithmeticException("Cannot trans to Identity:Not Full Rank");
            }
        }
        for (int i = 0; i < bound; i++) {
            makeUnit(mat, column, ops, i, i);
        }

        for (int i = bound - 1; i > -1; i--) {
            for (int j = i + 1; j < column; j++) {
                if (!mat[i][j].equals(mc.getZero())) {
                    T f = mc.negate(mat[i][j]);
                    mat[i][j] = mc.getZero();
                    ops.add(MatrixOperation.multiplyAddRow(j, i, f));
                }


            }
        }
        return ops;
    }
//	List<MatrixOperation<T>> toNormativeStep(T[][] mat,int row,int column){
//        return toStepMatrix(mat, row, column);
//	}

    /**
     * Return a List of operation with which the matrix can be transformed to identity matrix.
     * The sufficient and necessary condition is that this matrix is a full-rank matrix.(Firstly,this matrix should
     * be a square matrix.)
     *
     * @return a List of operation,the order is specified.
     * @see MatrixOperation
     */
    @SuppressWarnings("unchecked")
    public List<MatrixOperation<T>> toIdentityWay() {
        requireSquare();
        return toIdentity((T[][]) getValues(), row, column);
    }

    /**
     * Transform the matrix to normative step-matrix.Which is like a step-matrix but implements
     * following features:<p>
     * <ul>
     * <li>1.The first number in a non-zero line is ONE.
     * <li>2.Except the first number in a non-zero line,the other numbers in the identity column are ZERO.
     * </ul>
     * The step will be recorded and returned in the result.The transformed matrix will be created as a new
     * matrix and returned too.
     *
     * @return the operations and the result matrix.
     */
    public MatResult<T> toStepMatrix() {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) getValues();
        List<MatrixOperation<T>> ops = toStepMatrix(mat, row, column);
        Matrix<T> res = new DMatrix<>(mat, row, column, getMc());
        return new MatResult<>(ops, res);
    }

    /**
     * Returns the trail if this matrix.
     *
     * @return {@code tr(this)}
     */
    public T trail() {
        if (column != row) {
            throw new ArithmeticException("Not square");
        }
        T tr = getNumber(0, 0);
        for (int i = 1; i < row; i++) {
            tr = getMc().add(tr, getNumber(i, i));
        }
        return tr;
    }

    /**
     * Creates the eigenvalue equation of this matrix. It is required that
     * this matrix is a square matrix.
     *
     * @return an equation
     */
    public SVPEquation<T> eigenvalueEquation() {
        return SVPEquation.fromPolynomial(eigenPolynomial());
    }

    /**
     * Creates the eigenvalue equation of this matrix. It is required that
     * this matrix is a square matrix.
     *
     * @return an equation
     */
    public Polynomial<T> eigenPolynomial() {
        if (column != row) {
            throw new ArithmeticException("Not square");
        }
        var mc = getMc();
        //transform to a temporary matrix to compute the determinant
        //in multinomial
        MathCalculator<Polynomial<T>> mct = Polynomial.getCalculator(mc);
        Matrix<Polynomial<T>> tmat = this.mapTo(x -> Polynomial.constant(mc, x), mct),
                eigen = Matrix.diag(Polynomial.oneX(mc), row, mct);
        tmat = minusMatrix(eigen, tmat);
        return tmat.calDet();
    }

    /**
     * Returns the eigen-matrix:
     * <pre>λI-this</pre>which is a matrix of polynomial.
     */
    public Matrix<Polynomial<T>> eigenmatrix() {
        return MatrixSup.eigenmatrix(this);
    }

    /**
     * Returns the matrix of subtracting this matrix by <code>tI</code>, a diagonal matrix.
     */
    public Matrix<T> eigenmatrix(T t) {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[row][column];
        var mc = getMc();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (i == j) {
                    mat[i][j] = mc.subtract(getNumber(i, j), t);
                } else {
                    mat[i][j] = getNumber(i, j);
                }

            }
        }
        return new DMatrix<>(mat, row, column, mc);
    }

    /**
     * Returns a matrix which is similar to the matrix given and is a diagonal matrix.
     *
     * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to
     *                       the degree of the equation.
     */
    public Matrix<T> similarDiag(EquationSolver<T, SVPEquation<T>> equationSolver) {
        List<T> eigenvalues = eigenvalues(equationSolver);
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) eigenvalues.toArray();
        return Matrix.diag(arr, getMc());
    }

    /**
     * Computes the eigenvalues of this matrix.
     * <p>For example, assume {@code this} =
     * <pre>(1 0)
     * (0,1)</pre>
     * then this method will return a list of {@code [1,1]}
     *
     * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to
     *                       the degree of the equation.
     * @return a list of eigenvalues
     */
    public List<T> eigenvalues(EquationSolver<T, SVPEquation<T>> equationSolver) {
        SVPEquation<T> equation = eigenvalueEquation();
        return equationSolver.solve(equation);
    }

    /**
     * Computes the eigenvalues of this matrix and their corresponding vectors.
     *
     * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to
     *                       the degree of the equation.
     */
    public List<Pair<T, Vector<T>>> eigenvaluesAndVectors(EquationSolver<T, SVPEquation<T>> equationSolver) {
        List<T> eigenvalues = eigenvalues(equationSolver);
        //vectors
        List<Pair<T, Vector<T>>> result = new ArrayList<>(eigenvalues.size());
        int size = eigenvalues.size();
        var mc = getMc();
        for (int i = 0; i < size; ) {
            T x = eigenvalues.get(i);
            int times = 1;
            while (++i < size) {
                T y = eigenvalues.get(i);
                if (mc.isEqual(x, y)) {
                    times++;
                } else {
                    break;
                }
            }
            Matrix<T> A = minusMatrix(this, Matrix.diag(x, row, mc));
            LinearEquationSolution<T> solution = MatrixSup.solveHomogeneousLinearEquation(A);
            Vector<T>[] ks = solution.getBaseSolutions();
            for (Vector<T> k1 : ks) {
                result.add(new Pair<>(x, k1));
            }
            if (times > ks.length) {
                for (int k = ks.length; k < times; k++) {
                    result.add(new Pair<>(x, ks[0]));
                }
            }
        }

        return result;
    }

    private static class Entry implements Comparable<Entry> {
        private Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(@NotNull Entry o) {
            return this.value - o.value;
        }

        final int key;
        final int value;
    }

    /**
     * A structure to record the result of some operations to matrix.
     * This object may be returned by some complex operation where both
     * of the transformed matrix and the operation process could be useful.
     *
     * @author lyc
     */
    public static class MatResult<T> {
        MatResult(List<MatrixOperation<T>> ops, Matrix<T> result) {
            this.ops = ops;
            this.result = result;
        }

        /**
         * The list of the operations ,ordered.
         */
        public final List<MatrixOperation<T>> ops;
        /**
         * The result of the process.
         */
        public final Matrix<T> result;
    }

    /**
     * Calculate the rank of this matrix.
     */
    public int calRank() {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) getValues();
        var mc = getMc();
        toUpperTri0(mat, row, column);
//		Printer.printMatrix(mat);
        for (int i = row - 1; i > -1; i--) {
            for (int j = column - 1; j > -1; j--) {
                if (!mc.isZero(mat[i][j])) {
                    return Math.min(i + 1, column);
                }
            }
        }
        return 0;
    }

    /**
     * Return the matrix {@code mat} that {@code mat•this = E}. If there is not such
     * a matrix, then exception will be thrown.
     *
     * @return the inverse of this.
     * @throws ArithmeticException if this method failed
     */
    public Matrix<T> inverse() {
        //do size check first
        requireSquare();
        try {
            @SuppressWarnings("unchecked")
            List<MatrixOperation<T>> ops = toIdentity((T[][]) getValues(), row, column);
            Matrix<T> idt = identityMatrix(row, getMc());
            idt = idt.doOperation(ops);
            return idt;
        } catch (ArithmeticException ae) {
            throw new ArithmeticException("Cannot Inverse:Not Full Rank");
        }

    }

    /**
     * Determines whether this matrix is invertible.
     */
    public boolean isInvertible() {
        return !getMc().isZero(MatrixSup.fastDet(this));
    }

    /**
     * Operate the given operation to this Matrix.If a series of operations should be done,then use
     * {@link #doOperation(List)} method.
     *
     * @param op an operation
     * @return a new Matrix
     */
    public Matrix<T> doOperation(MatrixOperation<T> op) {
        switch (op.ope) {
            case EXCHANGE_COLUMN:
                return exchangeColumn(op.arg0, op.arg1);
            case EXCHANGE_ROW:
                return exchangeRow(op.arg0, op.arg1);
            case MULTIPLY_ADD_COLUMN:
                return multiplyAndAddColumn(op.num, op.arg0, op.arg1);
            case MULTIPLY_ADD_ROW:
                return multiplyAndAddRow(op.num, op.arg0, op.arg1);
            case MULTIPLY_COLUMN:
                return multiplyNumberColumn(op.num, op.arg0);
            case MULTIPLY_ROW:
                return multiplyNumberRow(op.num, op.arg0);
            default:
                return this;
        }
    }

    /**
     * Operate the given operations to this Matrix.This method is expected to be used with
     * {@link #toUpperTriangleWay()}
     *
     * @param ops a list of matrix operations
     * @return a new matrix after transformation
     */
    public Matrix<T> doOperation(List<MatrixOperation<T>> ops) {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) getValues();
        try {
            for (MatrixOperation<T> op : ops) {
                switch (op.ope) {
                    case EXCHANGE_COLUMN:
                        MatrixSup.exchangeColumn(mat, op.arg0, op.arg1);
                        break;
                    case EXCHANGE_ROW:
                        MatrixSup.exchangeRow(mat, op.arg0, op.arg1);
                        break;
                    case MULTIPLY_ADD_COLUMN:
                        multiplyAndAddColumn0(mat, op.arg0, op.arg1, op.num);
                        break;
                    case MULTIPLY_ADD_ROW:
                        multiplyAndAddRow0(mat, op.arg0, op.arg1, op.num);
                        break;
                    case MULTIPLY_COLUMN:
                        multiplyNumberColumn0(mat, op.arg0, op.num);
                        break;
                    case MULTIPLY_ROW:
                        multiplyNumberRow0(mat, op.arg0, op.num);
                        break;
                    default:
                        throw new ArithmeticException("No such operation:" + op.ope.name());
                }
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException("Operation failed", ex);
        }
        return new DMatrix<>(mat, row, column, getMc());
    }

    void multiplyAndAddColumn0(T[][] mat, int c1, int c2, T f) {
        var mc = getMc();
        for (int i = 0; i < mat.length; i++) {
            mat[i][c2] = mc.add(mat[i][c2], mc.multiply(mat[i][c1], f));
        }
    }

    void multiplyAndAddRow0(T[][] mat, int r1, int r2, T f) {
        var mc = getMc();
        for (int i = 0; i < mat[r1].length; i++) {
            mat[r2][i] = mc.add(mat[r2][i], mc.multiply(mat[r1][i], f));
        }
    }

    void multiplyNumberColumn0(T[][] mat, int c, T f) {
        var mc = getMc();
        for (int i = 0; i < mat.length; i++) {
            mat[i][c] = mc.multiply(mat[i][c], f);
        }
    }

    void multiplyNumberRow0(T[][] mat, int r, T f) {
        var mc = getMc();
        for (int i = 0; i < mat[r].length; i++) {
            mat[r][i] = mc.multiply(mat[r][i], f);
        }
    }


    /**
     * Range check
     */
    protected void rowRangeCheck(int... rs) {
        for (int r : rs) {
            rowRangeCheck(r);
        }
    }

    protected void rowRangeCheck(int r) {
        if (r < 0 || r >= row) {
            throw new IndexOutOfBoundsException("Row=" + row + ":" + r);
        }
    }

    /**
     * Range check
     */
    protected void columnRangeCheck(int... cs) {
        for (int c : cs) {
            columnRangeCheck(c);
        }
    }

    protected void columnRangeCheck(int c) {
        if (c < 0 || c >= column) {
            throw new IndexOutOfBoundsException("Column=" + column + ":" + c);
        }
    }

    /**
     * Return a sub matrix.{@code (i1,j1)-(i2,j2)}.The new matrix's row count
     * will be {@code i2-i1+1}
     *
     * @param i1 inclusive
     * @param j1 inclusive
     * @param i2 inclusive
     * @param j2 inclusive
     * @return a sub matrix
     */
    public Matrix<T> subMatrix(int i1, int j1, int i2, int j2) {
        // do range check
        subMatrixRangeCheck(i1, j1, i2, j2);
        return new SubMatrix<>(getValues(), i1, j1, i2 - i1 + 1, j2 - j1 + 1, getMc());
    }

    /**
     * Converts this matrix to a chuncked matrix.
     *
     * @param rows    the split lines of rows
     * @param columns the split lines of columns
     */
    public Matrix<T>[][] chuncked(int[] rows, int[] columns) {
        @SuppressWarnings("unchecked")
        Matrix<T>[][] data = new Matrix[rows.length + 1][columns.length + 1];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < columns.length; j++) {
                int i1 = getIfNotExceed(rows, i, 0, row - 1);
                int j1 = getIfNotExceed(columns, j, 0, column - 1);
                int i2 = getIfNotExceed(rows, i + 1, 0, row) - 1;
                int j2 = getIfNotExceed(columns, j + 1, 0, column) - 1;
                data[i][j] = subMatrix(i1, j1, i2, j2);
            }
        }
        return data;
    }

    @SuppressWarnings("SameParameterValue")
    private int getIfNotExceed(int[] eles, int index, int downer, int upper) {
        if (index < 0) {
            return downer;
        }
        if (index >= eles.length) {
            return upper;
        }
        return eles[index];
    }

    protected void subMatrixRangeCheck(int i1, int j1, int i2, int j2) {
        if (i1 < 0 || j1 < 0 || i2 >= row || j2 >= column || i1 > i2 || j1 > j2) {
            throw new IllegalArgumentException(
                    "Illegal Argument:" + row + "*" + column + ":(" + i1 + "," + j1 + ")-(" + i2 + "," + j2 + ")");
        }
    }

    /**
     * Replaces the Matrix with a row vector. The size of this vector must be
     * the identity as the column count in this matrix.
     *
     * @param row the row to replace, starts from 0.
     * @param v   a vector
     * @return a new Matrix
     */
    @SuppressWarnings("unchecked")
    public Matrix<T> replaceRow(int row, Vector<T> v) {
        rowRangeCheck(row);
        if (!v.isRow() || v.getSize() != column) {
            throw new IllegalArgumentException();
        }
        T[][] ndata = (T[][]) getValues();
        ndata[row] = (T[]) v.toArray();
        return new DMatrix<>(ndata, this.row, this.column, getMc());
    }

    /**
     * Replaces the Matrix with a column vector. The length of this vector must be
     * the identity as the row count in this matrix.
     *
     * @param column the column to replace, starts from 0.
     */
    @SuppressWarnings("unchecked")
    public Matrix<T> replaceColumn(int column, Vector<T> v) {
        columnRangeCheck(column);
        if (v.isRow() || v.getSize() != row) {
            throw new IllegalArgumentException();
        }
        T[][] ndata = (T[][]) getValues();
        for (int i = 0; i < row; i++) {
            ndata[i][column] = v.getNumber(i);
        }
        return new DMatrix<>(ndata, this.row, this.column, getMc());
    }

    /**
     * Returns the solution space of this matrix.
     */
    public VectorBase<T> solutionSpace() {
        return MatrixSup.solveHomogeneousLinearEquation(this).solutionSpace();
    }

    /**
     * Returns the column space of this matrix.
     */
    public VectorBase<T> columnSpace() {
        return VectorBase.generate(columnVectors());
    }

    public VectorBase<T> rowSpace() {
        return VectorBase.generate(rowVectors());
    }

    /**
     * Returns the frobenius form of this matrix.
     */
    public Matrix<T> frobeniusForm() {
        return MatrixSup.frobeniusForm(this);
    }

    public kotlin.Pair<Matrix<T>, Matrix<T>> congruenceDiagForm() {
        if (!isSquare()) {
            throw new IllegalArgumentException("The matrix must be a square matrix!");
        }
        var mc = getMc();
        @SuppressWarnings("unchecked")
        T[][] arr = (T[][]) new Object[row * 2][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                arr[i][j] = getNumber(i, j);
            }
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < i; j++) {
                if (!mc.isEqual(arr[i][j], arr[j][i])) {
                    throw new IllegalArgumentException("The matrix is not symmetric.");
                }
            }
        }
        var one = mc.getOne();
        var zero = mc.getZero();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (i == j) {
                    arr[row + i][j] = one;
                } else {
                    arr[row + i][j] = zero;
                }
            }
        }
        doCongruenceOperations(arr);
        T[][] m1 = Arrays.copyOf(arr, row);
        @SuppressWarnings("unchecked") T[][] m2 = (T[][]) new Object[row][];
        System.arraycopy(arr, row, m2, 0, row);
        var mat1 = new DMatrix<>(m1, row, column, mc);
        var mat2 = new DMatrix<>(m2, row, column, mc);
        return new kotlin.Pair<>(mat1, mat2);
    }

    private void doCongruenceOperations(T[][] mat) {
        var mc = getMc();
        int pos = 0;
        var one = mc.getOne();
        while (pos < row) {
            if (mc.isZero(mat[pos][pos])) {
                int pi = -1;
                int pj = -1;
                SEARCH:
                for (int i = pos; i < row; i++) {
                    for (int j = pos; j <= i; j++) {
                        if (!mc.isZero(mat[i][j])) {
                            pi = i;
                            pj = j;
                            break SEARCH;
                        }
                    }
                }
                if (pj < 0) {
                    break;
                } else {
                    if (pj != pos) {
                        multiplyAndAddRow0(mat, pj, pos, one);
                        multiplyAndAddColumn0(mat, pj, pos, one);
                    }
                    multiplyAndAddRow0(mat, pi, pos, one);
                    multiplyAndAddColumn0(mat, pi, pos, one);
                }
            }
            for (int i = pos + 1; i < row; i++) {
                if (mc.isZero(mat[pos][i])) {
                    continue;
                }
                var k = mc.negate(mc.divide(mat[pos][i], mat[pos][pos]));
                multiplyAndAddRow0(mat, pos, i, k);
                multiplyAndAddColumn0(mat, pos, i, k);
            }
            pos++;
        }
        return;
    }

    /**
     * Returns the QR-decomposition of this square matrix. Q is an orthogonal matrix and R is an
     * upper-triangle matrix. If this matrix is invertible, there is only one decomposition.
     *
     * @return (Q, R) as a pair
     */
    public kotlin.Pair<Matrix<T>, Matrix<T>> qrDecomposition() {
        if (!isSquare()) {
            throw new IllegalArgumentException();
        }
        var vecs = columnVectors();
        var mc = getMc();
        var RB = getBuilder(row, column, mc);
        @SuppressWarnings("unchecked")
        Vector<T>[] ws = new Vector[row];
        @SuppressWarnings("unchecked")
        Vector<T>[] temp = new Vector[row];
        ws[0] = vecs.get(0);
        if (!ws[0].isZeroVector()) {
            var length = ws[0].calLength();
            RB.set(length, 0, 0);
            ws[0] = ws[0].multiplyNumber(mc.reciprocal(length));
        }
        for (int i = 1; i < row; i++) {
            var u = vecs.get(i);
            for (int j = 0; j < i; j++) {
                var k = u.innerProduct(ws[j]);
                temp[j] = ws[j].multiplyNumber(k);
                RB.set(k, j, i);
            }
            var t = Vector.addVectors(i, temp);
            var v = Vector.subtractVector(u, t);
            if (v.isZeroVector()) {
                ws[i] = v;
            } else {
                var length = v.calLength();
                RB.set(length, i, i);
                ws[i] = v.multiplyNumber(mc.reciprocal(length));
            }
        }
        var Q = Matrix.fromVectors(false, ws);
        var R = RB.build();
        return new kotlin.Pair<>(Q, R);
    }


    @NotNull
    @Override
    public <N> Matrix<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
        Object[][] newData = new Object[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                newData[i][j] = mapper.apply(this.getNumber(i, j));
            }
        }
        return new DMatrix<>(newData, row, column, newCalculator);

    }

    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (obj instanceof Matrix) {
            Matrix<N> m = (Matrix<N>) obj;
            if (m.row == this.row && m.column == this.column) {
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < column; j++) {
                        T t = mapper.apply(m.getNumber(i, j));
                        if (!getMc().isEqual(t, getNumber(i, j))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (obj instanceof Matrix) {
            Matrix<T> m = (Matrix<T>) obj;
            if (!m.getMathCalculator().equals(this.getMathCalculator())) {
                return false;
            }
            if (m.row == this.row && m.column == this.column) {
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < column; j++) {
                        if (!getMc().isEqual(m.getNumber(i, j), getNumber(i, j))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }


    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
     */
    @NotNull
    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
        return "Matrix:row=" + row + ",column=" + column;
    }

    /**
     * Returns a string representation of this matrix in detail.
     */
    @SuppressWarnings("unchecked")
    public String contentToString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
        return StringSup.formatMatrix(ArraySup.mapTo2(getValues(), (Object n) -> nf.format((T) n, getMc()), String.class));
    }

    /**
     * Returns a string representation of this matrix in detail.
     */
    public String contentToString() {
        return contentToString(NumberFormatter.getToStringFormatter());
    }


    /**
     * SubMatrix is a matrix base on another Matrix.To reduce memory,the data in
     * this MatrixN is the identity as the original MatrixN but additional delta x and
     * y are added.
     *
     * @author lyc
     */
    static class SubMatrix<T> extends Matrix<T> {
        final Object[][] data;

        final int dx, dy;

        SubMatrix(Object[][] data, int dx, int dy, int row, int column, MathCalculator<T> mc) {
            super(row, column, mc);
            this.data = data;
            this.dx = dx;
            this.dy = dy;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T getNumber(int i, int j) {
            // range check
            if (i < 0 || i >= row || j < 0 || j >= column) {
                throw new IndexOutOfBoundsException("Out of range:" + row + "��" + column + " " + i + "," + j);
            }
            return (T) data[i + dx][j + dy];
        }

        @SuppressWarnings("unchecked")
        @Override
        public Matrix<T> negative() {
            Object[][] ne = new Object[row][column];
            for (int i = dx; i < row + dx; i++) {
                for (int j = dy; j < column + dy; j++) {

                    ne[i][j] = getMc().negate((T) data[i][j]);
                }
            }
            return new DMatrix<>(ne, row, column, getMc());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Matrix<T> multiplyNumber(long n) {
            Object[][] re = new Object[row][column];
            for (int i = dx; i < row + dx; i++) {
                for (int j = dy; j < column + dy; j++) {
                    re[i][j] = getMc().multiplyLong((T) data[i][j], n);
                }
            }
            return new DMatrix<>(re, row, column, getMc());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Matrix<T> multiplyNumber(T n) {
            Object[][] re = new Object[row][column];
            for (int i = dx; i < row + dx; i++) {
                for (int j = dy; j < column + dy; j++) {
                    re[i][j] = getMc().multiply((T) data[i][j], n);
                }
            }
            return new DMatrix<>(re, row, column, getMc());
        }

        @Override
        public Matrix<T> subMatrix(int i1, int j1, int i2, int j2) {
            // range check
            subMatrixRangeCheck(i1, j1, i2, j2);
            return new SubMatrix<>(data, dx + i1, dy + j1, i2 - i1 + 1, j2 - j1 + 1, getMc());
        }

        @Override
        public T[][] getValues() {
            @SuppressWarnings("unchecked")
            T[][] re = (T[][]) new Object[row][column];
            for (int i = 0; i < row; i++) {
                //noinspection SuspiciousSystemArraycopy
                System.arraycopy(data[i + dx], dy, re[i], 0, column);
            }
            return re;
        }

        @Override
        public Matrix<T> transportMatrix() {
            Object[][] re = new Object[column][row];
            for (int l = 0; l < row; ++l) {
                for (int c = 0; c < column; ++c) {
                    re[c][l] = data[l + dx][c + dy];
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
            System.arraycopy(data, dx, re, 0, row);
            re[r1] = data[r2 + dx];
            re[r2] = data[r1 + dx];
            return new DMatrix<>(re, column, row, getMc());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Matrix<T> multiplyAndAddRow(long k, int r1, int r2) {
            rowRangeCheck(r1, r2);
            if (r1 == r2) {
                throw new IllegalArgumentException("The identity row:" + r1);
            }
            Object[][] re = new Object[row][];
            System.arraycopy(data, dx, re, 0, row);

            re[r2] = new Object[column];
            // just create this row as a new array

            int lt = r2 + dy;
            r1 += dy;
            for (int i = dy; i < column + dy; ++i) {
                re[r2][i] = getMc().add((T) data[lt][i], getMc().multiplyLong((T) data[r1][i], k));
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
            System.arraycopy(data, dx, re, 0, row);

            re[r2] = new Object[column];
            // just create this row as a new array

            int lt = r2 + dy;
            r1 += dy;
            for (int i = dy; i < column + dy; ++i) {
                re[r2][i] = getMc().add((T) data[lt][i], getMc().multiply((T) data[r1][i], k));
            }
            return new DMatrix<>(re, row, column, getMc());
        }


        @Override
        public Matrix<T> cofactor(int r, int c) {

            if (row == 1 || column == 1)
                throw new ArithmeticException("Too small for cofactor");
            //check for edge situation which can use sub-matrix instead
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


            //just like what we do in DMatrix,but extra shift is needed.

            //create a new Matrix
            rowRangeCheck(r);
            columnRangeCheck(c);
            Object[][] ma = new Object[row - 1][column - 1];

            //upper-left
            for (int i = 0; i < r; i++) {
                if (c >= 0) System.arraycopy(data[i + dx], dy, ma[i], 0, c);
            }
            //upper-right
            for (int i = 0; i < r; i++) {
                if (column - c + 1 >= 0) System.arraycopy(data[i + dx], c + 1 + dy, ma[i], c + 1 - 1, column - c + 1);
            }
            //downer-left
            for (int i = r + 1; i < row; i++) {
                if (c >= 0) System.arraycopy(data[i + dx], dy, ma[i - 1], 0, c);
            }
            //downer-right
            for (int i = r + 1; i < row; i++) {
                if (column - c + 1 >= 0)
                    System.arraycopy(data[i + dx], c + 1 + dy, ma[i - 1], c + 1 - 1, column - c + 1);
            }
            //copy ends
            return new DMatrix<>(ma, row - 1, column - 1, getMc());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DMatrix) {
                DMatrix<?> mat = (DMatrix<?>) obj;
                if (this.column != mat.column | this.row != mat.row) {
                    return false;
                }
                Object[][] mData = mat.data;
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < column; j++) {
                        if (!data[i + dx][j + dy].equals(mData[i][j])) {
                            return false;
                        }
                    }
                }
                return true;
            } else if (obj instanceof SubMatrix) {
                //check whether this two matrix come from a identity parent.
                SubMatrix<?> sm = (SubMatrix<?>) obj;
                if (sm.data == this.data
                        && sm.dx == this.dx && sm.dy == this.dy) {
                    return true;
                }
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < column; j++) {
                        if (!data[i + dx][j + dy].equals(sm.data[i + sm.dx][j + sm.dy])) {
                            return false;
                        }
                    }
                }
                return true;
            }

            return super.equals(obj);
        }

    }

    /**
     * Fill the data with {@code t}
     */
    private static <T> void fillData(T[][] data, T t) {
        for (T[] datum : data) {
            Arrays.fill(datum, t);
        }
    }

    /**
     * Fill the data with 0 if null
     */
    @SuppressWarnings("unused")
    private static <T> void fillDataForNull(T[][] data, MathCalculator<T> mc) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = data[i][j] == null ? mc.getZero() : data[i][j];
            }
        }
    }


    /**
     * Create a matrix according to the given array.The row count of the matrix
     * will be the first dimension's length of the array,and the column count of
     * the matrix will be the second dimension's maximum length of the array.
     */
    public static <T> Matrix<T> valueOf(T[][] mat, MathCalculator<T> mc) {
        Objects.requireNonNull(mat);

        int row = mat.length;
        int column = -1;
        for (T[] arr : mat) {
            column = Math.max(column, arr.length);
        }
        if (row < 1 || column < 1) {
            throw new IllegalArgumentException("Illegal size:" + row + "��" + column);
        }

        @SuppressWarnings("unchecked")
        T[][] mat2 = (T[][]) new Object[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                mat2[i][j] = mat[i][j] == null ? mc.getZero() : mat[i][j];
            }
        }
        return new DMatrix<>(mat2, row, column, mc);
    }

    static <T> Matrix<T> valueOfNoCopy(T[][] mat, MathCalculator<T> mc) {
        int row = mat.length;
        int column = mat[0].length;
        for (T[] aMat : mat) {
            if (aMat.length != column) {
                throw new IllegalArgumentException();
            }
            for (int j = 0; j < column; j++) {
                if (aMat[j] == null) {
                    throw new IllegalArgumentException();
                }
            }
        }
        return new DMatrix<>(mat, row, column, mc);
    }

    /**
     * Create a matrix according to the given array.The row count of the matrix
     * will be the first dimension's length of the array,and the column count of
     * the matrix will be the second dimension's maximum length of the array.<p>
     * The returned matrix will hold the type Long.
     * <p><b>This kind of matrix may not support some methods usually. </b>
     */
    public static Matrix<Long> valueOf(long[][] mat) {
        Objects.requireNonNull(mat);

        int row = mat.length;
        int column = -1;
        for (long[] arr : mat) {
            column = Math.max(column, arr.length);
        }
        if (row < 1 || column < 1) {
            throw new IllegalArgumentException("Illegal size:" + row + "��" + column);
        }

        Long[][] mat2 = new Long[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                mat2[i][j] = mat[i][j];
            }
        }
        return new DMatrix<>(mat2, row, column, Calculators.getCalLong());
    }

    /**
     * Create a matrix according to the given array.The row count of the matrix
     * will be the first dimension's length of the array,and the column count of
     * the matrix will be the second dimension's maximum length of the array.<p>
     * The returned matrix will hold the type Double.<p>
     * The {@link MathCalculator} will be assigned through method {@link Calculators#getCalDouble()}.
     */
    public static Matrix<Double> valueOf(double[][] mat) {
        Objects.requireNonNull(mat);

        int row = mat.length;
        int column = -1;
        for (double[] arr : mat) {
            column = Math.max(column, arr.length);
        }
        if (row < 1 || column < 1) {
            throw new IllegalArgumentException("Illegal size:" + row + "��" + column);
        }

        Double[][] mat2 = new Double[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                mat2[i][j] = mat[i][j];
            }
        }
        return new DMatrix<>(mat2, row, column, Calculators.getCalDouble());
    }


    /**
     * Create a matrix according to the given array.The row count of the matrix
     * will be the first dimension's length of the array,and the column count of
     * the matrix will be the second dimension's maximum length of the array.<p>
     * This method only overload the similar method  {@link #valueOf(long[][])}
     */
    public static Matrix<Integer> valueOf(int[][] mat) {
        Objects.requireNonNull(mat);

        int row = mat.length;
        int column = -1;
        for (int[] arr : mat) {
            column = Math.max(column, arr.length);
        }
        if (row < 1 || column < 1) {
            throw new IllegalArgumentException("Illegal size:" + row + "��" + column);
        }

        Integer[][] mat2 = new Integer[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                mat2[i][j] = mat[i][j];
            }
        }
        return new DMatrix<>(mat2, row, column, Calculators.getCalInteger());
    }


    /**
     * Return a diagonal matrix containing the given numbers. M[i][i] = arr[i]
     */
    public static <T> Matrix<T> diag(T[] arr, MathCalculator<T> mc) {
        Objects.requireNonNull(arr);

        int n = arr.length;
        if (n < 1) {
            throw new IllegalArgumentException("Illegal size:" + n);
        }
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[n][n];
        fillData(mat, mc.getZero());
        for (int i = 0; i < n; i++) {
            mat[i][i] = arr[i];
        }
        return new DMatrix<>(mat, n, n, mc);
    }

    /**
     * Return a diagonal matrix containing the given numbers. M[i][i] = x
     */
    public static <T> Matrix<T> diag(T x, int n, MathCalculator<T> mc) {
        Objects.requireNonNull(x);
        if (n < 1) {
            throw new IllegalArgumentException("Illegal size:" + n);
        }
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[n][n];
        fillData(mat, mc.getZero());
        for (int i = 0; i < n; i++) {
            mat[i][i] = x;
        }
        return new DMatrix<>(mat, n, n, mc);
    }

    /**
     * Return an identity matrix whose size is n��n
     *
     * @param n the size of the matrix
     */
    public static <T> Matrix<T> identityMatrix(int n, MathCalculator<T> mc) {
        if (n < 1) {
            throw new IllegalArgumentException("Illegal size:" + n);
        }
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[n][n];
        fillData(mat, mc.getZero());
        for (int i = 0; i < n; i++) {
            mat[i][i] = mc.getOne();
        }
        return new DMatrix<>(mat, n, n, mc);
    }

    /**
     * A zero matrix is a matrix filled with zero.
     *
     * @param n the size
     */
    public static <T> Matrix<T> zeroMatrix(int n, MathCalculator<T> mc) {
        return zeroMatrix(n, n, mc);
    }

    /**
     * A zero matrix is a matrix filled with zero.
     *
     * @param row    the row count
     * @param column the column count
     */
    public static <T> Matrix<T> zeroMatrix(int row, int column, MathCalculator<T> mc) {
        if (row < 1 || column < 1) {
            throw new IllegalArgumentException("Illegal size:" + row + "*" + column);
        }
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[row][column];
        fillData(mat, mc.getZero());
        return new DMatrix<>(mat, row, column, mc);
    }


    /**
     * Add two matrix.The size of two matrix should be the identity.
     *
     * @param m1 a matrix
     * @param m2 another matrix
     * @return a new matrix mat = m1+m2
     * @throws IllegalArgumentException if size doesn't match
     * @throws NullPointerException     if m1==null || m2==null
     */
    @SuppressWarnings("unchecked")
    public static <T> Matrix<T> addMatrix(Matrix<T> m1, Matrix<T> m2) {
        if (m1.row != m2.row || m1.column != m2.column) {
            throw new IllegalArgumentException(
                    "Cannot add two matrix:" + m1.row + "*" + m1.column + " + " + m2.row + "*" + m2.column);
        }
        int row = m1.row;
        int column = m1.column;
        MathCalculator<T> mc = m1.getMc();
        T[][] re = (T[][]) new Object[row][column];
        if (m1 instanceof DMatrix && m2 instanceof DMatrix) {
            T[][] d1 = (T[][]) ((DMatrix<T>) m1).data;
            T[][] d2 = (T[][]) ((DMatrix<T>) m2).data;

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    re[i][j] = mc.add(d1[i][j], d2[i][j]);
                }
            }
        } else {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    re[i][j] = mc.add(m1.getNumber(i, j), m2.getNumber(i, j));
                }
            }
        }
        return new DMatrix<>(re, row, column, mc);
    }

    /**
     * Return m1-m2.The size of two matrix should be the identity.
     *
     * @param m1 a matrix
     * @param m2 another matrix
     * @return a new matrix mat = m1-m2
     * @throws IllegalArgumentException if size doesn't match
     * @throws NullPointerException     if m1==null || m2==null
     */
    @SuppressWarnings("unchecked")
    public static <T> Matrix<T> minusMatrix(Matrix<T> m1, Matrix<T> m2) {
        if (m1.row != m2.row || m1.column != m2.column) {
            throw new IllegalArgumentException(
                    "Cannot minus two matrix:" + m1.row + "*" + m1.column + " - " + m2.row + "*" + m2.column);
        }
        int row = m1.row;
        int column = m1.column;
        MathCalculator<T> mc = m1.getMc();
        T[][] re = (T[][]) new Object[row][column];
        if (m1 instanceof DMatrix && m2 instanceof DMatrix) {
            T[][] d1 = (T[][]) ((DMatrix<T>) m1).data;
            T[][] d2 = (T[][]) ((DMatrix<T>) m2).data;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    re[i][j] = mc.subtract(d1[i][j], d2[i][j]);
                }
            }
        } else {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    re[i][j] = mc.subtract(m1.getNumber(i, j), m2.getNumber(i, j));
                }
            }
        }

        return new DMatrix<>(re, row, column, mc);
    }

    /**
     * Multiply two matrix.The column count of m1 should be equal to the row count
     * of m2.This method only provide O(n^3) time performance.
     *
     * @param m1 a matrix
     * @param m2 another matrix
     * @return m1��m2
     * @throws IllegalArgumentException if size doesn't match
     * @throws NullPointerException     if m1==null || m2==null
     */
    @SuppressWarnings("unchecked")
    public static <T> Matrix<T> multiplyMatrix(Matrix<T> m1, Matrix<T> m2) {
        if (m1.column != m2.row) {
            throw new IllegalArgumentException(
                    "Cannot multiply two matrix:(" + m1.row + "*" + m1.column + ") * (" + m2.row + "*" + m2.column + ")");
        }
        int n = m1.column;
        int row = m1.row;
        int column = m2.column;
        MathCalculator<T> mc = m1.getMc();
        Object[][] re = new Object[row][column];
        if (m1 instanceof DMatrix && m2 instanceof DMatrix) {
            T[][] d1 = (T[][]) ((DMatrix<T>) m1).data;
            T[][] d2 = (T[][]) ((DMatrix<T>) m2).data;
            multiplyToArray(n, row, column, mc, re, d1, d2);
        } else {
            T[][] fs1 = (T[][]) m1.getValues();
            T[][] fs2 = (T[][]) m2.getValues();
            multiplyToArray(n, row, column, mc, re, fs1, fs2);
        }
        return new DMatrix<>(re, row, column, mc);
    }

    private static <T> void multiplyToArray(int n, int row, int column, MathCalculator<T> mc, Object[][] re, T[][] fs1, T[][] fs2) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                T sum = mc.getZero();
                for (int k = 0; k < n; k++) {
                    sum = mc.add(sum, mc.multiply(fs1[i][k], fs2[k][j]));
                }
                re[i][j] = sum;
            }
        }
    }

    /**
     * Calculate the result of {@code mat^pow},the given matrix must have the
     * identity column and column count.Negative power values are illegal and if
     * {@code pow==0} then this method is the identity as call
     * {@link #identityMatrix(int, MathCalculator)} which parameter is the given matrix's column
     * or column count.
     *
     * @param mat the base
     * @param pow the exponent
     * @return a MatrixN that equals to {@code (mat)}<sup>{@code pow}</sup>.
     */
    public static <T> Matrix<T> matrixPower(Matrix<T> mat, int pow) {
        // do range check
        mat.requireSquare();
        if (pow == 0)
            return identityMatrix(mat.row, mat.getMc());
        if (pow == 1)
            return mat;

        // calculate two power:
        int p = 1;
        int n = 1;
        while (n <= pow) {
            n *= 2;
            p++;
        }

        @SuppressWarnings("unchecked")
        Matrix<T>[] twoPowers = new Matrix[p];
        twoPowers[0] = mat;
        for (int i = 1; i < p; i++) {
            twoPowers[i] = Matrix.multiplyMatrix(twoPowers[i - 1], twoPowers[i - 1]);
        }
//		for (MatrixN ma : twoPowers) {
//			ma.printMatrix();
//		}

        // calculate
        Matrix<T> re = twoPowers[p - 1];
        n = n >>> 1;
        for (int i = p - 2; i > -1; i--) {
            n = n >>> 1;
            if ((pow & n) > 0) {
                re = Matrix.multiplyMatrix(re, twoPowers[i]);
            }
        }
        return re;
    }


    /**
     * Multiplies several matrix, using dynamic programming to minimize time cost.
     */
    @SafeVarargs
    public static <T> Matrix<T> multiplyMatrix(Matrix<T>... mats) {
        if (mats.length == 0) {
            throw new IllegalArgumentException("mats is empty!");
        }
        for (Matrix<T> mat : mats) {
            Objects.requireNonNull(mat);
        }
        Function<Matrix<T>, int[]> toModel = x -> new int[]{x.row, x.column};
        return ModelPatterns.reduceDP(0, mats.length, x -> mats[x], Matrix::multiplyMatrix, toModel, (x, y) ->
                new int[]{x[0], y[1]}, (x, y) -> x[0] * y[0] * y[1]);
    }

    @SuppressWarnings("unchecked")
    public static <T> Matrix<T> multiplyMatrix(List<Matrix<T>> mats) {
        return multiplyMatrix(mats.toArray(new Matrix[]{}));
    }

//    public Matrix<T> applyFunctionWithIndex(BiFunction<T,>)

    /**
     * Print the matrix using the Printer
     */
    public void printMatrix() {
        Printer.printMatrix(getValues());
    }

    /**
     * Creates a Matrix from several vectors, the vector should have
     * the same size and be either all row vector or column vector.
     *
     * @param v  a vector
     * @param vs remaining vectors
     * @return a new matrix
     */
    @SafeVarargs
    public static <T> Matrix<T> fromVectors(Vector<T> v, Vector<T>... vs) {
        final boolean isRow = v.isRow();
        final int column, row;
        final int size = v.getSize();
        for (Vector<T> vt : vs) {
            if (vt.getSize() != size) {
                throw new IllegalArgumentException("Size mismatch!");
            }
        }
        if (isRow) {
            row = vs.length + 1;
            column = v.getSize();
        } else {
            row = v.getSize();
            column = vs.length + 1;
        }
        @SuppressWarnings("unchecked")
        Vector<T>[] arr = new Vector[vs.length + 1];
        arr[0] = v;
        System.arraycopy(vs, 0, arr, 1, vs.length);
        return new VMatrix<>(arr, row, column, v.getMathCalculator(), isRow);
    }

    /**
     * Creates a Matrix from several vectors, the vector should have
     * the same size.
     *
     * @param asRowVector determines whether to treat all the vectors as row vector
     */
    @SafeVarargs
    public static <T> Matrix<T> fromVectors(boolean asRowVector, Vector<T>... vectors) {
        int size = vectors[0].getSize();
        var mc = vectors[0].getMc();
        @SuppressWarnings("unchecked")
        Vector<T>[] arr = (Vector<T>[]) new Vector[vectors.length];
        for (int i = 0; i < vectors.length; i++) {
            var v = vectors[i];
            if (v.getSize() != size) {
                throw new IllegalArgumentException("Different size!");
            }
            if (asRowVector ^ v.isRow) {
                v = v.transportMatrix();
            }
            arr[i] = v;
        }
        if (asRowVector) {
            return new VMatrix<>(arr, vectors.length, size, mc, true);
        } else {
            return new VMatrix<>(arr, size, vectors.length, mc, false);
        }
    }

    public static <T> Matrix<T> fromVectors(boolean asRowVector, List<Vector<T>> vectors) {
        int size = vectors.get(0).getSize();
        var mc = vectors.get(0).getMc();
        @SuppressWarnings("unchecked")
        Vector<T>[] arr = (Vector<T>[]) new Vector[vectors.size()];
        int index = 0;
        for (var v : vectors) {
            if (v.getSize() != size) {
                throw new IllegalArgumentException("Different size!");
            }
            if (asRowVector ^ v.isRow) {
                v = v.transportMatrix();
            }
            arr[index++] = v;
        }
        if (asRowVector) {
            return new VMatrix<>(arr, vectors.size(), size, mc, true);
        } else {
            return new VMatrix<>(arr, size, vectors.size(), mc, false);
        }
    }

    public static <T> Matrix<T> valueOf(int row, int column, MathCalculator<T> mc, BiFunction<Integer, Integer, T> f) {
        Object[][] data = new Object[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                data[i][j] = Objects.requireNonNull(f.apply(i, j));
            }
        }
        return new DMatrix<>(data, row, column, mc);
    }

    /**
     * Returns the kronecker product of matrix <code>a</code> and <code>b</code>. Assuming
     * <code>a</code> has the size of <text>m*n</text> and <code>b</code> has the size of <code>k*l</code>,
     * the result is a matrix whose size is <code>mk * nl</code>. The result can be shown as block matrix
     * as following:
     * <pre>
     *     a<sub>11</sub>B a<sub>12</sub>B ... a<sub>1n</sub>B
     *     a<sub>21</sub>B a<sub>22</sub>B ... a<sub>2n</sub>B
     *     ...
     *     a<sub>m1</sub>B a<sub>m2</sub>B ... a<sub>mn</sub>B
     * </pre>
     *
     * @param a a matrix
     * @param b another matrix
     * @return the kronecker produce of <code>a</code> and <code>b</code>
     */
    public static <T> Matrix<T> kroneckerProduct(Matrix<T> a, Matrix<T> b) {
        int reRow = a.row * b.row;
        int reCol = a.column * b.column;
        @SuppressWarnings("unchecked")
        T[][] data = (T[][]) new Object[reRow][reCol];
        var mc = a.getMc();
        for (int m = 0; m < a.row; m++) {
            for (int n = 0; n < a.column; n++) {
                T k = a.getNumber(m, n);
                for (int i = 0; i < b.row; i++) {
                    int destI = i + m * b.row;
                    for (int j = 0; j < b.column; j++) {
                        int destJ = j + n * b.column;
                        data[destI][destJ] = mc.multiply(k, b.getNumber(i, j));
                    }
                }
            }
        }
        return new DMatrix<>(data, reRow, reCol, mc);
    }

    public static <T> Matrix<T> concatRow(Matrix<T> m1, Matrix<T> m2) {
        int row = m1.row + m2.row;
        int column = Math.max(m1.column, m2.column);
        var bd = getBuilder(row, column, m1.getMathCalculator());
        bd.fillArea(0, 0, m1);
        bd.fillArea(m1.row, 0, m2);
        return bd.build();
    }

    public static <T> Matrix<T> concatColumn(Matrix<T> m1, Matrix<T> m2) {
        int row = Math.max(m1.row, m2.row);
        int column = m1.column + m2.column;
        var bd = getBuilder(row, column, m1.getMathCalculator());
        bd.fillArea(0, 0, m1);
        bd.fillArea(0, m1.column, m2);
        return bd.build();
    }


    public static <T> MatrixBuilder<T> getBuilder(int row, int column, MathCalculator<T> mc) {
        return new MatrixBuilder<>(row, column, mc);
    }


    public static class MatrixBuilder<T> implements Cloneable {
        final MathCalculator<T> mc;
        final int row, column;
        final Object[][] data;
        boolean disposed = false;

        MatrixBuilder(int row, int column, MathCalculator<T> mc) {
            super();
            this.mc = mc;
            this.row = row;
            this.column = column;
            data = new Object[row][column];
            fillData(data, mc.getZero());
        }

        private MatrixBuilder(int row, int column, MathCalculator<T> mc, Object[][] data) {
            super();
            this.mc = mc;
            this.row = row;
            this.column = column;
            this.data = data;
        }

        protected void rowRangeCheck(int r) {
            if (r < 0 || r >= row) {
                throw new IndexOutOfBoundsException("Row=" + row + ":" + r);
            }
        }

        protected void columnRangeCheck(int c) {
            if (c < 0 || c >= column) {
                throw new IndexOutOfBoundsException("Column=" + column + ":" + c);
            }
        }

        protected void checkDisposed() {
            if (disposed) {
                throw new IllegalStateException("The builder has already built!");
            }
        }

        public MatrixBuilder<T> set(T x, int i, int j) {
            checkDisposed();
            rowRangeCheck(i);
            columnRangeCheck(j);
            data[i][j] = x;
            return this;
        }

        public MatrixBuilder<T> fillRow(T x, int row) {
            checkDisposed();
            rowRangeCheck(row);
            for (int j = 0; j < column; j++) {
                data[row][j] = x;
            }
            return this;
        }

        public MatrixBuilder<T> fillArea(int i, int j, Matrix<T> mat) {
            checkDisposed();
            rowRangeCheck(i);
            rowRangeCheck(i + mat.row - 1);
            columnRangeCheck(j);
            columnRangeCheck(j + mat.column - 1);
            for (int i0 = 0; i0 < mat.row; i0++) {
                for (int j0 = 0; j0 < mat.column; j0++) {
                    data[i0 + i][j0 + j] = mat.getNumber(i0, j0);
                }
            }
            return this;
        }

        public MatrixBuilder<T> fillRowVector(int row, int columnStart, Vector<T> v) {
            checkDisposed();
            rowRangeCheck(row);
            columnRangeCheck(columnStart);
            columnRangeCheck(columnStart + v.getSize() - 1);
            for (int i = 0; i < v.getSize(); i++) {
                data[row][i + columnStart] = v.getNumber(i);
            }
            return this;
        }

        public MatrixBuilder<T> fillColumnVector(int column, int rowStart, Vector<T> v) {
            checkDisposed();
            columnRangeCheck(column);
            rowRangeCheck(rowStart);
            rowRangeCheck(rowStart + v.getSize() - 1);
            for (int i = 0; i < v.getSize(); i++) {
                data[i + rowStart][column] = v.getNumber(i);
            }
            return this;
        }


        public MatrixBuilder<T> fillColumn(T x, int column) {
            checkDisposed();
            columnRangeCheck(column);
            for (int i = 0; i < row; i++) {
                data[i][column] = x;
            }
            return this;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public MatrixBuilder<T> clone() {
            Object[][] ndata = ArraySup.deepCopy(data);
            return new MatrixBuilder<>(row, column, mc, ndata);
        }

        public Matrix<T> build() {
            checkDisposed();
            disposed = true;
            return new DMatrix<>(data, row, column, mc);
        }
    }

    public static <T> MatrixCal<T> matrixCalculator(int n, MathCalculator<T> mc) {
        if (n <= 0) {
            throw new IllegalArgumentException("n <= 0");
        }
        return new MatrixCal<>(mc, n);
    }

    public static class MatrixCal<T> extends MathCalculatorAdapter<Matrix<T>> implements ModuleCalculator<T, Matrix<T>> {
        private final MathCalculator<T> mc;
        private final int n;

        public MatrixCal(MathCalculator<T> mc, int n) {
            this.mc = mc;
            this.n = n;
        }

        @NotNull
        @Override
        public Matrix<T> getOne() {
            return Matrix.identityMatrix(n, mc);
        }

        @NotNull
        @Override
        public Matrix<T> getZero() {
            return Matrix.zeroMatrix(n, mc);
        }

        @Override
        public boolean isZero(@NotNull Matrix<T> para) {
            var mc = para.getMc();
            for (int i = 0; i < para.row; i++) {
                for (int j = 0; j < para.column; j++) {
                    if (!mc.isZero(para.getNumber(i, j))) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean isEqual(@NotNull Matrix<T> x, @NotNull Matrix<T> y) {
            return x.valueEquals(y);
        }

        @NotNull
        @Override
        public Matrix<T> add(@NotNull Matrix<T> x, @NotNull Matrix<T> y) {
            return Matrix.addMatrix(x, y);
        }

        @NotNull
        @Override
        public Matrix<T> negate(@NotNull Matrix<T> x) {
            return x.negative();
        }

        @NotNull
        @Override
        public Matrix<T> subtract(@NotNull Matrix<T> x, @NotNull Matrix<T> y) {
            return Matrix.minusMatrix(x, y);
        }

        @NotNull
        @Override
        public Matrix<T> multiply(@NotNull Matrix<T> x, @NotNull Matrix<T> y) {
            return Matrix.multiplyMatrix(x, y);
        }

        @NotNull
        @Override
        public Matrix<T> scalarMultiply(@NotNull T k, @NotNull Matrix<T> tMatrix) {
            return tMatrix.multiplyNumber(k);
        }

        @NotNull
        @Override
        public MathCalculator<T> getScalarCalculator() {
            return mc;
        }

        @NotNull
        @Override
        public T rAdd(@NotNull T r1, @NotNull T r2) {
            return mc.add(r1, r2);
        }

        @NotNull
        @Override
        public T rSubtract(@NotNull T r1, @NotNull T r2) {
            return mc.subtract(r1, r2);
        }

        @NotNull
        @Override
        public T rNegate(@NotNull T t) {
            return mc.negate(t);
        }

        @NotNull
        @Override
        public T rMultiply(@NotNull T r1, @NotNull T r2) {
            return mc.multiply(r1, r2);
        }

        @NotNull
        @Override
        public T getRZero() {
            return mc.getZero();
        }
    }
}