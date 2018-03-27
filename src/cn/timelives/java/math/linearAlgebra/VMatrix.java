/**
 * 
 */
package cn.timelives.java.math.linearAlgebra;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.utilities.ArraySup;

import java.util.Arrays;
import java.util.function.Function;

/**
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
	public VMatrix(Vector<T>[] vs,int row, int column, MathCalculator<T> mc,boolean isRow) {
		super(row, column, mc);
		this.vs = vs;
		this.isRow = isRow;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#getNumber(int, int)
	 */
	@Override
	public T getNumber(int i, int j) {
		rowRangeCheck(i);
		columnRangeCheck(j);
		if(isRow){
			return vs[i].getNumber(j);
		}else{
			return vs[j].getNumber(i);
		}
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#getValues()
	 */
	@Override
	public Object[][] getValues() {
		Object[][] obj;
		if(isRow){
			obj = new Object[row][];
			for(int i=0;i<row;i++){
				obj[i] = vs[i].toArray();
			}
		}else{
			obj = new Object[row][column];
			for(int j=0;j<column;j++){
				for(int i=0;i<row;i++){
					obj[i][j] = vs[j].getNumber(i);
				}
			}
		}
		return obj;
	}
	private Matrix<T> mapTo0(@SuppressWarnings("rawtypes") Function<Vector<T>,Vector> f){
		@SuppressWarnings("unchecked")
		Vector<T>[] vn = ArraySup.mapTo(vs, f,Vector.class);
		return new VMatrix<>(vn, row, column, mc, isRow);
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#negative()
	 */
	@Override
	public Matrix<T> negative() {
		return mapTo0(v -> v.negative());
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#transportMatrix()
	 */
	@Override
	public Matrix<T> transportMatrix() {
		return new VMatrix<>(vs, column, row, mc, !isRow);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#multiplyNumber(long)
	 */
	@Override
	public Matrix<T> multiplyNumber(long n) {
		return mapTo0(v -> v.multiplyNumber(n));
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#multiplyNumber(java.lang.Object)
	 */
	@Override
	public Matrix<T> multiplyNumber(T n) {
		return mapTo0(v -> v.multiplyNumber(n));
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#cofactor(int, int)
	 */
	@Override
	public Matrix<T> cofactor(int r, int c) {
		if(row<=1 || column<=1)
			throw new ArithmeticException("No cofactor");
		Object[][] mat = new Object[row-1][column-1];
		int x=0,y=0;
		for(int i=0;i<row;i++){
			if(i==r)
				continue;
			y = 0;
			for(int j=0;j<column;j++){
				if(j==c)
					continue;
				mat[x][y] = getNumber(i, j);
				y++;
			}
			x++;
		}
		return new DMatrix<>(mat, row-1,column-1, mc);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#replaceColumn(int, cn.timelives.java.math.linearAlgebra.AbstractVector)
	 */
	@Override
	public Matrix<T> replaceColumn(int column, Vector<T> v) {
		if(isRow){
			return super.replaceColumn(column, v);
		}
		columnRangeCheck(column);
		Vector<T>[] vn = Arrays.copyOf(vs, vs.length);
		vn[column] = v;
		return new VMatrix<>(vn, this.row, this.column, mc, isRow);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#replaceRow(int, cn.timelives.java.math.linearAlgebra.AbstractVector)
	 */
	@Override
	public Matrix<T> replaceRow(int row, Vector<T> v) {
		if(!isRow){
			return super.replaceRow(column, v);
		}
		rowRangeCheck(column);
		Vector<T>[] vn = Arrays.copyOf(vs, vs.length);
		vn[row] = v;
		return new VMatrix<>(vn, this.row, this.column, mc, isRow);
	}
}