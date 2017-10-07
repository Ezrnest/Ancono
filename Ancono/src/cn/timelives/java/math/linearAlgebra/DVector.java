package cn.timelives.java.math.linearAlgebra;

import java.util.Arrays;
import java.util.function.Function;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.MathCalculatorAdapter;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.utilities.ArraySup;

/**
 * A default implement of a vector using array to store the 
 * elements.
 * @author lyc
 *
 */
public final class DVector<T> extends Vector<T> {
	
	/**
	 * The data to be stored.
	 */
	final T[] vec;

	/**
	 * Create a new vector with the given array and
	 * 
	 * @param vec
	 * @param isRow
	 */
	protected DVector(T[] vec, boolean isRow,MathCalculator<T> mc) {
		super(vec.length,isRow,mc);
		this.vec = vec;
	}

	@Override
	public T getNumber(int i, int j) {
		super.rowRangeCheck(i);
		super.columnRangeCheck(j);
		if (isRow) {
			return vec[j];
		} else {
			return vec[i];
		}
	}
	
	/**
	 * The the dimension {@code i} of this vector.Which is equal to {@code getNumber( isRow ? 0 : i , isRow ? i : 0 )}
	 * @param i the index of dimension
	 * @return the number in {@code i} dimension of this vector
	 */
	@Override
	public T getNumber(int i){
		if(isRow){
			super.rowRangeCheck(i);
			return vec[i];
		}else{
			super.columnRangeCheck(i);
			return vec[i];
		}
	}
	
