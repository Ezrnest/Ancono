package cn.timelives.java.math.geometry.analytic.spaceAG;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

import java.lang.reflect.Array;
import java.util.function.Function;
/**
 * Point is one of the most basic elements in the plane.A point has three dimensions:x,y,z.
 * @author lyc
 *
 * @param <T> the type of number 
 */
public final class SPoint<T> extends SpacePointSet<T> {
	
	final T x,y,z;
	
	public SPoint(MathCalculator<T> mc,T x,T y,T z) {
		super(mc);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	/**
	 * Get the X coordinate of this point.
	 * @return x 
	 */
	public T getX(){
		return x;
	}
	/**
	 * Get the Y coordinate of this point.
	 * @return y 
	 */
	public T getY(){
		return y;
	}
	/**
	 * Get the Z coordinate of this point.
	 * @return z 
	 */
	public T getZ(){
		return z;
	}
	
	/**
	 * Get the vector representing this point.The returned vector will be a column 
	 * vector and the length of this vector will be 2.
	 * @return a vector
	 */
	public SVector<T> getVector(){
		return new SVector<>(x,y,z,mc);
	}
	/**
	 * Return the square of the distance between this point and the given point.This method 
	 * is often better than calling {@code distance()} method. 
	 * @param p another point
	 * @return {@literal (x-p.x)^2 + (y-p.y)^2 + (z-p.z)^2}
	 */
	public T distanceSq(SPoint<T> p){
		T dx = mc.subtract(x, p.x);
		T dy = mc.subtract(y, p.y);
		T dz = mc.subtract(z, p.z);
		return mc.add(mc.add(mc.multiply(dx, dx), mc.multiply(dy, dy)),mc.multiply(dz, dz));
	}
	/**
	 * Return the distance of {@code this} and the given point {@code p}.The operation 
	 * {@linkplain MathCalculator#squareRoot(Object)} is required when this method is called.
	 * Make sure that the calculator implements the operation. 
	 * @param p another point
	 * @return the distance of {@code this} and {@code p}
	 * @see SPoint#distance(SPoint)
	 */
	public T distance(SPoint<T> p){
		return mc.squareRoot(distanceSq(p));
	}
	
	/**
	 * Create a vector that is equal to <i>thisP</i>.
	 * @param p another point
	 * @return a column vector with two dimensions.
	 */
	public SVector<T> directVector(SPoint<T> p){
		T vx = mc.subtract(p.x, x);
		T vy = mc.subtract(p.y, y);
		T vz = mc.subtract(p.z, z);
		return SVector.valueOf(vx, vy, vz, mc);
	}
	
	/**
	 * Returns a point M that <i>ThisM</i> = k<i>MP</i>,the direction is specific and negative {@code k} value 
	 * is permitted,but it should not be {@code -1}.<p>
	 * The result will be {@code x = (kp.x+this.x)/(k+1)},
	 * {@code y = (kp.y+this.y)/(k+1)},
	 * {@code x = (kp.z+this.z)/(k+1)},
	 * @param p another point
	 * @param k a number except -1.
	 * @return the proportion point.
	 */
	public SPoint<T> proportionPoint(SPoint<T> p,T k){
		T de = mc.add(k, mc.getOne());
		T xN = mc.add(mc.multiply(k, p.x), x);
		T yN = mc.add(mc.multiply(k, p.y), y);
		T zN = mc.add(mc.multiply(k, p.z), z);
		xN = mc.divide(xN, de);
		yN = mc.divide(yN, de);
		zN = mc.divide(zN, de);
		return new SPoint<>(mc,xN,yN,zN);
	}
	
	/**
	 * Returns a point of {@code this} moves by {@code v}.
	 * @param v
	 * @return a new point
	 */
	public SPoint<T> moveToward(SVector<T> v){
		return new SPoint<>(mc,mc.add(x, v.x),mc.add(y, v.y),mc.add(z, v.z));
	}
	/**
	 * Returns a point of {@code this} moves by {@code v}.
	 * @param v
	 * @return a new point
	 */
	public SPoint<T> moveToward(T vx,T vy,T vz){
		return new SPoint<>(mc,mc.add(x, vx),mc.add(y, vy),mc.add(z, vz));
	}
	
	/**
	 * Return the middle point of {@code this} and {@code p}.
	 * @param p another point
	 * @return middle point
	 */
	public SPoint<T> middle(SPoint<T> p){
		T xm = mc.divideLong(mc.add(x, p.x), 2);
		T ym = mc.divideLong(mc.add(y, p.y), 2);
		T zm = mc.divideLong(mc.add(z, p.z), 2);
		return new SPoint<T>(mc,xm,ym,zm);
	}
	
	/**
	 * Returns {@code this.valueEquals(p)}
	 */
	@Override
	public boolean contains(SPoint<T> p) {
		return mc.isEqual(x, p.x) &&
				mc.isEqual(y, p.y) &&
				mc.isEqual(z, p.z) ;
	}
	@Override
	public <N> SPoint<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		return new SPoint<>(newCalculator,mapper.apply(x),mapper.apply(y),mapper.apply(z));
	}
	
	
	private int hashCode = 0;
	@Override
	public int hashCode() {
		if(hashCode == 0){
			int hash = 31 + x.hashCode() ;
			hash = hash*31 + y.hashCode();
			hashCode = hash*31 + z.hashCode();
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SPoint){
			SPoint<?> sv = (SPoint<?>) obj;
			return x.equals(sv.x) && y.equals(sv.y) && z.equals(sv.z);
		}
		return false;
	}
	
	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof SPoint){
			SPoint<N> s = (SPoint<N>) obj;
			return mc.isEqual(x, mapper.apply(s.x)) &&
					mc.isEqual(y, mapper.apply(s.y)) &&
					mc.isEqual(z, mapper.apply(s.z)) ;
		}
		return false;
	}
	
	@Override
	public boolean valueEquals(MathObject<T> obj) {
		if(obj instanceof SPoint){
			SPoint<T> s = (SPoint<T>) obj;
			return mc.isEqual(x, s.x) &&
					mc.isEqual(y, s.y) &&
					mc.isEqual(z, s.z) ;
		}
		return false;
	}
	
	/**
	 * Return the String expression of this point.The expression will 
	 * be like :
	 * <pre>
	 * (x,y,z)
	 *</pre>
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(x).append(",").append(y).append(",").append(z).append(")");
		return sb.toString();
	}
	/**
	 * Create a point with the given coordinates. 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param mc a MathCalculator
	 * @return a new point
	 */
	public static <T> SPoint<T> valueOf(T x,T y,T z,MathCalculator<T> mc){
		return new SPoint<>(mc,x,y,z);
	}
	/**
	 * Returns the point (0,0).
	 * @param mc a {@link MathCalculator}
	 * @return point (0,0)
	 */
	public static <T> SPoint<T> pointO(MathCalculator<T> mc){
		return new SPoint<>(mc,mc.getZero(),mc.getZero(),mc.getZero());
	}
	/**
	 * Create a point with the given vector coordinates. 
	 * @param v a space vector
	 * @return point(x,y,z)
	 */
	public static <T> SPoint<T> valueOf(SVector<T> v){
		return new SPoint<>(v.getMathCalculator(),v.getX(),v.getY(),v.getZ());
	}
	/**
	 * Returns the *average of the points, the value of x,y,z will be the 
	 * corresponding average.
	 * @param points
	 * @return
	 */
	@SafeVarargs
	public static <T> SPoint<T> average(SPoint<T>...points){
		MathCalculator<T> mc = points[0].mc;
		final int num = points.length;
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(points[0].x.getClass(), points.length);
		for(int i=0;i<num;i++){
			arr[i] = points[i].x;
		}
		T xm = mc.addX(arr);
		for(int i=0;i<num;i++){
			arr[i] = points[i].y;
		}
		T ym = mc.addX(arr);
		for(int i=0;i<num;i++){
			arr[i] = points[i].z;
		}
		T zm = mc.addX(arr);
		xm = mc.divideLong(xm, num);
		ym = mc.divideLong(ym, num);
		zm = mc.divideLong(zm, num);
		return new SPoint<T>(mc,xm, ym, zm);
	}
	
	public static class SPointGenerator<T> extends MathObject<T> {

		/**
		 * @param mc
		 */
		public SPointGenerator(MathCalculator<T> mc) {
			super(mc);
		}
		/**
		 * Returns a point 
		 * @param x
		 * @param y
		 * @param z
		 * @return
		 */
		public SPoint<T> of(T x,T y,T z){
			return SPoint.valueOf(x, y, z, mc);
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.utilities.math.MathCalculator)
		 */
		@Override
		public <N> SPointGenerator<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new SPointGenerator<>(newCalculator);
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.FlexibleMathObject#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof SPointGenerator){
				return mc.equals(((SPointGenerator<?>)obj).mc);
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
		public String toString(NumberFormatter<T> nf) {
			return "SPointGenerator";
		}
	}

}
