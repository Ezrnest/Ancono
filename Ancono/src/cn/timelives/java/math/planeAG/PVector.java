/**
 * 
 */
package cn.timelives.java.math.planeAG;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.linearAlgebra.LinearEquationSolution;
import cn.timelives.java.math.linearAlgebra.LinearEquationSolution.Situation;
import cn.timelives.java.math.linearAlgebra.Matrix;
import cn.timelives.java.math.linearAlgebra.MatrixSup;
import cn.timelives.java.math.linearAlgebra.Vector;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * The vector with two argument (x,y) in plane. Always serves as a column vector.
 * @author liyicheng
 *
 */
public final class PVector<T> extends Vector<T> {
	public final T x,y;
	private T length,lengthSq;
	/**
	 * @param row
	 * @param column
	 * @param mc
	 */
	protected PVector(T x,T y, MathCalculator<T> mc) {
		super(2, false, mc);
		this.x = x;
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#getSize()
	 */
	@Override
	public int getSize() {
		return 2;
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#isRow()
	 */
	@Override
	public boolean isRow() {
		return false;
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#getNumber(int)
	 */
	@Override
	public T getNumber(int index) {
		switch(index){
		case 0:
			return x;
		case 1:
			return y;
		default:
			throw new IndexOutOfBoundsException("Index="+index+" is out of bounds");
		}
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#toArray()
	 */
	@Override
	public Object[] toArray() {
		return new Object[]{x,y};
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#toArray(java.lang.Object[])
	 */
	@Override
	public T[] toArray(T[] arr) {
		if(arr.length<2){
			arr = Arrays.copyOf(arr, 2);
		}
		arr[0] = x;
		arr[1] = y;
		return arr;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#calLength()
	 */
	@Override
	public T calLength() {
		if(length==null){
			length = mc.squareRoot(calLengthSq());
		}
		return length;
	}
	
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#calLengthSq()
	 */
	@Override
	public T calLengthSq() {
		if(lengthSq==null){
			lengthSq = mc.add(mc.multiply(x, x),mc.multiply(y, y));
		}
		return lengthSq;
	}
	

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#applyFunction(cn.timelives.java.math.MathFunction)
	 */
	@Override
	public PVector<T> applyFunction(MathFunction<T, T> f) {
		return new PVector<T>(f.apply(x), f.apply(y), mc);
	}

	/** Ignores the parameter {@code i}.
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#getNumber(int, int)
	 */
	@Override
	public T getNumber(int i, int j) {
		return getNumber(j);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#getValues()
	 */
	@Override
	public T[][] getValues() {
		@SuppressWarnings("unchecked")
		T[][] res = (T[][])Array.newInstance(x.getClass(), 1,2);
		res[0][0] = x;
		res[1][0] = y;
		return res;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#negative()
	 */
	@Override
	public PVector<T> negative() {
		return new PVector<T>(mc.negate(x), mc.negate(y), mc);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#transportMatrix()
	 */
	@Override
	public Vector<T> transportMatrix() {
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(x.getClass(), 2);
		arr[0] = x;
		arr[1] = y;
		return Vector.createVector(mc, true, arr);
	}

	@Override
	public PVector<T> multiplyNumber(long n) {
		return new PVector<>(mc.multiplyLong(x,n),mc.multiplyLong(y,n),mc);
	}


	@Override
	public PVector<T> multiplyNumber(T n) {
		return new PVector<>(mc.multiply(x,n),mc.multiply(y,n),mc);
	}

	
	@Override
	public Matrix<T> cofactor(int r, int c) {
		throw new ArithmeticException("Too small for cofactor");
	}
	@Override
	public int getRowCount() {
		return 2;
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}
	
	/**
	 * Returns the value of x in the vector.
	 * @return x 
	 */
	public T getX(){
		return x;
	}
	/**
	 * Returns the value of y in the vector.
	 * @return y
	 */
	public T getY(){
		return y;
	}
	
	/**
	 * Returns {@code this + s}.
	 * @param s another SVector
	 * @return this + s
	 */
	public PVector<T> add(PVector<T> s){
		return new PVector<>(mc.add(x, s.x),mc.add(y, s.y),mc);
	}
	/**
	 * Returns {@code this - s}.
	 * @param s another SVector
	 * @return this - s
	 */
	public PVector<T> subtract(PVector<T> s){
		return new PVector<>(mc.subtract(x, s.x),mc.subtract(y, s.y),mc);
	}
	
	/**
	 * Returns the inner(scalar) product of {@code this} and {@code s}, which 
	 * is equal to <pre>
	 * this · s
	 * </pre>
	 * @param s
	 * @return this · s
	 */
	public T innerProduct(PVector<T> s){
		return mc.add(mc.multiply(x, s.x), mc.multiply(y, s.y));
	}
	
	/**
	 * Returns the angle of {@code this} and {@code s}.
	 * <pre> arccos(this · s / (|this| |s|))</pre>
	 * @param s
	 * @return <pre> arccos(this · s / (|this| |s|))</pre>
	 */
	public <R> R angle(PVector<T> s,MathFunction<T, R> arccos){
		T pro = innerProduct(s);
		pro = mc.divide(pro, mc.multiply(calLength(), s.calLength()));
		return arccos.apply(pro);
	}
	/**
	 * Returns the cos value of the angle of {@code this} and {@code s}.
	 * <pre>this · s / (|this| |s|)</pre>
	 * @param s
	 * @return <pre>this · s / (|this| |s|)</pre>
	 */
	public T angleCos(PVector<T> s){
		T pro = innerProduct(s);
		return mc.divide(pro, mc.multiply(calLength(), s.calLength()));
	}
	
	/**
	 * Determines whether the two vectors are parallel.
	 * @param s
	 * @return
	 */
	public boolean isParallel(PVector<T> s){
		return mc.isEqual(mc.multiply(x, s.y),mc.multiply(y, s.x));
	}
	/**
	 * Determines whether the two vectors are perpendicular.
	 * @param s
	 * @return
	 */
	public boolean isPerpendicular(PVector<T> s){
		return mc.isZero(innerProduct(s));
	}
	
	/**
	 * Returns a unit vector which is parallel to this vector.
	 * @return an unit vector
	 */
	@Override
	public PVector<T> unitVector(){
		T length = calLength();
		PVector<T> s = new PVector<>(mc.divide(x, length), 
				mc.divide(y, length), 
				 mc);
		s.length = mc.getOne();
		s.lengthSq = mc.getOne();
		return s;
	}
	/**
	 * Returns a SVector that has the same direct of this but length is given.
	 * @param length the length
	 * @return a new SVector
	 */
	public PVector<T> parallel(T len){
		T length = calLength();
		PVector<T> s = new PVector<>(mc.multiply(len, mc.divide(x, length)), 
				mc.multiply(len, mc.divide(y, length)), 
						 mc);
		s.length = len;
		return s;
	}
	
	/**
	 * Determines whether this vector is a zero vector.
	 * @return
	 */
	public boolean isZeroVector(){
		return mc.isZero(x) && mc.isZero(y);
	}
	/**
	 * 
	 * @param angle
	 * @return
	 */
	public PVector<T> rotate(T angle){
		//TODO
		return null;
	}
	
	@Override
	public <N> PVector<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		PVector<N> sn =  new PVector<>(mapper.apply(x),mapper.apply(y),newCalculator);
		if(length!=null){
			sn.length = mapper.apply(length);
		}
		if(lengthSq!=null){
			sn.lengthSq = mapper.apply(lengthSq);
		}
		return sn;
	}
	
	
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		sb.append('(').append(nf.format(x,mc))
			.append(',').append(nf.format(y,mc))
			.append(')');
		return sb.toString();
	}
	
	private int hashCode = 0;
	@Override
	public int hashCode() {
		if(hashCode == 0){
			int hash = 1;
			hash = hash*31 + x.hashCode() ;
			hash = hash*31 + y.hashCode();
			hashCode = hash;
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj){
			return true;
		}
		if(obj instanceof PVector){
			PVector<?> sv = (PVector<?>) obj;
			return x.equals(sv.x) && y.equals(sv.y) ;
		}
		return super.equals(obj);
	}
	
	@Override
	public <N> boolean valueEquals(FlexibleMathObject<N> obj, Function<N, T> mapper) {
		
		if(obj instanceof PVector){
			PVector<N> s = (PVector<N>) obj;
			return mc.isEqual(x, mapper.apply(s.x)) &&
					mc.isEqual(y, mapper.apply(s.y)); 
		}
		return false;
	}
	
	@Override
	public boolean valueEquals(FlexibleMathObject<T> obj) {
		if(this==obj){
			return true;
		}
		if(obj instanceof PVector){
			PVector<T> s = (PVector<T>) obj;
			return mc.isEqual(x, s.x) &&
					mc.isEqual(y, s.y) ;
		}
		return false;
	}
	/**
	 * Returns the reduce of the vector, try to reduce {@code this} 
	 * into <pre>ax + by + cz</pre>
	 * This three vector must not be parallel.
	 * @param x
	 * @param y
	 * @param z
	 * @return a SVector of (a,b,c)
	 * @see
	 */
	public PVector<T> reduce(PVector<T> x,PVector<T> y){
		if(x.isParallel(y)){
			throw new IllegalArgumentException("Parallel");
		}
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[2][3];
		mat[0][0] = x.x;
		mat[0][1] = y.x;
		mat[0][2] = this.x;
		
		mat[1][0] = x.y;
		mat[1][1] = y.y;
		mat[1][2] = this.y;
		
		LinearEquationSolution<T> sol = MatrixSup.solveLinearEquation(mat, mc);
		if(sol.getSolutionSituation()!= Situation.SINGLE_SOLUTION){
			throw new ArithmeticException("Not single?");
		}
		return fromVector(sol.getBase());
	}
	
	
	/**
	 * Create a vector with the given x y  arguments. 
	 * @param x
	 * @param y
	 * @param mc a {@link MathCalculator}
	 * @return a new SVector
	 */
	public static <T> PVector<T> valueOf(T x,T y,MathCalculator<T> mc){
		if(x == null || y == null){
			throw new NullPointerException("");
		}
		return new PVector<>(x,y, mc);
	}
	/**
	 * Returns a vector of 
	 * <pre>
	 * __
	 * AB
	 * </pre>
	 * @param A point A
	 * @param B point B
	 * @param mc a {@link MathCalculator}
	 * @return a new vector
	 */
	public static <T> PVector<T> vector(Point<T> A,Point<T> B,MathCalculator<T> mc){
		return new PVector<>(mc.subtract(B.x, A.x),mc.subtract(B.y, A.y),mc);
	}
	
	/**
	 * Returns a vector of 
	 * <pre>
	 * __
	 * AB
	 * </pre>
	 * <p>The {@link MathCalculator} will be taken from the first parameter of {@link FlexibleMathObject}
	 * @param A point A
	 * @param B point B
	 * @return a new vector
	 */
	public static <T> PVector<T> vector(Point<T> A,Point<T> B){
		return vector(A,B,A.getMathCalculator());
	}
	
	/**
	 * Create a vector with the array,if the 
	 * array's length is not equal to 2, only the first three element will be considered.
	 * @param xy
	 * @param mc a {@link MathCalculator}
	 * @return a new SVector
	 */
	public static <T> PVector<T> vector(T[] xy,MathCalculator<T> mc){
		if(xy.length < 2){
			throw new IllegalArgumentException("Not enough length");
		}
			//check not null
			if(xy[0] != null && xy[1] != null){
				return new PVector<>(xy[0],xy[1], mc);
			}
			throw new NullPointerException("null in xy");
		
	}
	/**
	 * Returns the sum of the vectors, this method is generally faster than add the 
	 * vectors one by one because it reduce the cost to create new objects.
	 * @param vectors
	 * @return
	 */
	@SafeVarargs
	public static <T> PVector<T> sum(PVector<T>...vectors){
		MathCalculator<T> mc = vectors[0].mc;
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(vectors[0].x.getClass(), vectors.length);
		for(int i=0;i<vectors.length;i++){
			arr[i] = vectors[i].x;
		}
		T xm = mc.addX(arr);
		for(int i=0;i<vectors.length;i++){
			arr[i] = vectors[i].y;
		}
		T ym = mc.addX(arr);
		return new PVector<T>(xm, ym, mc);
	}
	
	/**
	 * Create the SVector through another vector, the method only considers 
	 * the first two dimensions of the given vector.
	 * <p>Notice: The MathCalculator of the new vector will be the same as the vector's.
	 * @param v a vector whose size is bigger than or equal to 2.
	 * @return a new SVector
	 */
	public static <T> PVector<T> fromVector(Vector<T> v){
		if(v.getSize()< 2){
			throw new IllegalArgumentException("Too small");
		}
		return new PVector<T>(v.getNumber(0), v.getNumber(1),v.getMathCalculator());
	}
	/**
	 * Returns a vector according to the list first three elements.
	 * @param list a list
	 * @param mc a {@link MathCalculator}
	 * @return a new vector
	 */
	public static <T> PVector<T> fromList(List<T> list,MathCalculator<T> mc){
		return new PVector<T>(list.get(0), list.get(1), mc);
	}
	/**
	 * Gets a zero vector:{@literal (0,0)}
	 * @param mc
	 * @return a new vector
	 */
	public static <T> PVector<T> zeroVector(MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		PVector<T> v = (PVector<T>) zvs.get(mc);
		if(v == null) {
			T z = mc.getZero();
			v =  new PVector<T>(z, z, mc);
			zvs.put(mc, v);
		}
		return v;
		
	}
	
	private static final Map<MathCalculator<?>,PVector<?>> zvs = new ConcurrentHashMap<>();
	
}
