package cn.timelives.java.math.geometry.analytic.spaceAG;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.algebra.linearAlgebra.LinearEquationSolution;
import cn.timelives.java.math.algebra.linearAlgebra.LinearEquationSolution.Situation;
import cn.timelives.java.math.algebra.linearAlgebra.Matrix;
import cn.timelives.java.math.algebra.linearAlgebra.MatrixSup;
import cn.timelives.java.math.algebra.linearAlgebra.Vector;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.utilities.ArraySup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
/**
 * The SVector is a column vector with length of 3,
 * and it is the basic part for the relationship management of plane, line and other 
 * space objects.
 * <p>
 * The vector can be commonly shown as (x,y,z), where the first element (0 for index) is 
 * the length in x coordinate, and the second element for y, the last for z.<p>
 * The vector provides operations like add, minus, opposite, and products. <p> 
 * Generally speaking, the vector is often used when creating points, lines, planes and there 
 * are lots of method providing a SVector as the result. 
 * 
 * @author liyicheng
 *
 * @param <T>
 */
public final class SVector<T> extends Vector<T> {
	
	final T x,y,z;
	
	private T length;
	
	private T lenSq ;
	
	SVector(T[] vec, MathCalculator<T> mc) {
		super(3,false,mc);
		this.x = vec[0];
		this.y = vec[1];
		this.z = vec[2];
	}
	SVector(T x,T y,T z,MathCalculator<T> mc){
		super(3,false,mc);
		this.x =x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public T[][] getValues() {
		@SuppressWarnings("unchecked")
		T[][] res = (T[][])Array.newInstance(x.getClass(), 1,3);
		res[0][0] = x;
		res[1][0] = y;
		res[2][0] = z;
		return res;
	}


	@Override
	public SVector<T> negative() {
		return new SVector<>(mc.negate(x),mc.negate(y),mc.negate(z),mc);
	}


	@Override
	public Vector<T> transportMatrix() {
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(x.getClass(), 3);
		arr[0] = x;
		arr[1] = y;
		arr[2] = z;
		return Vector.createVector(mc, true, arr);
	}


	@Override
	public SVector<T> multiplyNumber(long n) {
		return new SVector<>(mc.multiplyLong(x,n),mc.multiplyLong(y,n),mc.multiplyLong(z,n),mc);
	}


	@Override
	public SVector<T> multiplyNumber(T n) {
		return new SVector<>(mc.multiply(x,n),mc.multiply(y,n),mc.multiply(z,n),mc);
	}

	
	@Override
	public Matrix<T> cofactor(int r, int c) {
		throw new ArithmeticException("Too small for cofactor");
	}
	@Override
	public int getRowCount() {
		return 3;
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
	 * Returns the value of z in the vector.
	 * @return z 
	 */
	public T getZ(){
		return z;
	}
	/**
	 * Returns an array of the element.
	 * @return a new array of type T
	 */
	@Override
	public T[] toArray(){
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(x.getClass(), 3);
		arr[0] = x;
		arr[1] = y;
		arr[2] = z;
		return arr;
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.AbstractVector#toArray(java.lang.Object[])
	 */
	@Override
	public T[] toArray(T[] arr) {
		if(arr.length<3){
			arr = Arrays.copyOf(arr, 3);
		}
		arr[0] = x;
		arr[1] = y;
		arr[2] = z;
		return arr;
	}
	
	/**
	 * Returns {@code this + s}.
	 * @param s another SVector
	 * @return this + s
	 */
	public SVector<T> add(SVector<T> s){
		return new SVector<>(mc.add(x, s.x),mc.add(y, s.y),mc.add(z, s.z),mc);
	}
	/**
	 * Returns {@code this - s}.
	 * @param s another SVector
	 * @return this - s
	 */
	public SVector<T> subtract(SVector<T> s){
		return new SVector<>(mc.subtract(x, s.x),mc.subtract(y, s.y),mc.subtract(z, s.z),mc);
	}
	/**
	 * Returns the inner(scalar) product of {@code this} and {@code s}, which 
	 * is equal to <pre>
	 * this · s
	 * </pre>
	 * @param s
	 * @return this · s
	 */
	public T innerProduct(SVector<T> s){
		return mc.add(mc.add(mc.multiply(x, s.x), mc.multiply(y, s.y)), mc.multiply(z, s.z));
	}
	
	/**
	 * Returns the outer product of {@code this} and {@code s}, which 
	 * is the result of<pre>
	 * this × s
	 * </pre>
	 * @param s
	 * @return this × s
	 */
	public SVector<T> outerProduct(SVector<T> s){
		T nx = mc.subtract(mc.multiply(y, s.z), mc.multiply(s.y, z));
		T ny = mc.subtract(mc.multiply(z, s.x), mc.multiply(s.z, x));
		T nz = mc.subtract(mc.multiply(x, s.y), mc.multiply(s.x, y));
		return new SVector<T>(nx, ny, nz, mc);
	}
	
	/**
	 * Returns the length of {@code this}.
	 * <pre>|this|</pre>
	 * @return |this|
	 */
	@Override
	public T calLength(){
		if(length == null){
			length = mc.squareRoot(calLengthSq());
		}
		return length;
	}
	
	@Override
	public T calLengthSq(){
		if(lenSq == null){
			lenSq = innerProduct(this);
		}
		return lenSq;
	}
	
	/**
	 * This method will ignore j.
	 */
	@Override
	public T getNumber(int i, int j) {
		return getNumber(i);
	}
	@Override
	public int getSize() {
		return 3;
	}
	@Override
	public T getNumber(int i) {
		switch(i){
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			throw new IndexOutOfBoundsException("for index:" + i);
		}
	}
	
	/**
	 * Returns the angle of {@code this} and {@code s}.
	 * <pre> arccos(this �� s / (|this| |s|))</pre>
	 * @param s
	 * @return <pre> arccos(this �� s / (|this| |s|))</pre>
	 */
	public <R> R angle(SVector<T> s,MathFunction<T, R> arccos){
		T pro = innerProduct(s);
		pro = mc.divide(pro, mc.multiply(calLength(), s.calLength()));
		return arccos.apply(pro);
	}
	/**
	 * Returns the cos value of the angle of {@code this} and {@code s}.
	 * <pre>this �� s / (|this| |s|)</pre>
	 * @param s
	 * @return <pre>this �� s / (|this| |s|)</pre>
	 */
	public T angleCos(SVector<T> s){
		T pro = innerProduct(s);
		return mc.divide(pro, mc.multiply(calLength(), s.calLength()));
	}
	
	/**
	 * Determines whether the two vectors are parallel.
	 * @param s
	 * @return
	 */
	public boolean isParallel(SVector<T> s){
		if(!mc.isZero(x)){
			if(mc.isZero(s.x)){
				return false;
			}
			return mc.isEqual(mc.multiply(x, s.y), mc.multiply(y, s.x)) && 
					mc.isEqual(mc.multiply(x, s.z), mc.multiply(z, s.x));
		}
		//x == 0
		if(!mc.isZero(s.x)){
			return false;
		}
		return mc.isEqual(mc.multiply(y, s.z), mc.multiply(z, s.y));
	}
	/**
	 * Determines whether the given vector is in the same direction of this vector, which means 
	 * the vector the result of {@code this.angleCos(s)} will be 1. If {@code s} is a zero vector, 
	 * an exception will be thrown.
	 * @param s
	 * @return
	 */
	public boolean isOfSameDirection(SVector<T> s){
		if(s.isZeroVector()){
			throw new ArithmeticException("s==0");
		}
		if(!isParallel(s)){
			return false;
		}
		T t1 = x,t2 = s.x;
		if(mc.isZero(t1)){
			if(mc.isZero(y)){
				t1 = z;
				t2 = s.z;
			}else{
				t1 = y;
				t2 = s.y;
			}
		}
		return Calculators.isSameSign(t1, t2, mc);
	}
	
	/**
	 * Determines whether the two vectors are perpendicular.
	 * @param s
	 * @return
	 */
	public boolean isPerpendicular(SVector<T> s){
		return mc.isZero(innerProduct(s));
	}
	
	/**
	 * Returns a unit vector which is parallel to this vector.
	 * @return an unit vector
	 */
	@Override
	public SVector<T> unitVector(){
		T length = calLength();
		SVector<T> s = new SVector<>(mc.divide(x, length), 
				mc.divide(y, length), 
				mc.divide(z, length), mc);
		s.length = mc.getOne();
		s.lenSq = mc.getOne();
		return s;
	}
	/**
	 * Returns a SVector that has the same direct of this but length is given.
	 * @param length the length
	 * @return a new SVector
	 */
	public SVector<T> parallel(T len){
		T length = calLength();
		SVector<T> s = new SVector<>(mc.multiply(len, mc.divide(x, length)), 
				mc.multiply(len, mc.divide(y, length)), 
						mc.multiply(len, mc.divide(z, length)), mc);
		s.length = len;
		return s;
	}
	
	/**
	 * Determines whether this vector is a zero vector.
	 * @return
	 */
	public boolean isZeroVector(){
		return mc.isZero(x) && mc.isZero(y) && mc.isZero(z);
	}
	
	
	/**
	 * Returns the 'projection' of v.<p>
	 * The returned vector {@code r} will on this plane of this and v 
	 * and be perpendicular to {@code this}.<pre>
	 *          ^\
	 *          | \ <-v
	 *  result->|  \
	 *          <---- 
	 *            ^
	 *           this 
	 * </pre>
	 * @param v a vector
	 * @return
	 */
	public SVector<T> perpendicular(SVector<T> v){
		T k = mc.negate(mc.divide(innerProduct(v), calLengthSq()));
		T nx = mc.add(v.x, mc.multiply(k, x));
		T ny = mc.add(v.y, mc.multiply(k, y));
		T nz = mc.add(v.z, mc.multiply(k, z));
		return new SVector<T>(nx, ny, nz, mc);
	}
	
	@Override
	public <N> SVector<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		SVector<N> sn =  new SVector<>(mapper.apply(x),mapper.apply(y),mapper.apply(z),newCalculator);
		if(length!=null){
			sn.length = mapper.apply(length);
		}
		if(lenSq!=null){
			sn.lenSq = mapper.apply(lenSq);
		}
		return sn;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(').append(x)
			.append(',').append(y)
			.append(',').append(z)
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
			hash = hash*31 + z.hashCode();
			hashCode = hash;
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SVector){
			SVector<?> sv = (SVector<?>) obj;
			return x.equals(sv.x) && y.equals(sv.y) && z.equals(sv.z);
		}
		return super.equals(obj);
	}
	
	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof SVector){
			SVector<N> s = (SVector<N>) obj;
			return mc.isEqual(x, mapper.apply(s.x)) &&
					mc.isEqual(y, mapper.apply(s.y)) &&
					mc.isEqual(z, mapper.apply(s.z)) ;
		}
		return false;
	}
	
