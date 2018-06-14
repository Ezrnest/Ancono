/**
 * 
 */
package cn.timelives.java.math.geometry.analytic.planeAG;

import cn.timelives.java.math.property.Composable;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.algebra.linearAlgebra.Matrix;
import cn.timelives.java.math.algebra.linearAlgebra.MatrixSup;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.utilities.ArraySup;

import java.util.function.Function;

/**
 * The utility class for transform matrixes.
 * @author liyicheng
 *
 */
public final class TransMatrix<T> extends Matrix<T> implements Composable<TransMatrix<T>> {
	
	
	private final T[][] data;
	
	/**
	 * This method won't check mat's size and all the changed to the mat
	 * will be reflected to this Matrix.
	 * 
	 * @param mat
	 */
	TransMatrix(T[][] mat,MathCalculator<T> mc) {
		super(2,2,mc);
		data = mat;
	}
	/**
	 * This method won't check mat's size and all the changed to the mat
	 * will be reflected to this Matrix.
	 * 
	 * @param mat
	 */
	@SuppressWarnings("unchecked")
	TransMatrix(Matrix<T> data,MathCalculator<T> mc) {
		super(2,2,mc);
		this.data = (T[][]) data.getValues();
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#getNumber(int, int)
	 */
	@Override
	public T getNumber(int i, int j) {
		return data[i][j];
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#getValues()
	 */
	@Override
	public T[][] getValues() {
		return ArraySup.deepCopy(data);
	}
	@SuppressWarnings("unchecked")
	private static <T> T[][] gd(){
		return (T[][]) new Object[2][2];
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#negative()
	 */
	@Override
	public TransMatrix<T> negative() {
		T[][] d2 = gd();
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				d2[i][j] = mc.negate(data[i][j]);
			}
		}
		return new TransMatrix<>(d2, mc);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#transportMatrix()
	 */
	@Override
	public TransMatrix<T> transportMatrix() {
		T[][] d2 = gd();
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				d2[i][j] = data[j][i];
			}
		}
		return new TransMatrix<>(d2, mc);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#multiplyNumber(long)
	 */
	@Override
	public TransMatrix<T> multiplyNumber(long n) {
		T[][] d2 = gd();
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				d2[i][j] = mc.multiplyLong(data[i][j], n);
			}
		}
		return new TransMatrix<>(d2, mc);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#multiplyNumber(java.lang.Object)
	 */
	@Override
	public TransMatrix<T> multiplyNumber(T n) {
		T[][] d2 = gd();
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				d2[i][j] = mc.multiply(data[i][j], n);
			}
		}
		return new TransMatrix<>(d2, mc);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Matrix#cofactor(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Matrix<T> cofactor(int r, int c) {
		rowRangeCheck(r);
		columnRangeCheck(c);
		return Matrix.valueOf((T[][])new Object[][]{{data[1-r][1-c]}}, mc);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#applyFunction(cn.timelives.java.math.MathFunction)
	 */
	@Override
	public TransMatrix<T> applyFunction(MathFunction<T, T> f) {
		T[][] d2 = gd();
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				d2[i][j] = f.apply(data[i][j]);
			}
		}
		return new TransMatrix<>(d2, mc);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#calDet()
	 */
	@Override
	public T calDet() {
		return MatrixSup.det2(data, mc);
	}
	
	private int rank = -1;
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#calRank()
	 */
	@Override
	public int calRank() {
		if(rank==-1)
			rank = Matrix.valueOf(data,mc).calRank();
		return rank;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return 2;
	}
	
	//store the inverse of this 
	private TransMatrix<T> inversed;
	/** 
	 * 
	 * @throws ArithmeticException if this method failed
	 */
	@Override
	public TransMatrix<T> inverse() {
		if(inversed == null){
			T deno = calDet();
			if(mc.isZero(deno)){
				throw new ArithmeticException("det == 0");
			}
			inversed =  valueOf(mc.divide(data[1][1], deno), mc.divide(mc.negate(data[0][1]), deno), 
					mc.divide(mc.negate(data[1][0]), deno), mc.divide(data[0][0], deno), mc);
			inversed.inversed = this;
		}
		return inversed;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public <N> TransMatrix<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		N[][] d2 = gd();
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				d2[i][j] = mapper.apply(data[i][j]);
			}
		}
		return new TransMatrix<>(d2, newCalculator);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.linearAlgebra.Matrix#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		sb.append("[[");
		sb.append(nf.format(data[0][0], mc)).append(",");
		sb.append(nf.format(data[0][1], mc)).append("],[");
		sb.append(nf.format(data[1][0], mc)).append(",");
		sb.append(nf.format(data[1][1], mc)).append("]]");
		return sb.toString();
	}
	
	/**
	 * Transforms the given vector��<br />
	 * {@code this��v}
	 * @param v a vector
	 * @return transformed vector
	 */
	public PVector<T> transform(PVector<T> v){
		T _x = mc.add(mc.multiply(data[0][0],v.x ), mc.multiply(data[0][1], v.y));
		T _y = mc.add(mc.multiply(data[1][0],v.x ), mc.multiply(data[1][1], v.y));
		return new PVector<T>(_x, _y, mc);
	}
	/**
	 * Transforms the given point��<br />
	 * @param p a point
	 * @return transformed vector
	 */
	public Point<T> transform(Point<T> p){
		T _x = mc.add(mc.multiply(data[0][0],p.x ), mc.multiply(data[0][1], p.y));
		T _y = mc.add(mc.multiply(data[1][0],p.x ), mc.multiply(data[1][1], p.y));
		return new Point<T>( mc,_x, _y);
	}
	/**
	 * Returns this��tm
	 * @param tm
	 * @return
	 */
	TransMatrix<T> multiply0(TransMatrix<T> tm){
		T[][] mat = gd();
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				mat[i][j] = mc.add(mc.multiply(data[i][0], tm.data[0][j]),
						mc.multiply(data[i][1], tm.data[1][j]));
;			}
		}
		return new TransMatrix<>(mat, mc);
	}
	
	/**
	 * Returns a TransformMatrix that is equivalent to a composed transformation 
	 * that first apply this and then {@code after}.
	 * @param after another matrix
	 * @return
	 */
	public TransMatrix<T> andThen(TransMatrix<T> after){
		return after.multiply0(this);
	}
	
	/*
	 * @see cn.timelives.java.math.property.Composable#compose(cn.timelives.java.math.property.Composable)
	 */
	@Override
	public TransMatrix<T> compose(TransMatrix<T> before) {
		return this.multiply0(before);
	}
	
	/**
	 * Creates a new TransfromMatrix, which is 
	 * <pre> (x.x  x.y)
	 * (y.x  y.y)
	 * </pre>
	 * @param x vector
	 * @param y
	 * @return
	 */
	public static <T> TransMatrix<T> fromVector(PVector<T> x,PVector<T> y){
		@SuppressWarnings("unchecked")
		T[][] data = (T[][]) new Object[][]{
			{x.x,x.y},{y.x,y.y}
		};
		return new TransMatrix<>(data, x.getMathCalculator());
	}
	/**
	 * Creates a new TransfromMatrix, which is 
	 * <pre> (a b)
	 * (c d)
	 * </pre>
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param mc
	 * @return
	 */
	public static <T> TransMatrix<T> valueOf(T a,T b,T c,T d,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		T[][] data = (T[][]) new Object[][]{
			{a,b},{c,d}
		};
		return new TransMatrix<>(data,mc);
	}
	/**
	 * Creates a TransformMatrix:
	 * <pre>
	 * (0 1)
	 * (1 0)
	 * </pre>
	 * @param mc
	 * @return
	 */
	public static <T> TransMatrix<T> flipXY(MathCalculator<T> mc){
		T z = mc.getZero();
		T o = mc.getOne();
		return valueOf(z,o, o, z, mc);
	}
	/**
	 * Creates a TransformMatrix:
	 * <pre>
	 * (-1 0)
	 * ( 0 1)
	 * </pre>
	 * @param mc
	 * @return
	 */
	public static <T> TransMatrix<T> flipX(MathCalculator<T> mc){
		T z = mc.getZero();
		T o = mc.getOne();
		return valueOf(mc.negate(o),z,z,o,mc);
	}
	/**
	 * Creates a TransformMatrix:
	 * <pre>
	 * (1  0)
	 * (0 -1)
	 * </pre>
	 * @param mc
	 * @return
	 */
	public static <T> TransMatrix<T> flipY(MathCalculator<T> mc){
		T z = mc.getZero();
		T o = mc.getOne();
		return valueOf(o,z,z,mc.negate(o),mc);
	}
	/**
	 * Creates a TransformMatrix:
	 * <pre>
	 * (-1 0)
	 * (0 -1)
	 * </pre>
	 * @param mc
	 * @return
	 */
	public static <T> TransMatrix<T> centralSymmetry(MathCalculator<T> mc){
		T z = mc.getZero();
		T o = mc.negate(mc.getOne());
		return valueOf(o,z,z,o,mc);
	}
	
	/**
	 * Creates a TransformMatrix that performs a rotate operation.The returned matrix(mat)
	 * will fit the following result:
	 * <pre>mat * (x,y)<sup>T</sup> = (x',y')<sup>T</sup></pre>
	 * Where (x,y) is the coordinate before rotation, and (x',y') is the coordinate after rotation.
	 * @param angle the angle to rotate (anti-clockwise)
	 * @param mc a {@link MathCalculator}
	 * @return
	 */
	public static <T> TransMatrix<T> rotate(T angle, MathCalculator<T> mc){
		//(cos x -sinx)
		//(sin x cos x)
		T cos = mc.cos(angle);
		T sin = mc.sin(angle);
		return valueOf(cos,mc.negate(sin),sin,cos,mc);
	}

	/**
	 * Returns a TransMatrix that rotates the vector parallel to the positive direction of x axis(such as (1,0))
	 * to the given vector. The vector must be non-zero.
	 * @param v
	 * @param <T>
	 * @return
	 */
	public static <T> TransMatrix<T> rotateXAxisTo(PVector<T> v){
		if(v.isZeroVector()){
			throw new IllegalArgumentException("zero vector.");
		}
		v = v.unitVector();
		T cos = v.x, sin = v.y;
		MathCalculator<T> mc = v.getMathCalculator();
		return valueOf(cos,mc.negate(sin),sin,cos,mc);
	}

	/**
	 * Returns a TransformMatrix that performs multiplication.
	 * <pre>
	 * (kx 0)
	 * (0 ky)
	 * </pre>
	 * @param kx
	 * @param ky
	 * @return
	 */
	public static <T> TransMatrix<T> multiply(T kx,T ky, MathCalculator<T> mc){
		T z = mc.getZero();
		return valueOf(kx,z,z,ky,mc);
	}
	/**
	 * Returns a TransformMatrix that performs multiplication.
	 * <pre>
	 * (kx 0)
	 * (0  1)
	 * </pre>
	 * @param kx
	 * @return
	 */
	public static <T> TransMatrix<T> multiplyX(T kx, MathCalculator<T> mc){
		T z = mc.getZero();
		return valueOf(kx,z,z,mc.getOne(),mc);
	}
	/**
	 * Returns a TransformMatrix that performs multiplication.
	 * <pre>
	 * (1 0 )
	 * (0 ky)
	 * </pre>
	 * @param ky
	 * @return
	 */
	public static <T> TransMatrix<T> multiplyY(T ky, MathCalculator<T> mc){
		T z = mc.getZero();
		return valueOf(mc.getOne(),z,z,ky,mc);
	}
	
	/**
	 * Returns the identity transformation :
	 * <pre>
	 * (1 0)
	 * (0 1)
	 * </pre>
	 * @param mc a {@link MathCalculator}
	 * @return a new TransMatrix
	 */
	public static <T> TransMatrix<T> identityTrans(MathCalculator<T> mc){
		T z = mc.getZero();
		T o = mc.getOne();
		return valueOf(o, z, z, o, mc);
	}
	
	
//	//test
//	public static void main(String[] args) {
//		MathCalculator<Double> mc = Calculators.getCalculatorDoubleDev();
//		TransMatrix<Double> t1 = rotate(Math.PI/4,mc),
//				t2 = t1.andThen(t1);
//		t1.printMatrix();
//		Point<Double> p = Point.valueOf(1d, 0d, mc);
//		print(t2.transform(p));
//	}
}
