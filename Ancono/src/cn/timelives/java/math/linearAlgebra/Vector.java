package cn.timelives.java.math.linearAlgebra;

import java.util.Arrays;
import java.util.function.Function;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.MathCalculatorAdapter;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.math.planeAG.PVector;

/**
 * A vector is a matrix but one dimension (row or column) is one in length.
 * @author liyicheng
 *
 * @param <T>
 */
public abstract class Vector<T> extends Matrix<T> {
	/**
	 * Decide whether this vector is a row-vector which means column count is
	 * the length of vec. Otherwise,the column count will be 1 and row count will
	 * be vec.length.
	 */
	protected final boolean isRow;
	protected Vector(int length,boolean isRow ,MathCalculator<T> mc) {
		super(isRow ? length : 1, isRow ? 1 : length, mc);
		this.isRow = isRow;
	}
	/**
	 * Returns the number of dimension of this vector.
	 * @return
	 */
	public int getSize() {
		return isRow ? row : column;
	}
	
	/**
	 * Determines whether the two vectors are of the same size.
	 * @param v another vector.
	 * @return {@code true} if they are the same in size.
	 */
	public boolean isSameSize(Vector<?> v) {
		return getSize() == v.getSize();
	}
	
	public abstract T getNumber(int index);
	/**
	 * Returns an array containing all of the elements in this vector in
     * proper sequence (from first to last element),.
	 * @return
	 */
	public abstract Object[] toArray();
	/**
	 * Returns an array containing all of the elements in this vector in
     * proper sequence (from first to last element), the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
	 * @param arr
	 * @return
	 */
	public abstract T[] toArray(T[] arr);
	
	/*
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#getValues()
	 */
	@Override
	public Object[][] getValues() {
		if(isRow) {
			Object[][] mat = new Object[1][];
			mat[0] = toArray();
			return mat;
		}else {
			int size = getSize();
			Object[][] mat = new Object[size][1];
			for(int i=0;i<size;i++) {
				mat[i][0] = getNumber(i);
			}
			return mat;
		}
	}
	
	
	/**
	 * Return the value of |this|.The value will be a non-negative value.
	 * @return |this|
	 */
	public T calLength(){
		return mc.squareRoot(calLengthSq());
	}
	/**
	 * Returns a unit vector of this vector's direction, throws an exception 
	 * if this vector is an zero vector.
	 * <pre>
	 * this/|this|
	 * </pre>
	 * @return a vector
	 */
	public abstract Vector<T> unitVector();
	
	/**
	 * Calculate the square of |this|,which has full precision and use T as the 
	 * returning result.The result is equal to use {@link #scalarProduct(Vector, Vector)} as 
	 * {@code scalarProduct(this,this)} but this method will have a better performance.
	 * @return |this|^2
	 */
	public T calLengthSq(){
		T re = mc.getZero();
		int size = getSize();
		for(int i=0;i<size;i++){
			T t = getNumber(i);
			re = mc.add(mc.multiply(t, t), re);
		}
		return re;
	}
	
	protected final void checkSameSize(Vector<?> v) {
		if(!isSameSize(v)) {
			throw new ArithmeticException("Different dimension:"+getSize()+":"+v.getSize());
		}
	}
	