	@Override
	public boolean valueEquals(MathObject<T> obj) {
		if(obj instanceof SVector){
			SVector<T> s = (SVector<T>) obj;
			return mc.isEqual(x, s.x) &&
					mc.isEqual(y, s.y) &&
					mc.isEqual(z, s.z) ;
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
	public SVector<T> reduce(SVector<T> x,SVector<T> y,SVector<T> z){
		if(x.isParallel(y) || y.isParallel(z) || x.isParallel(z)){
			throw new IllegalArgumentException("Parallel");
		}
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[3][4];
		mat[0][0] = x.x;
		mat[0][1] = y.x;
		mat[0][2] = z.x;
		mat[0][3] = this.x;
		
		mat[1][0] = x.y;
		mat[1][1] = y.y;
		mat[1][2] = z.y;
		mat[1][3] = this.y;
		
		mat[2][0] = x.z;
		mat[2][1] = y.z;
		mat[2][2] = z.z;
		mat[2][3] = this.z;
		
		LinearEquationSolution<T> sol = MatrixSup.solveLinearEquation(mat, mc);
		if(sol.getSolutionSituation()!= Situation.SINGLE_SOLUTION){
			throw new ArithmeticException("Not single?");
		}
		return fromVector(sol.getBase());
	}
//	/**
//	 * Reduce this vector, but in two vectors, which means the three vectors must be on the 
//	 * same plane.
//	 * @param x
//	 * @param y
//	 * @return
//	 */
//	public SVector<T> reduce(SVector<T> x,SVector<T> y){
//		
//	}
	
	@Override
	public SVector<T> applyFunction(MathFunction<T, T> f) {
		return new SVector<>(f.apply(x), f.apply(y), f.apply(z), mc);
	}
	
	/**
	 * Create a vector with the given x y z arguments. 
	 * @param x
	 * @param y
	 * @param z
	 * @param mc a {@link MathCalculator}
	 * @return a new SVector
	 */
	public static <T> SVector<T> valueOf(T x,T y,T z,MathCalculator<T> mc){
		if(x == null || y == null || z == null){
			throw new NullPointerException("");
		}
		return new SVector<>(x,y,z, mc);
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
	public static <T> SVector<T> vector(SPoint<T> A,SPoint<T> B,MathCalculator<T> mc){
		return new SVector<>(mc.subtract(B.x, A.x),mc.subtract(B.y, A.y),mc.subtract(B.z, A.z),mc);
	}
	
	/**
	 * Returns a vector of 
	 * <pre>
	 * __
	 * AB
	 * </pre>
	 * <p>The {@link MathCalculator} will be taken from the first parameter of {@link MathObject}
	 * @param A point A
	 * @param B point B
	 * @return a new vector
	 */
	public static <T> SVector<T> vector(SPoint<T> A,SPoint<T> B){
		return vector(A,B,A.getMathCalculator());
	}
	
	/**
	 * Create a vector with the array,if the 
	 * array's length is not equal to 3, only the first three element will be considered.
	 * @param xyz
	 * @param mc a {@link MathCalculator}
	 * @return a new SVector
	 */
	public static <T> SVector<T> vector(T[] xyz,MathCalculator<T> mc){
		if(xyz.length < 3){
			throw new IllegalArgumentException("Not enough length");
		}
			//check not null
			if(xyz[0] != null && xyz[1] != null && xyz[2] !=null){
				return new SVector<>(xyz[0],xyz[1],xyz[2], mc);
			}
			throw new NullPointerException("null in xyz");
		
	}
	/**
	 * Returns the sum of the vectors, this method is generally faster than add the 
	 * vectors one by one because it reduce the cost to create new objects.
	 * @param vectors
	 * @return
	 */
	@SafeVarargs
	public static <T> SVector<T> sum(SVector<T>...vectors){
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
		for(int i=0;i<vectors.length;i++){
			arr[i] = vectors[i].z;
		}
		T zm = mc.addX(arr);
		return new SVector<T>(xm, ym, zm, mc);
	}
	
	/**
	 * Create the SVector through another vector, the method only considers 
	 * the first three dimensions of the given vector.
	 * <p>Notice: The MathCalculator of the new vector will be the same as the vector's.
	 * @param v a vector whose size is bigger than or equal to 3.
	 * @return a new SVector
	 */
	public static <T> SVector<T> fromVector(Vector<T> v){
		if(v.getSize()< 3){
			throw new IllegalArgumentException("Too small");
		}
		return new SVector<T>(v.getNumber(0), v.getNumber(1), v.getNumber(2), v.getMathCalculator());
	}
	/**
	 * Returns a vector according to the list first three elements.
	 * @param list a list
	 * @param mc a {@link MathCalculator}
	 * @return a new vector
	 */
	public static <T> SVector<T> fromList(List<T> list,MathCalculator<T> mc){
		return new SVector<T>(list.get(0), list.get(1), list.get(2), mc);
	}
	
	/**
	 * Returns the mixed product of a,b,c which is equal to <pre>
	 * (a �� b) �� c
	 * </pre>
	 * @param a
	 * @param b
	 * @param c
	 * @return result
	 */
	public static <T> T mixedProduct(SVector<T> a,SVector<T> b,SVector<T> c){
		return MatrixSup.det3(toMatrix(a,b,c), a.mc);
	}
	/**
	 * Returns a vector which is on the same plane of {@code n �� v} and {@code v} and the cosine value of the 
	 * angle of it and {@code v} is equal to {@code cos}. The mix product of {@code (result �� v) �� n}
	 * will be positive.
	 * @param v
	 * @param n
	 * @return
	 */
	public static <T> SVector<T> angledVector(SVector<T> v,SVector<T> n,T tan){
		MathCalculator<T> mc = v.getMathCalculator();
		SVector<T> perp = n.outerProduct(v);
		SVector<T> res = perp.multiplyNumber(v.calLength());
		res = res.add(v.multiplyNumber(mc.divide(perp.calLength(), tan)));
		return res;
	}
	/**
	 * Returns the two possible vectors that are on the same plane of {@code n �� v} and {@code v} 
	 *  and the cosine value of the 
	 * angle of either of them and {@code v} is equal to {@code cos}.
	 * @param v
	 * @param n
	 * @param cos
	 * @return
	 */
	public static <T> List<SVector<T>> angledVectorTwo(SVector<T> v,SVector<T> n,T tan){
		List<SVector<T>> list = new ArrayList<>(2);
		MathCalculator<T> mc = v.getMathCalculator();
		SVector<T> perp = n.outerProduct(v);
		SVector<T> res = perp.multiplyNumber(mc.multiply(tan, v.calLength()));
		SVector<T> t = v.multiplyNumber(perp.calLength());
		list.add(res.add(t));
		list.add(res.negative().add(t));
		return list;
	}
	/**
	 * Returns the vector that is on the plane {@code pl} and has the angle of the 
	 * tangent value.
	 * @param vec
	 * @param pl
	 * @return
	 */
	public static <T> SVector<T> angleSamePlane(SVector<T> vec,Plane<T> pl,T tan){
		return angledVector(vec,pl.getNormalVector(),tan); 
	}
	
	private static <T> T[][] toMatrix(SVector<T> x,SVector<T> y,SVector<T> z){
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[3][3];
		mat[0][0] = x.x;
		mat[0][1] = x.y;
		mat[0][2] = x.z;
		
		mat[1][0] = y.x;
		mat[1][1] = y.y;
		mat[1][2] = y.z;
		
		mat[2][0] = z.x;
		mat[2][1] = z.y;
		mat[2][2] = z.z;
		return mat;
	}
	
	/**
	 * Describe a vector base in space
	 * @author liyicheng
	 *
	 * @param <T>
	 */
	public static final class SVectorBase<T> extends MathObject<T> {
		private final SVector<T> x,y,z;
		public SVectorBase(SVector<T> x,SVector<T> y,SVector<T> z,T[][] mat,T D,
				MathCalculator<T> mc) {
			super(mc);
			this.x = x;
			this.y = y;
			this.z = z;
			this.mat = mat;
			this.D = D;
		}
		private final T D ;
		private T[][] mat;
		public SVector<T> reduce(SVector<T> s){
			@SuppressWarnings("unchecked")
			T[] v = (T[]) new Object[]{s.x,s.y,s.z};
			T[][] mt2 = mat.clone();
			T[] t = mt2[0];
			mt2[0] = v;
			T D1 = MatrixSup.det3(mt2, mc);
			mt2[0] = t;
			t = mt2[1];
			mt2[1] = v;
			T D2 = MatrixSup.det3(mt2, mc);
			mt2[1] = t;
			mt2[2] = v;
			T D3 = MatrixSup.det3(mt2, mc);
			return new SVector<>(mc.divide(D1, D), mc.divide(D2, D), mc.divide(D3, D), mc);
		}
		
		
		@Override
		public <N> SVectorBase<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			N[][] ret = ArraySup.mapTo(mat, (T[] arr) -> 
				ArraySup.mapTo(arr, mapper)
			);
			N d = MatrixSup.det3(ret,newCalculator);
			return new SVectorBase<>(x.mapTo(mapper, newCalculator),
					y.mapTo(mapper, newCalculator),
					z.mapTo(mapper, newCalculator),ret,d,
					newCalculator);
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof SVectorBase){
				SVectorBase<?> svb = (SVectorBase<?>) obj;
				return x.equals(svb.x) && y.equals(svb.y) && z.equals(svb.z);
			}
			return false;
		}
		@Override
		public int hashCode() {
			int hash = x.hashCode();
			hash = hash*37 + y.hashCode();
			return hash*37 + z.hashCode();
		}
		@Override
		public boolean valueEquals(MathObject<T> obj) {
			if(obj instanceof SVectorBase){
				SVectorBase<T> svb = (SVectorBase<T>) obj;
				return x.valueEquals(svb.x) && y.valueEquals(svb.y) && z.valueEquals(svb.z);
			}
			return false;
		}
		@Override
		public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
			if(obj instanceof SVectorBase){
				SVectorBase<N> svb = (SVectorBase<N>) obj;
				return x.valueEquals(svb.x,mapper) && y.valueEquals(svb.y,mapper) && z.valueEquals(svb.z,mapper);
			}
			return false;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
		 */
		@Override
		public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
			return "SVectorBase";
		}
		
		/**
		 * Create a new vector base, the three SVector must not be parallel.
		 * <p>The {@link MathCalculator} will be taken from the first parameter of {@link MathObject}
		 * @param x
		 * @param y
		 * @param z
		 * @return a new vector base
		 */
		public static <T> SVectorBase<T> createBase(SVector<T> x,SVector<T> y,SVector<T> z){
			MathCalculator<T> mc = x.mc;
			T[][] mat = toMatrix(x, y, z);
			T d = MatrixSup.det3(mat, mc);
			if(mc.isZero(d)){
				throw new IllegalArgumentException("They are on the same plane");
			}
			return new SVectorBase<>(x, y, z, mat,d,mc);
		}
	}
	public static class SVectorGenerator<T> extends MathObject<T> {