	@Override
	public int getSize() {
		return vec.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[][] getValues() {
		if (isRow) {
			return (T[][]) new Object[][] { vec.clone() };
		} else {
			T[][] mat = (T[][]) new Object[row][1];
			for (int i = 0; i < row; i++) {
				mat[i][0] = vec[i];
			}
			return mat;
		}
	}
	
	 /* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#toArray()
	 */
	@Override
	public T[] toArray() {
		return Arrays.copyOf(vec, vec.length);
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#toArray(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T[] toArray(T[] arr) {
		if (arr.length < vec.length)
			// Make a new array of a's runtime type, but my contents:
			return (T[]) Arrays.copyOf(vec, vec.length, arr.getClass());
		System.arraycopy(vec, 0, arr, 0, vec.length);
		if (arr.length > vec.length)
			arr[vec.length] = null;
		return arr;
	}

	@Override
	public DVector<T> negative() {
		int len = isRow ? column : row;
		@SuppressWarnings("unchecked")
		T[] reV = (T[]) new Object[len];
		for (int i = 0; i < len; i++) {
			reV[i] = mc.negate(vec[i]);
		}
		return new DVector<>(reV, isRow,mc);
	}

	@Override
	public DVector<T> transportMatrix() {
		return new DVector<>(vec, !isRow,mc);
	}

	@Override
	public DVector<T> multiplyNumber(long n) {
		return multiplyNumberVector(n);
	}

	@Override
	public DVector<T> multiplyNumber(T n) {
		return multiplyNumberVector(n);
	}

	@Override
	public Matrix<T> cofactor(int r, int c) {
		throw new ArithmeticException("Too small for cofactor");
	}

	@Override
	public DVector<T> multiplyAndAddColumn(T k, int c1, int c2) {
		if (!isRow) {
			throw new IllegalArgumentException("A column vector");
		}
		if (c1 == c2) {
			throw new IllegalArgumentException("The same column:" + c1);
		}
		T[] rev = vec.clone();
		rev[c2] = mc.add(mc.multiply(rev[c1], k), rev[c2]);
		return new DVector<T>(rev, true,mc);
	}

	@Override
	public DVector<T> multiplyAndAddColumn(long k, int c1, int c2) {
		if (!isRow) {
			throw new IllegalArgumentException("A column vector");
		}
		if (c1 == c2) {
			throw new IllegalArgumentException("The same column:" + c1);
		}
		T[] rev = vec.clone();
		rev[c2] = mc.add(mc.multiplyLong(rev[c1], k), rev[c2]);
		return new DVector<T>(rev, true,mc);
	}

	@Override
	public DVector<T> multiplyAndAddRow(long k, int r1, int r2) {
		if (isRow) {
			throw new IllegalArgumentException("A column vector");
		}
		if (r1 == r2) {
			throw new IllegalArgumentException("The same row:" + r1);
		}
		T[] rev = vec.clone();
		rev[r2] = mc.add(mc.multiplyLong(rev[r1], k), rev[r2]);
		return new DVector<T>(rev, false,mc);
	}

	@Override
	public DVector<T> multiplyAndAddRow(T k, int r1, int r2) {
		if (isRow) {
			throw new IllegalArgumentException("A column vector");
		}
		if (r1 == r2) {
			throw new IllegalArgumentException("The same row:" + r1);
		}
		T[] rev = vec.clone();
		rev[r2] = mc.add(mc.multiply(rev[r1], k), rev[r2]);
		return new DVector<T>(rev, false,mc);
	}

	@Override
	public int calRank() {
		T z = mc.getZero();
		for (int i = 0; i < vec.length; i++) {
			if (!mc.isEqual(vec[i], z)) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public T calDet() {
		if (vec.length == 1) {
			return vec[0];
		}
		throw new ArithmeticException("Cannot calculate det for: " + row + "��" + column);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Matrix<T> subMatrix(int i1, int j1, int i2, int j2) {
		super.subMatrix(i1, j1, i2, j2);
		if (isRow) {
			int len = j2 - j1 + 1;
			T[] fs = (T[]) new Object[len];
			System.arraycopy(vec, j1, fs, 0, len);
			return new DVector<T>(fs, true,mc);
		} else {
			int len = i2 - i1 + 1;
			T[] fs = (T[]) new Object[len];
			System.arraycopy(vec, i1, fs, 0, len);
			return new DVector<T>(fs, false,mc);
		}

	}

	@Override
	public DVector<T> exchangeRow(int r1, int r2) {
		rowRangeCheck(r1, r2);
		T[] rev = vec.clone();
		rev[r1] = vec[r2];
		rev[r2] = vec[r1];
		return new DVector<T>(rev, false,mc);
	}

	@Override
	public DVector<T> exchangeColumn(int c1, int c2) {
		columnRangeCheck(c1, c2);
		T[] rev = vec.clone();
		rev[c1] = vec[c2];
		rev[c2] = vec[c1];
		return new DVector<T>(rev, true,mc);
	}

	@Override
	public DVector<T> multiplyNumberColumn(T n, int c) {
		columnRangeCheck(c);
		T[] rev = vec.clone();
		rev[c] =  mc.multiply(rev[c], n);
		return new DVector<T>(rev, true,mc);
	}

	@Override
	public DVector<T> multiplyNumberColumn(long n, int c) {
		columnRangeCheck(c);
		T[] rev = vec.clone();
		rev[c] =  mc.multiplyLong(rev[c], n);
		return new DVector<T>(rev, true,mc);
	}

	@Override
	public DVector<T> multiplyNumberRow(T n, int r) {
		rowRangeCheck(r);
		T[] rev = vec.clone();
		rev[r] =  mc.multiply(rev[r], n);
		return new DVector<T>(rev, false,mc);
	}

	@Override
	public DVector<T> multiplyNumberRow(long n, int r) {
		rowRangeCheck(r);
		T[] rev = vec.clone();
		rev[r] = mc.multiplyLong(rev[r], n);
		return new DVector<T>(rev, false,mc);
	}
	/**
	 * Return a new Vector = k * this.This method is generally the same 
	 * to {@link #multiplyNumber(long)} , yet the returning object is 
	 * sure to be a Vector.
	 * @return k * this
	 */
	public DVector<T> multiplyNumberVector(long k){
		int len = vec.length;
		@SuppressWarnings("unchecked")
		T[] reV = (T[]) new Object[len];
		for (int i = 0; i < len; i++) {
			reV[i] = mc.multiplyLong(vec[i], k);
		}
		return new DVector<T>(reV, isRow,mc);
	}
	
	/**
	 * Return a new Vector = k * this.This method is generally the same 
	 * to {@link #multiplyNumber(T)} , yet the returning object is 
	 * sure to be a Vector.
	 * @return k * this
	 */
	public DVector<T> multiplyNumberVector(T k){
		int len = vec.length;
		@SuppressWarnings("unchecked")
		T[] reV = (T[]) new Object[len];
		for (int i = 0; i < len; i++) {
			reV[i] = mc.multiply(vec[i], k);
		}
		return new DVector<T>(reV, isRow,mc);
	}
	/**
	 * Return whether is vector is a row vector.
	 * @return true if this vector is a row vector.
	 */
	@Override
	public boolean isRow() {
		return isRow;
	}
	/**
	 * Return the value of |this|.The value will be a non-zero value. 
	 * @return |this|
	 */
	@Override
	public T calLength(){
		T re = mc.getZero();
		for(int i=0;i<vec.length;i++){
			re = mc.add(mc.multiply(vec[i], vec[i]), re);
		}
		return mc.squareRoot(re);
	}
	/**
	 * Calculate the square of |this|,which has full precision and use T as the 
	 * returning result.The result is equal to use {@link #scalarProduct(DVector, DVector)} as 
	 * {@code scalarProduct(this,this)} but this method will have a better performance.
	 * @return |this|^2
	 */
	@Override
	public T calLengthSq(){
		T re = mc.getZero();
		for(int i=0;i<vec.length;i++){
			re = mc.add(mc.multiply(vec[i], vec[i]), re);
		}
		return re;
	}
	@Override
	public DVector<T> unitVector() {
		T l = calLength();
		@SuppressWarnings("unchecked")
		T[] vecn = (T[]) new Object[this.vec.length];
		for(int i=0;i<vecn.length;i++){
			vecn[i] = mc.divide(vec[i], l);
		}
		return new DVector<>(vecn,isRow,mc);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.AbstractVector#applyFunction(cn.timelives.java.utilities.math.MathFunction)
	 */
	@Override
	public DVector<T> applyFunction(MathFunction<T, T> f) {
		return new DVector<>(ArraySup.mapTo(vec, f),isRow,mc);
	}
	
	/*
	 * @see cn.timelives.java.math.linearAlgebra.Vector#toString(cn.timelives.java.math.numberModels.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for(int i=0,size= getSize();i<size;i++) {
			sb.append(nf.format(getNumber(i), mc)).append(',');
		}
		sb.setCharAt(sb.length()-1, ']');
		return sb.toString();
	}
	@Override
	public <N> DVector<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		N[] narr = ArraySup.mapTo(vec, mapper);
		return new DVector<>(narr, isRow, newCalculator);
	}
	
	
	
	
}
 