	/**
	 * This method will return the inner product of {@code this} and {@code v}.
	 * The size of the two vectors must be the same while what kind of vector
	 * (row or column) is ignored. 
	 * @param v a vector
	 * @return the inner(scalar) product of this two vectors.
	 * @throws ArithmeticException if dimension doesn't match
	 */
	public T innerProduct(Vector<T> v) {
		checkSameSize(v);
		final int size = getSize();
		T re =  mc.getZero();
		for(int i=0;i<size;i++){
			re = mc.add(mc.multiply(getNumber(i), v.getNumber(i)), re);
		}
		return re;
	}
	
	
	/**
	 * Determines whether the two vectors are perpendicular.
	 * @param v another vector 
	 * @return {@code true} of the vectors are perpendicular
	 */
	public boolean isPerpendicular(Vector<T> v){
		return mc.isZero(innerProduct(v));
	}
	/**
	 * Returns the angle of {@code this} and {@code v}.
	 * <pre> arccos(this · v / (|this| |v|))</pre>
	 * @param s
	 * @return <pre> arccos(this · v / (|this| |v|))</pre>
	 */
	public T angle(Vector<T> v) {
		return mc.arccos(angleCos(v));
	}
	/**
	 * Returns the cos value of the angle of {@code this} and {@code v}.
	 * <pre>this · v / (|this| |v|)</pre>
	 * @param s
	 * @return <pre>this · v / (|this| |v|)</pre>
	 */
	public T angleCos(Vector<T> v) {
		T pro = innerProduct(v);
		return mc.divide(pro, mc.multiply(calLength(), v.calLength()));
	}
	
	
	/**
	 * Determines whether this vector is a zero vector.
	 * @return
	 */
	public boolean isZeroVector(){
		for(int i=0,size=getSize();i<size;i++) {
			if(!mc.isZero(getNumber(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Determines whether the two vectors are parallel. 
	 * If any of the two vector is a zero vector , than 
	 * the method will return true.
	 * @param v a vector
	 * @return {@code true} if {@code this // v}
	 */
	public boolean isParallel(Vector<T> v){
		// dimension check
		checkSameSize(v);
		if (isZeroVector() || v.isZeroVector()) {
			return true;
		}
		final int size = getSize();
		int not0 = 0;
		while (mc.isZero(getNumber(not0))) {
			if (mc.isZero(v.getNumber(not0)) == false) {
				return false;
			}
			not0++;
			if (not0 + 1 == size) {
				return true;
			}
		}
		T t1 = getNumber(not0);
		T t2 = v.getNumber(not0);
		for (int i = not0 + 1; i < size; i++) {
			if (mc.isEqual(mc.multiply(t1, v.getNumber(i)), mc.multiply(t2, getNumber(i))) == false) {
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.Matrix#applyFunction(cn.timelives.java.utilities.math.MathFunction)
	 */
	@Override
	public abstract Vector<T> applyFunction(MathFunction<T, T> f);
	/**
	 * Gets whether it is a row vector.
	 * @return
	 */
	public boolean isRow(){
		return isRow;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#multiplyNumber(long)
	 */
	@Override
	public abstract Vector<T> multiplyNumber(long n);
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#multiplyNumber(java.lang.Object)
	 */
	@Override
	public abstract Vector<T> multiplyNumber(T n);
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#negative()
	 */
	@Override
	public abstract Vector<T> negative();
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#transportMatrix()
	 */
	@Override
	public abstract Vector<T> transportMatrix();
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		int size = getSize();
		for(int i=0;i<size;i++){
			sb.append(nf.format(getNumber(i), mc)).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}
	/*
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public abstract <N> Vector<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
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
	 * @see DVector#createVector(boolean, long[])
	 */
	public static <T> Vector<T> createVector(MathCalculator<T> mc,boolean isRow,
			@SuppressWarnings("unchecked") T...fs){
		@SuppressWarnings("unchecked")
		T[] vec = (T[]) new Object[fs.length];
		for(int i=0;i<vec.length;i++){
			vec[i] = fs[i] == null ? mc.getZero() : fs[i];
		}
		return new DVector<T>(vec,isRow,mc);
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
	 * @see DVector#createVector(boolean, T[])
	 */
	public static Vector<Long> createVector(boolean isRow,long[] ns){
		Long[] vec = new Long[ns.length];
		for(int i=0;i<vec.length;i++){
			vec[i] = Long.valueOf(ns[i]);
		}
		return new DVector<Long>(vec,isRow,MathCalculatorAdapter.getCalculatorLong());
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
	 * This method provides a more suitable implement for vector adding than {@link Matrix#addMatrix(Matrix, Matrix)},
	 * this method will add the two vector and return a column vector as the result.
	 * @return a column vector as result
	 * @throws ArithmeticException if dimension doesn't match
	 */
	public static <T> Vector<T> addVector(Vector<T> v1 , Vector<T> v2){
		v1.checkSameSize(v2);
		final int size = v1.getSize();
		@SuppressWarnings("unchecked")
		T[] re = (T[]) new Object[size];
		MathCalculator<T> mc = v1.mc;
		for(int i=0;i<re.length;i++){
			re[i] = mc.add(v1.getNumber(i), v2.getNumber(i));
		}
		return new DVector<T>(re,false,mc);
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
	public static <T,R> R intersectionAngle(DVector<T> v1,DVector<T> v2,MathFunction<T,R> arccos){
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
	public static <T> T cosValueOfIntersectionAngle(DVector<T> v1,DVector<T> v2){
		T re = v1.innerProduct(v2);
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
		return new DVector<T>(f,isRow,mc);
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
	 * Returns a vector that is filled with the same value.
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
		return new DVector<T>(arr,isRow,mc);
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
		return new DVector<>(arr, false, mat.getMathCalculator());
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
		return new DVector<>(arr, true, mat.getMathCalculator());
	}
}
