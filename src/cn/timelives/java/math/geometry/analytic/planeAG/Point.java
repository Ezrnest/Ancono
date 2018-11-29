package cn.timelives.java.math.geometry.analytic.planeAG;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
/**
 * Point is one of the most basic elements in the plane.A point has two dimensions,x and y.
 * @author lyc
 *
 * @param <T> the type of number 
 */
public final class Point<T> extends MathObject<T> {
	
	public final T x,y;
	
	public Point(MathCalculator<T> mc,T x,T y) {
		super(mc);
		this.x = x;
		this.y = y;
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
	 * Get the vector representing this point.The returned vector will be a column 
	 * vector and the length of this vector will be 2.
	 * @return a vector
	 */
	public PVector<T> getVector(){
        return PVector.valueOf(x, y, getMc());
	}
	/**
	 * Return the square of the distance between this point and the given point.This method 
	 * is often better than calling {@code distance()} method. 
	 * @param p another point
	 * @return {@literal (x-p.x)^2 + (y-p.y)^2}
	 */
	public T distanceSq(Point<T> p){
        T dx = getMc().subtract(x, p.x);
        T dy = getMc().subtract(y, p.y);
        return getMc().add(getMc().multiply(dx, dx), getMc().multiply(dy, dy));
	}
	/**
	 * Return the distance of {@code this} and the given point {@code p}.The operation 
	 * {@linkplain MathCalculator#squareRoot(Object)} is required when this method is called.
	 * Make sure that the calculator implements the operation. 
	 * @param p another point
	 * @return the distance of {@code this} and {@code p}
	 * @see Point#distance(Point)
	 */
	public T distance(Point<T> p){
        return getMc().squareRoot(distanceSq(p));
	}
	/**
	 * Returns a point M that <i>ThisM</i> = k<i>MP</i>,the direction is specific and negative {@code k} value 
	 * is permitted,but it should not be {@code -1}.<p>
	 * The result will be {@code x = (kp.x+this.x)/(k+1)},
	 * {@code y = (kp.y+this.y)/(k+1)}.
	 * @param p another point
	 * @param k a number except -1.
	 * @return the proportion point.
	 */
	public Point<T> proportionPoint(Point<T> p,T k){
        T de = getMc().add(k, getMc().getOne());
        T xN = getMc().add(getMc().multiply(k, p.x), x);
        T yN = getMc().add(getMc().multiply(k, p.y), y);
        xN = getMc().divide(xN, de);
        yN = getMc().divide(yN, de);
        return new Point<>(getMc(), xN, yN);
	}
	
	/**
	 * Return the middle point of {@code this} and {@code p}.
	 * @param p another point
	 * @return middle point
	 */
	public Point<T> middle(Point<T> p){
        T xm = getMc().divideLong(getMc().add(x, p.x), 2);
        T ym = getMc().divideLong(getMc().add(y, p.y), 2);
        return new Point<T>(getMc(), xm, ym);
	}
	/**
	 * Translate this point to a new point with the vector. The result will be
	 * {@literal (x+v.x,y+v.y)}.
	 * @param v a vector
	 * @return {@literal (x+v.x,y+v.y)}
	 */
	public Point<T> translate(PVector<T> v){
        return new Point<>(getMc(), getMc().add(x, v.x), getMc().add(y, v.y));
	}
	
	@Override
    public <N> Point<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
		return new Point<>(newCalculator,mapper.apply(x),mapper.apply(y));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof Point){
			Point<?> p = (Point<?>) obj;
			return x.equals(p.x) && y.equals(p.y);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
        int h = getMc().hashCode();
		h = h*31 + x.hashCode();
		h = h*31 + y.hashCode();
		return h;
	}
	
	@Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
		if(obj instanceof Point){
			Point<N> p = (Point<N>) obj;
            return getMc().isEqual(x, mapper.apply(p.x)) && getMc().isEqual(y, mapper.apply(p.y));
		}
		return false;
	}
	@Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
		if(obj ==this){
			return true;
		}
		if(obj instanceof Point){
			Point<T> p = (Point<T>) obj;
            return getMc().isEqual(x, p.x) && getMc().isEqual(y, p.y);
		}
		return false;
	}
	
	/**
	 * Return the String expression of this point.The expression will 
	 * be like :
	 * <pre>
	 * "( "+x.toString()+" , "+y.toString()+" )"
	 *</pre>
	 */
	@Override
	public String toString() {
		return toString(FlexibleNumberFormatter.getToStringFormatter());
	}
	
	/**
	 * Return the String expression of this point.The expression will 
	 * be like :
	 * <pre>
	 * "( "+x.toString()+" , "+y.toString()+" )"
	 *</pre>
	 */
	@Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
		StringBuilder sb = new StringBuilder();
		sb.append("( ");
        sb.append(nf.format(x, getMc())).append(" , ").append(nf.format(y, getMc())).append(" )");
		return sb.toString();
	}
	
	/**
	 * Create a point with the given coordinates. 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param mc a MathCalculator
	 * @return a new point
	 */
	public static <T> Point<T> valueOf(T x,T y,MathCalculator<T> mc){
		return new Point<>(mc,x,y);
	}
	/**
	 * Returns the point (0,0).
	 * @param mc a {@link MathCalculator}
	 * @return point (0,0)
	 */
	public static <T> Point<T> pointO(MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		Point<T> p = (Point<T>) opoints.get(mc);
		if(p == null) {
			p = new Point<>(mc,mc.getZero(),mc.getZero());
			opoints.put(mc, p);
		}
		return p;
	}
	
	private static final Map<MathCalculator<?>,Point<?>> opoints = new ConcurrentHashMap<>();
	
	
	
	/**
	 * Create a vector that is equal to <i>thisP</i>.
	 * @param p another point
	 * @return a column vector with two dimensions.
	 */
	public PVector<T> directVector(Point<T> p){
        @SuppressWarnings("SuspiciousNameCombination")
        T vx = getMc().subtract(p.x, x);
        @SuppressWarnings("SuspiciousNameCombination")
        T vy = getMc().subtract(p.y, y);
        return PVector.valueOf(vx, vy, getMc());
	}

	public static <T> Point<T> fromVector(PVector<T>v){
	    return new Point<>(v.getMathCalculator(),v.x,v.y);
    }


}
