package cn.timelives.java.math.algebra.linearAlgebra;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static cn.timelives.java.utilities.Printer.print;

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
		super(isRow ? 1:length, isRow ?  length : 1, mc);
		this.isRow = isRow;
	}
	/**
	 * Returns the number of dimension of this vector.
	 */
	public int getSize() {
		return isRow ? row : column;
	}
	
	/**
     * Determines whether the two vectors are of the identity size.
	 * @param v another vector.
     * @return {@code true} if they are the identity in size.
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
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#getValues()
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
        return getMc().squareRoot(calLengthSq());
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
	 * returning result.The result is equal to use {@link #innerProduct(Vector)} as
	 * {@code innerProduct(this)} but this method will have a better performance.
	 * @return |this|^2
	 */
	public T calLengthSq(){
	    var mc = getMc();
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
     * The size of the two vectors must be the identity while what kind of vector
	 * (row or column) is ignored. 
	 * @param v a vector
	 * @return the inner(scalar) product of this two vectors.
	 * @throws ArithmeticException if dimension doesn't match
	 */
	public T innerProduct(Vector<T> v) {
		checkSameSize(v);
		var mc = getMc();
		final int size = getSize();
        T re = mc.getZero();
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
        return getMc().isZero(innerProduct(v));
	}
	/**
	 * Returns the angle of {@code this} and {@code v}.
	 * <pre> arccos(this · v / (|this| |v|))</pre>
	 * @param v
	 * @return <pre> arccos(this · v / (|this| |v|))</pre>
	 */
	public T angle(Vector<T> v) {
        return getMc().arccos(angleCos(v));
	}
	/**
	 * Returns the cos value of the angle of {@code this} and {@code v}.
	 * <pre>this · v / (|this| |v|)</pre>
	 * @param v
	 * @return <pre>this · v / (|this| |v|)</pre>
	 */
	public T angleCos(Vector<T> v) {
		T pro = innerProduct(v);
        return getMc().divide(pro, getMc().multiply(calLength(), v.calLength()));
	}
	
	
	/**
	 * Determines whether this vector is a zero vector.
	 */
	public boolean isZeroVector(){
	    var mc = getMc();
		for(int i=0,size=getSize();i<size;i++) {
            if (!mc.isZero(getNumber(i))) {
				return false;
			}
		}
		return true;
	}

    /**
     * Determines whether this vector is an unit vector.
     */
	public boolean isUnitVector(){
	    var mc = getMc();
	    return mc.isEqual(calLengthSq(),mc.getOne());
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
        while (getMc().isZero(getNumber(not0))) {
            if (!getMc().isZero(v.getNumber(not0))) {
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
            if (!getMc().isEqual(getMc().multiply(t1, v.getNumber(i)), getMc().multiply(t2, getNumber(i)))) {
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
	 */
	public boolean isRow(){
		return isRow;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#multiplyNumber(long)
	 */
	@Override
	public abstract Vector<T> multiplyNumber(long n);
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#multiplyNumber(java.lang.Object)
	 */
	@Override
	public abstract Vector<T> multiplyNumber(T n);
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#negative()
	 */
	@Override
	public abstract Vector<T> negative();
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#transportMatrix()
	 */
	@Override
	public abstract Vector<T> transportMatrix();

	public Vector<T> toRowVector(){
	    if(isRow){
	        return this;
        }else{
	        return transportMatrix();
        }
    }

    public Vector<T> toColumnVector(){
	    if(isRow){
	        return transportMatrix();
        }else{
	        return this;
        }
    }
	@NotNull
	@Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		int size = getSize();
		for(int i=0;i<size;i++){
            sb.append(nf.format(getNumber(i), getMc())).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}
	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
	 */
    @NotNull
    @Override
    public abstract <N> Vector<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);
	
	/**
	 * Create a new Matrix with the given fraction array.A boolean representing whether the 
	 * vector should be a row-vector or column-vector is necessary.The {@link Matrix} returned by 
	 * this method generally has a better performance in contrast to the matrix return by simply call 
	 * {@link Matrix#valueOf(Object[][], MathCalculator)} using a two-dimension array as parameter.
	 * <p>For example , assume {@code fs} is 
	 * an array contains following values:[1,3,4,5],then {@code createVector(true,fs} will return a 
	 * matrix whose row count is one and column count is 4, while {@code createVector(false,fs} will 
	 * return a matrix with 4 rows and 1 column.
	 * @param isRow decides whether the vector return is a row-vector
	 * @param fs the numbers,null values will be considered as ZERO
	 * @return a newly created vector 
	 * @see DVector#createVector(boolean, long[])
	 */
	@SafeVarargs
    public static <T> Vector<T> createVector(MathCalculator<T> mc, boolean isRow,
                                             @SuppressWarnings("unchecked") T...fs){
		@SuppressWarnings("unchecked")
		T[] vec = (T[]) new Object[fs.length];
		for(int i=0;i<vec.length;i++){
			vec[i] = fs[i] == null ? mc.getZero() : fs[i];
		}
		return new DVector<>(vec,isRow,mc);
	}
	/**
	 * Create a new Matrix with the given fraction array.A boolean representing whether the 
	 * vector should be a row-vector or column-vector is necessary.The {@link Matrix} returned by 
	 * this method generally has a better performance in contrast to the matrix return by simply call 
	 * {@link Matrix#valueOf(Object[][], MathCalculator)} using a two-dimension array as parameter.
	 * <p>For example , assume {@code ns} is 
	 * an array contains following values:[1,3,4,5],then {@code createVector(true,ns} will return a 
	 * matrix whose row count is one and column count is 4, while {@code createVector(false,ns} will 
	 * return a matrix with 4 rows and 1 column.
	 * @param isRow decides whether the vector return is a row-vector
	 * @param ns the numbers
	 * @return a newly created vector 
	 * @see DVector#valueOf(Object[][], MathCalculator)
	 */
	public static Vector<Long> createVector(boolean isRow,long[] ns){
		Long[] vec = new Long[ns.length];
		for(int i=0;i<vec.length;i++){
			vec[i] = ns[i];
		}
		return new DVector<>(vec,isRow,Calculators.getCalculatorLong());
	}
	/**
	 * Create a new column vector according to the array of fraction given.Null values will be considered 
	 * as {@link MathCalculator#getZero()}
	 * @param fs the numbers
	 * @return a newly created column vector 
	 * @see #createVector(MathCalculator, boolean, Object[])
	 */
	@SafeVarargs
	public static <T> Vector<T> createVector(MathCalculator<T> mc,T...fs){
		return createVector(mc,false,fs);
	}
	
	/**
	 * Create a new column vector according to the array of fraction given.
	 * @param arr the numbers
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
        MathCalculator<T> mc = v1.getMc();
		for(int i=0;i<re.length;i++){
			re[i] = mc.add(v1.getNumber(i), v2.getNumber(i));
		}
		return new DVector<>(re, false, mc);
	}
	
	/**
	 * A method similar to {@link #addVector(Vector, Vector)}, but subtract.
	 * @return a column vector as result
	 * @throws ArithmeticException if dimension doesn't match
	 */
	public static <T> Vector<T> subtractVector(Vector<T> v1 , Vector<T> v2){
		v1.checkSameSize(v2);
		final int size = v1.getSize();
		@SuppressWarnings("unchecked")
		T[] re = (T[]) new Object[size];
        MathCalculator<T> mc = v1.getMc();
		for(int i=0;i<re.length;i++){
			re[i] = mc.subtract(v1.getNumber(i), v2.getNumber(i));
		}
		return new DVector<>(re, false, mc);
	}
	/**
	 * Provides a better efficiency for adding several vectors without creating 
	 * a new vector when adding each time. 
	 * @return a column vector as result
	 * @throws ArithmeticException if dimension doesn't match
	 */
	@SafeVarargs
	public static <T> Vector<T> addVectors(Vector<T>...vecs){
		return addVectors(vecs.length,vecs);
	}
	/**
	 * Provides a better efficiency for adding several vectors without creating 
	 * a new vector when adding each time. 
	 * @param n the number of vectors to add
	 * @return a column vector as result
	 * @throws ArithmeticException if dimension doesn't match
	 */
	@SafeVarargs
	public static <T> Vector<T> addVectors(int n,Vector<T>...vecs){
		if(n>vecs.length) {
			throw new IllegalArgumentException();
		}
		final int size = vecs[0].getSize();
		@SuppressWarnings("unchecked")
		T[] re = (T[]) vecs[0].toArray();
        MathCalculator<T> mc = vecs[0].getMc();
		for(int j=1;j<n;j++) {
			Vector<T> v = vecs[j];
			if(v.getSize() != size) {
				throw new IllegalArgumentException();
			}
			for(int i=0;i<size;i++){
				re[i] = mc.add(re[i], v.getNumber(i));
			}
		}
		return new DVector<>(re, false, mc);
	}
	/**
	 * Provides a better efficiency for adding several vectors without creating 
	 * a new vector when adding each time. 
	 * @return a column vector as result
	 * @throws ArithmeticException if dimension doesn't match
	 */
	@SafeVarargs
	public static <T> Vector<T> addVectors(Vector<T> v,Vector<T>...vecs){
		final int size = v.getSize();
		@SuppressWarnings("unchecked")
		T[] re = (T[]) v.toArray();
        MathCalculator<T> mc = vecs[0].getMc();
		for (Vector<T> vt : vecs) {
			if (vt.getSize() != size) {
				throw new IllegalArgumentException();
			}
			for (int i = 0; i < size; i++) {
				re[i] = mc.add(re[i], vt.getNumber(i));
			}
		}
		return new DVector<>(re, false, mc);
	}
	
	/**
	 * Calculate the intersection angle of the two vector.Which is usually shown as {@literal <v1,v2>}.
	 * @param v1 a vector
     * @param v2 another vector
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
	 * @param v1 a vector
	 * @param v2 another vector
	 * @return cos{@literal <v1,v2>}.
	 * @throws ArithmeticException if one of the vectors is zero vector
	 */
	public static <T> T cosValueOfIntersectionAngle(Vector<T> v1,Vector<T> v2){
		T re = v1.innerProduct(v2);
        MathCalculator<T> mc = v1.getMc();
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
		return new DVector<>(f, isRow, mc);
	}
	/**
	 * Return a zero vector of the given length.
	 */
	public static <T> Vector<T> zeroVector(int length,MathCalculator<T> mc){
		return zeroVector(length, false,mc);
	}

    /**
     * Returns a unit column vector of the given length.
     */
	public static <T> Vector<T> unitVector(int length,int unitIndex, MathCalculator<T> mc){
        if(length<1){
            throw new IllegalArgumentException("length < 1");
        }
	    @SuppressWarnings("unchecked") T[] arr = (T[]) new Object[length];
	    T zero = mc.getZero();
	    T one = mc.getOne();
	    Arrays.fill(arr,zero);
	    arr[unitIndex] = one;
	    return new DVector<>(arr,false,mc);
    }



	/**
     * Returns a vector that is filled with the identity value.
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
		return new DVector<>(arr, isRow, mc);
	}


	public static <T> Vector<T> resizeOf(Vector<T> v,int leftExpansion,int rightExpansion){
	    if(leftExpansion+v.getSize()<= 0 || rightExpansion + v.getSize()<=0 ){
	        throw new IllegalArgumentException();
        }
	    var z = v.getMathCalculator().getZero();
	    int size = v.getSize();
	    int nSize = leftExpansion+rightExpansion+size;
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[nSize];
	    for(int i=0;i<leftExpansion;i++){
            arr[i] = z;
        }
        for(int i=leftExpansion;i<leftExpansion+size;i++){
            if(i-leftExpansion<0 || i < 0){
                continue;
            }
            if(i >= nSize){
                break;
            }
            arr[i] = v.getNumber(i-leftExpansion);
        }
        for(int i=leftExpansion+size;i<nSize;i++){
            arr[i] = z;
        }
        return new DVector<>(arr,v.isRow,v.getMathCalculator());
    }

	/**
	 * Returns a column vector from the matrix. 
	 * @param mat
	 * @param column from 0
	 * @return
	 */
	public static <T> Vector<T> column(Matrix<T> mat,int column){
		return mat.getColumn(column);
	}
	/**
	 * Returns a row vector from the matrix. 
	 * @param mat
	 * @param row from 0
	 * @return
	 */
	public static <T> Vector<T> row(Matrix<T> mat,int row){
        return mat.getRow(row);
	}
	/**
	 * Orthogonalizes the given vectors by using Schmidt method.
	 * 
	 * @param vs an array of vectors
	 * @return a new list of vectors
	 */
	@SafeVarargs
	public static <T> List<Vector<T>> orthogonalize(Vector<T>... vs) {
		//vs    : a1,a2,a3 ... an
		//list  : b1,b2,b3 ... bn
		//temp1 : -b1/b1^2 ... -bn/bn^2
		//temp2 : used when adding
		final int n = vs.length;
		if(n<2) {
			return Arrays.asList(vs);
		}
		final int size = vs[0].getSize();
		//size check
		for (int i = 1; i < n; i++) {
			if (vs[i].getSize() != size) {
				throw new IllegalArgumentException("vector's length=" + vs[i].getSize() + " != " + n);
			}
		}
		
		MathCalculator<T> mc = vs[0].getMathCalculator();
		List<Vector<T>> list = new ArrayList<>(n);
		
		@SuppressWarnings("unchecked")
		Vector<T>[] temp1 = new Vector[n-1],//temp1: b/b^2
			temp2 = new Vector[n];
		
		list.add(vs[0]);
		//b1 = a1
		Vector<T> prev = vs[0];
		for(int i=1;i<n;i++) {
			temp1[i-1] = prev.multiplyNumber(mc.negate(
					mc.reciprocal(prev.calLengthSq())));
			
			Vector<T> vec = vs[i];
			for(int j=0;j<i;j++) {
				temp2[j] = list.get(j).multiplyNumber(temp1[j].innerProduct(vec)); 
			}
			temp2[i] = vec;
			Vector<T> result =  addVectors(i+1,temp2);
			list.add(result);
			prev = result;
		}
		return list;
	}
	
	/**
	 * Orthogonalizes the given vectors by using Schmidt method.
	 * 
	 * @param vs an array of vectors
	 * @return a new list of vectors
	 */
	@SafeVarargs
	public static <T> List<Vector<T>> orthogonalizeAndUnit(Vector<T>... vs) {
		List<Vector<T>> list = orthogonalize(vs);
		for(int i=0;i<list.size();i++) {
			list.set(i, list.get(i).unitVector());
		}
		return list;
	}

	/**
	 * Returns a column vector whose n-th element is map.get(n)(0 for null), 0<= n <=length-1
	 * @param map
	 * @param length
	 * @param <T>
	 * @return
	 */
	public static <T> Vector<T> fromIndexMap(Map<Integer,T> map,int length,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) new Object[length];
		T z = mc.getZero();
		for(int i=0;i<length;i++){
			T t = map.get(i);
			if(t == null){
				arr[i] = z;
			}else{
				arr[i] = t;
			}
		}
		return new DVector<>(arr,false,mc);
	}

    /**
     * Returns the result of {@literal mat * v}, it is required the vector's size is
     * equal to the matrix's column count. This method will ignore whether the vector is
     * a row vector.
     *
     * @param mat a matrix
     * @param v   a vector
     * @return {@literal mat * v} as a vector, which has the length of {@code mat.getRowCount()}.
     */
    public static <T> Vector<T> multiplyToVector(Matrix<T> mat, Vector<T> v) {
        if (mat.column != v.getSize()) {
            throw new IllegalArgumentException("mat.column != v.size");
        }
        var mc = mat.getMathCalculator();
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[mat.row];
        for (int i = 0; i < mat.row; i++) {
            T t = mc.getZero();
            for (int j = 0; j < mat.column; j++) {
                t = mc.add(t, mc.multiply(mat.getNumber(i, j), v.getNumber(j)));
            }
            result[i] = t;
        }
        return new DVector<>(result, false, mc);
    }


    /**
     * Determines whether the given vectors are linear relevant. It is required that
     * all the given vectors haves the same size.
     * @param vectors a series of vectors of the same size
     * @return <code>true</code> if they are linear relevant
     */
    @SafeVarargs
    public static <T> boolean isLinearRelevant(Vector<T>...vectors){
        int size = vectors[0].getSize();
        for(var v : vectors){
            if(v.getSize()!=size){
                throw new IllegalArgumentException("Different size!");
            }
        }
        if(vectors.length > size){
            return true;
        }
        var mat =  fromVectors(true,vectors);
        int rank = mat.calRank();
        return rank < vectors.length;
//        return mat.calRank() >= size;
    }


	/**
	 * Determines whether the given vectors are linear relevant. It is required that
	 * all the given vectors haves the same size.
	 * @param vectors a series of vectors of the same size
	 * @return <code>true</code> if they are linear relevant
	 */
	public static <T> boolean isLinearRelevant(List<Vector<T>> vectors){
		int size = vectors.get(0).getSize();
		for(var v : vectors){
			if(v.getSize()!=size){
				throw new IllegalArgumentException("Different size!");
			}
		}
		if(vectors.size() > size){
			return true;
		}
		var mat =  fromVectors(true,vectors);
		int rank = mat.calRank();
		return rank < vectors.size();
	}

//	public static void main(String[] args) {
//	    var v1 = Vector.createVector(new long[]{1,3,14});
//	    v1 = Vector.resizeOf(v1,-1,1);
//	    print(v1);
//		MathCalculator<Double> mc = Calculators.getCalculatorDouble();
//		@SuppressWarnings("unchecked")
//		Vector<Double>[] vecs = new Vector[3];
//		vecs[0] = Vector.createVector(mc, 1d,1d,0d);
//		vecs[1] = Vector.createVector(mc, 1d,0d,1d);
//		vecs[2] = Vector.createVector(mc, -1d,0d,0d);
////		print(addVectors(vecs));
//		List<Vector<Double>> list = orthogonalize(vecs);
//		print(list);
//		vecs = list.toArray(vecs);
//		print(vecs[0].innerProduct(vecs[1]));
//		print(vecs[0].innerProduct(vecs[2]));
//		print(vecs[2].innerProduct(vecs[1]));
//	}
}
