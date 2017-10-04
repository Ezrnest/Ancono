package cn.timelives.java.math.linearAlgebra;

import java.util.Arrays;
import java.util.function.Function;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.MathCalculatorAdapter;
import cn.timelives.java.utilities.ArraySup;

/**
 * A vector is a matrix but one dimension (row or column) is one in length.
 * 
 * @author lyc
 *
 */
public class Vector<T> extends AbstractVector<T> {
	
	/**
	 * The data to be stored.
	 */
	private final T[] vec;

	/**
	 * Create a new vector with the given array and
	 * 
	 * @param vec
	 * @param isRow
	 */
	protected Vector(T[] vec, boolean isRow,MathCalculator<T> mc) {
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
	public Vector<T> negative() {
		int len = isRow ? column : row;
		@SuppressWarnings("unchecked")
		T[] reV = (T[]) new Object[len];
		for (int i = 0; i < len; i++) {
			reV[i] = mc.negate(vec[i]);
		}
		return new Vector<>(reV, isRow,mc);
	}

	@Override
	public Vector<T> transportMatrix() {
		return new Vector<>(vec, !isRow,mc);
	}

	@Override
	public Vector<T> multiplyNumber(long n) {
		return multiplyNumberVector(n);
	}

	@Override
	public Vector<T> multiplyNumber(T n) {
		return multiplyNumberVector(n);
	}

	@Override
	public Matrix<T> cofactor(int r, int c) {
		throw new ArithmeticException("Too small for cofactor");
	}

	@Override
	public Vector<T> multiplyAndAddColumn(T k, int c1, int c2) {
		if (!isRow) {
			throw new IllegalArgumentException("A column vector");
		}
		if (c1 == c2) {
			throw new IllegalArgumentException("The same column:" + c1);
		}
		T[] rev = vec.clone();
		rev[c2] = mc.add(mc.multiply(rev[c1], k), rev[c2]);
		return new Vector<T>(rev, true,mc);
	}

	@Override
	public Vector<T> multiplyAndAddColumn(long k, int c1, int c2) {
		if (!isRow) {
			throw new IllegalArgumentException("A column vector");
		}
		if (c1 == c2) {
			throw new IllegalArgumentException("The same column:" + c1);
		}
		T[] rev = vec.clone();
		rev[c2] = mc.add(mc.multiplyLong(rev[c1], k), rev[c2]);
		return new Vector<T>(rev, true,mc);
	}

	@Override
	public Vector<T> multiplyAndAddRow(long k, int r1, int r2) {
		if (isRow) {
			throw new IllegalArgumentException("A column vector");
		}
		if (r1 == r2) {
			throw new IllegalArgumentException("The same row:" + r1);
		}
		T[] rev = vec.clone();
		rev[r2] = mc.add(mc.multiplyLong(rev[r1], k), rev[r2]);
		return new Vector<T>(rev, false,mc);
	}

	@Override
	public Vector<T> multiplyAndAddRow(T k, int r1, int r2) {
		if (isRow) {
			throw new IllegalArgumentException("A column vector");
		}
		if (r1 == r2) {
			throw new IllegalArgumentException("The same row:" + r1);
		}
		T[] rev = vec.clone();
		rev[r2] = mc.add(mc.multiply(rev[r1], k), rev[r2]);
		return new Vector<T>(rev, false,mc);
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
		throw new ArithmeticException("Cannot calculate det for: " + row + "¡Á" + column);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Matrix<T> subMatrix(int i1, int j1, int i2, int j2) {
		super.subMatrix(i1, j1, i2, j2);
		if (isRow) {
			int len = j2 - j1 + 1;
			T[] fs = (T[]) new Object[len];
			System.arraycopy(vec, j1, fs, 0, len);
			return new Vector<T>(fs, true,mc);
		} else {
			int len = i2 - i1 + 1;
			T[] fs = (T[]) new Object[len];
			System.arraycopy(vec, i1, fs, 0, len);
			return new Vector<T>(fs, false,mc);
		}

	}

	@Override
	public Vector<T> exchangeRow(int r1, int r2) {
		rowRangeCheck(r1, r2);
		T[] rev = vec.clone();
		rev[r1] = vec[r2];
		rev[r2] = vec[r1];
		return new Vector<T>(rev, false,mc);
	}

	@Override
	public Vector<T> exchangeColumn(int c1, int c2) {
		columnRangeCheck(c1, c2);
		T[] rev = vec.clone();
		rev[c1] = vec[c2];
		rev[c2] = vec[c1];
		return new Vector<T>(rev, true,mc);
	}

	@Override
	public Vector<T> multiplyNumberColumn(T n, int c) {
		columnRangeCheck(c);
		T[] rev = vec.clone();
		rev[c] =  mc.multiply(rev[c], n);
		return new Vector<T>(rev, true,mc);
	}

	@Override
	public Vector<T> multiplyNumberColumn(long n, int c) {
		columnRangeCheck(c);
		T[] rev = vec.clone();
		rev[c] =  mc.multiplyLong(rev[c], n);
		return new Vector<T>(rev, true,mc);
	}

	@Override
	public Vector<T> multiplyNumberRow(T n, int r) {
		rowRangeCheck(r);
		T[] rev = vec.clone();
		rev[r] =  mc.multiply(rev[r], n);
		return new Vector<T>(rev, false,mc);
	}

	@Override
	public Vector<T> multiplyNumberRow(long n, int r) {
		rowRangeCheck(r);
		T[] rev = vec.clone();
		rev[r] = mc.multiplyLong(rev[r], n);
		return new Vector<T>(rev, false,mc);
	}
	/**
	 * Return a new Vector = k * this.This method is generally the same 
	 * to {@link #multiplyNumber(long)} , yet the returning object is 
	 * sure to be a Vector.
	 * @return k * this
	 */
	public Vector<T> multiplyNumberVector(long k){
		int len = vec.length;
		@SuppressWarnings("unchecked")
		T[] reV = (T[]) new Object[len];
		for (int i = 0; i < len; i++) {
			reV[i] = mc.multiplyLong(vec[i], k);
		}
		return new Vector<T>(reV, isRow,mc);
	}
	
	/**
	 * Return a new Vector = k * this.This method is generally the same 
	 * to {@link #multiplyNumber(T)} , yet the returning object is 
	 * sure to be a Vector.
	 * @return k * this
	 */
	public Vector<T> multiplyNumberVector(T k){
		int len = vec.length;
		@SuppressWarnings("unchecked")
		T[] reV = (T[]) new Object[len];
		for (int i = 0; i < len; i++) {
			reV[i] = mc.multiply(vec[i], k);
		}
		return new Vector<T>(reV, isRow,mc);
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
	 * returning result.The result is equal to use {@link #scalarProduct(Vector, Vector)} as 
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
	public Vector<T> unitVector() {
		T l = calLength();
		@SuppressWarnings("unchecked")
		T[] vecn = (T[]) new Object[this.vec.length];
		for(int i=0;i<vecn.length;i++){
			vecn[i] = mc.divide(vec[i], l);
		}
		return new Vector<>(vecn,isRow,mc);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.AbstractVector#applyFunction(cn.timelives.java.utilities.math.MathFunction)
	 */
	@Override
	public Vector<T> applyFunction(MathFunction<T, T> f) {
		return new Vector<>(ArraySup.mapTo(vec, f),isRow,mc);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Arrays.toString(vec);
	}
	@Override
	public <N> Vector<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		N[] narr = ArraySup.mapTo(vec, mapper);
		return new Vector<>(narr, isRow, newCalculator);
		
	}
	
	/**
	 * Create a new Matrix with the given fraction array.A boolean representing whether the 
	 * vector should be a row-vector or column-vector is necessary.The {@link Matrix} returned by 
	 * this method generally has a better performance in contrast to the matrix return by simply call 
	 * {@link Matrix#valueOf(T[][])} using a two-dimension array as parameter.
	 * <p>For example , assume {@code fs} is 
	 * an array contains following values:[1,3,4,5],then {@code createVector(true,fs} will return a 
	 * matrix whose row count is one and column count is 4, while {@code createVector(false,fs} will 
	 * return a matrix with 4 rows and 1 column.
	 * @param isRow decides whether the vector return is a row-vector
	 * @param fs the numbers,null values will be considered as {@value T#ZERO}
	 * @return a newly created vector 
	 * @see Vector#createVector(boolean, long[])
	 */
	public static <T> Vector<T> createVector(MathCalculator<T> mc,boolean isRow,@SuppressWarnings("unchecked") T...fs){
		@SuppressWarnings("unchecked")
		T[] vec = (T[]) new Object[fs.length];
		for(int i=0;i<vec.length;i++){
			vec[i] = fs[i] == null ? mc.getZero() : fs[i];
		}
		return new Vector<T>(vec,isRow,mc);
	}
	/**
	 * Create a new Matrix with the given fraction array.A boolean representing whether the 
	 * vector should be a row-vector or column-vector is necessary.The {@link Matrix} returned by 
	 * this method generally has a better performance in contrast to the matrix return by simply call 
	 * {@link Matrix#valueOf(T[][])} using a two-dimension array as parameter.
	 * <p>For example , assume {@code ns} is 
	 * an array contains following values:[1,3,4,5],then {@code createVector(true,ns} will return a 
	 * matrix whose row count is one and column count is 4, while {@code createVector(false,ns} will 
	 * return a matrix with 4 rows and 1 column.
	 * @param isRow decides whether the vector return is a row-vector
	 * @param ns the numbers
	 * @return a newly created vector 
	 * @see Vector#createVector(boolean, T[])
	 */
	public static Vector<Long> createVector(boolean isRow,long[] ns){
		Long[] vec = new Long[ns.length];
		for(int i=0;i<vec.length;i++){
			vec[i] = Long.valueOf(ns[i]);
		}
		return new Vector<Long>(vec,isRow,MathCalculatorAdapter.getCalculatorLong());
	}
	/**
	 * Create a new column vector according to the array of fraction given.Null values will be considered 
	 * as {@link MathCalculator#getZero()}
	 * @param fs the numbers
	 * @return a newly created column vector 
	 * @see #createVector(boolean, T[])
	 */
	@SafeVarargs
	public static <T> Vector<T> createVector(MathCalculator<T> mc,T...fs){
		return createVector(mc,false,fs);
	}
	
	/**
	 * Create a new column vector according to the array of fraction given.
	 * @param fs the numbers
	 * @return a newly created vector 
	 * @see #createVector(boolean, long[])
	 */
	public static Vector<Long> createVector(long[] arr){
		return createVector(false, arr);
	}
	
	/**
	 * This method will return the scalar product of the two vector.The two vector 
	 * must have the same length while row vector and column vector are both acceptable.
	 * @param v1 a vector
	 * @param v2 another vector
	 * @return the scalar product of this two vectors.
	 * @throws ArithmeticException if dimension doesn't match
	 */
	public static <T> T scalarProduct(Vector<T> v1,Vector<T> v2){
		//length check
		T[] f1 =v1.vec;
		T[] f2 = v2.vec;
		if(f1.length!=f2.length){
			throw new ArithmeticException("Different dimension:"+v1.vec.length+":"+v2.vec.length);
		}
		MathCalculator<T> mc = v1.mc;
		T re =  mc.getZero();
		for(int i=0;i<f1.length;i++){
			re = mc.add(mc.multiply(f1[i], f2[i]), re);
		}
		return re;
	}
	/**
	 * This method provides a more suitable implement for vector adding than {@link Matrix#addMatrix(Matrix, Matrix)},
	 * this method will add the two vector and return a column vector as the result.
	 * @return a column vector as result
	 * @throws ArithmeticException if dimension doesn't match
	 */
	public static <T> Vector<T> addVector(Vector<T> v1 , Vector<T> v2){
		T[] f1 = v1.vec;
		T[] f2 = v2.vec;
		if(f1.length!=f2.length){
			throw new ArithmeticException("Different dimension:"+v1.vec.length+":"+v2.vec.length);
		}
		@SuppressWarnings("unchecked")
		T[] re = (T[]) new Object[f1.length];
		MathCalculator<T> mc = v1.mc;
		for(int i=0;i<re.length;i++){
			re[i] = mc.add(f1[i], f2[i]);
		}
		return new Vector<T>(re,false,mc);
	}
	/**
	 * Calculate the intersection angle of the two vector.Which is usually shown as {@literal <v1,v2>}.
	 * 
	 * @param v1
	 * @param v2
	 * @param arccos a function to calculate arccos value of T 
	 * @return {@literal <v1,v2>}.
	 * @throws ArithmeticException if one of the vectors is zero vector
	 */
	public static <T,R> R intersectionAngle(Vector<T> v1,Vector<T> v2,MathFunction<T,R> arccos){
		return arccos.apply(cosValueOfIntersectionAngle(v1, v2));
	}
	/**
	 * Calculate the cos value of the intersection angle of the two vector.
	 * Which is usually shown as {@literal cos<v1,v2>}.
	 * The value will be in [-1,1].
	 * 
	 * @param v1
	 * @param v2
	 * @return cos{@literal <v1,v2>}.
	 * @throws ArithmeticException if one of the vectors is zero vector
	 */
	public static <T> T cosValueOfIntersectionAngle(Vector<T> v1,Vector<T> v2){
		T re = scalarProduct(v1, v2);
		MathCalculator<T> mc = v1.mc;
		T d1 = v1.calLength();
		T d2 = v2.calLength();
		if(mc.isEqual(mc.getZero(), d1)||mc.isEqual(mc.getZero(), d2)){
			throw new ArithmeticException("Zero vector");
		}
		return mc.divide(re, mc.multiply(d1, d2));
	}
	private static void checkPositiveLength(int length){
		if(length<1){
			throw new IllegalArgumentException("length<=0");
		}
	}
	
	/**
	 * Return a zero vector of the given length.The length of 
	 * @param length
	 * @return zero vector
	 */
	public static <T> Vector<T> zeroVector(int length,boolean isRow,MathCalculator<T> mc){
		checkPositiveLength(length);
		T zero = mc.getZero();
		@SuppressWarnings("unchecked")
		T[] f = (T[]) new Object[length];
		for(int i=0;i<length;i++){
			f[i] = zero;
		}
		return new Vector<T>(f,isRow,mc);
	}
	/**
	 * Return a zero vector of the given length.The length of 
	 * @param length
	 * @return
	 */
	public static <T> Vector<T> zeroVector(int length,MathCalculator<T> mc){
		return zeroVector(length, false,mc);
	}
	/**
	 * Returns a vector that is 
	 * @param length
	 * @param value
	 * @param mc
	 * @return
	 */
	public static <T> Vector<T> sameValueOf(int length,T value,boolean isRow, MathCalculator<T> mc){
		checkPositiveLength(length);
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) new Object[length];
		Arrays.fill(arr, value);
		return  new Vector<T>(arr,isRow,mc);
	}
	
	/**
	 * Test the vector's dimension except for 0 dimension.
	 * @param v
	 * @return
	 */
	private static <T> boolean isZeroVector0(T[] fs,MathCalculator<T> mc){
		T zero = mc.getZero();
		for(int i=1;i<fs.length;i++){
			if(!mc.isEqual(zero, fs[i])){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Calculate whether two vectors are parallel.If any of the two vector is a zero vector , than 
	 * the method will return true.
	 * @param v1 a vector 
	 * @param v2 another vector
	 * @return true if parallel , false if not.
	 */
	public static <T> boolean areParallel(Vector<T> v1 , Vector<T> v2){
		//dimension check
		T[] fs1 = v1.vec;
		T[] fs2 = v2.vec;
		MathCalculator<T> mc = v1.mc;
		if(fs1.length!=fs2.length){
			throw new ArithmeticException("Different dimension:"+fs1.length+":"+fs2.length);
		}
		if(isZeroVector0(fs2, mc) || isZeroVector0(fs1, mc)){
			return true;
		}
		int not0 = 0;
		while(mc.isZero(fs1[not0])){
			if(mc.isZero(fs2[not0])==false){
				return false;
			}
			not0 ++ ;
			if(not0+1 == fs1.length){
				return true;
			}
		}
		T t1 = fs1[not0];
		T t2 = fs2[not0];
		for(int i=not0+1;i<fs1.length;i++){
			if(mc.isEqual(mc.multiply(t1, fs2[i]), mc.multiply(t2, fs1[i]))==false){
				return false;
			}
		}
		return true;
	}
	/**
	 * Calculate whether two vectors are perpendicular.If any of the two vector is a zero vector , than 
	 * the method will return true.This method is generally equal to call {@link #scalarProduct(Vector, Vector)}
	 * and test whether the value is equal to zero.
	 * @param v1 a vector 
	 * @param v2 another vector
	 * @return true if the two vectors are perpendicular , false if not. 
	 */
	public static <T> boolean arePerpendicular(Vector<T> v1 , Vector<T> v2){
		MathCalculator<T> mc = v1.mc;
		return mc.isEqual(mc.getZero(), scalarProduct(v1,v2));
	}

	/**
	 * Returns a column vector from the matrix. 
	 * @param mat
	 * @param column from 0
	 * @return
	 */
	public static <T> Vector<T> column(Matrix<T> mat,int column){
		if(column < 0 || mat.column <= column){
			throw new IndexOutOfBoundsException("column = "+column);
		}
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) new Object[mat.row];
		for(int i=0;i<arr.length;i++){
			arr[i] = mat.getNumber(i, column);
		}
		return new Vector<>(arr, false, mat.getMathCalculator());
	}
	/**
	 * Returns a row vector from the matrix. 
	 * @param mat
	 * @param row from 0
	 * @return
	 */
	public static <T> Vector<T> row(Matrix<T> mat,int row){
		if(row < 0 || mat.row <= row){
			throw new IndexOutOfBoundsException("row = "+row);
		}
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) new Object[mat.row];
		for(int i=0;i<arr.length;i++){
			arr[i] = mat.getNumber(row,i);
		}
		return new Vector<>(arr, true, mat.getMathCalculator());
	}
	
	
}
 
