/**
 * 
 */
package cn.timelives.java.math.algebra.linearAlgebra;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.utilities.Printer;

import java.util.Arrays;

/**
 * The default implement for Matrix, this class 
 * @author liyicheng
 *
 */
class DMatrix<T> extends Matrix<T> {
	final Object[][] data;

	/**
	 * This method won't check mat's size and all the changed to the mat will be
	 * reflected to this Matrix.
	 * 
	 * @param mat
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
		return data.hashCode();
	}

	@Override
	public String toString() {
		return Arrays.deepToString(data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Matrix<T> negative() {
		Object[][] ne = new Object[row][column];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
                ne[i][j] = getMc().negate((T) data[i][j]);
			}
		}
        return new DMatrix<T>(ne, row, column, getMc());
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
        return new DMatrix<T>(re, row, column, getMc());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNumber(int i, int j) {
		return (T) data[i][j];
	}

	@Override
	public Matrix<T> subMatrix(int i1, int j1, int i2, int j2) {
        subMatrixRangeCheck(i1, j1, i2, j2);
		// range check
        SubMatrix<T> sm = new SubMatrix<T>(data, i1, j1, i2 - i1 + 1, j2 - j1 + 1, getMc());
		return sm;
	}

	@Override
	public T[][] getValues() {
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) new Object[row][column];
		for (int i = 0; i < row; i++) {
			System.arraycopy(data[i], 0, re[i], 0, column);
		}
		return re;
	}

	@Override
	public Matrix<T> transportMatrix() {
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
		for (int l = 0; l < row; ++l) {
			re[l] = data[l];
		}
		re[r1] = data[r2];
		re[r2] = data[r1];
        return new DMatrix<T>(re, row, column, getMc());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Matrix<T> multiplyNumberRow(T n, int l) {
		rowRangeCheck(l);
		Object[][] re = new Object[row][];
		for (int i = 0; i < row; ++i) {
			re[i] = data[i];
		}

		re[l] = new Object[column];
		for (int i = 0; i < column; ++i) {
            re[l][i] = getMc().multiply((T) data[l][i], n);
        }
        return new DMatrix<T>(re, row, column, getMc());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Matrix<T> multiplyNumberRow(long n, int l) {
		rowRangeCheck(l);
		Object[][] re = new Object[row][];
		for (int i = 0; i < row; ++i) {
			re[i] = data[i];
		}

		re[l] = new Object[column];
		for (int i = 0; i < column; ++i) {
            re[l][i] = getMc().multiplyLong((T) data[l][i], n);
        }
        return new DMatrix<T>(re, row, column, getMc());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Matrix<T> multiplyAndAddRow(long k, int r1, int r2) {
		rowRangeCheck(r1, r2);
		if (r1 == r2) {
            throw new IllegalArgumentException("The identity row:" + r1);
		}
		Object[][] re = new Object[row][];
		for (int l = 0; l < row; ++l) {
			re[l] = data[l];
		}

		re[r2] = new Object[column];
		// just copy this row

		for (int i = 0; i < column; ++i) {

            re[r2][i] = getMc().add((T) data[r2][i], getMc().multiplyLong((T) data[r1][i], k));
        }
        return new DMatrix<T>(re, row, column, getMc());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Matrix<T> multiplyAndAddRow(T k, int r1, int r2) {
		rowRangeCheck(r1, r2);
		if (r1 == r2) {
            throw new IllegalArgumentException("The identity row:" + r1);
		}
		Object[][] re = new Object[row][];
		for (int l = 0; l < row; ++l) {
			re[l] = data[l];
		}

		re[r2] = new Object[column];
		// just copy this row

		for (int i = 0; i < column; ++i) {
            re[r2][i] = getMc().add((T) data[r2][i], getMc().multiply((T) data[r1][i], k));
        }
        return new DMatrix<T>(re, row, column, getMc());
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
        return new DMatrix<T>(re, row, column, getMc());
	}

	/**
	 * Print the matrix using the Printer
	 * 
	 * @param ma
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

	@Override
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
		Object[][] ma = new Object[row - 1][column - 1];

		// upper-left
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				ma[i][j] = data[i][j];
			}
		}
		// upper-right
		for (int i = 0; i < r; i++) {
			for (int j = c + 1; j < column; j++) {
				ma[i][j - 1] = data[i][j];
			}
		}
		// downer-left
		for (int i = r + 1; i < row; i++) {
			for (int j = 0; j < c; j++) {
				ma[i - 1][j] = data[i][j];
			}
		}
		// downer-right
		for (int i = r + 1; i < row; i++) {
			for (int j = c + 1; j < column; j++) {
				ma[i - 1][j - 1] = data[i][j];
			}
		}
		// copy ends
        return new DMatrix<T>(ma, row - 1, column - 1, getMc());
	}

}