		/**
		 * @param mc
		 */
		public SVectorGenerator(MathCalculator<T> mc) {
			super(mc);
		}
		/**
		 * Returns a point 
		 * @param x
		 * @param y
		 * @param z
		 * @return
		 */
		public SVector<T> of(T x,T y,T z){
			return SVector.valueOf(x, y, z, mc);
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.utilities.math.MathCalculator)
		 */
		@Override
		public <N> SVectorGenerator<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new SVectorGenerator<>(newCalculator);
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.FlexibleMathObject#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof SVectorGenerator){
				return mc.equals(((SVectorGenerator<?>)obj).mc);
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.FlexibleMathObject#hashCode()
		 */
		@Override
		public int hashCode() {
			return mc.hashCode();
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.FlexibleMathObject#valueEquals(cn.timelives.java.utilities.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(MathObject<T> obj) {
			return equals(obj);
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.FlexibleMathObject#valueEquals(cn.timelives.java.utilities.math.FlexibleMathObject, java.util.function.Function)
		 */
		@Override
		public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
			return equals(obj);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
		 */
		@Override
		public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
			return "SVectorGenerator";
		}
	}
	/**
	 * Adds the several given vectors.<br>
	 * THis method will use the {@link MathCalculator} from the first vector. 
	 * @param vs
	 * @return
	 */
	@SafeVarargs
	public static <T> SVector<T> add(SVector<T>...vs){
		SVector<T> v1 = vs[0];
		MathCalculator<T> mc = v1.getMathCalculator();
		T x,y,z;
		Object[] arr = new Object[vs.length];
		for(int i=0;i<vs.length;i++){
			arr[i] = vs[i].getX();
		}
		x = mc.addX(arr);
		for(int i=0;i<vs.length;i++){
			arr[i] = vs[i].getY();
		}
		y = mc.addX(arr);
		for(int i=0;i<vs.length;i++){
			arr[i] = vs[i].getZ();
		}
		z = mc.addX(arr);
		return new SVector<T>(x, y, z, mc);
	}
	
//	public static void main(String[] args) {
//		MathCalculator<Double> mc = MathCalculatorAdapter.getCalculatorDouble();
//		SVector<Double> v1 = vector(1d,0d,0d,mc),v2 = vector(0d,1d,0d,mc),v3 = vector(0d,0d,1d,mc),
//				v4 = vector(1d,1d,1d,mc);
//		Printer.print(v4.reduce(v1, v2, v3));
//		SVectorBase<Double> b = SVectorBase.createBase(v1, v2, v3);
//		Printer.print(b.reduce(v4));
//	}
	

}